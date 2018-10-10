package Transactional;

/**
 * Created by lin on 2018/10/10.
 * 该类定义了事务的传播行为
 */
public enum  Propagation {

    //如果当前没有事务，就新建一个事务，如果已近有事务就加入当前事务当中
    REQUIRED,
    //支持当前事务，如果当前没有事务，就以非事务的方式执行
    SUPPORTS,
    //新建事务，如果当前存在事务，把当前事务挂起
    REQUIRES_NEW
}
