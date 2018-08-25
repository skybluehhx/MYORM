package excutor;

/**
 * Created by zoujianglin
 * on 2018/8/19 0019.
 */

import session.SqlSession;

import java.sql.ResultSet;
import java.util.List;

/**
 * 处理从数据库中返回的结果
 */
public interface ResultHandler {
    public <T> List<T> handlerResult(SqlSession sqlSession, Class<?> mapperClass, ResultSet resultSet);


}
