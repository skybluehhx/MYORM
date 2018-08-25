package anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zoujianglin
 * 2018/8/22 0022.
 */

/**
 * 作用于字段上面，如果model类的字段名与数据库中
 * 的字段名不一致时使用该注解进行额外的说明
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ORMColumn {
    String value();
}
