package excutor;

import Transactional.TransactionManage;
import mapper.MapperModelParams;
import mapper.SqlCommandType;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import session.SqlSession;
import support.JDBCUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by zoujianglin
 * 2018/8/20 0020.
 */
@Component("excutor")
public class DefaultExcutor implements Excutor {
    @Autowired
    private TransactionManage transactionManage;

    private ParameterHandler parameterHandler = new DefaultParameterHandler();
    private StatementHandler statementHandler = new DefaultStatementHandler();

    //private StatementHandler statementHandlerForInsert = new StatementHandlerForInsert();
    private ResultHandler resultHandler = new DefaultResultHandler();


    /**
     * 执行除select以外的，其他三种查询
     *
     * @param sqlCommandType
     * @param sql
     * @param model          传入的实体类
     * @return
     * @throws SQLException
     */
    public Object execute(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException {
        Object result = null;
        switch (sqlCommandType) {
            case DELETE:
                result = executeDelete(sqlSession, mapperClass, sqlCommandType, sql, model);
                break;
            case INSERT:
                result = executeInsert(sqlSession, mapperClass, sqlCommandType, sql, model);
                break;
            case UPDATE:
                result = executeUpdate(sqlSession, mapperClass, sqlCommandType, sql, model);
                break;
            case UNKNOWN:
                throw new RuntimeException("语句必须是 select，insert,update,delete");
        }
        return result;
    }


    public <T> List<T> executeSelect(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model, Class<T> tClass) throws SQLException {
        PreparedStatement preparedStatement = dopreparedstatement(sqlSession, mapperClass, sqlCommandType, sql, model);
        return doExecuteSelect(sqlSession, mapperClass, preparedStatement, tClass);
    }

    public <T> T executeSelectOne(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model, Class<T> tClass) throws SQLException {
        List<T> list = executeSelect(sqlSession, mapperClass, sqlCommandType, sql, model, tClass);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    private <T> List<T> doExecuteSelect(SqlSession sqlSession, Class<?> mapperClass, PreparedStatement preparedStatement, Class<T> tClass) {


        ResultSet resultSet;
        try {
            //委托JDBCUtil进行查询操作
            resultSet = JDBCUtil.selectRecord(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            transactionManage.realseCurrentThreadConnection();
            throw new RuntimeException(e);
        }
        List<T> list = resultHandler.handlerResult(sqlSession, mapperClass, resultSet);
        //释放连接
        transactionManage.realseCurrentThreadConnection();
        return list;
        // return JDBCUtil.getListCommBean(preparedStatement, tClass);

    }


    private int executeInsert(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException {

        return executeUpdate(sqlSession, mapperClass, sqlCommandType, sql, model);
    }

    private int executeUpdate(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException {

        PreparedStatement preparedStatement = dopreparedstatement(sqlSession, mapperClass, sqlCommandType, sql, model);
        return doExecuteUpdate(preparedStatement);
    }


    private int doExecuteUpdate(PreparedStatement preparedStatement) {
        int result = 0;
        try {
            result = JDBCUtil.updateRecord(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
            transactionManage.realseCurrentThreadConnection();
            throw new RuntimeException(e);
        }
        transactionManage.realseCurrentThreadConnection();
        return result;

    }

    private int executeDelete(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException {
        return executeUpdate(sqlSession, mapperClass, sqlCommandType, sql, model);
    }


    /**
     * PreparedStatement
     *
     * @return 返回已经准备好的 PreparedStatement
     */
    private PreparedStatement dopreparedstatement(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException {
        PreparedStatement statement;

        statement = statementHandler.getStatement(sqlSession, mapperClass, sql, model);
        //设置参数,这里没有做特殊处理
        statement = parameterHandler.setSqlParameter(sqlSession, mapperClass, statement, model);
        return statement;
    }

    public TransactionManage getTransactionManage() {
        return this.transactionManage;
    }

    public void setParameterHandler(ParameterHandler parameterHandler) {
        this.parameterHandler = parameterHandler;
    }

    public void setStatementHandler(StatementHandler statementHandler) {
        this.statementHandler = statementHandler;
    }


}
