package org.hzero.report.infra.engine.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hzero.jdbc.statement.DatasourceStatement;
import org.hzero.report.infra.engine.query.Query;

/**
 * 报表数据源
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午7:04:07
 */
public class ReportDataSource extends DatasourceStatement {

    private final Long tenantId;
    private final String datasourceCode;
    private final String queryerClass;
    private final String dbPoolClass;
    private final String driverClass;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final Map<String, Object> options;

    public ReportDataSource(final Long tenantId, final String datasourceCode, final String driverClass, final String jdbcUrl, final String user,
                            final String password, final String queryerClass, final String dbPoolClass) {
        this(tenantId, datasourceCode, driverClass, jdbcUrl, user, password, queryerClass, dbPoolClass, new HashMap<>(16));
    }

    public ReportDataSource(final Long tenantId, final String datasourceCode, final String driverClass, final String jdbcUrl, final String username,
                            final String password, final String queryerClass, final String dbPoolClass,
                            final Map<String, Object> options) {
        super(tenantId, datasourceCode, driverClass, jdbcUrl, username, password, queryerClass, dbPoolClass, options);
        this.tenantId = tenantId;
        this.datasourceCode = datasourceCode;
        this.driverClass = driverClass;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.queryerClass = queryerClass;
        this.dbPoolClass = dbPoolClass;
        this.options = options;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public String getDatasourceCode() {
        return datasourceCode;
    }

    /**
     * 获取数据源驱动类
     *
     * @return 数据源驱动类
     */
    @Override
    public String getDriverClass() {
        return this.driverClass;
    }

    /**
     * 获取数据源连接字符串(JDBC)
     *
     * @return 数据源连接字符串(JDBC)
     */
    @Override
    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    /**
     * 获取数据源登录用户名
     *
     * @return 数据源登录用户名
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * 获取数据源登录密码
     *
     * @return 数据源登录密码
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * 获取报表引擎查询器类名(如:org.hzero.report.engine.query.MySqlQueryer)
     *
     * @return 具体Queryer类完全名称
     * @see Query
     */
    @Override
    public String getQueryerClass() {
        return this.queryerClass;
    }

    /**
     * 获取报表引擎查询器使用的数据源连接池类名(如:org.hzero.report.engine.dbpool.C3p0DataSourcePool)
     *
     * @return 具体DataSourcePoolWrapper类完全名称
     */
    @Override
    public String getDbPoolClass() {
        return this.dbPoolClass;
    }

    /**
     * 获取数据源配置选项,如果没有配置选项则设置为默认选项
     *
     * @return 数据源配置选项
     */
    @Override
    public Map<String, Object> getOptions() {
        return this.options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReportDataSource that = (ReportDataSource) o;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(tenantId, that.tenantId)
                .append(datasourceCode, that.datasourceCode)
                .append(queryerClass, that.queryerClass)
                .append(dbPoolClass, that.dbPoolClass)
                .append(driverClass, that.driverClass)
                .append(jdbcUrl, that.jdbcUrl)
                .append(username, that.username)
                .append(password, that.password)
                .append(options, that.options)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(tenantId)
                .append(datasourceCode)
                .append(queryerClass)
                .append(dbPoolClass)
                .append(driverClass)
                .append(jdbcUrl)
                .append(username)
                .append(password)
                .append(options)
                .toHashCode();
    }
}
