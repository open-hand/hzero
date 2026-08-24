package org.hzero.gateway.helper.domain;

import java.util.List;

/**
 * @author bojiangzhou 2020/02/21
 */
public class PermissionCheckDTO {

    private Long memberId;
    private String memberType;
    private Long tenantId;
    private List<Long> roleIds;
    private String permissionCode;
    private String sourceType;
    private boolean checkCurrentRole;

    public PermissionCheckDTO(Long memberId, String memberType, List<Long> roleIds) {
        this.memberId = memberId;
        this.memberType = memberType;
        this.roleIds = roleIds;
    }

    public PermissionCheckDTO(Long memberId, String memberType, Long tenantId, List<Long> roleIds, String permissionCode, String sourceType, boolean checkCurrentRole) {
        this.memberId = memberId;
        this.memberType = memberType;
        this.tenantId = tenantId;
        this.roleIds = roleIds;
        this.permissionCode = permissionCode;
        this.sourceType = sourceType;
        this.checkCurrentRole = checkCurrentRole;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMemberType() {
        return memberType;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public String getSourceType() {
        return sourceType;
    }

    public boolean isCheckCurrentRole() {
        return checkCurrentRole;
    }

    @Override
    public String toString() {
        return "PermissionCheckDTO{" +
                "memberId=" + memberId +
                ", memberType='" + memberType + '\'' +
                ", tenantId=" + tenantId +
                ", roleIds=" + roleIds +
                ", permissionCode='" + permissionCode + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", checkCurrentRole=" + checkCurrentRole +
                '}';
    }
}
