package Transactional.Aspect;

import Transactional.TransactionManage;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @Pointcut("execution( public * test.test. *(..))")
    public void pointCut() {

    }

    @Around("execution( public * test.UserDao.*(..))")
    public <T> T trancation(ProceedingJoinPoint pjp) {
        try {
            // transactionManage.beginTransactionForCurrentThread();
            //int a = 1 / 0;
            Object t = pjp.proceed();
            //T t = (T) pjp.proceed();
            //transactionManage.commitTracsactionForCurrentThread();
            return (T) t;
        } catch (Throwable throwable) {
            System.out.println(throwable + "进行回滚操作");
            transactionManage.rollbackTracsactionForCurrentThread();

        }

        return null;
    }

    public TransactionManage getTransactionManage() {
        return transactionManage;
    }

    public void setTransactionManage(TransactionManage transactionManage) {
        this.transactionManage = transactionManage;
    }
}
