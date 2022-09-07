package org.hzero.platform.api.dto;

import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.PermissionRule;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Objects;

/**
 * <p>
 * 数据权限规则DTO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/08/20 9:18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("屏蔽规则")
public class PermissionRuleDTO extends PermissionRule {

    @ApiModelProperty("规则ID")
    @Encrypt
    private Long ruleId;
    @Pattern(regexp = Regexs.CODE)
    @ApiModelProperty("规则代码")
    private String ruleCode;
    @ApiModelProperty("规则名称")
    private String ruleName;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("租户名")
    private String tenantName;
    @ApiModelProperty("规则类型代码")
    @Pattern(regexp = Regexs.CODE)
    @LovValue(lovCode = "HPFM.PERMISSION_RULE_TYPE", meaningField = PermissionRule.FIELD_RULE_TYPE_CODE_MEANING)
    private String ruleTypeCode;
    @Transient
    private String ruleTypeCodeMeaning;
    private Long objectVersionNumber;

    /**
     * 自定义：当前租户ID = 数据租户ID
     * 预定义：当前租户ID != 数据租户ID
     *
     * @return 返回是否自定义
     */
    public String getSource() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails != null) {
            if (Objects.equals(customUserDetails.getTenantId(), tenantId)) {
                return MessageAccessor.getMessage("hpfm.info.customize").desc();
            }
        }
        return MessageAccessor.getMessage("hpfm.info.predefined").desc();
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    @Override
    public Long getRuleId() {
        return ruleId;
    }

    @Override
    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    @Override
    public String getRuleCode() {
        return ruleCode;
    }

    @Override
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    @Override
    public String getRuleName() {
        return ruleName;
    }

    @Override
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Override
    public String getRuleTypeCode() {
        return ruleTypeCode;
    }

    @Override
    public void setRuleTypeCode(String ruleTypeCode) {
        this.ruleTypeCode = ruleTypeCode;
    }

    @Override
    public String getRuleTypeCodeMeaning() {
        return ruleTypeCodeMeaning;
    }

    @Override
    public void setRuleTypeCodeMeaning(String ruleTypeCodeMeaning) {
        this.ruleTypeCodeMeaning = ruleTypeCodeMeaning;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
