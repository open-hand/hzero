package org.hzero.mybatis.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.annotation.CrossSchema;
import org.hzero.mybatis.helper.CrossSchemaHelper;
import org.hzero.mybatis.util.SqlUtils;

/**
 * <p>
 * 跨 Schema 查询多个租户的信息拦截器
 * </p>
 *
 * @author qingsheng.chen 2018/9/20 星期四 14:26
 */
@InterceptorOrder(100)
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})})
public class CrossSchemaInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(CrossSchemaInterceptor.class);
    private static final String UNION_ALL = " union all ";
    private static final String PREFIX = "hpfm:database:";
    private static final int ARGS_LENGTH_FOUR = 4;
    public static final ThreadLocal<Boolean> CROSS_ENABLE = new ThreadLocal<>();
    private static String applicationName;
    private static RedisHelper redisHelper;

    public CrossSchemaInterceptor() {
        ApplicationContextHelper.asyncStaticSetter(RedisHelper.class, CrossSchemaInterceptor.class, "redisHelper");
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (BooleanUtils.isTrue(CROSS_ENABLE.get())) {
            if (!SqlUtils.isCountSql(invocation)) {
                CROSS_ENABLE.remove();
            }
            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            CrossSchema crossSchema = CrossSchemaHelper.crossSchema(ms.getId());
            if (crossSchema != null && invocation.getArgs()[1] instanceof MapperMethod.ParamMap) {
                List<Long> tenantIds = getTenantIds(crossSchema, invocation);
                if (!tenantIds.isEmpty()) {
                    processSql(invocation, tenantIds);
                }
            }
        }
        return invocation.proceed();
    }

    private void processSql(Invocation invocation, List<Long> tenantIds) {
        BoundSql boundSql = getBoundSql(invocation);
        if (boundSql == null) {
            return;
        }
        List<String> tableNameList = getTableNameList(boundSql.getSql());
        List<String> tenantSchema = getTenantSchema(tenantIds);
        List<ParameterMapping> parameterMappingList = new ArrayList<>();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (!CollectionUtils.isEmpty(tenantSchema) && !CollectionUtils.isEmpty(tableNameList)) {
            List<String> sqlList = new ArrayList<>();
            tenantSchema.forEach(schema -> {
                AtomicReference<String> sql = new AtomicReference<>(boundSql.getSql());
                tableNameList.forEach(tableName -> sql.set(sql.get().replace(tableName, schema + "." + tableName)));
                sqlList.add(sql.get());
                parameterMappingList.addAll(parameterMappings);
            });
            SystemMetaObject.forObject(boundSql).setValue("sql", StringUtils.collectionToDelimitedString(sqlList, UNION_ALL));
            SystemMetaObject.forObject(boundSql).setValue("parameterMappings", parameterMappingList);
            invocation.getArgs()[0] = copyFromMappedStatement((MappedStatement) invocation.getArgs()[0], new SimpleSqlSource(boundSql));
        }
    }

    private List<Long> getTenantIds(CrossSchema crossSchema, Invocation invocation) {
        List<Long> tenantIds = new ArrayList<>();
        String paramName = crossSchema.value();
        MapperMethod.ParamMap paramMap = (MapperMethod.ParamMap) invocation.getArgs()[1];
        Object tenantId = paramMap.get(paramName);
        if (tenantId instanceof Long) {
            tenantIds.add((Long) tenantId);
        } else if (tenantId instanceof Collection) {
            for (Object item : ((Collection) tenantId)) {
                if (item instanceof Long) {
                    tenantIds.add((Long) item);
                }
            }
        }
        return tenantIds;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length > 0) {
            builder.keyProperty(ms.getKeyProperties()[0]);
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }

    private static BoundSql getBoundSql(Invocation invocation) {
        if (invocation == null) {
            return null;
        } else {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            BoundSql boundSql;
            // 由于逻辑关系，只会进入一次
            if (args.length == ARGS_LENGTH_FOUR) {
                // 4 个参数时
                boundSql = ms.getBoundSql(parameter);
            } else {
                // 6 个参数时
                boundSql = (BoundSql) args[5];
            }
            return boundSql;
        }
    }

    private List<String> getTenantSchema(List<Long> tenantIds) {
        List<String> tenantSchema = new ArrayList<>();
        if (!CollectionUtils.isEmpty(tenantIds)) {
            tenantIds.forEach(tenantId -> {
                String key = PREFIX + tenantId + "." + getApplicationName();
                String schema = getRedisHelper().strGet(key);
                if (StringUtils.hasText(schema)) {
                    tenantSchema.add(schema);
                } else {
                    logger.error("Unable to read schema for tenant : [{}]", tenantId);
                }
            });
        }
        return tenantSchema;
    }

    private List<String> getTableNameList(String sql) {
        List<String> tableNameList = new ArrayList<>();
        try {
            Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                SelectBody selectBody = ((Select) statement).getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    tableNameList.addAll(getTableNameList((PlainSelect) selectBody));
                } else if (selectBody instanceof SetOperationList) {
                    List<SelectBody> selectBodies = ((SetOperationList) selectBody).getSelects();
                    if (!CollectionUtils.isEmpty(selectBodies)) {
                        selectBodies.forEach(item -> {
                            if (item instanceof PlainSelect) {
                                tableNameList.addAll(getTableNameList((PlainSelect) item));
                            }
                        });
                    }
                }
            }
        } catch (JSQLParserException e) {
            logger.error("Unable to parse sql : [{}], because : [{}]", sql, ExceptionUtils.getStackFrames(e));
        }
        return tableNameList;
    }

    private List<String> getTableNameList(PlainSelect select) {
        List<String> tableNameList = new ArrayList<>();
        if (select != null) {
            FromItem fromItem = select.getFromItem();
            if (fromItem instanceof Table) {
                tableNameList.add(((Table) fromItem).getName());
            }
            List<Join> joinList = select.getJoins();
            if (!CollectionUtils.isEmpty(joinList)) {
                joinList.forEach(join -> {
                    FromItem item = join.getRightItem();
                    if (item instanceof Table) {
                        tableNameList.add(((Table) item).getName());
                    }
                });
            }
        }
        return tableNameList;
    }

    public static String getApplicationName() {
        if (!StringUtils.hasText(applicationName)) {
            applicationName = ApplicationContextHelper.getContext().getEnvironment().getProperty("spring.application.name");
        }
        return applicationName;
    }

    public static RedisHelper getRedisHelper() {
        return redisHelper;
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

    private static class SimpleSqlSource implements SqlSource {
        private BoundSql boundSql;

        public SimpleSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
