package test;


import anno.ORMDelete;
import anno.ORMSelect;
import config.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import session.SqlSession;
import support.spring.BeanPostMappedSacn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Administrator on 2018/8/19 0019.
 */

public class test {


    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        //  Configuration configuration = (Configuration) context.getBean("configuration");
        BeanPostMappedSacn beanPostMappedSacn = (BeanPostMappedSacn) context.getBean("beanPostMappedSacn");
        System.out.println(beanPostMappedSacn.getBasePackage());

        UserDao userDao = (UserDao) context.getBean("userDao");
        User user = new User();
        /*user.setId(18);
        user.setMyAge(12);
        List<Integer> list =new ArrayList();
        list.add(2);
        list.add(7);
        user.setName(list);
*/
        //user.setId(18);
        user=userDao.select(user);
        System.out.println(user);


    }
}
