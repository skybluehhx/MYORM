package excutor;

import mapper.MapperModelParams;
import mapper.MapperRegistry;
import org.apache.commons.beanutils.BeanUtils;
import session.SqlSession;
import support.Converter;
import support.StringsUtil;
import test.User;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zoujianglin
 * 2018/8/23 0023.
 */


public class DefaultResultHandler implements ResultHandler {

    public <T> List<T> handlerResult(SqlSession sqlSession, Class<?> mapperClass, ResultSet resultSet) {

        if (resultSet == null) {
            throw new RuntimeException("resultSet 不能为空，请检查");
        }
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

        return doHandlerResult(filedToDBColumn, filedAndColumnConverter, modelClass, resultSet);

    }


    /**
     * @param filedToDBColumn
     * @param filedAndColumnConverter
     * @param modelClass
     * @param resultSet
     * @param <T>
     * @return 返回model的list集合
     */
    private <T> List<T> doHandlerResult(Map<String, String> filedToDBColumn, Map<String, Class<? extends Converter>>
            filedAndColumnConverter, Class<?> modelClass, ResultSet resultSet) {
        List<T> modelList = new ArrayList<T>();

        try {

            //遍历获取每一行，每一行对应一个model对象
            while (resultSet.next()) {
                //获取model实例
                T t = (T) modelClass.newInstance();
                Set<String> filedNames = filedToDBColumn.keySet();
                //获取每一个字段的数据库值
                for (String filedName : filedNames) {
                    Object result = resultSet.getObject(filedToDBColumn.get(filedName));
                    //如果字段上带有转换器注解 进行转换

                    Class<? extends Converter> converClass = filedAndColumnConverter.get(filedName);
                    //如果该字段上有转换器，则需要进行转化
                    if (converClass != null) {
                        Converter converter = converClass.newInstance();
                        //将数据库中查出的数据转换为字段 所对应的类型
                        result = converter.ConverterFiled(result);
                    }
                    //将属性值得第一个字母变为小写，由于使用的是apache工具，
                    //所以在进行属性设置时，需要遵守其规范
                    filedName = StringsUtil.toLowerCaseFirstOne(filedName);
                    //使用反射设置值
                    BeanUtils.setProperty(t, filedName, result);

                }
                //加入到集合当中
                modelList.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return modelList;

    }


}
