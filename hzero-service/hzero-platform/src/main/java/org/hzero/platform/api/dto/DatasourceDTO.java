package org.hzero.platform.api.dto;

import javax.validation.constraints.Pattern;

import javax.validation.constraints.NotBlank;
import org.hzero.core.jackson.annotation.Sensitive;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.Datasource;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据库DTO
 *
 * @author like.zhang@hand-china.com 2018/09/07 11:45
 */
@ApiModel("数据库DTO")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class DatasourceDTO extends Datasource {

    @ApiModelProperty("表ID，主键")
    private Long datasourceId;
    @ApiModelProperty(value = "数据源编码", required = true)
    @NotBlank
    @Pattern(regexp = Regexs.CODE)
    private String datasourceCode;
    @ApiModelProperty(value = "说明")
    private String description;
    @ApiModelProperty(value = "数据源URL地址")
    private String datasourceUrl;
    @ApiModelProperty(value = "用户")
    private String username;
    @ApiModelProperty(value = "加密密码")
    @Sensitive(cipher = "0-")
    private String passwordEncrypted;
    @ApiModelProperty(value = "是否启用，1启用、0禁用")
    private Integer enabledFlag;
    private String dbType;
    @ApiModelProperty(value = "数据库驱动类")
    @NotBlank
    private String driverClass;
    @ApiModelProperty(value = "连接池类型，独立值集：HPFM.DB_POOL_TYPE")
    @NotBlank
    private String dbPoolType;
    @ApiModelProperty(value = "获取报表引擎查询器类名")
    private String queryerClass;
    @ApiModelProperty(value = "报表引擎查询器使用的数据源连接池类名")
    private String poolClass;
    @ApiModelProperty(value = "数据源配置选项(JSON格式）")
    private String options;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "数据源用途，值集：HPFM.DATASOURCE_PURPOSE")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String dsPurposeCode;

    public DatasourceDTO(String datasourceCode, String description, String datasourceUrl) {
        this.datasourceCode = datasourceCode;
        this.description = description;
        this.datasourceUrl = datasourceUrl;
    }

    public DatasourceDTO(){}

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    @Override
    public Long getDatasourceId() {
        return datasourceId;
    }

    @Override
    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    @Override
    public String getDatasourceCode() {
        return datasourceCode;
    }

    @Override
    public void setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    @Override
    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    @Override
    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    @Override
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    @Override
    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    @Override
    public String getDbType() {
        return dbType;
    }

    @Override
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @Override
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public String getDbPoolType() {
        return dbPoolType;
    }

    @Override
    public void setDbPoolType(String dbPoolType) {
        this.dbPoolType = dbPoolType;
    }

    @Override
    public String getQueryerClass() {
        return queryerClass;
    }

    @Override
    public void setQueryerClass(String queryerClass) {
        this.queryerClass = queryerClass;
    }

    @Override
    public String getPoolClass() {
        return poolClass;
    }

    @Override
    public void setPoolClass(String poolClass) {
        this.poolClass = poolClass;
    }

    @Override
    public String getOptions() {
        return options;
    }

    @Override
    public void setOptions(String options) {
        this.options = options;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String getDsPurposeCode() {
        return dsPurposeCode;
    }

    @Override
    public void setDsPurposeCode(String dsPurposeCode) {
        this.dsPurposeCode = dsPurposeCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DatasourceDTO [datasourceId=");
        builder.append(datasourceId);
        builder.append(", datasourceCode=");
        builder.append(datasourceCode);
        builder.append(",description=");
        builder.append(description);
        builder.append(", datasourceCode=");
        builder.append(datasourceCode);
        builder.append(", datasourceUrl=");
        builder.append(datasourceUrl);
        builder.append(", username=");
        builder.append(username);
        builder.append(", passwordEncrypted=");
        builder.append(passwordEncrypted);
        builder.append(", enabledFlag=");
        builder.append(enabledFlag);
        builder.append("]");
        return builder.toString();
    }

}
