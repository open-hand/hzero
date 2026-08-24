package org.hzero.jdbc;

import io.choerodon.core.exception.CommonException;
import org.hzero.jdbc.constant.DBPoolTypeEnum;
import org.hzero.jdbc.constant.DatabaseTypeEnum;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.hzero.jdbc.util.JdbcUtils;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class Update {
    private DatasourceStatement datasourceStatement;
    private Connection connection;

    public Update(DatasourceStatement datasourceStatement) {
        this.datasourceStatement = datasourceStatement;
    }

    public Update(Long tenantId, String datasourceCode, String driverClass, String datasourceUrl, String username, String password, DatabaseTypeEnum dbType, DBPoolTypeEnum dbPoolType, Map<String, Object> options) {
        // 设置查询类和连接池类
        this(new DatasourceStatement(tenantId, datasourceCode, driverClass, datasourceUrl, username, password, dbType, dbPoolType, options));
    }

    public void executeAndClose(String sqlText) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getJdbcConnection();
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlText);
        } catch (Exception ex) {
            throw new CommonException(ex);
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, null);
            this.connection = null;
        }
    }

    public void executeAndClose(List<String> sqlTexts) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.getJdbcConnection();
            conn.setAutoCommit(true);
            stmt = conn.createStatement();
            for (String text : sqlTexts) {
                stmt.addBatch(text);
            }
            stmt.executeBatch();
            stmt.clearBatch();
        } catch (Exception ex) {
            throw new CommonException(ex);
        } finally {
            JdbcUtils.releaseJdbcResource(conn, stmt, null);
            this.connection = null;
        }
    }

    public Connection execute(String sqlText) {
        Connection conn;
        Statement stmt = null;
        try {
            conn = getJdbcConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlText);
        } catch (Exception ex) {
            throw new CommonException(ex);
        } finally {
            JdbcUtils.releaseJdbcResource(null, stmt, null);
        }
        return conn;
    }

    /**
     * 批量更新
     * <strong>慎用，只有部分数据库支持</strong>
     * 批量更新语句中禁止使用<code>SELECT</code>
     * 可以通过 {@link java.sql.DatabaseMetaData#supportsBatchUpdates()} 方法查看当前数据源是否支持批量更新
     *
     * @param sqlTexts 批量更新命令
     * @return 数据库连接
     */
    public Connection execute(List<String> sqlTexts) {
        Connection conn;
        Statement stmt = null;
        try {
            conn = getJdbcConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            for (String text : sqlTexts) {
                stmt.addBatch(text);
            }
            stmt.executeBatch();
            stmt.clearBatch();
        } catch (Exception ex) {
            throw new CommonException(ex);
        } finally {
            JdbcUtils.releaseJdbcResource(null, stmt, null);
        }
        return conn;
    }

    public void commitAndClose() {
        Connection connection = null;
        try {
            connection = getJdbcConnection();
            connection.commit();
        } catch (Exception ex) {
            throw new CommonException(ex);
        } finally {
            JdbcUtils.releaseJdbcResource(connection, null, null);
        }
    }

    /**
     * 获取当前报表查询器的JDBC Connection对象
     *
     * @return Connection
     */
    public Connection getJdbcConnection() {
        if (connection == null) {
            try {
                Class.forName(this.datasourceStatement.getDriverClass());
                connection = JdbcUtils.getDataSource(this.datasourceStatement).getConnection();
            } catch (Exception ex) {
                throw new CommonException(ex);
            }
        }
        return connection;
    }
}
