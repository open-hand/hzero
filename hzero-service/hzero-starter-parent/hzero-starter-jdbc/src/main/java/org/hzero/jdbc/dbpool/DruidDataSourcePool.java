package org.hzero.jdbc.dbpool;

import com.alibaba.druid.pool.DruidDataSource;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.MapUtils;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.dbpool.option.DruidOption;
import org.hzero.jdbc.statement.DatasourceStatement;

import javax.sql.DataSource;

/**
 * Druid数据源连接池包装类
 *
 * <a href="https://github.com/alibaba/druid/wiki>Druid</a>
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:44:37
 */
public class DruidDataSourcePool implements DataSourcePoolWrapper {
    @Override
    public DataSource wrap(DatasourceStatement reportDataSource) {
        try {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName(reportDataSource.getDriverClass());
            dataSource.setUrl(reportDataSource.getJdbcUrl());
            dataSource.setUsername(reportDataSource.getUsername());
            dataSource.setPassword(reportDataSource.getPassword());
            dataSource.setInitialSize(MapUtils.getInteger(reportDataSource.getOptions(), DruidOption.INITIAL_SIZE, 3));
            dataSource.setMaxActive(MapUtils.getInteger(reportDataSource.getOptions(), DruidOption.MAX_ACTIVE, 20));
            dataSource.setMinIdle(MapUtils.getInteger(reportDataSource.getOptions(), DruidOption.MIN_IDLE, 1));
            dataSource.setMaxWait(MapUtils.getInteger(reportDataSource.getOptions(), DruidOption.MAX_WAIT, 60000));
            dataSource.setTimeBetweenEvictionRunsMillis(MapUtils.getInteger(reportDataSource.getOptions(),
                    DruidOption.TIME_BETWEEN_EVICTION_RUNS_MILLIS, 60000));
            dataSource.setMinEvictableIdleTimeMillis(MapUtils.getInteger(reportDataSource.getOptions(),
                    DruidOption.MIN_EVICTABLE_IDLE_TIME_MILLIS, 300000));
            dataSource.setValidationQuery(MapUtils.getString(reportDataSource.getOptions(), DruidOption.VALIDATION_QUERY, "select 1"));
            dataSource.setTestWhileIdle(
                    MapUtils.getBoolean(reportDataSource.getOptions(), DruidOption.TEST_WHILE_IDLE, true));
            dataSource.setTestOnBorrow(
                    MapUtils.getBoolean(reportDataSource.getOptions(), DruidOption.TEST_ON_BORROW, false));
            dataSource.setTestOnReturn(
                    MapUtils.getBoolean(reportDataSource.getOptions(), DruidOption.TEST_ON_RETURN, false));
            dataSource.setMaxOpenPreparedStatements(MapUtils.getInteger(reportDataSource.getOptions(),
                    DruidOption.MAX_OPEN_PREPARED_STATEMENTS, 20));
            dataSource.setRemoveAbandoned(
                    MapUtils.getBoolean(reportDataSource.getOptions(), DruidOption.REMOVE_ABANDONED, true));
            dataSource.setRemoveAbandonedTimeout(MapUtils.getInteger(reportDataSource.getOptions(),
                    DruidOption.REMOVE_ABANDONED_TIMEOUT, 1800));
            dataSource.setLogAbandoned(
                    MapUtils.getBoolean(reportDataSource.getOptions(), DruidOption.LOG_ABANDONED, true));
            return dataSource;
        } catch (Exception ex) {
            throw new CommonException(QueryConstants.Error.ERROR_DATASOURCE_POOL_CREATE, "Druid", ex);
        }
    }
}
