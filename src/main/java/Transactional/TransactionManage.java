package Transactional;

import ORMException.ConnectionUnopendException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by zoujianglin
 * 2018/8/26 0026.
 */
public interface TransactionManage {
    //获取当前事务的连接
    Connection getCurrentThreadConnection();

    //开启事务
    boolean beginTransactionForCurrentThread();

    //关闭事务
    boolean commitTracsactionForCurrentThread();

    //提交事务
    boolean rollbackTracsactionForCurrentThread();

    //释放连接
    boolean realseCurrentThreadConnection();


}
