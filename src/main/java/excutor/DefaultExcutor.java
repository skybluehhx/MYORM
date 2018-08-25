package excutor;

import mapper.MapperModelParams;
import mapper.SqlCommandType;
import org.apache.ibatis.jdbc.SQL;
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

    private ParameterHandler parameterHandler = new DefaultParameterHandler();
    private StatementHandler statementHandler = new DefaultStatementHandler();

    //private StatementHandler statementHandlerForInsert = new StatementHandlerForInsert();
    private ResultHandler resultHandler = new DefaultResultHandler();


    /**
     * 执行除select以外的，其他三种查询
     *
     * @param sqlCommandType
     * @param sql
     * @param model 传入的实体类
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


        ResultSet resultSet ;
        try {
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return resultHandler.handlerResult(sqlSession, mapperClass, resultSet);
        // return JDBCUtil.getListCommBean(preparedStatement, tClass);

    }


    private int executeInsert(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException {
        return executeUpdate(sqlSession, mapperClass, sqlCommandType, sql, model);
    }

    private int executeUpdate(SqlSession sqlSession, Class<?> mapperClass, SqlCommandType sqlCommandType, String sql, Object model) throws SQLException {

        PreparedStatement preparedStatement = dopreparedstatement(sqlSession, mapperClass, sqlCommandType, sql, model);
        return doExecuteUpdate(preparedStatement);
    }



    private int doExecuteUpdate(PreparedStatement preparedStatement) throws SQLException {
        return JDBCUtil.updateRecord(preparedStatement);

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

        statement =statementHandler.getStatement(sqlSession, mapperClass, sql, model);
        //设置参数,这里没有做特殊处理
        statement = parameterHandler.setSqlParameter(sqlSession, mapperClass, statement, model);
        return statement;
    }


    public void setParameterHandler(ParameterHandler parameterHandler) {
        this.parameterHandler = parameterHandler;
    }

    public void setStatementHandler(StatementHandler statementHandler) {
        this.statementHandler = statementHandler;
    }


}
