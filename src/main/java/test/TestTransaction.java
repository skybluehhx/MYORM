package test;

import Transactional.Propagation;
import anno.ORMTransactional;
import org.springframework.stereotype.Component;

/**
 * Created by lin on 2018/10/10.
 */
@Component
public class TestTransaction {

    @ORMTransactional(propagation = Propagation.SUPPORTS)
    public void  test(){
        System.out.println("transaction");
    }


}
