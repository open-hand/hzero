package org.hzero.platform.domain.vo;

/**
 * redis存储对象
 *
 * @author like.zhang@hand-china.com 2018/09/18 17:17
 */
public class DatasourceVO {

    /**
     * 数据源名称
     */
    private String description;
    private String driverClass;
    private String dbPoolType;
    private String queryerClass;
    private String poolClass;
    private String options;
    private String datasourceCode;
    private String datasourceUrl;
    private String username;
    private String passwordEncrypted;
    private String dbType;
    private String dsPurposeCode;
    private Long tenantId;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDatasourceCode() {
        return datasourceCode;
    }

    public void setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
    }

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDbPoolType() {
        return dbPoolType;
    }

    public void setDbPoolType(String dbPoolType) {
        this.dbPoolType = dbPoolType;
    }

    public String getQueryerClass() {
        return queryerClass;
    }

    public void setQueryerClass(String queryerClass) {
        this.queryerClass = queryerClass;
    }

    public String getPoolClass() {
        return poolClass;
    }

    public void setPoolClass(String poolClass) {
        this.poolClass = poolClass;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getDsPurposeCode() {
        return dsPurposeCode;
    }

    public void setDsPurposeCode(String dsPurposeCode) {
        this.dsPurposeCode = dsPurposeCode;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
