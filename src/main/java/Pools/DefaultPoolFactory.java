package Pools;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 */
public class DefaultPoolFactory {

    private static volatile Pool defaultpool;

    private DataSource dataSource;

    public DefaultPoolFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

   public   Pool getDefaultPool() {
        if (defaultpool == null) {
            synchronized (this) {
                if (defaultpool == null) {
                    defaultpool = new DefaultPool(dataSource);
                }
                return defaultpool;
            }

        }

        return defaultpool;

    }




}
