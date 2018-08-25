package excutor;

import session.SqlSession;
import sql.DefaultSqlHandler;
import sql.SqlHandler;
import sql.SqlHandlerForInsert;
import support.JDBCUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */
public class StatementHandlerForInsert implements StatementHandler {
    private SqlHandler sqlHandler = new SqlHandlerForInsert();

    public PreparedStatement getStatement(SqlSession sqlSession, Class<?> mapperClass, String PreSql, Object model) throws SQLException {
        //获取符合JDBC规范的sql语句
        String sql = sqlHandler.handSql(sqlSession, mapperClass, PreSql, model);
        //获取PreparedStatement
        PreparedStatement preparedStatement = JDBCUtil.getStmt(sql);
        if (preparedStatement == null) {
            throw new RuntimeException("preparedStatement is null");
        }
        return preparedStatement;
    }
}

