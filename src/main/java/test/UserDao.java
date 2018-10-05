package test;

import anno.ORMColumn;
import anno.ORMInsert;
import anno.ORMMapper;
import anno.ORMSelect;
import org.springframework.stereotype.Component;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */
@Component
@ORMMapper(User.class)
public interface UserDao {
    //模式匹配，进行设置值，
   @ORMInsert("insert into #{User} values( #{User.id},#{User.MyAge},#{User.name})")
    public int add(User user);

   @ORMSelect("select * from #{User} where id = #{User.id}")
   public User select(User user);

}


