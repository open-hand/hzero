package org.hzero.iam.api.dto;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * description
 *
 * @author xingxingwu.hand-china.com 2019/10/25 11:39
 */
public class SecGrpPermissionSearchDTO {
    /**
     * 角色ID
     */
    @Encrypt
    private Long roleId;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 安全组ID
     */
    @Encrypt
    private Long secGrpId;
    /**
     * 权限集名称
     */
    private String menuName;
    /**
     * 层级
     */
    private String secGrpLevel;

    private String secGrpSource;


    public Long getSecGrpId() {
        return secGrpId;
    }

    public void setSecGrpId(Long secGrpId) {
        this.secGrpId = secGrpId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getSecGrpLevel() {
        return secGrpLevel;
    }

    public void setSecGrpLevel(String secGrpLevel) {
        this.secGrpLevel = secGrpLevel;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getSecGrpSource() {
        return secGrpSource;
    }

    public void setSecGrpSource(String secGrpSource) {
        this.secGrpSource = secGrpSource;
    }
}
