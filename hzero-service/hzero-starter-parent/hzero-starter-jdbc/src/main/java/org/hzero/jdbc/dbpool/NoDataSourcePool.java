package org.hzero.jdbc.dbpool;


import org.hzero.jdbc.statement.DatasourceStatement;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 无数据源连接池,直接使用jdbc连接
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:52:55
 */
public class NoDataSourcePool implements DataSourcePoolWrapper {
    @Override
    public DataSource wrap(DatasourceStatement rptDs) {
        return new NoDataSource(rptDs);
    }

    private static class NoDataSource implements DataSource {

        private final DatasourceStatement reportDataSource;

        public NoDataSource(DatasourceStatement reportDataSource) {
            this.reportDataSource = reportDataSource;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(this.reportDataSource.getJdbcUrl(), this.reportDataSource.getUsername(),
                    this.reportDataSource.getPassword());
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {

        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {

        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }
    }
}
