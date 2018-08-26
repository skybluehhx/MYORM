package Pools;

import Transactional.TransactionManage;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 * PoolConnection为连接池对连接的抽象
 */
public class PoolConnection {

    //表示连接的唯一标识，
    private long id = 0;

    private AtomicLong Iid = new AtomicLong(0);
    // 维持着数据库连接
    private Connection connection;
    //标志着该连接是否被其他线程使用
    // false表示连接不可用，表明连接已经被占用
    //true表示该连接未被占用，可以正常使用
    private volatile boolean isAvailable;
    //管理该连接的事务管理器，只有从事务管理器中获取的连接才会被设置 暂未使用
    // private TransactionManage transactionManage;

    //创建时，不带参数默认没有被使用
    public PoolConnection(Connection connection) {
        this(connection, true);
    }

    public PoolConnection(Connection connection, boolean isAvailable) {
        this.connection = connection;
        this.isAvailable = isAvailable;
        this.id = Iid.getAndIncrement();
    }

    @Override
    public String toString() {
        return "PoolConnection{" +
                "connection=" + connection +
                ", isAvailable=" + isAvailable + "connection=" + id +
                '}';
    }

    public long getId() {
        return id;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean isAvailable() {
        return isAvailable;
    }


}
