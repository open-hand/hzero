package org.hzero.jdbc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.hzero.core.util.Pair;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.dbpool.DataSourcePoolFactory;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.exception.CommonException;

/**
 * JDBC工具类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:42:29
 */
public class JdbcUtils {

    private JdbcUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtils.class);
    private static final Map<Pair<Long, String>, DataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>(100);
    private static final Map<Pair<Long, String>, DatasourceStatement> KEY_MAP = new ConcurrentHashMap<>(100);


    public static DataSource getDataSource(DatasourceStatement reportDataSource) {
        Pair<Long, String> key = Pair.of(reportDataSource.getTenantId(), reportDataSource.getDatasourceCode());
        DataSource dataSource;
        if (KEY_MAP.containsKey(key)) {
            // 存在
            DatasourceStatement old = KEY_MAP.get(key);
            if (old.equals(reportDataSource)) {
                dataSource = DATA_SOURCE_MAP.get(key);
            } else {
                // 释放连接
                try {
                    DATA_SOURCE_MAP.get(key).getConnection().close();
                } catch (SQLException e) {
                    logger.warn("connection release failed.");
                }
                // 新建datasource
                KEY_MAP.put(key, reportDataSource);
                dataSource = DataSourcePoolFactory.create(reportDataSource.getDbPoolClass()).wrap(reportDataSource);
                DATA_SOURCE_MAP.put(key, dataSource);
            }
        } else {
            // 不存在
            KEY_MAP.put(key, reportDataSource);
            dataSource = DataSourcePoolFactory.create(reportDataSource.getDbPoolClass()).wrap(reportDataSource);
            DATA_SOURCE_MAP.put(key, dataSource);
        }
        return dataSource;
    }

    public static void releaseJdbcResource(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            logger.error("Release jdbc resource error", ex);
            throw new CommonException(QueryConstants.Error.ERROR_RELEASE_JDBC_RESOURCE, ex);
        }
    }
}
