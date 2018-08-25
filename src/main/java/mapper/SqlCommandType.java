package mapper;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */
public enum SqlCommandType {
    UNKNOWN,
    INSERT,
    UPDATE,
    DELETE,
    SELECT;

    private SqlCommandType() {
    }
}

