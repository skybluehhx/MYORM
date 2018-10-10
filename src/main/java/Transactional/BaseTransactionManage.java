package Transactional;

import ORMException.ConnectionUnopendException;
import Pools.Pool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by zoujianglin
 * 2018/8/26 0026.
 * 事务管理器
 */
@Component("transactionManage")
public class BaseTransactionManage implements TransactionManage {
    private ThreadLocal<Connection> threadConnections = new ThreadLocal();
    /*主要用于表示当前连接的事务状态*/
    private ThreadLocal<TransactionStatus> tanscationStatus=new ThreadLocal(){
        @Override
        protected TransactionStatus initialValue() {
            return TransactionStatus.unopen;
        }
    };

    @Autowired
    private Pool pool;


    public Connection getCurrentThreadConnection() {
        Connection connection = threadConnections.get();
        if (connection == null) {
            connection = pool.getPoolConnection().getConnection();
            threadConnections.set(connection);
        }
        return connection;
    }

    //释放前请确保，当前线程以获取连接
    public boolean realseCurrentThreadConnection() {
        Connection connection = threadConnections.get();
        threadConnections.remove();
        return pool.relaseConnection(connection);
    }

    public boolean beginTransactionForCurrentThread() {
        Connection connection = threadConnections.get();
        if (connection == null) {
            connection= getCurrentThreadConnection();
      //      throw new ConnectionUnopendException("连接未开启，使用事务前确保事务开启");
        }

        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
                tanscationStatus.set(TransactionStatus.open);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            tanscationStatus.set(TransactionStatus.unopen);
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean commitTracsactionForCurrentThread() {
        Connection connection = threadConnections.get();
        if (connection == null) {
          //  throw new ConnectionUnopendException("连接未开启，使用事务前确保事务开启");
        }
        boolean isSuccess=true;
        tanscationStatus.set(TransactionStatus.runing);
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
                tanscationStatus.set(TransactionStatus.finish);
                isSuccess=false;
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if(!isSuccess){
                rollbackTracsactionForCurrentThread();
                tanscationStatus.set(TransactionStatus.Exception);
            }
           // realseCurrentThreadConnection();
        }
        return false;
    }

    public boolean rollbackTracsactionForCurrentThread() {
        Connection connection = threadConnections.get();
        if (connection == null) {
            throw new ConnectionUnopendException("连接未开启，使用事务前确保事务开启");
        }
        try {
            connection.rollback();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
         //   realseCurrentThreadConnection();
        }
        return false;

    }

    public TransactionStatus getStatus(){
        return tanscationStatus.get();
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}







