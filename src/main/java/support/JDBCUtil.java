package support;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */

import org.apache.log4j.Logger;

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

    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();
    private static Logger logger = Logger.getLogger(JDBCUtil.class);
    private static String driverClassName = "";
    private static String url = "";
    private static String userName = "";
    private static String password = "";

    private static void loadConfig() {
        DBConfig config = new DBConfig();
        driverClassName = config.getDriverClassName();
        url = config.getUrl();
        userName = config.getUserName();
        password = config.getPassword();

    }

    static {
        try {
            loadConfig();
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            logger.error("数据库驱动类加载异常：" + e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {
        String sql = "insert into TestPerson values(?,?,?)";

        PreparedStatement preparedStatement = getStmt(sql);
        preparedStatement.setObject(1, 14);
        preparedStatement.setObject(2, 4);
        preparedStatement.setObject(3, "1$4");
        preparedStatement.executeUpdate();


    }


    public static Connection getConn() throws SQLException {
        Connection connection = null;
        connection = getConnectionFromThreadLocal();
        if (connection != null) {
            return connection;
        }
        try {
            connection = DriverManager.getConnection(url, userName, password);
        } catch (RuntimeException re) {
            logger.error("获取数据库连接异常：" + re.getMessage(), re);
            throw re;
        }
        return connection;
    }

    private static Connection getConnectionFromThreadLocal() {
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
/*
    public static ResultSet getRs(PreparedStatement preparedStatement) throws SQLException {
        try {
            rs = preparedStatement.executeQuery();
        } catch (RuntimeException re) {
            logger.error("获取查询结果发生异常：" + re.getMessage(), re);
            throw re;
        }
        return rs;
    }
*/


    /**
     * 封装结果集为：List<T>  T为实体bean的泛型
     * 因为没有对特殊类型做处理，不支持特殊类型的查询字段：比如：数字类型，日期类型等
     * 主要用于支持字符串类型
     *
     * @param sql
     * @param clazz  实体类的Class，T.calss
     * @param params List<Object>
     * @return List<T> T为实体bean的泛型
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    /*
    public static <T> List<T> getListBean(String sql, Class<T> clazz, List<Object> params) throws InstantiationException, IllegalAccessException, SQLException {
        List<T> list = new ArrayList<T>();
        T t = null;
        try {
            Object[] paramers = params.toArray();
            Field[] fields = clazz.getDeclaredFields();
            ResultSetMetaData rsmd = prepareRsmd(sql, paramers);
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                t = (T) clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rsmd.getColumnLabel(i + 1);
                    for (int j = 0; j < fields.length; j++) {
                        Field field = fields[j];
                        if (!(field.getName().equalsIgnoreCase(columnName))) continue;

                        boolean bFlag = field.isAccessible();
                        field.setAccessible(true);//打开javabean的访问权限
                        field.set(t, rs.getObject(i + 1));
                        field.setAccessible(bFlag);
                    }
                }
                list.add(t);
            }
        } catch (RuntimeException re) {
            logger.error("获取查询结果集异常：" + re.getMessage(), re);
            throw re;
        } finally {

        }

        return list;
    }
*/

    /**
     * 查询实体类的list结果集
     *
     * @param preparedStatement 已经预编译好的preparedStatement
     * @param clazz             实体类的Class
     * @return List<T>  T 实体类
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws SQLException
     */
    public static <T> List<T> getListCommBean(PreparedStatement preparedStatement, Class<T> clazz) throws InstantiationException, IllegalAccessException, SQLException {
        List<T> list = new ArrayList<T>();
        T t = null;
        try {
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            Field[] fields = clazz.getDeclaredFields();
            while (rs.next()) {
                t = (T) clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = rsmd.getColumnLabel(i + 1);
                    for (int j = 0; j < fields.length; j++) {
                        Field field = fields[j];
                        Class<?> fieldType = field.getType();//取得成员变量的数据类型的 Class
                        String fieldTypeString = fieldType.toString().toLowerCase();//成员变量数据类型的class的字符串只，并小写化
                        String fieldName = field.getName();//取得成员变量名
                        if (!fieldName.equalsIgnoreCase(columnName)) continue;//成员变量名，不等于查询出的字段的标签名称（忽略大小写），继续下次循环
                        setBeanValue(t, rs, i, field, fieldTypeString);//设置值
                    }
                }
                list.add(t);
            }

        } catch (RuntimeException re) {
            logger.error("获取查询结果集异常：" + re.getMessage(), re);
            throw re;
        } finally {


            Connection connection = threadLocal.get();
            if (connection != null) {
                threadLocal.remove();
                connection.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        return list;
    }

    /**
     * 同用实体类查询方法 ：只返回一条记录的sql语句，如果有多条，只返回第一条记录
     *
     * @param clazz  实体类的Class
     * @param sql    查询语句，只返回一条记录的sql语句，如果有多条，只返回第一条记录
     * @param params 查询语句的参数：list<Object>
     * @return 实体类：只返回一条记录的sql语句，如果有多条，只返回第一条记录
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IntrospectionException
     * @throws InvocationTargetException
     */
    /*
    public static <T> T getResultBean(Class<T> clazz, String sql, Object[] params) throws SQLException, InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        T t = null;
        try {
            Field[] fields = clazz.getDeclaredFields();//取得实体类成员变量对象
            ResultSetMetaData rsmd = prepareRsmd(sql, params);
            int columnCount = rsmd.getColumnCount();//取得数据列数
            while (rs.next()) {
                t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    String columnLabelName = rsmd.getColumnLabel(i + 1);//取得查询数据类的标签名：即as 后面的别名，如果没有别名，则为字段名称，取出的为大写格式
                    for (Field field : fields) {
                        String propertyName = field.getName();//取得实体类成员变量的名称
                        String propertyType = field.getType().toString().toLowerCase();//通过Field取得实体类变量 类型
                        if (!propertyName.equalsIgnoreCase(columnLabelName)) continue;
                        invokeBeanValue(t, i, propertyType, propertyName);//对带有指定参数的指定对象调用由此 Method 对象表示的底层方法invoke初始化值。t 实体类对象 - 从中调用底层方法的对象 args - 用于方法调用的参数
                    }
                }
                break;//此处，直接终止本次循环，只返回第一条结果。
            }

        } catch (RuntimeException re) {
            throw re;
        } finally {

        }
        return t;
    }
*/

    /**
     * 对带有指定参数的指定对象调用由此 Method 对象表示的底层方法invoke初始化值。
     *
     * @param t            t 实体类对象
     * @param i
     * @param propertyType
     * @param propertyName
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws SQLException
     */
    private static <T> void invokeBeanValue(T t, ResultSet rs, int i, String propertyType, String propertyName) throws IllegalAccessException, InvocationTargetException, IntrospectionException, SQLException {
        //对带有指定参数的指定对象调用由此 Method 对象表示的底层方法invoke初始化值。t 实体类对象 - 从中调用底层方法的对象 args - 用于方法调用的参数
        PropertyDescriptor propdscrp = new PropertyDescriptor(propertyName, t.getClass());//通过调用 get 和 set 存取方法，为符合标准 Java 约定的属性构造一个 PropertyDescriptor,成员变量aab001,则标准为：getAab001,setAab001。
        Method method = propdscrp.getWriteMethod();//读取写入方法，即：set方法
        if (propertyType.indexOf("byte") >= 0) {
            method.invoke(t, rs.getByte(i + 1));
        } else if (propertyType.indexOf("boolean") >= 0) {
            method.invoke(t, rs.getBoolean(i + 1));
        } else if (propertyType.indexOf("short") >= 0) {
            method.invoke(t, rs.getShort(i + 1));
        } else if (propertyType.indexOf("int") >= 0 || propertyType.indexOf("integer") >= 0) {
            method.invoke(t, rs.getInt(i + 1));
        } else if (propertyType.indexOf("long") >= 0) {
            method.invoke(t, rs.getLong(i + 1));
        } else if (propertyType.indexOf("float") >= 0) {
            method.invoke(t, rs.getFloat(i + 1));
        } else if (propertyType.indexOf("double") >= 0) {
            method.invoke(t, rs.getDouble(i + 1));
        } else if (propertyType.indexOf("string") >= 0) {
            method.invoke(t, rs.getString(i + 1));
        } else if (propertyType.indexOf("date") >= 0) {
            method.invoke(t, rs.getDate(i + 1));
        } else if (propertyType.indexOf("time") >= 0) {
            method.invoke(t, rs.getTime(i + 1));
        } else if (propertyType.indexOf("timestamp") > 0) {
            method.invoke(t, rs.getTimestamp(i + 1));
        } else if (propertyType.indexOf("bigdecimal") >= 0) {
            method.invoke(t, rs.getBigDecimal(i + 1));
        } else if (propertyType.indexOf("clob") >= 0) {
            method.invoke(t, rs.getClob(i + 1));
        } else if (propertyType.indexOf("blob") >= 0) {
            method.invoke(t, rs.getBlob(i + 1));//一般用：rs.getBinaryStream(i+1);
        } else {
            method.invoke(t, rs.getObject(i + 1));
        }
    }

    /**
     * 对带有指定参数的指定对象调用由此 Method ,实体类对象属性Field 的set方法。
     *
     * @param t
     * @param i
     * @param field
     * @param fieldTypeString
     * @throws IllegalAccessException
     * @throws SQLException
     */
    private static <T> void setBeanValue(T t, ResultSet rs, int i, Field field, String fieldTypeString) throws IllegalAccessException, SQLException {
        boolean bFlag = field.isAccessible();
        field.setAccessible(true);//打开javabean的访问权限
        if (fieldTypeString.indexOf("byte") >= 0) {
            field.set(t, rs.getByte(i + 1));
        } else if (fieldTypeString.indexOf("boolean") >= 0) {
            field.set(t, rs.getBoolean(i + 1));
        } else if (fieldTypeString.indexOf("short") >= 0) {
            field.set(t, rs.getShort(i + 1));
        } else if (fieldTypeString.indexOf("int") >= 0 || fieldTypeString.indexOf("integer") >= 0) {
            field.set(t, rs.getInt(i + 1));
        } else if (fieldTypeString.indexOf("long") >= 0) {
            field.set(t, rs.getLong(i + 1));
        } else if (fieldTypeString.indexOf("float") >= 0) {
            field.set(t, rs.getFloat(i + 1));
        } else if (fieldTypeString.indexOf("double") >= 0) {
            field.set(t, rs.getDouble(i + 1));
        } else if (fieldTypeString.indexOf("string") >= 0) {
            field.set(t, rs.getString(i + 1));
        } else if (fieldTypeString.indexOf("date") >= 0) {
            field.set(t, rs.getDate(i + 1));
        } else if (fieldTypeString.indexOf("time") >= 0) {
            field.set(t, rs.getTime(i + 1));
        } else if (fieldTypeString.indexOf("timestamp") >= 0) {
            field.set(t, rs.getTimestamp(i + 1));
        } else if (fieldTypeString.indexOf("bigdecimal") >= 0) {
            field.set(t, rs.getBigDecimal(i + 1));
        } else if (fieldTypeString.indexOf("clob") >= 0) {
            field.set(t, rs.getClob(i + 1));
        } else if (fieldTypeString.indexOf("blob") >= 0) {
            field.set(t, rs.getBlob(i + 1));//一般用：rs.getBinaryStream(i+1);
        } else {
            field.set(t, rs.getObject(i + 1));
        }
        field.setAccessible(bFlag);
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
                connection.close();
            }

        }
        return iNum;
    }

}

