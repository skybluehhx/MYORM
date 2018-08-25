package mapper;

import mapper.MapperMethod;
import session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by zoujianglin
 * 2018/8/20 0020.
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {

    private static final long serialVersionUID = -6424540398559729838L;
    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;
    private final Map<Method, MapperMethod> methodCache;


    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
        this.methodCache = methodCache;

    }

    /**
     * 动态代理，通过此方法来返回代理的mapper代理
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        } else {
            MapperMethod mapperMethod = this.cachedMapperMethod(method);
            return mapperMethod.execute(sqlSession, args[0]);


        }
    }

    private MapperMethod cachedMapperMethod(Method method) {
        MapperMethod mapperMethod = methodCache.get(method);
        if (mapperMethod != null) {
            return mapperMethod;
        }
        mapperMethod = new MapperMethod(sqlSession.getConfiguration(), this.mapperInterface, method);
        methodCache.put(method, mapperMethod);
        return mapperMethod;

    }
}
