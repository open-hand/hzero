package org.hzero.iam.domain.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 屏蔽范围
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@ApiModel("数据屏蔽范围")
public class DataPermissionRangeVO extends AuditDomain {

    public static final String FIELD_RANGE_ID = "rangeId";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_SQL_ID = "sqlId";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_SERVICE_NAME = "serviceName";

    @ApiModelProperty("范围ID")
    @Encrypt
    private Long rangeId;
    @ApiModelProperty("表名")
    private String tableName;
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("SQLID")
    private String sqlId;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("服务名")
    private String serviceName;
    @ApiModelProperty("自定义规则标识")
    private Integer customRuleFlag;
    @ApiModelProperty("编辑标识")
    private Integer editableFlag;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getRangeId() {
        return rangeId;
    }

    public DataPermissionRangeVO setRangeId(Long rangeId) {
        this.rangeId = rangeId;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public DataPermissionRangeVO setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public DataPermissionRangeVO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public DataPermissionRangeVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getSqlId() {
        return sqlId;
    }

    public DataPermissionRangeVO setSqlId(String sqlId) {
        this.sqlId = sqlId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DataPermissionRangeVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public DataPermissionRangeVO setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public Integer getCustomRuleFlag() {
        return customRuleFlag;
    }

    public DataPermissionRangeVO setCustomRuleFlag(Integer customRuleFlag) {
        this.customRuleFlag = customRuleFlag;
        return this;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public DataPermissionRangeVO setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataPermissionRangeVO that = (DataPermissionRangeVO) o;

        return new EqualsBuilder()
                .append(rangeId, that.rangeId)
                .append(tableName, that.tableName)
                .append(enabledFlag, that.enabledFlag)
                .append(tenantId, that.tenantId)
                .append(sqlId, that.sqlId)
                .append(description, that.description)
                .append(serviceName, that.serviceName)
                .append(customRuleFlag, that.customRuleFlag)
                .append(editableFlag, that.editableFlag)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(rangeId)
                .append(tableName)
                .append(enabledFlag)
                .append(tenantId)
                .append(sqlId)
                .append(description)
                .append(serviceName)
                .append(customRuleFlag)
                .append(editableFlag)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "DataPermissionRangeVO{" +
                "rangeId=" + rangeId +
                ", tableName='" + tableName + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", tenantId=" + tenantId +
                ", sqlId='" + sqlId + '\'' +
                ", description='" + description + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", customRuleFlag=" + customRuleFlag +
                ", editableFlag=" + editableFlag +
                '}';
    }
}
