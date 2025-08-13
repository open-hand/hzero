/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.mybatis.pagehelper;

import io.choerodon.mybatis.pagehelper.cache.Cache;
import io.choerodon.mybatis.pagehelper.cache.CacheFactory;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.mybatis.pagehelper.exception.PageException;
import io.choerodon.mybatis.pagehelper.parser.IOrderByParser;
import io.choerodon.mybatis.pagehelper.parser.OrderByParser;
import io.choerodon.mybatis.pagehelper.util.MappedStatementUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Mybatis - 通用分页拦截器
 * 项目地址 : http://git.oschina.net/free/Mybatis_PageHelper
 *
 * @author liuzh/abel533/isea533
 * @version 5.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@InterceptorOrder(0)
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
public class PageInterceptor implements Interceptor {
    private static final String CUSTOM_COUNT_SQL_POSTFIX = "_COUNT";
    /**
     * 缓存count查询的ms
     **/
    private Cache<CacheKey, MappedStatement> msCountMap = null;
    private Dialect dialect = null;
    private IOrderByParser orderByParser;
    private Field additionalParametersField;

    private static final Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

    public PageInterceptor(Dialect dialect, IOrderByParser orderByParser) {
        this.dialect = dialect;
        this.orderByParser = orderByParser;
    }

    public PageInterceptor(Dialect dialect, Field additionalParametersField, Cache<CacheKey, MappedStatement> msCountMap) {
        this.dialect = dialect;
        this.additionalParametersField = additionalParametersField;
        this.msCountMap = msCountMap;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            Executor executor = (Executor) invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            //由于逻辑关系，只会进入一次
            if (args.length == 4) {
                //4 个参数时
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                //6 个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
            List resultList;
            BoundSql sqlWithOrderBy = null;
            boolean executePage = !dialect.skip(ms, parameter, rowBounds);
            //获得ThreadLocal里面的sort参数
            Sort sort = PageHelper.getLocalSort();
            boolean executeSort = (sort != null);
            //反射获取动态参数
            Map<String, Object> additionalParameters = (Map<String, Object>) additionalParametersField.get(boundSql);
            if (executePage && !executeSort) {
                //只分页，count统计sql
                if (doCount(ms, parameter, rowBounds, resultHandler, executor, boundSql, additionalParameters)) {
                    return dialect.afterPage(new ArrayList(), parameter, rowBounds);
                }
                //判断是否需要进行分页查询
                DoPage doPage =
                        new DoPage(ms, parameter, rowBounds, resultHandler,
                                executor, cacheKey, boundSql, sqlWithOrderBy, additionalParameters).invoke();
                resultList = doPage.getResultList();
                parameter = doPage.getParameter();
                return dialect.afterPage(resultList, parameter, rowBounds);
            } else if (!executePage && executeSort) {
                //只排序
                parameter = dialect.processParameterObject(ms, parameter, boundSql, cacheKey);
                //拼接排序order by操作
                sqlWithOrderBy = doSort(ms, parameter, boundSql, sqlWithOrderBy, sort, executeSort);
                //设置动态参数
                setDynamicParam(sqlWithOrderBy, additionalParameters);
                //执行排序查询
                return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, sqlWithOrderBy);
            } else if (executePage && executeSort) {
                //分页和排序
                if (doCount(ms, parameter, rowBounds, resultHandler, executor, boundSql, additionalParameters)) {
                    return dialect.afterPage(new ArrayList(), parameter, rowBounds);
                }
                //拼接排序order by操作
                sqlWithOrderBy = doSort(ms, parameter, boundSql, sqlWithOrderBy, sort, executeSort);
                //判断是否需要进行分页查询
                DoPage doPage =
                        new DoPage(ms, parameter, rowBounds, resultHandler, executor,
                                cacheKey, boundSql, sqlWithOrderBy, additionalParameters).invoke();
                resultList = doPage.getResultList();
                parameter = doPage.getParameter();
                return dialect.afterPage(resultList, parameter, rowBounds);
            } else {
                //即不分页也不排序
                //rowBounds用参数值，不使用分页插件处理时，仍然支持默认的内存分页
                return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
            }
        } finally {
            dialect.afterAll();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        dialect.setProperties(properties);
        msCountMap = CacheFactory.createCache(properties.getProperty("msCountCache"), "ms", properties);
        try {
            additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
            additionalParametersField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            logger.debug("NoSuchFieldException:" + e);
        }
    }

    private class DoPage {
        private MappedStatement ms;
        private Object parameter;
        private RowBounds rowBounds;
        private ResultHandler resultHandler;
        private Executor executor;
        private CacheKey cacheKey;
        private BoundSql boundSql;
        private BoundSql sqlWithOrderBy;
        private Map<String, Object> additionalParameters;
        private List resultList;

        public DoPage(MappedStatement ms, Object parameter, RowBounds rowBounds,
                      ResultHandler resultHandler, Executor executor, CacheKey cacheKey,
                      BoundSql boundSql, BoundSql sqlWithOrderBy, Map<String, Object> additionalParameters) {
            this.ms = ms;
            this.parameter = parameter;
            this.rowBounds = rowBounds;
            this.resultHandler = resultHandler;
            this.executor = executor;
            this.cacheKey = cacheKey;
            this.boundSql = boundSql;
            this.sqlWithOrderBy = sqlWithOrderBy;
            this.additionalParameters = additionalParameters;
        }

        public Object getParameter() {
            return parameter;
        }

        public List getResultList() {
            return resultList;
        }

        public DoPage invoke() throws java.sql.SQLException {
            if (dialect.beforePage(ms, parameter, rowBounds)) {
                //生成分页的缓存 key
                CacheKey pageKey = cacheKey;
                //处理参数对象
                parameter = dialect.processParameterObject(ms, parameter, boundSql, pageKey);
                //调用方言获取分页 sql
                String pageSql;
                if (sqlWithOrderBy != null) {
                    pageSql = dialect.getPageSql(ms, sqlWithOrderBy, parameter, rowBounds, pageKey);
                } else {
                    pageSql = dialect.getPageSql(ms, boundSql, parameter, rowBounds, pageKey);
                }
                BoundSql pageBoundSql =
                        new BoundSql(ms.getConfiguration(), pageSql, boundSql.getParameterMappings(), parameter);
                setDynamicParam(pageBoundSql, additionalParameters);
                //执行分页查询
                resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, pageKey, pageBoundSql);
            } else {
                if (sqlWithOrderBy != null) {
                    setDynamicParam(sqlWithOrderBy, additionalParameters);
                    resultList =
                            executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, sqlWithOrderBy);
                } else {
                    setDynamicParam(boundSql, additionalParameters);
                    resultList = executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
                }
            }
            return this;
        }
    }

    private void setDynamicParam(BoundSql boundSql, Map<String, Object> additionalParameters) {
        for (Map.Entry<String, Object> entry : additionalParameters.entrySet()) {
            boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
        }
    }

    private BoundSql doSort(MappedStatement ms, Object parameter, BoundSql boundSql,
                            BoundSql sqlWithOrderBy, Sort sort, boolean executeSort) {
        if (executeSort) {
            //拼接order by并不会影响count查询，这里不对count查询的sql拼接order by
            //转换sort对象为sql字符串
            String orderBySql = orderByParser.sortToString(sort, ms);
            String sql = boundSql.getSql();
            if (orderByParser.containOrderBy(sql)) {
                throw new PageException("the select sql can not contains order by while using doPageAndSort or doSort");
            }
            sql = sql + " order by " + orderBySql;
            sqlWithOrderBy = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), parameter);
        }
        return sqlWithOrderBy;
    }

    private boolean doCount(MappedStatement ms, Object parameter, RowBounds rowBounds,
                            ResultHandler resultHandler, Executor executor, BoundSql boundSql,
                            Map<String, Object> additionalParameters) throws java.sql.SQLException {
        //判断是否需要进行 count 查询
        Object countResultList;
        if (dialect.beforeCount(ms, parameter, rowBounds)) {
            MappedStatement customCountMs = null;
            try {
                customCountMs = ms.getConfiguration().getMappedStatement(ms.getId() + CUSTOM_COUNT_SQL_POSTFIX);
            } catch (Exception e) {
                //ignore
            }
            if (customCountMs != null) {
                //自定义Count SQL
                countResultList = executor.query(customCountMs, parameter, RowBounds.DEFAULT, resultHandler);
            } else {
                //自动生成Count SQL
                CacheKey countKey = executor.createCacheKey(ms, parameter, RowBounds.DEFAULT, boundSql);
                countKey.update(MappedStatementUtils.COUNT);
                MappedStatement countMs = msCountMap.get(countKey);
                if (countMs == null) {
                    //根据当前的 ms 创建一个返回值为 Long 类型的 ms
                    countMs = MappedStatementUtils.newCountMappedStatement(ms);
                    msCountMap.put(countKey, countMs);
                }
                //调用方言获取 count sql
                String countSql = dialect.getCountSql(ms, boundSql, parameter, rowBounds, countKey);
                BoundSql countBoundSql =
                        new BoundSql(ms.getConfiguration(), countSql, boundSql.getParameterMappings(), parameter);
                //当使用动态 SQL 时，可能会产生临时的参数，这些参数需要手动设置到新的 BoundSql 中
                setDynamicParam(countBoundSql, additionalParameters);
                //执行 count 查询
                countResultList =
                        executor.query(countMs, parameter, RowBounds.DEFAULT, resultHandler, countKey, countBoundSql);
            }
            Long count = (Long) ((List) countResultList).get(0);
            //处理查询总数
            //返回 true 时继续分页查询，false 时直接返回
            if (!dialect.afterCount(count, parameter, rowBounds)) {
                //当查询总数为 0 时，直接返回空的结果
                return true;
            }
        }
        return false;
    }
}
