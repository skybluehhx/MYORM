package session;

import Transactional.TransactionManage;
import config.Configuration;
import excutor.Excutor;
import mapper.MappedStatement;
import mapper.MapperMethod;
import mapper.MapperRegistry;
import mapper.SqlCommandType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */
@Component("defaultSqlSession")
public class DefaultSqlSession<T> implements SqlSession {

    @Autowired
    private Configuration configuration;
    //记得注入
    @Autowired
    private Excutor excutor;

    public <T> T selectOne(int id) {
        return null;
    }

    public <T> T getMapper(Class<T> clazz) {
        MapperRegistry mapperRegistry = configuration.getMapperRegistry();
        return mapperRegistry.getMapper(clazz, this);
    }

    public int update(String name, Object model) {
        int result;
        MappedStatement mappedStatement = configuration.getMappedStatement(name);
        String sql = mappedStatement.getResource();
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Class mapperClass = mappedStatement.getMapperClass();
        if (mapperClass == null) {
            throw new RuntimeException("数据库注解语句必须与mapper绑定");
        }
        try {
            result = (Integer) excutor.execute(this, mapperClass, sqlCommandType, sql, model);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    public int delete(String name, Object model) {
        return this.update(name, model);
    }

    public int insert(String name, Object model) {
        return this.update(name, model);
    }


    /**
     * @param name
     * @param model
     * @param tClass
     * @param <T>
     * @return
     */

    public <T> List<T> selectList(String name, Object model, Class<T> tClass) {
        List<T> result;
        MappedStatement mappedStatement = configuration.getMappedStatement(name);
        String sql = mappedStatement.getResource();
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        Class<?> mapperClass = mappedStatement.getMapperClass();
        try {
            result = (List<T>) excutor.executeSelect(this, mapperClass, sqlCommandType, sql, model, tClass);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;

    }

    /**
     * @param name
     * @param model
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T selectOne(String name, Object model, Class<T> tClass) {
        T result;
        MappedStatement mappedStatement = configuration.getMappedStatement(name);
        String sql = mappedStatement.getResource();
        Class<?> mapperClass = mappedStatement.getMapperClass();
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        try {
            result = excutor.executeSelectOne(this, mapperClass, sqlCommandType, sql, model, tClass);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    public Connection getConnection() {
        return excutor.getTransactionManage().getCurrentThreadConnection();
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }


}
