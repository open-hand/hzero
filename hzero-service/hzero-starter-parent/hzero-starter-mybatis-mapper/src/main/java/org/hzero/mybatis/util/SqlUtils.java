package org.hzero.mybatis.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.hzero.mybatis.common.SimpleSqlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 数据屏蔽sql处理工具类
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 20:27
 */
public class SqlUtils {
    private static final Logger logger = LoggerFactory.getLogger(SqlUtils.class);

    private SqlUtils() {

    }

    private static final int ARGS_LENGTH_SIX = 6;
    private static final int ARGS_LENGTH_FOUR = 4;
    private static final int ARGS_LENGTH_TWO = 2;
    public static final String COUNT_SQL_SUFFIX = "_COUNT";


    /**
     * 获取BoundSql
     *
     * @param invocation
     * @return BoundSql
     */
    public static BoundSql getBoundSql(Invocation invocation) {
        if (invocation == null) {
            return null;
        } else {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            BoundSql boundSql;
            // 由于逻辑关系，只会进入一次
            if (args.length == ARGS_LENGTH_FOUR || args.length == ARGS_LENGTH_TWO) {
                // 4 个参数时
                boundSql = ms.getBoundSql(parameter);
            } else {
                // 6 个参数时
                boundSql = (BoundSql) args[5];
            }
            return boundSql;
        }
    }

    public static Invocation resetSql(final Invocation invocation, final BoundSql boundSql, String newSql) {
        // invocation 是静态全局的，为了避免多线程的问题，需要new一个新对象
        Object[] newArgs = new Object[invocation.getArgs().length];
        for (int i = 0; i < invocation.getArgs().length; ++i) {
            newArgs[i] = invocation.getArgs()[i];
        }
        Invocation newInvocation = new Invocation(invocation.getTarget(), invocation.getMethod(), newArgs);
        MappedStatement mappedStatement = (MappedStatement) newInvocation.getArgs()[0];
        BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), newSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        try {
            FieldUtils.writeField(newBoundSql, "additionalParameters", FieldUtils.readField(boundSql, "additionalParameters", true), true);
            FieldUtils.writeField(newBoundSql, "metaParameters", FieldUtils.readField(boundSql, "metaParameters", true), true);
        } catch (IllegalAccessException e) {
            logger.error("Error write new invocation.", e);
            return invocation;
        }
        MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), mappedStatement.getId(), new SimpleSqlSource(newBoundSql), mappedStatement.getSqlCommandType())
                .resource(mappedStatement.getResource())
                .parameterMap(mappedStatement.getParameterMap())
                .resultMaps(mappedStatement.getResultMaps())
                .fetchSize(mappedStatement.getFetchSize())
                .timeout(mappedStatement.getTimeout())
                .statementType(mappedStatement.getStatementType())
                .resultSetType(mappedStatement.getResultSetType())
                .cache(mappedStatement.getCache())
                .flushCacheRequired(mappedStatement.isFlushCacheRequired())
                .useCache(mappedStatement.isUseCache())
                .resultOrdered(mappedStatement.isResultOrdered())
                .keyGenerator(mappedStatement.getKeyGenerator())
                .databaseId(mappedStatement.getDatabaseId())
                .lang(mappedStatement.getLang());
        if (mappedStatement.getKeyProperties() != null && mappedStatement.getKeyProperties().length > 0) {
            for (String keyProperty : mappedStatement.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        if (mappedStatement.getKeyColumns() != null && mappedStatement.getKeyColumns().length > 0) {
            for (String keyColumn : mappedStatement.getKeyColumns()) {
                builder.keyColumn(keyColumn);
            }
        }
        if (mappedStatement.getResultSets() != null && mappedStatement.getResultSets().length > 0) {
            for (String resultSet : mappedStatement.getResultSets()) {
                builder.resultSets(resultSet);
            }
        }
        newInvocation.getArgs()[0] = builder.build();
        if (newInvocation.getArgs().length == ARGS_LENGTH_SIX) {
            newInvocation.getArgs()[5] = newBoundSql;
        }
        return newInvocation;
    }

    /**
     * 获取mapper中的sqlId
     *
     * @param invocation
     * @return sqlId
     */
    public static String getSqlId(Invocation invocation) {
        if (invocation == null) {
            return null;
        } else {
            return ((MappedStatement) invocation.getArgs()[0]).getId();
        }
    }

    public static boolean isCountSql(Invocation invocation) {
        String sqlId = getSqlId(invocation);
        return StringUtils.hasText(sqlId) && sqlId.endsWith(COUNT_SQL_SUFFIX);
    }
}
