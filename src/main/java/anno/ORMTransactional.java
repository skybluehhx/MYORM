package anno;

import Transactional.Propagation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lin on 2018/10/10.
 * 事务注解，在方法上配置该注解表明开启事务
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ORMTransactional {
    Propagation propagation () default Propagation.REQUIRED;
}
