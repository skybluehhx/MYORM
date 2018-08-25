package sql;

/**
 * Created by zoujianglin
 * 2018/8/24 0024.
 */
public class SqlAndField<K, V> {

    K sql;
    V filedName;

    public SqlAndField(K sql, V filedName) {
        this.filedName = filedName;
        this.sql = sql;
    }

    public K getSql() {
        return sql;
    }


    public V getFiledName() {
        return filedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SqlAndField<?, ?> that = (SqlAndField<?, ?>) o;

        if (sql != null ? !sql.equals(that.sql) : that.sql != null) return false;
        return filedName != null ? filedName.equals(that.filedName) : that.filedName == null;
    }

    @Override
    public int hashCode() {
        int result = sql != null ? sql.hashCode() : 0;
        result = 31 * result + (filedName != null ? filedName.hashCode() : 0);
        return result;
    }
}
