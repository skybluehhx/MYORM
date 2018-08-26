package Pools;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 * <p>
 * 数据源 保留着数据库连接的基本配置
 */
public class DataSource {

    private  String driverClassName;
    private  String userName;
    private  String password;
    private  String url;
    private int maxConnection = 10;
    private int minConnection = 20;
    private int timeout;


    public DataSource(){

    }



    protected DataSource(String driverClassName, String userName, String password, String url) {
        this.driverClassName = driverClassName;
        this.userName = userName;
        this.password = password;
        this.url = url;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public int getMinConnection() {
        return minConnection;
    }

    public void setMinConnection(int minConnection) {
        this.minConnection = minConnection;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
