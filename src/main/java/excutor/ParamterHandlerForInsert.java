package excutor;

import session.SqlSession;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 * 该参数处理器专门为insert,delete语句而设置，在进行这些操作时需要进行解码
 *将
 */
public class ParamterHandlerForInsert implements ParameterHandler {
    public PreparedStatement setSqlParameter(SqlSession sqlSession, Class<?> mapperClass, PreparedStatement preparedStatement, Object model) throws SQLException {
        return preparedStatement;
    }
}
