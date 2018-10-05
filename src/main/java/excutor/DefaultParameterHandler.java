package excutor;

/**
 * Created by zoujianglin
 * 2018/8/20 0020.
 */

import mapper.MapperModelParams;
import mapper.MapperRegistry;
import session.SqlSession;
import sql.DefaultSqlHandler;
import support.JDBCUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 本类为模仿mybatis存在，由于我们写的sql语句，自身遵守jdbc规范，顾
 * 不需要做特殊处理，但允许用户自定义实现自己的格式字只需要实现此方法
 * 注入到Excutor中即可
 */
public class DefaultParameterHandler implements ParameterHandler {
    /**
     * @param model
     * @return
     */


    public PreparedStatement setSqlParameter(SqlSession sqlSession, Class<?> mapperClass, PreparedStatement preparedStatement, Object model) throws SQLException {

        if (preparedStatement == null) {
            throw new RuntimeException("preparedStatement is null");
        }
        MapperRegistry mapperRegistry = sqlSession.getConfiguration().getMapperRegistry();
        Map<Class<?>, MapperModelParams> mapperModelToDB = mapperRegistry.getMapperModelToDB();
        Class<?> modelClass = mapperRegistry.getMapperModel().get(mapperClass);
        MapperModelParams mapperModelParams = mapperModelToDB.get(modelClass);
        List paramtersList = mapperModelParams.getParameters();
        //注入参数
        for (int i = 0; i < paramtersList.size(); i++) {
            preparedStatement.setObject(i + 1, paramtersList.get(i));
        }

        return preparedStatement;
    }

}

