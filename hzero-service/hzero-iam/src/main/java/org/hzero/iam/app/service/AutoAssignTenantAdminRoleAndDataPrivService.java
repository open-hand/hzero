package org.hzero.iam.app.service;

import org.hzero.iam.api.dto.TenantAdminRoleAndDataPrivAutoAssignmentDTO;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/11
 */
public interface AutoAssignTenantAdminRoleAndDataPrivService {

    /**
     * 自动分配租户管理员角色及数据权限<br/>
     *
     * @param tenantAdminRoleAndDataPrivAutoAssignmentDTO
     */
    void autoAssignTenantAdminRoleAndDataPriv(
                    TenantAdminRoleAndDataPrivAutoAssignmentDTO tenantAdminRoleAndDataPrivAutoAssignmentDTO);
}
