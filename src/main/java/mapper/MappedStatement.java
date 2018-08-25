package mapper;

import config.Configuration;

/**
 * Created by zoujianglin
 * 2018/8/19 0019.
 */
public class MappedStatement {

    //保存所属的mapperClass
    private Class<?> mapperClass;

    //执行的sql命令语句
    private String resource;
    //所属的配置类
    private Configuration configuration;
    //执行的命令类型
    SqlCommandType sqlCommandType;

    public Class<?> getMapperClass() {
        if (mapperClass == null) {
            throw new RuntimeException("在获取mapperClass之前，你必须设置它，否则后续将没法工作");
        }
        return mapperClass;
    }

    public void setMapperClass(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }
}
