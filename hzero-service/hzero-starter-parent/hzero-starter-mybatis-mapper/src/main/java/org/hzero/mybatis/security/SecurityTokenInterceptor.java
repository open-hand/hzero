package org.hzero.mybatis.security;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.mybatis.util.SecurityTokenUtils;
import org.hzero.mybatis.util.SqlUtils;

import java.util.Collection;
import java.util.Properties;

/**
 * <p>
 * 数据防篡改拦截器
 * </p>
 *
 * @author qingsheng.chen 2018/9/6 星期四 17:36
 */
@InterceptorOrder(300)
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SecurityTokenInterceptor implements Interceptor {
    public static final ThreadLocal<Boolean> SECURITY_ENABLE = new ThreadLocal<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (BooleanUtils.isNotFalse(SECURITY_ENABLE.get())) {
            if (!SqlUtils.isCountSql(invocation)) {
                SECURITY_ENABLE.remove();
            }
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            if (SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
                if (result instanceof Collection) {
                    for (Object item : (Collection) result) {
                        if (item instanceof SecurityToken) {
                            SecurityTokenUtils.setToken((SecurityToken) item);
                        }
                    }
                } else {
                    if (result instanceof SecurityToken) {
                        SecurityTokenUtils.setToken((SecurityToken) result);
                    }
                }
            } else if (SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType())) {
                Object arg1 = invocation.getArgs()[1];
                if (arg1 instanceof SecurityToken) {
                    SecurityTokenUtils.setToken((SecurityToken) arg1);
                }
            }
        }
        return result;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        // no need properties
    }
}
