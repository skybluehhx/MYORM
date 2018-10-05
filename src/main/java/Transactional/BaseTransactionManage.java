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
    ThreadLocal<Connection> threadConnections = new ThreadLocal();

    @Autowired()
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
            throw new ConnectionUnopendException("连接未开启，使用事务前确保事务开启");
        }

        try {
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
                return true;
            }
            ;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean commitTracsactionForCurrentThread() {
        Connection connection = threadConnections.get();
        if (connection == null) {
          //  throw new ConnectionUnopendException("连接未开启，使用事务前确保事务开启");
        }

        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}


