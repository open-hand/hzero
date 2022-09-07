package org.hzero.platform.api.dto;

import javax.validation.constraints.Pattern;

import org.hzero.core.util.Regexs;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.PermissionRel;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * <p>
 * 数据权限关系DTO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/08/29 15:50
 */
@ApiModel("数据权限关系")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionRelDTO extends PermissionRel {

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Encrypt
    private Long permissionRelId;
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty("规则代码")
    private String ruleCode;
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("规则描述")
    private String description;
    @ApiModelProperty("屏蔽SQL值")
    private String sqlValue;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty("规则类型代码")
    private String ruleTypeCode;
    private Long objectVersionNumber;
    @ApiModelProperty("是否启用")
    private Integer enabledFlag;

    @Override
    public String toString() {
        return "PermissionRelDTO{" +
                "permissionRelId=" + permissionRelId +
                ", ruleCode='" + ruleCode + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", description='" + description + '\'' +
                ", sqlValue='" + sqlValue + '\'' +
                ", tenantId=" + tenantId +
                ", objectVersionNumber=" + objectVersionNumber +
                '}';
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    @Override
    public Long getPermissionRelId() {
        return permissionRelId;
    }

    @Override
    public void setPermissionRelId(Long permissionRelId) {
        this.permissionRelId = permissionRelId;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSqlValue() {
        return sqlValue;
    }

    public void setSqlValue(String sqlValue) {
        this.sqlValue = sqlValue;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getRuleTypeCode() {
        return ruleTypeCode;
    }

    public void setRuleTypeCode(String ruleTypeCode) {
        this.ruleTypeCode = ruleTypeCode;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}
