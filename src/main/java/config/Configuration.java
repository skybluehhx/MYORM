package config;


import mapper.MappedStatement;
import mapper.MapperRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */
@Component
public class Configuration {
    //用于存储所有方法 建为方法名加类名和方法对应的sql语句
    protected final Map<String, MappedStatement> mappedStatements;
    //用于存储所有mapper的映射
    protected final MapperRegistry mapperRegistry;



    public Configuration() {
        this.mappedStatements = new HashMap<String, MappedStatement>();
        this.mapperRegistry = new MapperRegistry(this);

        initConfiguration();
    }


    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public MappedStatement getMappedStatement(String name) {
        return this.mappedStatements.get(name);
    }

    public MapperRegistry getMapperRegistry() {
        return mapperRegistry;
    }

    private void initConfiguration() {


    }

    @Override
    public String toString() {
        return "configture";
    }
}