package Pools;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zoujianglin
 * 2018/8/25 0025.
 */
public class DefaultPool extends BasePool {



    public DefaultPool(DataSource dataSource) {
        super(dataSource, new LinkedBlockingQueue());
    }

    public DefaultPool(DataSource dataSource, int times) {
        super(dataSource, new LinkedBlockingQueue(), times);

    }






}