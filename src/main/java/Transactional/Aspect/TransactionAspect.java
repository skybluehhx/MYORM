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
    @Pointcut("execution(public * *(..))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public <T> T trancation(ProceedingJoinPoint pjp) {
        try {
            transactionManage.beginTransactionForCurrentThread();
            int a = 1 / 0;
            T t = (T) pjp.proceed();
            transactionManage.commitTracsactionForCurrentThread();
            return t;
        } catch (Throwable throwable) {
            System.out.println(throwable + "进行回滚操作");
            transactionManage.rollbackTracsactionForCurrentThread();

        }

        return null;
    }


}
