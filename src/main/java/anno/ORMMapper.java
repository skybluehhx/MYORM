package anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */

/**
 * 用于Mapper注解，相当于mybatis中的mapper
 * 需要自己显示生明
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ORMMapper {
    //操作所对应的model
    Class<?> value();
}
