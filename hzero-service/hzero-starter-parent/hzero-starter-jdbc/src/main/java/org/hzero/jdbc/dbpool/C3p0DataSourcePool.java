package org.hzero.jdbc.dbpool;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.MapUtils;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.dbpool.option.C3p0Option;
import org.hzero.jdbc.statement.DatasourceStatement;

import javax.sql.DataSource;

/**
 * c3p0数据源连接池包装类
 *
 * <a href="http://www.mchange.com/projects/c3p0/#quickstart>c3po</a>
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:44:37
 */
public class C3p0DataSourcePool implements DataSourcePoolWrapper {
    @Override
    public DataSource wrap(DatasourceStatement reportDataSource) {
        try {
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setDriverClass(reportDataSource.getDriverClass());
            dataSource.setJdbcUrl(reportDataSource.getJdbcUrl());
            dataSource.setUser(reportDataSource.getUsername());
            dataSource.setPassword(reportDataSource.getPassword());
            dataSource.setInitialPoolSize(
                    MapUtils.getInteger(reportDataSource.getOptions(), C3p0Option.INITIAL_POOL_SIZE, 3));
            dataSource.setMinPoolSize(MapUtils.getInteger(reportDataSource.getOptions(), C3p0Option.MIN_POOL_SIZE, 1));
            dataSource.setMaxPoolSize(MapUtils.getInteger(reportDataSource.getOptions(), C3p0Option.MAX_POOL_SIZE, 20));
            dataSource.setMaxStatements(
                    MapUtils.getInteger(reportDataSource.getOptions(), C3p0Option.MAX_STATEMENTS, 50));
            dataSource.setMaxIdleTime(
                    MapUtils.getInteger(reportDataSource.getOptions(), C3p0Option.MAX_IDLE_TIME, 1800));
            dataSource.setAcquireIncrement(
                    MapUtils.getInteger(reportDataSource.getOptions(), C3p0Option.ACQUIRE_INCREMENT, 3));
            dataSource.setAcquireRetryAttempts(MapUtils.getInteger(reportDataSource.getOptions(),
                    C3p0Option.BREAK_AFTER_ACQUIRE_FAILURE, 30));
            dataSource.setIdleConnectionTestPeriod(MapUtils.getInteger(reportDataSource.getOptions(),
                    C3p0Option.IDLE_CONNECTION_TEST_PERIOD, 60));
            dataSource.setBreakAfterAcquireFailure(MapUtils.getBoolean(reportDataSource.getOptions(),
                    C3p0Option.BREAK_AFTER_ACQUIRE_FAILURE, false));
            dataSource.setTestConnectionOnCheckout(MapUtils.getBoolean(reportDataSource.getOptions(),
                    C3p0Option.TEST_CONNECTION_ON_CHECKOUT, false));
            return dataSource;
        } catch (Exception ex) {
            throw new CommonException(QueryConstants.Error.ERROR_DATASOURCE_POOL_CREATE, "C3p0", ex);
        }
    }
}
