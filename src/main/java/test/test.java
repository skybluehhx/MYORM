package test;


import anno.ORMDelete;
import anno.ORMSelect;
import config.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import session.SqlSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/19 0019.
 */

public class test {


    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        Configuration configuration = (Configuration) context.getBean("configuration");


        // test1.test();
        //TransactionAspect transactionAspect = (TransactionAspect) context.getBean("transactionAspect");
        //  org.apache.ibatis.session.SqlSession sqlSession1;
        SqlSession sqlSession = (SqlSession) context.getBean("defaultSqlSession");
        UserDao u = sqlSession.getMapper(UserDao.class);
        User user = new User();
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("4");
        user.setId(11);
        //  user.setMyAge(17);
        user.setMyAge(6);
        user.setName(list);
        //  user.setMyage(1);
        //u.add(user);

        u.add(user);
        System.out.println(user);
    /*
        for (int i = 0; i < user.getName().size(); i++) {
            System.out.println(user.getName().get(i));
        }
        System.out.println(user.getId() + " age" + user.getMyAge() + user.getName().size());
*/
        //  System.out.println(configuration.getMapperRegistry().hasMapper(UserDao.class));
        //System.out.println(configuration);
    /*   InputStream inStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("jdbc.properties");
        Properties properties=new Properties();
        try {
            properties.load(inStream);
        } catch (IOException e) {

        }
      String  driverClassName = properties.getProperty("jdbc.driverClassName");
        String userName = properties.getProperty("jdbc.userName");
        String password = properties.getProperty("jdbc.password");;
        String url = properties.getProperty("jdbc.url");
        System.out.print(driverClassName);
*/
        /*test test1 = new test();
        boolean flag= test1.getClass().isAnnotationPresent(ORMMapper.class);
        System.out.print(flag);

        Method[] methods= test1.getClass().getMethods();
        for(Method method:methods){

            ORMDelete ormDelete= method.getAnnotation(ORMDelete.class);
            System.out.print(ormDelete);
        }
*/

/*
       InputStream inputStream;
        inputStream = Resources.getResourceAsStream("mapConfig.xml");

        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
         UserDao user = sqlSession.getMapper(UserDao.class);

     //   sqlSession.


        sqlSession.close();
*/
    }

    @ORMSelect("")
    @ORMDelete("")
    public void test1() {

    }
}
