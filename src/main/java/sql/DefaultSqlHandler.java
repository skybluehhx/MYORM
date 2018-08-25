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
 * <p>
 * 为查询专门准备的sqlHandler
 */

public class DefaultSqlHandler implements SqlHandler {

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

        return doHandleSql(preSql, filedToDBColumn, modelTOTableName, filedAndColumnConverter, model);
    }

    /**
     * @param preSql          处理前sql语句，形如 select * from #{modelName} where #{id} = #{user.long}?
     * @param filedToDBColumn
     * @return
     */
    private String doHandleSql(String preSql, Map<String, String> filedToDBColumn, ModelTOTableName<String, String> modelTOTableName, Map<String, Class<? extends Converter>> filedAndColumnConverter, Object model) {
        Set<String> filedNames = filedToDBColumn.keySet();
        //先替换字段名
        for (String filedName : filedNames) {

            try {
                //数值从参数值中获取
                Field field = model.getClass().getDeclaredField(filedName);
                //开启访问权限
                field.setAccessible(true);
                Object fieldValue = field.get(model);
                //字段值为空，说明该字段值没有被当做参数传入
                if (fieldValue == null) {
                    continue;
                }
                //属性值不为空，说明可能在sql语句中
                //先替换字段名
                String replace = "\\#" + "\\{" + filedName + "\\}";
                String columnName = filedToDBColumn.get(filedName);
                //字段名替换完成
                preSql = preSql.replaceAll(replace, columnName);

                //接着需要替换插入值 ，值一般的形式为#{user.id}
                String replaceFiledValue = "\\#" + "\\{" + modelTOTableName.getModelName() + "." + filedName + "\\}";
                //获取字段上的转换器，需要判断是否有转换器
                Class<? extends Converter> converter = filedAndColumnConverter.get(filedName);


                String converString = "";
                if (converter != null) { //有转换器
                    //将model该属性的值，进行转换，与数据库中的字段值想对应
                    converString = "'" + converter.newInstance().ConverterColumn(fieldValue) + "'";
                } else { //没有转换器，直接获取字段值，（说明为基本类型(或者为其包装类型)，复杂类型必须使用转换器）
                    converString = field.get(model).toString();
                }
                preSql = preSql.replaceAll(replaceFiledValue, converString);

            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "没有权限访问");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                throw new RuntimeException(e + "没有这样的属性");
            }


        }
        //接着替换类似#{user.id} 这些将会使用注入的方式进行设置值，


        //最后 替换表名
        String replace = "\\#" + "\\{" + modelTOTableName.getModelName() + "\\}";
        String compeleteSql = preSql.replaceAll(replace, modelTOTableName.getTableName());
        return compeleteSql;
        /*
        String content = "id";
        String preSql = "select #{id}  from user  #{id}";
        String join = "\\#" + "\\{" + content + "\\}";

        String sql= preSql.replaceAll( join,content);


*/

    }
}
