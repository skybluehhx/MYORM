package Pools;

import ORMException.DestoryPoolException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 * 提供连接池的基本功能
 */

public class BasePool implements Pool {

    private static Logger logger = Logger.getLogger(BasePool.class);
    //数据源，获取连接数据库的基本信息
    @Autowired
    private DataSource dataSource;
    //用来持有连接池连接
    private final BlockingQueue<PoolConnection> blockingQueue;

    //数据库持有连接数量，初始值为0；
    private AtomicInteger poolSize = new AtomicInteger(0);
    //扩容标志，当为true时，表明连接池正在扩容，
    //一个线程池我们设定只有一个线程进行扩容,后面想了想多个线程同时扩容也没事，所以注释掉
    //private AtomicBoolean isDilatation = new AtomicBoolean(false);

    //初始化标志 只能被初始化一次
    private AtomicBoolean isInitialization;
    //销毁标志
    private AtomicBoolean isDestroy = new AtomicBoolean(false);
    //默认重试次数，当数据库获取连接失败时，将会重试，默认
    //重试次数为连接池规定连接数，当为0时将不会重试，
    private int tryTimes;
    //增长的步伐，从连接池中获取连接时，如果没有
    //获取到连接而连接数小于最大连接时将以该步长增长连接数
    //默认步长为4
    private int step;

    public BasePool() {
        this(new LinkedBlockingQueue());
    }

    public BasePool(BlockingQueue blockingQueue) {
        this.blockingQueue = blockingQueue;
    }



    public BasePool(BlockingQueue<PoolConnection> blockingQueue, int times, int step) {
        this.blockingQueue = blockingQueue;
        this.tryTimes = times;
        this.isInitialization = new AtomicBoolean(false);
        this.step = step;
        init();
    }
/**
 public PoolConnection getConnection(long timeout, TimeUnit unit) {

 return null;
 }
 **/
    /**
     * 获取连接，获取连接时并不能保证当连接池尺寸小于设定的最大连接数时
     * 能立马获取到连接，但能保证最终获取连接
     *
     * @return
     */
    public PoolConnection getPoolConnection() {
        if (!isDestroy.get()) {
            try {

                PoolConnection poolConnection = blockingQueue.poll();
                if (poolConnection == null) { //判断线程池大小是否达到最大
                    if (poolSize.get() >= dataSource.getMaxConnection()) {//达到最大阻塞等待
                        poolConnection = blockingQueue.take();
                    } else {

                        //if (isDilatation.compareAndSet(false, true)) {
                        //进行判断,并确保每次只有一个线程进行扩增操作
                        int currentSize;
                        int afterSize;
                        int newSize;
                        while (true) {
                            currentSize = poolSize.get();
                            afterSize = currentSize + step;
                            newSize = afterSize > dataSource.getMaxConnection() ? dataSource.getMaxConnection() : afterSize;
                            int addNum = newSize - currentSize;
                            //确保只有一个线程进行扩容线程池
                            if (poolSize.compareAndSet(currentSize, newSize)) {
                                for (int i = 0; i < addNum; i++) {
                                    //这里为提高效率可以返回第一个PoolConnecton，其他连接操作可以交由另一个线程操作
                                    blockingQueue.add(getNewOneConnection(dataSource.getUrl(),
                                            dataSource.getUserName(), dataSource.getPassword()));
                                }
                                break;
                            }
                            //isDilatation.set(false);看前面关于isDilatation属性的介绍，后发现多线程扩容也没事

                        }
                        return blockingQueue.take();
                        //} else { //有线程在扩容，直接阻塞等待返回
                        //  return blockingQueue.take();
                        //}
                    }

                }
                return poolConnection;

            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        throw new DestoryPoolException("连接池已被毁坏");

    }


    public int getPoolSize() {
        return poolSize.get();
    }

    public int getFreeConnectioNums() {
        return blockingQueue.size();
    }

    /**
     * 归还连接，
     *
     * @param connection
     * @return
     */
    public boolean relasePoolConnection(PoolConnection connection) {

        blockingQueue.add(connection);
        return true;
    }

    //释放连接，正如你所料的 我们依靠poolSize来确保连接池大小
    //所以在释放连接失败时，poolSize的值应该减一，为了避免被错误的
    //使用，我们增加限制，传入空值时，将会抛出异常，这里并没有
    //做强制的保证，如果用户归还的连接不是连接池中的连接 我们也会
    //确保它归还成功，错误的使用该方法该会造成连接池实际数量
    //大于现有数量，其实可以使用一个ThrealLocal 保存一个标志
    //当线程获取连接时，将标志设置为true,只有带有标志的线程才能归还
    //并且归还后需要重新置为false，考虑到该线程池，是内置使用，
    //这里并没有实现该功能，如果需求特殊，后续考虑补加
    public boolean relaseConnection(Connection connection) {
        if (connection == null) {
            throw new RuntimeException("请确保释放的连接不为空");
        }
        //在归还前 确保归还的连接可用，
        boolean falg = false; //poolSize 减一是否成功的标志
        try {

            if (connection.isClosed()) { //连接已关闭，但连接池大小小于规定最小数目
                //连接释放失败，直接尺寸减一
                poolSize.getAndDecrement();
                falg = true;
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (!falg) {
                poolSize.getAndDecrement();
            }
        }
        return relasePoolConnection(new PoolConnection(connection));
    }

    public boolean destroy() {
        return false;
    }


    public void init() {
        //没有被初始化才进行初始化
        if (isInitialization.compareAndSet(false, true)) {
            logger.error("开始初始化线程池");
            try {
                Class driver = Class.forName(dataSource.getDriverClassName());
                DriverManager.registerDriver((Driver) driver.newInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "数据库驱动类错误");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "注册数据库驱动失败");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "注册数据库驱动失败");
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "注册数据库驱动失败");
            }
            String url = dataSource.getUrl();
            String userName = dataSource.getUserName();
            String password = dataSource.getPassword();
            for (int i = 0; i < dataSource.getMinConnection(); i++) {
                try {
                    Connection connection = DriverManager.getConnection(url, userName, password);
                    blockingQueue.add(new PoolConnection(connection));
                    poolSize.getAndIncrement();
                    //将连接放入阻塞队列
                } catch (SQLException e) {
                    logger.error("获取一条数据库连接失败", e);
                    //补入重试机制，确保数据库连接能够完成
                    while (tryTimes > 0) {
                        try {
                            Connection connection = DriverManager.getConnection(url, userName, password);
                            blockingQueue.add(new PoolConnection(connection));
                            poolSize.getAndIncrement();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                            logger.error("重试时获取一条数据库连接失败", e);
                        }
                        tryTimes--;
                        logger.error("重试次数剩余" + tryTimes, e);
                    }

                }


            }


        } else {
            logger.warn("线程池正在被或已经被初始化,一个线程池只能被初始化一次");
            throw new RuntimeException("请确保线程池只被初始化一次");
        }

    }

    private PoolConnection getNewOneConnection(String url, String userName, String password) {

        try {
            Connection connection = DriverManager.getConnection(url, userName, password);
            return new PoolConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            while (tryTimes > 0) { //重试
                try {
                    Connection connection = DriverManager.getConnection(url, userName, password);
                    return new PoolConnection(connection);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    logger.error("重试时获取一条数据库连接失败", e);
                }
                tryTimes--;
                logger.error("重试次数剩余" + tryTimes, e);
            }
            throw new RuntimeException("重试次数用光，获取连接失败");
        }

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public void setPoolSize(AtomicInteger poolSize) {
        this.poolSize = poolSize;
    }




    public int getTryTimes() {
        return tryTimes;
    }

    public void setTryTimes(int tryTimes) {
        this.tryTimes = tryTimes;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
