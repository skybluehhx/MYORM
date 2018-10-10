package Transactional.Aspect;

import Transactional.Propagation;
import Transactional.TransactionManage;
import anno.ORMTransactional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by zoujianglin
 * 2018/8/26 0026.
 */

@Component
@Aspect
public class TransactionAspect {

    @Autowired
    private TransactionManage transactionManage;

    //public * *(..)
    @Pointcut("@annotation(anno.ORMTransactional)")
    public void pointTransactional() {

    }


    @Around("pointTransactional()")
    public <T> T trancation(ProceedingJoinPoint pjp) {
        T t = null;
        boolean isSuccess = true;
        transactionManage.beginTransactionForCurrentThread();
        try {
            Class<?> clazz= pjp.getTarget().getClass(); //获取被代理对象
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            Method proxyMethod= methodSignature.getMethod();//获取被代理的方法
            System.out.println(proxyMethod);
            System.out.println(proxyMethod.isAnnotationPresent(ORMTransactional.class));
            if(proxyMethod.isAnnotationPresent(ORMTransactional.class)){
               ORMTransactional ormTransactional= proxyMethod.getAnnotation(ORMTransactional.class);
              Propagation propagation= ormTransactional.propagation();
              handlerPropagation(propagation);
               // System.out.println("事务"+ormTransactional.propagation());
            }else{
                throw new IllegalArgumentException();//不可能执行到这
            }
           // System.out.println(pjp.getTarget().getClass());
            t = (T) pjp.proceed(); //执行被代理的方法
            System.out.println("开启事务");
            //T t = (T) pjp.proceed();
            //transactionManage.commitTracsactionForCurrentThread();
            return (T) t;
        } catch (Throwable throwable) {
            isSuccess=false;
            System.out.println(throwable + "事务执行失败进行回滚操作");

        }finally {
            if(isSuccess){
                transactionManage.commitTracsactionForCurrentThread();
            }else {
                transactionManage.rollbackTracsactionForCurrentThread();
            }

        }
        return t;
    }

    /**
     * 用于处理事务的传播行为
     * @param propagation
     */
    private void handlerPropagation(Propagation propagation){
        switch (propagation){
            case REQUIRED:

            case SUPPORTS:
            case REQUIRES_NEW:
        }


    }

    public TransactionManage getTransactionManage() {
        return transactionManage;
    }

    public void setTransactionManage(TransactionManage transactionManage) {
        this.transactionManage = transactionManage;
    }
}
