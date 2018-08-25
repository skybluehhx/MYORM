package support;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */
public class DBConfig {
    private static Logger logger=Logger.getLogger(DBConfig.class);
    private String driverClassName;
    private String userName;
    private String password;
    private String url;


    public DBConfig() {
        //读取db.properties文件

/*          Properties props = new Properties();
              代表java命令运行的目录
              在java项目下，. java命令的运行目录从项目的根目录开始
              在web项目下，  . java命令的而运行目录从tomcat/bin目录开始
               所以不能使用点.
             FileInputStream in = new FileInputStream("./src/db.properties");
             使用类路径的读取方式
              斜杠表示classpath的根目录
              在java项目下，classpath的根目录从bin目录开始
              在web项目下，classpath的根目录从WEB-INF/classes目录开始
             InputStream in = DBConfig.class.getResourceAsStream("/db.properties");*/

        InputStream inStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jdbc.properties");
       // InputStream inStream=DBConfig.class.getResourceAsStream("/jdbc.properties");
        Properties properties=new Properties();
        try {
            properties.load(inStream);
        } catch (IOException e) {
            logger.error("加载数据库配置文件时发生异常："+e.getMessage(),e);
        }
        this.driverClassName = properties.getProperty("jdbc.driverClassName");
        this.userName = properties.getProperty("jdbc.userName");
        this.password = properties.getProperty("jdbc.password");;
        this.url = properties.getProperty("jdbc.url");
    }



    public DBConfig(String driverClassName, String userName, String password,
                    String url) {
        super();
        this.driverClassName = driverClassName;
        this.userName = userName;
        this.password = password;
        this.url = url;
    }



    public String getDriverClassName() {
        return driverClassName;
    }
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }



}
