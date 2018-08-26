package excutor;

import Transactional.TransactionManage;
import mapper.SqlCommandType;
import session.SqlSession;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zoujianglin
 * on 2018/8/19 0019.
 */
public interface Excutor {

    // Object execute(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object[] params) throws SQLException;

    Object execute(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException;


    <T> List<T> executeSelect(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model, Class<T> tClass) throws SQLException;

    <T> T executeSelectOne(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model, Class<T> tClass) throws SQLException;

    //update 2018/ 8/26 为执行器添加事务功能
    TransactionManage getTransactionManage();

}
