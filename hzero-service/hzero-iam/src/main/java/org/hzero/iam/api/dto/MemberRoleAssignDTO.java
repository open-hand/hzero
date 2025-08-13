package org.hzero.iam.api.dto;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/8/10
 */
public class MemberRoleAssignDTO {
    private Long userId;
    private String loginName;
    private Long roleId;
    private String roleCode;

    public MemberRoleAssignDTO() {
    }

    public MemberRoleAssignDTO(Long userId, String loginName, Long roleId, String roleCode) {
        this.userId = userId;
        this.loginName = loginName;
        this.roleId = roleId;
        this.roleCode = roleCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
