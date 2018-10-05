package support.spring;

import anno.ORMMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;
import session.SqlSession;
import support.ClassUtils;
import support.StringsUtil;

import java.util.Set;

/**
 * Created by lin on 2018/10/5.
 * 添加与spring结合
 */

public class BeanPostMappedSacn implements BeanFactoryPostProcessor {
    private DefaultListableBeanFactory beanFactory;

    private String basePackage;

    private SqlSession sqlSession;

    public BeanPostMappedSacn() {

    }


    public void postProcessBeanFactory(
            ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Set<Class<?>> classSet = ClassUtils.getClassSet(basePackage);
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
        for (Class clazz : classSet) {
            if (clazz.isAnnotationPresent(ORMMapper.class)) {
                System.out.println(sqlSession);
                Object mapperObject = sqlSession.getMapper(clazz);
                //将mapper添加到spring容器中
                String beanName = StringsUtil.toLowerCaseFirstOne(clazz.getSimpleName());
                this.beanFactory.registerSingleton(beanName, mapperObject);
                System.out.println(beanName + " 11");
            }

        }

    }


    public SqlSession getSqlSession() {
        return sqlSession;
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
    /**
     *
     */


}
