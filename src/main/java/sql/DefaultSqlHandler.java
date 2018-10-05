package sql;

import mapper.MapperModelParams;
import mapper.MapperRegistry;
import mapper.ModelTOTableName;
import session.SqlSession;
import support.Converter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 * <p>
 * 为查询专门准备的sqlHandler
 */

public class DefaultSqlHandler implements SqlHandler {
    public String baseregex = "\\#\\{modelName\\.[a-zA-Z0-9]+\\}";

    public String handSql(SqlSession sqlSession, Class<?> mapperClass, String preSql, Object model) {

        //处理sql语句，sql语句遵守规范，变量间都是用#{id} 进行
        //首先获取mapperRegistry注册中心
        MapperRegistry mapperRegistry = sqlSession.getConfiguration().getMapperRegistry();
        //获取mapper所绑定的model对象
        Class<?> modelClass = mapperRegistry.getMapperModel().get(mapperClass);
        //获取所有 mapper所绑定的model对应数据库表的具体信息
        Map<Class<?>, MapperModelParams> mapperModelToDB = mapperRegistry.getMapperModelToDB();

        //获取指定model对应的maooerModelParams
        MapperModelParams mapperModelParams = mapperRegistry.getMapperModelToDB().get(modelClass);

        //获取字段到数据库表字段间的映射关系
        Map<String, String> filedToDBColumn = mapperModelParams.getFiledToDBColumn();
        //进行具体的处理得到符合规范的数据库语句
        ModelTOTableName<String, String> modelTOTableName = mapperModelParams.getModelTOTableName();

        //获取每个字段上的参数类型转换器
        Map<String, Class<? extends Converter>> filedAndColumnConverter = mapperModelParams.getFiledAndColumnConverter();

        return doHandleSql(preSql, filedToDBColumn, modelTOTableName, filedAndColumnConverter, model, mapperModelParams);
    }

    /**
     * @param preSql          处理前sql语句，形如 select * from #{modelName} where #{id} = #{user.long}?
     * @param filedToDBColumn
     * @return
     */
    private String doHandleSql(String preSql, Map<String, String> filedToDBColumn, ModelTOTableName<String, String> modelTOTableName, Map<String, Class<? extends Converter>> filedAndColumnConverter, Object model, MapperModelParams mapperModelParams) {
        String newSql = preSql;
        Set<String> filedNames = filedToDBColumn.keySet();
        baseregex = baseregex.replace("modelName", modelTOTableName.getModelName());
        Pattern p = Pattern.compile(baseregex);
        Matcher m = p.matcher(preSql);
        //替换回来以便复用
        baseregex.replace(modelTOTableName.getModelName(), "modelName");

        ArrayList arrayList = new ArrayList();//该arrayList存放着需要注入的参数值
        //该步骤主要是将形如的格式为#{user.id}替换为？
        while (m.find()) {
            String filednameWithClass = m.group();
            newSql = newSql.replace(filednameWithClass, "?");
            //获取属性名
            String filedName = filednameWithClass.substring(modelTOTableName.getModelName().length() + 3, filednameWithClass.length()-1).trim();

            try {
                Field field = model.getClass().getDeclaredField(filedName);
                //开启访问权限
                field.setAccessible(true);
                //获取指定的值
                Object fieldValue = field.get(model);
                //字段值为空，说明该字段值没有被当做参数传入
                if (fieldValue == null) {
                    throw new IllegalArgumentException("缺少参数值");
                }
                Class<? extends Converter> converter = filedAndColumnConverter.get(filedName);


                Object converString = "";
                if (converter != null) { //有转换器
                    //将model该属性的值，进行转换，与数据库中的字段值想对应
                    converString = converter.newInstance().ConverterColumn(fieldValue);
                } else { //没有转换器，直接获取字段值，（说明为基本类型(或者为其包装类型)，复杂类型必须使用转换器）
                    converString = field.get(model);
                }
                arrayList.add(converString);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

        }
        //添加要注入的参数
        mapperModelParams.addParameters(arrayList);
        //替换字段
        newSql = replaceFiledName(newSql, filedNames, filedToDBColumn);
        //最后替换表名
        String replace = "\\#" + "\\{" + modelTOTableName.getModelName() + "\\}";
        String compeleteSql = newSql.replaceAll(replace, modelTOTableName.getTableName());
        return compeleteSql;

    }

    /**
     * 该方法的作用主要是sql中的参数替换为"?"，方便后续sql注入参数
     *
     * @param newSql
     */
    private String replaceFiledName(String newSql, Set<String> filedNames, Map<String, String> filedToDBColumn) {
        for (String filedName : filedNames) {
            //先替换字段名
            String replace = "\\#" + "\\{" + filedName + "\\}";
            String columnName = filedToDBColumn.get(filedName);
            //字段名替换完成
            newSql = newSql.replaceAll(replace, columnName);
        }
        return newSql;
    }


}
