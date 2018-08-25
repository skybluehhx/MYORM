package test;

import Pools.*;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 */
public class TestPool {

    public static void main(String[] args) {
        String driverClassName = "com.mysql.jdbc.Driver";
        String userName = "root";
        String password = "root";
        String url = "jdbc:mysql://127.0.0.1:3306/test";
        DataSource dataSource = new DataSourceBuilder(driverClassName, userName, password, url)
                .addMinConnection(10).addMaxConnection(20).build();


        Pool pool = new DefaultPool(dataSource);
        PoolConnection poolConnection = pool.getConnection();

    }


}
