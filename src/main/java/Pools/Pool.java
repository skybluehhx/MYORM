package Pools;

import java.sql.Connection;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 */
public interface Pool {

    //获取连接池中的“连接”
    PoolConnection getPoolConnection();


    boolean relasePoolConnection(PoolConnection connection);

    void setDataSource(DataSource dataSource);

    //
    int getPoolSize();

    //销毁连接池
    boolean destroy();

    //释放连接,归还连接，使用该方法并不是销毁连接而是
    //重新归回线程池，共其他线程复用
    boolean relaseConnection(Connection connection);

}
