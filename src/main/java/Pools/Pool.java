package Pools;

import java.sql.Connection;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 */
public interface Pool {

    //从连接池中获取连接
    PoolConnection getConnection();


    boolean relasePoolConnection(PoolConnection connection);

    //
    int getPoolSize();

    //销毁连接池
    boolean destroy();

    //释放连接,归还连接，使用该方法并不是销毁连接而是
    //重新归回线程池，共其他线程复用
    boolean relaseConnection(Connection connection);

}