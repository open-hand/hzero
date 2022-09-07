package org.hzero.mybatis.parser;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.hzero.mybatis.util.SqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author qingsheng.chen@hand-china.com 2019-04-28 09:14
 */
@InterceptorOrder(600)
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SqlParserInterceptor implements Interceptor {
    public static final Logger logger = LoggerFactory.getLogger(SqlParserInterceptor.class);
    private static final ThreadLocal<Invocation> currentInvocation = new ThreadLocal<>();
    private static final ThreadLocal<BoundSql> currentBoundSql = new ThreadLocal<>();
    public static final ThreadLocal<Boolean> sqlParserEnable = new ThreadLocal<>();
    private final String serviceName;
    private final List<SqlInterceptor> sqlInterceptors;

    public SqlParserInterceptor(String serviceName, List<SqlInterceptor> sqlInterceptors) {
        this.serviceName = serviceName;
        this.sqlInterceptors = sqlInterceptors;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (CollectionUtils.isEmpty(sqlInterceptors) || SqlParserHelper.isClose()) {
            return invocation.proceed();
        }
        // update -> prepared key
        if (invocation.getArgs().length == 2) {
            preparedGenerateKey(invocation);
        }
        BoundSql boundSql = SqlUtils.getBoundSql(invocation);
        if (boundSql == null) {
            return invocation.proceed();
        }
        currentBoundSql.set(boundSql);
        currentInvocation.set(invocation);
        String sqlId = SqlUtils.getSqlId(invocation);
        Statement statement;
        SqlInterceptor interceptorCursor = null;
        try {
            statement = CCJSqlParserUtil.parse(boundSql.getSql());
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            for (SqlInterceptor sqlInterceptor : sqlInterceptors) {
                interceptorCursor = sqlInterceptor;
                sqlInterceptor.before();
                statement = sqlInterceptor.handleStatement(statement, serviceName, sqlId, getArgs(invocation, statement), userDetails != null ? userDetails : DetailsHelper.getAnonymousDetails());
                sqlInterceptor.after();
                interceptorCursor = null;
            }
        } catch (Exception e) {
            logger.error("An error occurred while parsing SQL or an error occurred while intercepting SQL processing", e);
            if (interceptorCursor != null && interceptorCursor.interrupted()) {
                throw e;
            }
            return invocation.proceed();
        } finally {
            currentInvocation.remove();
            currentBoundSql.remove();
        }
        return SqlUtils.resetSql(invocation, boundSql, statement.toString()).proceed();
    }

    private void preparedGenerateKey(Invocation invocation) {
        Object arg = invocation.getArgs()[0];
        if (arg instanceof MappedStatement
                && invocation.getTarget() instanceof Executor
                && SqlCommandType.INSERT.equals(((MappedStatement) arg).getSqlCommandType())) {
            ((MappedStatement) arg).getKeyGenerator()
                    .processBefore((Executor) invocation.getTarget(), (MappedStatement) arg, null, invocation.getArgs()[1]);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // unnecessary
    }

    private Map getArgs(Invocation invocation, Statement statement) {
        Object[] args = invocation.getArgs();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof MapperMethod.ParamMap && statement instanceof Select) {
                    return (Map) arg;
                }
                if (arg != null && !(arg instanceof MappedStatement) && statement instanceof Select) {
                    return BeanMap.create(arg);
                }
                if (arg != null && !(arg instanceof MappedStatement) && statement instanceof Update) {
                    return BeanMap.create(arg);
                }
                if (arg != null && !(arg instanceof MappedStatement) && statement instanceof Insert) {
                    return BeanMap.create(arg);
                }
            }
        }
        return Collections.emptyMap();
    }

    public static Invocation getCurrentInvocation() {
        return currentInvocation.get();
    }

    public static BoundSql getCurrentBoundSql() {
        return currentBoundSql.get();
    }
}
