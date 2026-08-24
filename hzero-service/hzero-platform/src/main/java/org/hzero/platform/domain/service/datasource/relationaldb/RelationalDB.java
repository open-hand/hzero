package org.hzero.platform.domain.service.datasource.relationaldb;

/**
 * MySQL校验连接所需参数实体
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/15 15:08
 */
public class RelationalDB {
    /**
     * jdbc连接url
     */
    private String datasourceUrl;

    /**
     * 数据库连接驱动类
     */
    private String driverClass;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String passwordEncrypted;

    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
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
}
