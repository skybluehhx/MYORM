package sql;

import session.SqlSession;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */
public interface SqlHandler {
    //对原先sql语句进行处理，返回符合数据库规范的sql语句
    String handSql(SqlSession sqlSession, Class<?> mapperClass, String preSql,Object model);



}
