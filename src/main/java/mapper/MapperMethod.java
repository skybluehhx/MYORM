package mapper;

import config.Configuration;

import session.SqlSession;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * Created by zoujianglin
 * 2018/8/20 0020.
 */

/**
 * 具体方法的执行
 */
public class MapperMethod {
    private final boolean returnsVoid;
    private final boolean returnsMany;
    private final MapperMethod.SqlCommand command;
    private final Method method;
    private final Class<?> returnType;
    private final Class<?> mapperInterface;


    public MapperMethod(Configuration config, Class mapperInterface, Method method) {
        this.command = new MapperMethod.SqlCommand(config, mapperInterface, method);
        this.mapperInterface = mapperInterface;
        this.method = method;
        this.returnType = method.getReturnType();
        this.returnsVoid = Void.TYPE.equals(this.returnType);
        //返回值是否为集合类型或数组类型
        this.returnsMany = this.returnType.isArray() || Collection.class.isAssignableFrom(this.returnType);


    }

    public Object execute(SqlSession sqlSession, Object model) {

        Object param;
        Object result = null;
        if (SqlCommandType.INSERT == this.command.getType()) {
            result = this.rowCountResult(sqlSession.insert(this.command.getName(), model));
        } else if (SqlCommandType.UPDATE == this.command.getType()) {
            result = this.rowCountResult(sqlSession.update(this.command.getName(), model));
        } else if (SqlCommandType.DELETE == this.command.getType()) {
            result = this.rowCountResult(sqlSession.delete(this.command.getName(), model));
        } else {
            if (SqlCommandType.SELECT != this.command.getType()) {
                throw new RuntimeException("Unknown execution method for: " + this.command.getName());
            }

            //进入这里进行的是查询，并且不需要返回值
            // 使用懒汉模式，直接不查询，直接返回
            if (Void.TYPE.equals(this.returnType)) {
                result = null;
            } else if (this.returnsMany) {
                result = this.executeForMany(sqlSession, model);
            } else {

                //获取对用model的类型
                //这里需要进行修改
                Class<?> modelClass = sqlSession.getConfiguration().getMapperRegistry().
                        getMapperBindModel(mapperInterface);
                result = sqlSession.selectOne(this.command.getName(), model,modelClass);
            }

        }

        //如果返回值为空 并且方法返回值类型是基础类型 并且不是VOID 则抛出异常
        if (result == null && this.method.getReturnType().isPrimitive() && !this.returnsVoid) {
            throw new RuntimeException("Mapper method '" + this.command.getName() + " attempted to return null from a method with a primitive return type (" + this.method.getReturnType() + ").");
        } else {
            return result;
        }

    }


    private <E> Object executeForMany(SqlSession sqlSession, Object model) {

        List result;
        Class<?> modelClass = sqlSession.getConfiguration().getMapperRegistry().getMapperBindModel(mapperInterface);
        result = sqlSession.selectList(this.command.getName(), model,modelClass);

        return result;
    }

    /**
     * 包装返回类型
     *
     * @param rowCount
     * @return
     */
    private Object rowCountResult(int rowCount) {
        Object result;
        if (this.returnsVoid) {
            result = null;
        } else if (!Integer.class.equals(this.method.getReturnType()) && !Integer.TYPE.equals(this.method.getReturnType())) {
            if (!Long.class.equals(this.method.getReturnType()) && !Long.TYPE.equals(this.method.getReturnType())) {
                if (!Boolean.class.equals(this.method.getReturnType()) && !Boolean.TYPE.equals(this.method.getReturnType())) {
                    throw new RuntimeException("Mapper method '" + this.command.getName() + "' has an unsupported return type: " + this.method.getReturnType());
                }

                result = Boolean.valueOf(rowCount > 0);
            } else {
                result = Long.valueOf((long) rowCount);
            }
        } else {
            result = Integer.valueOf(rowCount);
        }

        return result;
    }

    /**
     * sql 命令，用于获取对应mapper上方法，和执行的类型
     */
    public static class SqlCommand {
        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> declaringInterface, Method method) throws RuntimeException {
            this.name = declaringInterface.getName() + "." + method.getName();

            MappedStatement ms;
            try {
                ms = configuration.getMappedStatement(this.name);
            } catch (Exception var6) {
                throw new RuntimeException("Invalid bound statement (not found): " + this.name, var6);
            }

            this.type = ms.getSqlCommandType();
            if (this.type == SqlCommandType.UNKNOWN) {
                throw new RuntimeException("Unknown execution method for: " + this.name);
            }
        }

        public String getName() {
            return this.name;
        }

        public SqlCommandType getType() {
            return this.type;
        }
    }
}
