package excutor;

/**
 * Created by zoujianglin
 * 2018/8/19
 */

import session.SqlSession;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 用于生成statement
 */
public interface StatementHandler {

    public PreparedStatement getStatement(SqlSession sqlSession, Class<?> mapperClass, String sql, Object model) throws SQLException;
}
