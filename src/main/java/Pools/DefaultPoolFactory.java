package Pools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 */
@Component
public class DefaultPoolFactory {

    private static volatile Pool defaultpool;

    @Autowired
    private static DataSource dataSource;

    public DefaultPoolFactory() {

    }

    public static Pool getDefaultPool() {
        if (defaultpool == null) {
            synchronized (DefaultPoolFactory.class) {
                if (defaultpool == null) {
                    defaultpool = new DefaultPool();
                    defaultpool.setDataSource(dataSource);
                }
                return defaultpool;
            }

        }

        return defaultpool;

    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
