package support;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */

import Pools.DefaultPoolFactory;
import Pools.Pool;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JDBCUtil {
    //默认连接池
    private static Pool pool = DefaultPoolFactory.getDefaultPool();

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
    private static Logger logger = Logger.getLogger(JDBCUtil.class);


    public static void main(String[] args) throws Exception {
        String sql = "insert into TestPerson values(?,?,?)";

        PreparedStatement preparedStatement = getStmt(sql);
        preparedStatement.setObject(1, 14);
        preparedStatement.setObject(2, 4);
        preparedStatement.setObject(3, "1$4");
        preparedStatement.executeUpdate();


    }


    public static Connection getConn() throws SQLException {
        Connection connection;
        connection = getConnectionFromThreadLocal();
        if (connection != null) {
            return connection;
        }
        try {
            connection = pool.getPoolConnection().getConnection();
            if (!connection.isClosed()) {
                threadLocal.set(connection);
            }
            //connection = DriverManager.getConnection(url, userName, password);
        } catch (RuntimeException re) {
            logger.error("获取数据库连接异常：" + re.getMessage(), re);
            throw re;
        }
        return connection;
    }

    public static Connection getConnectionFromThreadLocal() {
        return threadLocal.get();
    }

    public static PreparedStatement getStmt(String sql) throws SQLException {
        PreparedStatement preparedStatement = null;
        try {
            Connection connection = getConn();
            preparedStatement = connection.prepareStatement(sql);
        } catch (RuntimeException re) {
            logger.error("获取数据库处理命令异常：" + re.getMessage(), re);
            throw re;
        }
        return preparedStatement;
    }

    /**
     * 设置参数
     *
     * @param preparedStatement
     * @param params
     * @return
     * @throws SQLException
     */
    public static PreparedStatement setParams(PreparedStatement preparedStatement, Object[] params) throws SQLException {
        try {
            //if(params!=null&&params.size()>0){ 数据库最底层，不进行任何逻辑判断，也只捕获RuntimeExcepiton  并重新抛出
            for (int i = 0; i < params.length; i++) {

                preparedStatement.setObject(i + 1, params[i]);
            }
            //}
        } catch (RuntimeException re) {
            logger.error("设置查询参数发生异常：" + re.getMessage(), re);
            throw re;
        }
        return preparedStatement;
    }


    /**
     * INSERT, UPDATE or DELETE  ,增、删、改 通用查询方法
     *
     * @param preparedStatement 注意改prearedStatement已经注册好了参数
     * @return 1、有数据被更新：则返回更新的记录数；2、没有记录被更新：返回0；     either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public static int updateRecord(PreparedStatement preparedStatement) throws SQLException {
        int iNum = 0;
        try {
            iNum = preparedStatement.executeUpdate();//更新成功，返回更新的记录数，没有更新返回0


        } catch (RuntimeException re) {
            logger.error("更新数据库记录异常:" + re.getMessage());
            throw re;
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            Connection connection = threadLocal.get();
            if (connection != null) {
                threadLocal.remove();
                pool.relaseConnection(connection);
            }

        }
        return iNum;
    }


    /**
     * INSERT, UPDATE or DELETE  ,增、删、改 通用查询方法
     *
     * @param preparedStatement 注意改prearedStatement已经注册好了参数
     * @return 1、有数据被更新：则返回更新的记录数；2、没有记录被更新：返回0；     either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public static ResultSet selectRecord(PreparedStatement preparedStatement) throws SQLException {
        ResultSet resultSet;
        try {
            resultSet = preparedStatement.executeQuery();//更新成功，返回更新的记录数，没有更新返回0
        } catch (RuntimeException re) {
            logger.error("更新数据库记录异常:" + re.getMessage());
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            Connection connection = threadLocal.get();
            if (connection != null) {

                threadLocal.remove();
                //这里不需要释放连接，可能是连接出现问题，为确保安全直接置为空
                connection = null;
            }

            throw re;
        }
        return resultSet;
    }


    public static boolean relaseConnection(Connection connection) {
        return pool.relaseConnection(connection);
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }
}

