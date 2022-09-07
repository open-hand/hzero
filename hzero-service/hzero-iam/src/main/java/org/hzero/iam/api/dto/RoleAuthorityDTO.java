package org.hzero.iam.api.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author mingke.yan@hand-china.com
 * @version 1.0
 * @date 2018/8/7
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleAuthorityDTO {

    public static final String FIELD_RULE_LINE = "roleAuthorityLines";

    private Long orderSeq;

    @Encrypt
    private Long roleAuthId;

    @NotNull
    @Encrypt
    private Long roleId;

    @NotNull
    @Encrypt
    private Long authDocTypeId;

    @LovValue(lovCode = "HIAM.AUTHORITY_SCOPE_CODE",meaningField = "authScopeMeaning")
    private String authScopeCode;

    private String authScopeMeaning;

    @NotNull
    private Integer msgFlag;

    private String docTypeName;

    private Long objectVersionNumber;

    private Long docEnabledFlag;

    private Long organizationId;

    @Encrypt
    private Long userId;

    private List<RoleAuthorityLine> roleAuthorityLines;

    /**
     * ADD 20191015
     * 角色所属租户
     */
    private String tenantName;
    /**
     * ADD 20191015
     * 单据分配角色代码
     */
    private String roleCode;
    /**
     * ADD 20191015
     * 单据分配角色名称
     */
    private String roleName;
    /**
     * ADD 20191015
     * 所分配的角色父级角色
     */
    private String parentRoleName;
    /**
     * ADD 20191015
     * 租户id、 查询条件
     */
    private Long tenantId;

    /**
     * 权限控制类型字段，控制前端是否默认勾选角色权限头信息
     */
    private String authControlType;

    public String getAuthControlType() {
        return authControlType;
    }

    public RoleAuthorityDTO setAuthControlType(String authControlType) {
        this.authControlType = authControlType;
        return this;
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
    }

    public Long getDocEnabledFlag() {
        return docEnabledFlag;
    }

    public void setDocEnabledFlag(Long docEnabledFlag) {
        this.docEnabledFlag = docEnabledFlag;
    }

    public String getAuthScopeMeaning() {
        return authScopeMeaning;
    }

    public void setAuthScopeMeaning(String authScopeMeaning) {
        this.authScopeMeaning = authScopeMeaning;
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


    public List<RoleAuthorityLine> getRoleAuthorityLines() {
        return roleAuthorityLines;
    }

    public void setRoleAuthorityLines(List<RoleAuthorityLine> roleAuthorityLines) {
        this.roleAuthorityLines = roleAuthorityLines;
    }

    public Long getRoleAuthId() {
        return roleAuthId;
    }

    public void setRoleAuthId(Long roleAuthId) {
        this.roleAuthId = roleAuthId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getAuthDocTypeId() {
        return authDocTypeId;
    }

    public void setAuthDocTypeId(Long authDocTypeId) {
        this.authDocTypeId = authDocTypeId;
    }

    public String getAuthScopeCode() {
        return authScopeCode;
    }

    public void setAuthScopeCode(String authScopeCode) {
        this.authScopeCode = authScopeCode;
    }

    public Integer getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(Integer msgFlag) {
        this.msgFlag = msgFlag;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getParentRoleName() {
        return parentRoleName;
    }

    public void setParentRoleName(String parentRoleName) {
        this.parentRoleName = parentRoleName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
