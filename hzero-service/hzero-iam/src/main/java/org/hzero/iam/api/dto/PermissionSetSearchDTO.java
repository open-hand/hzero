package org.hzero.iam.api.dto;

import java.util.Set;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 权限集查询条件
 *
 * @author mingwei.liu@hand-china.com 2018/9/4
 */
public class PermissionSetSearchDTO {

    @Encrypt
    private Long id;
    private String code;
    private String name;
    private Boolean enabledFlag;
    private String description;
    private String permissionCode;
    /**
     * 父级菜单ID
     */
    private Long parentMenuId;
    /**
     * 父级菜单
     */
    private String parentName;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 权限集ID
     */
    @Encrypt
    private Long permissionSetId;
    /**
     * 当前角色ID
     */
    private Long currentRoleId;
    /**
     * 待分配的角色ID
     */
    private Long allocateRoleId;
    private String condition;
    private String level;
    private Set<String> labels;

    public PermissionSetSearchDTO() {
    }

    public PermissionSetSearchDTO(Long tenantId, Long permissionSetId, String condition) {
        this.tenantId = tenantId;
        this.permissionSetId = permissionSetId;
        this.condition = condition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(Long parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getPermissionSetId() {
        return permissionSetId;
    }

    public void setPermissionSetId(Long permissionSetId) {
        this.permissionSetId = permissionSetId;
    }

    public Long getCurrentRoleId() {
        return currentRoleId;
    }

    public void setCurrentRoleId(Long currentRoleId) {
        this.currentRoleId = currentRoleId;
    }

    public Long getAllocateRoleId() {
        return allocateRoleId;
    }

    public void setAllocateRoleId(Long allocateRoleId) {
        this.allocateRoleId = allocateRoleId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }
}
