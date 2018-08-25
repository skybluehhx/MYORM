package sql;

import mapper.MapperModelParams;
import mapper.MapperRegistry;
import mapper.ModelTOTableName;
import session.SqlSession;
import support.Converter;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */
public class SqlHandlerForInsert implements SqlHandler {

    public String handSql(SqlSession sqlSession, Class<?> mapperClass, String preSql, Object model) {

        //首先获取mapperRegistry注册中心
        MapperRegistry mapperRegistry = sqlSession.getConfiguration().getMapperRegistry();
        //获取mapper所绑定的model对象
        Class<?> modelClass = mapperRegistry.getMapperModel().get(mapperClass);

        //获取所有 mapper所绑定的model对应数据库表的具体信息
        Map<Class<?>, MapperModelParams> mapperModelToDB = mapperRegistry.getMapperModelToDB();
        //获取指定model对应的maooerModelParams
        MapperModelParams mapperModelParams = mapperModelToDB.get(modelClass);
        //获取每个字段上的参数类型转换器
        Map<String, Class<? extends Converter>> filedAndColumnConverter = mapperModelParams.getFiledAndColumnConverter();
        //获取字段名到数据库列名的映射关系
        Map<String, String> filedToDBColumn = mapperModelParams.getFiledToDBColumn();
        //获取model名对应的表明
        ModelTOTableName<String, String> modelTOTableName = mapperRegistry.getMapperModelToDB().get(mapperClass).getModelTOTableName();

        return doHandlerSql(preSql, filedToDBColumn, modelTOTableName, filedAndColumnConverter, model);

    }

    private String doHandlerSql(String preSql, Map<String, String> filedToDBColumn,
                                ModelTOTableName<String, String> modelTOTableName, Map<String, Class<? extends Converter>> filedAndColumnConverter, Object model) {


        //进行转化操作

        Set<String> filedNames = filedToDBColumn.keySet();

        //先替换字段名
        for (String filedName : filedNames) {
            //拼接替换语句
            String replaceFiledName = "\\#" + "\\{" + filedName + "\\}";
            //获取列名
            String columnName = filedToDBColumn.get(filedName);
            //替换将{#id} 替换为id
            preSql = preSql.replaceAll(replaceFiledName, columnName);

            //接着需要替换插入值 ，值一般的形式为#{user.id}
            String replaceFiledValue = "\\#" + "\\{" + modelTOTableName + "." + filedName + "\\}";
            //获取字段上的转换器
            Class<? extends Converter> converter = filedAndColumnConverter.get(filedName);
            if (model == null) {
                throw new IllegalArgumentException("对于插入语句 传入的model不能为空");
            }
            try {
                //数值从参数值中获取
                Field field = model.getClass().getDeclaredField(filedName);
                //开启访问权限
                field.setAccessible(true);
                String converString = "";
                if (converter != null) { //有转换器
                    //获取model上该属性的值，并进行转换
                    converString = converter.newInstance().ConverterColumn(field.get(model));
                } else { //没有转换器，直接获取字段值，（说明为基本类型，复杂类型必须使用转换器）
                    converString = (String) field.get(model);
                }
                //用转化后的值代替原来的值
                preSql = preSql.replaceAll(replaceFiledValue, converString);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "转化器异常");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "没有权限访问");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "没有这样的属性");
            }


        }
        // 最后 替换表名
        String replace = "\\#" + "\\{" + modelTOTableName.getModelName() + "\\}";
        return preSql.replaceAll(replace, modelTOTableName.getTableName());
    }


}
