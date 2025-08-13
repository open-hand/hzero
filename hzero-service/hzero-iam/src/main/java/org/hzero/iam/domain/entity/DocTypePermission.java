package org.hzero.iam.domain.entity;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 单据类型数据权限关联
 *
 * @author qingsheng.chen@hand-china.com 2019-07-01 19:39
 */
@ApiModel("单据类型数据权限关联")
@VersionAudit
@ModifyAudit
@Table(name = "hiam_doc_type_permission")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocTypePermission {
    public static final String ENCRYPT_KEY = "hiam_doc_type_permission";
    public static final String FIELD_AUTH_DIM_ID = "authDimId";
    public interface Insert{}

    @ApiModelProperty("表ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long docTypePermissionId;
    @ApiModelProperty(value = "单据类型维度分配ID，hiam_doc_type_auth_dim.auth_dim_id")
    @NotNull
    @Encrypt
    private Long authDimId;
    @ApiModelProperty(value = "数据权限范围ID,hpfm_permission_rule.rule_id")
    @NotNull
    @Encrypt
    private Long ruleId;
    @ApiModelProperty(value = "数据权限规则ID.hpfm_permission_range.range_id")
    @NotNull
    @Encrypt
    private Long rangeId;
    @ApiModelProperty(value = "租户ID")
    @NotNull(groups = Insert.class)
    private Long tenantId;

    @Transient
    private String ruleCode;
    @Transient
    private String ruleName;

    @Transient
    private String tenantName;

    public Long getTenantId() {
        return tenantId;
    }

    public DocTypePermission setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public DocTypePermission setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public Long getDocTypePermissionId() {
        return docTypePermissionId;
    }

    public DocTypePermission setDocTypePermissionId(Long docTypePermissionId) {
        this.docTypePermissionId = docTypePermissionId;
        return this;
    }

    public Long getAuthDimId() {
        return authDimId;
    }

    public DocTypePermission setAuthDimId(Long authDimId) {
        this.authDimId = authDimId;
        return this;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public DocTypePermission setRuleId(Long ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public Long getRangeId() {
        return rangeId;
    }

    public DocTypePermission setRangeId(Long rangeId) {
        this.rangeId = rangeId;
        return this;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public DocTypePermission setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
        return this;
    }

    public String getRuleName() {
        return ruleName;
    }

    public DocTypePermission setRuleName(String ruleName) {
        this.ruleName = ruleName;
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
        DocTypePermission that = (DocTypePermission) o;
        return Objects.equals(docTypePermissionId, that.docTypePermissionId) &&
                Objects.equals(authDimId, that.authDimId) &&
                Objects.equals(ruleId, that.ruleId) &&
                Objects.equals(rangeId, that.rangeId) &&
                Objects.equals(ruleCode, that.ruleCode) &&
                Objects.equals(ruleName, that.ruleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docTypePermissionId, authDimId, ruleId, rangeId, ruleCode, ruleName);
    }

    @Override
    public String toString() {
        return "DocTypePermission{" +
                "docTypePermissionId=" + docTypePermissionId +
                ", authDimId=" + authDimId +
                ", ruleId=" + ruleId +
                ", rangeId=" + rangeId +
                ", ruleCode='" + ruleCode + '\'' +
                ", ruleName='" + ruleName + '\'' +
                '}';
    }
}
