package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.api.dto.AutoProcessResultDTO;
import org.hzero.iam.api.dto.TenantAdminRoleAndDataPrivAutoAssignmentDTO;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/8/11
 */
public interface MemberRoleExternalService {

    /**
     * 批量自动分配租户管理员角色及数据权限<br/>
     * 
     * @param tenantAdminRoleAndDataPrivAutoAssignmentDTOList
     * @return
     */
    List<AutoProcessResultDTO> batchAutoAssignTenantAdminRoleAndDataPriv(
                    List<TenantAdminRoleAndDataPrivAutoAssignmentDTO> tenantAdminRoleAndDataPrivAutoAssignmentDTOList);
}
