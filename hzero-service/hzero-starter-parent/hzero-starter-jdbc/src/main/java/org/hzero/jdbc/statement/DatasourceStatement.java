package org.hzero.jdbc.statement;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hzero.jdbc.Query;
import org.hzero.jdbc.constant.DBPoolTypeEnum;
import org.hzero.jdbc.constant.DatabaseTypeEnum;
import org.hzero.jdbc.constant.QueryConstants;
import org.hzero.jdbc.dbpool.DataSourcePoolWrapper;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class DatasourceStatement {

    private final Long tenantId;
    private final String datasourceCode;
    private final String queryerClass;
    private final String dbPoolClass;
    private final String driverClass;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String dbType;
    private final Map<String, Object> options;

    public DatasourceStatement(final Long tenantId, final String datasourceCode, final String driverClass, final String jdbcUrl, final String user,
                               final String password, final String queryerClass, final String dbPoolClass) {
        this(tenantId, datasourceCode, driverClass, jdbcUrl, user, password, queryerClass, dbPoolClass, null, new HashMap<>(3));
    }

    public DatasourceStatement(final Long tenantId, final String datasourceCode, final String driverClass, final String jdbcUrl, final String username,
                               final String password, final String queryerClass, final String dbPoolClass,
                               final Map<String, Object> options) {
        this(tenantId, datasourceCode, driverClass, jdbcUrl, username, password, queryerClass, dbPoolClass, null, options);
    }

    public DatasourceStatement(final Long tenantId, final String datasourceCode, final String driverClass, final String datasourceUrl, final String username,
                               final String password, final DatabaseTypeEnum dbType, final DBPoolTypeEnum dbPoolType,
                               final Map<String, Object> options) {
        this(tenantId, datasourceCode, driverClass, datasourceUrl, username, password,
                String.format(QueryConstants.Datasource.QUERYER_TEMPLATE, dbType.getValue()),
                String.format(QueryConstants.Datasource.DBPOOL_TEMPLATE, dbPoolType.getValue()),
                dbType.getValue(), options);
    }

    public DatasourceStatement(final Long tenantId, final String datasourceCode, final String driverClass, final String jdbcUrl, final String username,
                               final String password, final String queryerClass, final String dbPoolClass,
                               final String dbType,
                               final Map<String, Object> options) {
        this.tenantId = tenantId;
        this.datasourceCode = datasourceCode;
        this.driverClass = driverClass;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.queryerClass = queryerClass;
        this.dbPoolClass = dbPoolClass;
        this.dbType = dbType;
        this.options = options;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getDatasourceCode() {
        return datasourceCode;
    }

    /**
     * 获取数据源驱动类
     *
     * @return 数据源驱动类
     */
    public String getDriverClass() {
        return this.driverClass;
    }

    /**
     * 获取数据源连接字符串(JDBC)
     *
     * @return 数据源连接字符串(JDBC)
     */
    public String getJdbcUrl() {
        return this.jdbcUrl;
    }

    /**
     * 获取数据源登录用户名
     *
     * @return 数据源登录用户名
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * 获取数据源登录密码
     *
     * @return 数据源登录密码
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 获取报表引擎查询器类名(如:org.hzero.report.engine.query.MySqlQueryer)
     *
     * @return 具体Queryer类完全名称
     * @see Query
     */
    public String getQueryerClass() {
        return this.queryerClass;
    }

    /**
     * @return 数据库类型
     * @see DatabaseTypeEnum
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * 获取报表引擎查询器使用的数据源连接池类名(如:org.hzero.report.engine.dbpool.C3p0DataSourcePool)
     *
     * @return 具体DataSourcePoolWrapper类完全名称
     * @see DataSourcePoolWrapper
     */
    public String getDbPoolClass() {
        return this.dbPoolClass;
    }

    /**
     * 获取数据源配置选项,如果没有配置选项则设置为默认选项
     *
     * @return 数据源配置选项
     */
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
        DatasourceStatement that = (DatasourceStatement) o;

        return new EqualsBuilder()
                .append(tenantId, that.tenantId)
                .append(datasourceCode, that.datasourceCode)
                .append(queryerClass, that.queryerClass)
                .append(dbPoolClass, that.dbPoolClass)
                .append(driverClass, that.driverClass)
                .append(jdbcUrl, that.jdbcUrl)
                .append(username, that.username)
                .append(password, that.password)
                .append(dbType, that.dbType)
                .append(options, that.options)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tenantId)
                .append(datasourceCode)
                .append(queryerClass)
                .append(dbPoolClass)
                .append(driverClass)
                .append(jdbcUrl)
                .append(username)
                .append(password)
                .append(dbType)
                .append(options)
                .toHashCode();
    }
}
