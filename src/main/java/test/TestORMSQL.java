package test;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */
public class TestORMSQL {


    public static void main(String[] name) throws Exception {

        int i = 5;
        User obj = new User();
        //obj.setId(5);
        //obj.setMyage(3);
        BeanUtils.setProperty(obj, "myAge", 2);
        //   System.out.println(obj.getMyAge());


        String sql = "Insert into #{user} values{ #{user.id},#{user.name},#{user.age} }";
        String sql2 = "Insert into #{id},#{name},#{age} values{ #{user.id},#{user.name},#{user.age} }";
        String sql3 = "delete form #{user} where #{id} = #{user.id} join in #{order} where #{order.3}>#{user.} ";
        String sql4 = "update #{user}  set #{id} = #{user.id} where #{id} =1";
        String replaceFile = "\\#\\{[a-zA-Z0-9]+\\.[a-zA-Z0-9]+\\}";
        String replaceFiledValue = "\\#" + "\\{" + "user" + "." + "*" + "\\}";
        String sql5 = sql4.replaceAll(replaceFile, "tt");
        //   System.out.println(sql5);
        Pattern p = Pattern.compile(replaceFile);
        Matcher m = p.matcher(sql3);
        while (m.find()) {
            System.out.println(m.group());
            sql3 = sql3.replace(m.group(), "?");
        }
          System.out.println(sql3);

    }

}
