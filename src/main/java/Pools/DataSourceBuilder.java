package Pools;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 */
public class DataSourceBuilder {


    private DataSource dataSource;

    public DataSourceBuilder(String driverClassName, String userName, String password, String url) {
        dataSource = new DataSource(driverClassName, userName, password, url);
    }

    public DataSourceBuilder addMaxConnection(int maxConnection) {
        dataSource.setMaxConnection(maxConnection);
        return this;
    }

    public DataSourceBuilder addMinConnection(int minConnection) {
        dataSource.setMinConnection(minConnection);
        return this;
    }

    public DataSourceBuilder addTimeout(int timeout) {
        dataSource.setTimeout(timeout);
        return this;
    }

    public DataSource build() {
        validate();
        return this.dataSource;
    }


    private void validate() {
        if (dataSource.getMinConnection() < 0) {
            throw new RuntimeException("最小连接数不能为负数");
        }
        if (dataSource.getMaxConnection() < dataSource.getMinConnection()) {
            throw new RuntimeException("最大连接数不能小于最小连接数");
        }

    }
}
