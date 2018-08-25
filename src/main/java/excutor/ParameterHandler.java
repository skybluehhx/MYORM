package excutor;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */

import session.SqlSession;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 用来设置参数
 */
public interface ParameterHandler {

    //设置sql的参数
     PreparedStatement setSqlParameter( SqlSession sqlSession,Class<?> mapperClass, PreparedStatement preparedStatement, Object model) throws SQLException;


}
