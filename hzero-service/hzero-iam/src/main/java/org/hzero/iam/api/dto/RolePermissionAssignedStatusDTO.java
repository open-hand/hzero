package org.hzero.iam.api.dto;

/**
 * <p>
 * 角色分配的权限数量信息
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/10/29
 */
public class RolePermissionAssignedStatusDTO {
    /**
     * 父级角色已分配权限数量
     */
    private Integer parentCount;
    /**
     * 当前角色已分配数量
     */
    private Integer currentCount;

    public Integer getParentCount() {
        return parentCount;
    }

    public void setParentCount(Integer parentCount) {
        this.parentCount = parentCount;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }
}
