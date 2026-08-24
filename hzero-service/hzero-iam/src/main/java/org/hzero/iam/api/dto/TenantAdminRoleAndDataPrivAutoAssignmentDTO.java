package org.hzero.iam.api.dto;

import java.util.List;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/11
 */
public class TenantAdminRoleAndDataPrivAutoAssignmentDTO {
    public static final String DEFAULT_TENANT_ADMIN_BASIC_TPL = "role/organization/default/template/administrator";
    @Encrypt
    private Long userId; // 管理员用户ID
    private Long tenantId; // 租户ID
    private Boolean newTenantFlag; // 是否为新建租户
    private String companyNum; // 公司编码
    private List<String> roleTemplateCodes; // 租户管理员基础角色模板

    private String sourceKey;
    private String sourceCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }


    public Boolean isNewTenantFlag() {
        return newTenantFlag;
    }

    public void setNewTenantFlag(Boolean newTenantFlag) {
        this.newTenantFlag = newTenantFlag;
    }

    public String getCompanyNum() {
        return companyNum;
    }

    public void setCompanyNum(String companyNum) {
        this.companyNum = companyNum;
    }

    public List<String> getRoleTemplateCodes() {
        return roleTemplateCodes;
    }

    public void setRoleTemplateCodes(List<String> roleTemplateCodes) {
        this.roleTemplateCodes = roleTemplateCodes;
    }

    public Boolean getNewTenantFlag() {
        return newTenantFlag;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
