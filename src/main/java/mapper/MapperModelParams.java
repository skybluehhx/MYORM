package mapper;

import support.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoujianglin
 * 2018/8/22 0022.
 */
public class MapperModelParams {

    //字段名到数据库名的映射
    private final Map<String, String> filedToDBColumn;
    //记录每个字段类型的类型
    private final Map<String, Class<?>> filedToType;
    //每个字段上对应的类型转换器
    private final Map<String, Class<? extends Converter>> filedAndColumnConverter;

    private final ModelTOTableName modelTOTableName;


    private MapperModelParams() {
        filedToDBColumn = new HashMap<String, String>();
        filedToType = new HashMap<String, Class<?>>();
        filedAndColumnConverter = new HashMap<String, Class<? extends Converter>>(4);
        modelTOTableName = ModelTOTableName.pairValueOf();
    }

    public static MapperModelParams valueOf() {
        return new MapperModelParams();
    }


    public boolean setModelAndTableName(String modelName, String tableName) {
        modelTOTableName.setModelName(modelName);
        modelTOTableName.setTableName(tableName);
        return true;
    }

    /**
     * @param fieldName  字段名
     * @param columnName 数据库对应字段名
     * @return
     */
    public boolean putFiledColumnAnnotationValue(String fieldName, String columnName) {
        filedToDBColumn.put(fieldName, columnName);
        return true;
    }

    /**
     * @param fieldName      字段名
     * @param converterClass 转换器类名
     * @return
     */
    public boolean putFiledConverterAnnotationValue(String fieldName, Class<? extends Converter> converterClass) {
        filedAndColumnConverter.put(fieldName, converterClass);
        return true;
    }

    /**
     * @param fieldName      字段名
     * @param filedClass 字段类型
     * @return
     */
    public boolean putFiledClass(String fieldName, Class<?> filedClass) {
        filedToType.put(fieldName, filedClass);
        return true;
    }

    public ModelTOTableName getModelTOTableName() {
        return modelTOTableName;
    }

    public Map<String, String> getFiledToDBColumn() {
        return filedToDBColumn;
    }

    public Map<String, Class<?>> getFiledToType() {
        return filedToType;
    }

    public Map<String, Class<? extends Converter>> getFiledAndColumnConverter() {
        return filedAndColumnConverter;
    }
}
