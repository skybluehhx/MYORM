package test;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println(obj.getMyAge());
        /*
        Field field = obj.getClass().getDeclaredField("id");
        field.setAccessible(true);
        System.out.print(field.get(obj).toString());

        Float f = 5.001f;
        System.out.println(f.toString());

*/
        //SqlSession;

        // Field field = null;

        String sql = "Insert into #{user} values{ #{user.id},#{user.name},#{user.age} }";
        String sql2 = "Insert into #{id},#{name},#{age} values{ #{user.id},#{user.name},#{user.age} }";


        String sql3 = "delete form #{user} where #{id} = #{user.id } ";

        String sql4 = "update #{user}  set #{id} = #{user.id} where #{id} =1";

        String replaceFiledValue = "\\#" + "\\{" + "user" + "." + "*" + "\\}";

         //sql2.
        //sql2.replaceFirst(replaceFiledValue, "?");

       // sql2 = sql2.replaceAll(replaceFiledValue, " '1\\$4\\'");
        System.out.print(sql2);
        /*
        User obj = new User();
        BeanUtils.setProperty(obj, "id", "1");

        BeanUtils.setProperty(obj, "age", "19");
        List list = new ArrayList();
        list.add(1);
        BeanUtils.setProperty(obj, "list", list);
        System.out.print(obj.getAge());
        System.out.print(obj.getId());
        System.out.print(obj.getList().size());
*/
    }

}
