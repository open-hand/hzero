package org.hzero.jdbc.dbpool;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.dbpool.option.Dbcp2Option;
import org.hzero.jdbc.statement.DatasourceStatement;

import javax.sql.DataSource;

/**
 * DBCP2数据源连接池包装类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:47:34
 */
public class Dbcp2DataSourcePool implements DataSourcePoolWrapper {
    @Override
    public DataSource wrap(DatasourceStatement reportDataSource) {
        try {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(reportDataSource.getDriverClass());
            dataSource.setUrl(reportDataSource.getJdbcUrl());
            dataSource.setUsername(reportDataSource.getUsername());
            dataSource.setPassword(reportDataSource.getPassword());
            dataSource.setInitialSize(MapUtils.getInteger(reportDataSource.getOptions(), Dbcp2Option.INITIAL_SIZE, 3));
            dataSource.setMaxIdle(MapUtils.getInteger(reportDataSource.getOptions(), Dbcp2Option.MAX_IDLE, 20));
            dataSource.setMinIdle(MapUtils.getInteger(reportDataSource.getOptions(), Dbcp2Option.MAX_IDLE, 1));
            dataSource.setLogAbandoned(
                    MapUtils.getBoolean(reportDataSource.getOptions(), Dbcp2Option.LOG_ABANDONED, true));
            dataSource.setRemoveAbandonedTimeout(MapUtils.getInteger(reportDataSource.getOptions(),
                    Dbcp2Option.REMOVE_ABANDONED_TIMEOUT, 180));
            dataSource.setMaxWaitMillis(MapUtils.getInteger(reportDataSource.getOptions(), Dbcp2Option.MAX_WAIT, 1000));
            return dataSource;
        } catch (Exception ex) {
            throw new CommonException(QueryConstants.Error.ERROR_DATASOURCE_POOL_CREATE, "Dbcp2", ex);
        }
    }
}
