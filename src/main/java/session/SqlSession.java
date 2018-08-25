package session;

import config.Configuration;

import java.util.List;

/**
 * Created by zoujianlin
 * on 2018/8/19 0019.
 */
public interface SqlSession {

    public <T> T getMapper(Class<T> clazz);


    public int insert(String name,Object model);

    public int delete(String name, Object model);

    public int update(String name, Object model);

    /**
     * 返回查询语句
     *
     * @param name   sql语句
     * @param model  参数
     * @param tClass 绑定model的类型
     * @param <T>
     * @return
     */
    public <T> List<T> selectList(String name, Object model, Class<T> tClass);

    public <T> T selectOne(String name,Object model, Class<T> tClass);


    public Configuration getConfiguration();

}
