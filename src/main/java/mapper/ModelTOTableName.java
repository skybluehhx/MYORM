package mapper;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */

/**
 * 该数据结构用于保存数据库表明 到model的简单映射
 * 该对象最好不要至于散列集合中
 *
 * @param <K>
 * @param <V>
 */
public class ModelTOTableName<K, V> {
    K modelName;
    V tableName;


    private ModelTOTableName(K modelName, V tableName) {
        this.modelName = modelName;
        this.tableName = tableName;
    }

    private ModelTOTableName() {
    }

    public static <K,V> ModelTOTableName<K,V> pairValueOf(K modelName, V tableName) {

        return new ModelTOTableName(modelName, tableName);
    }

    public static  <K,V> ModelTOTableName<K,V> pairValueOf() {
        return new ModelTOTableName<K,V>();
    }

    public K getModelName() {
        if (modelName == null) {
            throw new RuntimeException("modelName 不能为空");
        }
        return modelName;
    }

    public void setModelName(K modelName) {
        this.modelName = modelName;
    }

    public V getTableName() {
        if (modelName == null) {
            throw new RuntimeException("tableName 不能为空");
        }
        return tableName;
    }

    public void setTableName(V tableName) {
        this.tableName = tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelTOTableName<?, ?> that = (ModelTOTableName<?, ?>) o;

        if (modelName != null ? !modelName.equals(that.modelName) : that.modelName != null) return false;
        return tableName != null ? tableName.equals(that.tableName) : that.tableName == null;
    }

    @Override
    public int hashCode() {
        int result = modelName != null ? modelName.hashCode() : 0;
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}
