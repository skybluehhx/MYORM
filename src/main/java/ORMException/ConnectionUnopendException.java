package ORMException;

/**
 * Created by zoujianglin
 * 2018/8/26 0026.
 */
public class ConnectionUnopendException extends RuntimeException {

    private final String msgError;

    public ConnectionUnopendException(String msgError) {
        super(msgError);
        this.msgError = msgError;
    }


}
