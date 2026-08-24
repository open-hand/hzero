package org.hzero.boot.platform.ds.vo;

import java.util.Map;

/**
 * 
 * 数据源配置
 * 
 * @author xianzhi.chen@hand-china.com 2019年1月21日下午7:17:24
 */
public class DatasourceVO {

    private Long datasourceId;
    private String datasourceCode;
    private String description;
    private String datasourceUrl;
    private String username;
    private String passwordEncrypted;
    private Integer enabledFlag;
    private String dsPurposeCode;
    private String dbType;
    private String driverClass;
    private String dbPoolType;
    private String queryerClass;
    private String poolClass;
    private String options;
    private String remark;
    private String extConfig;
    private String driverId;
    private Map<String, Object> extConfigMap;
    private Map<String, Object> optionsMap;
    private String datasourceClass;
    private String driverType;

    public String getDatasourceClass() {
        return datasourceClass;
    }

    public void setDatasourceClass(String datasourceClass) {
        this.datasourceClass = datasourceClass;
    }

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public Map<String, Object> getOptionsMap() {
        return optionsMap;
    }

    public void setOptionsMap(Map<String, Object> optionsMap) {
        this.optionsMap = optionsMap;
    }

    public Map<String, Object> getExtConfigMap() {
        return extConfigMap;
    }

    public void setExtConfigMap(Map<String, Object> extConfigMap) {
        this.extConfigMap = extConfigMap;
    }

    public String getExtConfig() {
        return extConfig;
    }

    public void setExtConfig(String extConfig) {
        this.extConfig = extConfig;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Long getDatasourceId() {
        return datasourceId;
    }

    public DatasourceVO setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
        return this;
    }

    public String getDatasourceCode() {
        return datasourceCode;
    }

    public DatasourceVO setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DatasourceVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public DatasourceVO setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public DatasourceVO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public DatasourceVO setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public DatasourceVO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getDsPurposeCode() {
        return dsPurposeCode;
    }

    public DatasourceVO setDsPurposeCode(String dsPurposeCode) {
        this.dsPurposeCode = dsPurposeCode;
        return this;
    }

    public String getDbType() {
        return dbType;
    }

    public DatasourceVO setDbType(String dbType) {
        this.dbType = dbType;
        return this;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public DatasourceVO setDriverClass(String driverClass) {
        this.driverClass = driverClass;
        return this;
    }

    public String getDbPoolType() {
        return dbPoolType;
    }

    public DatasourceVO setDbPoolType(String dbPoolType) {
        this.dbPoolType = dbPoolType;
        return this;
    }

    public String getQueryerClass() {
        return queryerClass;
    }

    public DatasourceVO setQueryerClass(String queryerClass) {
        this.queryerClass = queryerClass;
        return this;
    }

    public String getPoolClass() {
        return poolClass;
    }

    public DatasourceVO setPoolClass(String poolClass) {
        this.poolClass = poolClass;
        return this;
    }

    public String getOptions() {
        return options;
    }

    public DatasourceVO setOptions(String options) {
        this.options = options;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public DatasourceVO setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    @Override
    public String toString() {
        return "DatasourceVO{" + "datasourceId=" + datasourceId + ", datasourceCode='" + datasourceCode + '\''
                        + ", description='" + description + '\'' + ", datasourceUrl='" + datasourceUrl + '\''
                        + ", username='" + username + '\'' + ", passwordEncrypted='" + passwordEncrypted + '\''
                        + ", enabledFlag=" + enabledFlag + ", dsPurposeCode='" + dsPurposeCode + '\'' + ", dbType='"
                        + dbType + '\'' + ", driverClass='" + driverClass + '\'' + ", dbPoolType='" + dbPoolType + '\''
                        + ", queryerClass='" + queryerClass + '\'' + ", poolClass='" + poolClass + '\'' + ", options='"
                        + options + '\'' + ", remark='" + remark + '\'' + ", extConfig='" + extConfig + '\''
                        + ", driverId='" + driverId + '\'' + ", extConfigMap=" + extConfigMap + ", optionsMap="
                        + optionsMap + ", datasourceClass='" + datasourceClass + '\'' + ", driverType='" + driverType
                        + '\'' + '}';
    }
}
