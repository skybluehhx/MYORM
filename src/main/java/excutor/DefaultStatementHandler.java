package excutor;

import mapper.MapperRegistry;
import session.SqlSession;
import sql.DefaultSqlHandler;
import support.JDBCUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by zoujianglin
 * 2018/8/20 0020.
 */
public class DefaultStatementHandler implements StatementHandler {

    private DefaultSqlHandler sqlHandler = new DefaultSqlHandler();

    public PreparedStatement getStatement(SqlSession sqlSession, Class<?> mapperClass, String PreSql, Object model) throws SQLException {
        //获取符合JDBC规范的sql语句
        String sql = sqlHandler.handSql(sqlSession, mapperClass, PreSql, model);
        //获取PreparedStatement
        PreparedStatement preparedStatement = JDBCUtil.getStmt(sqlSession.getConnection(), sql);
        if (preparedStatement == null) {
            throw new RuntimeException("preparedStatement is null");
        }
        return preparedStatement;
    }


}
