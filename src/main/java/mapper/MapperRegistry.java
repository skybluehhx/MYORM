package mapper;

import anno.*;
import config.Configuration;
import session.SqlSession;
import support.Converter;


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */
public final class MapperRegistry {
    //保留mapper类和其动态代理
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap();

    //维持着mapper类和其所绑定的model的映射关系
    private final Map<Class<?>, Class<?>> mapperModel = new HashMap<Class<?>, Class<?>>();

    //8-22新增，
    // 维持mapper所绑定  model对象 到数据库表间的映射关系的具体信息
    private final Map<Class<?>, MapperModelParams> mapperModelToDB = new HashMap<Class<?>, MapperModelParams>();

    private Configuration configuration;

    public MapperRegistry(Configuration config) {
        this.configuration = config;
    }

    /**
     * @param mapperClass
     * @return
     */
    public Class<?> getMapperBindModel(Class<?> mapperClass) {
        Class<?> className = mapperModel.get(mapperClass);
        if (className == null) {
            throw new IllegalArgumentException("mapperClass绑定的model不能为空");
        }
        return className;
    }

    /**
     * @param mapperClass 为mapper类型
     * @param sqlSession
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> mapperClass, SqlSession sqlSession) {

        MapperProxyFactory mapperProxyFactory = knownMappers.get(mapperClass);
        //说明首次使用
        if (mapperProxyFactory == null) {
            //首次获取，添加到configuration中
            addMapperTOConfiguration(mapperClass);
            mapperProxyFactory = knownMappers.get(mapperClass);
        }

        return (T) mapperProxyFactory.newInstance(sqlSession);
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public <T> void addMapper(Class<T> type) {
        MapperProxyFactory mapperProxyFactory = new MapperProxyFactory(type);
        //添加新的mapperProxyFactory
        knownMappers.put(type, mapperProxyFactory);
        return;


    }

    /**
     * 添加mapper相关信息 于配置类中
     *
     * @param mapperClass
     */
    private void addMapperTOConfiguration(Class<?> mapperClass) {
        if (mapperClass.isAnnotationPresent(ORMMapper.class)) {
            ORMMapper ormMapper = mapperClass.getAnnotation(ORMMapper.class);
            //对应mapper绑定model的路劲
            Class modelClass = ormMapper.value();
            //加入到容器中，保留了beanClass 和其对应数据库的model
            mapperModel.put(mapperClass, modelClass);
            System.out.println(mapperClass + "mapperRegistry 66 rows");

            // 维持mapper对应model字段到数据库字段的一系列映射关系
            //MapperModelToDB
            MapperModelParams mapperModelParams = getMapperModelParams(mapperClass);
            //添加对应信息
            mapperModelToDB.put(modelClass, mapperModelParams);

            //添加mapper于注册器中
            configuration.getMapperRegistry().addMapper(mapperClass);
            Method[] methods = mapperClass.getMethods();
            for (Method method : methods) {
                MappedStatement mappedStatement = getMappedStatement(method, mapperClass);
                configuration.getMappedStatements().put(mapperClass.getName() + "." + method.getName(), mappedStatement);
            }


        }


    }

    /**
     * 获取mapper绑定model上对应的信息
     *
     * @param mapperClass 对用的mapper 类
     * @return
     */
    private MapperModelParams getMapperModelParams(Class mapperClass) {
        //获取其上的modelClass
        Class<?> modelClass = getMapperBindModel(mapperClass);
        //modle对应数据库的一系列封装
        MapperModelParams mapperModelParams = MapperModelParams.valueOf();
        //获取model名与数据库表对应的关系
        String tableName = getTableName(modelClass);
        mapperModelParams.setModelAndTableName(modelClass.getSimpleName(), tableName);
        Field[] fields = modelClass.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            mapperModelParams.putFiledColumnAnnotationValue(fieldName, getFiledColumnAnnotationValue(field));
            mapperModelParams.putFiledConverterAnnotationValue(fieldName, getFiledConverterAnnotationValue(field));
            mapperModelParams.putFiledClass(fieldName, field.getType());

        }

        return mapperModelParams;
    }


    private String getTableName(Class<?> modelClass) {
        if (modelClass.isAnnotationPresent(ORMTable.class)) {
            ORMTable ormTable = modelClass.getAnnotation(ORMTable.class);
            return ormTable.value();
        }
        return modelClass.getSimpleName();
    }

    /**
     * 获取字段上 ORMColumn注解的值
     *
     * @param field
     * @return
     */
    private String getFiledColumnAnnotationValue(Field field) {
        if (field == null) {
            throw new RuntimeException("field为空，不应该");
        }
        if (field.isAnnotationPresent(ORMColumn.class)) {
            ORMColumn ormColumn = field.getAnnotation(ORMColumn.class);
            return ormColumn.value();
        }
        return field.getName();

    }

    /**
     * 获取字段上 ORMConverter注解的值
     *
     * @param field
     * @return
     */
    private Class<? extends Converter> getFiledConverterAnnotationValue(Field field) {
        if (field == null) {
            throw new RuntimeException("field为空，不应该");
        }
        if (field.isAnnotationPresent(ORMConverter.class)) {
            ORMConverter ormConverter = field.getAnnotation(ORMConverter.class);
            return ormConverter.value();

        }
        return null;
    }

    private MappedStatement getMappedStatement(Method method, Class<?> mapperClass) {
        ORMSelect ormSelect = method.getAnnotation(ORMSelect.class);
        ORMInsert ormInsert = method.getAnnotation(ORMInsert.class);
        ORMUpdate ormUpdate = method.getAnnotation(ORMUpdate.class);
        ORMDelete ormDelete = method.getAnnotation(ORMDelete.class);

        if (ormSelect != null) {
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setSqlCommandType(SqlCommandType.SELECT);
            mappedStatement.setResource(ormSelect.value());
            mappedStatement.setConfiguration(configuration);
            mappedStatement.setMapperClass(mapperClass);
            return mappedStatement;
        }
        if (ormInsert != null) {

            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setSqlCommandType(SqlCommandType.INSERT);
            mappedStatement.setResource(ormInsert.value());
            mappedStatement.setConfiguration(configuration);
            mappedStatement.setMapperClass(mapperClass);
            return mappedStatement;
        }
        if (ormUpdate != null) {
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setSqlCommandType(SqlCommandType.UPDATE);
            mappedStatement.setResource(ormUpdate.value());
            mappedStatement.setConfiguration(configuration);
            mappedStatement.setMapperClass(mapperClass);
            return mappedStatement;
        }
        if (ormDelete != null) {
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setSqlCommandType(SqlCommandType.DELETE);
            mappedStatement.setResource(ormDelete.value());
            mappedStatement.setConfiguration(configuration);
            mappedStatement.setMapperClass(mapperClass);
            return mappedStatement;
        }
        return null;
    }

    public Map<Class<?>, MapperModelParams> getMapperModelToDB() {
        return mapperModelToDB;
    }

    public Map<Class<?>, Class<?>> getMapperModel() {
        return mapperModel;
    }
}
