package org.hzero.iam.app.service;

import org.hzero.iam.api.dto.RoleAuthDataDTO;
import org.hzero.iam.api.dto.UserAuthorityDTO;

import java.util.List;

/**
 * @author jianbo.li
 * @date 2019/11/15 14:58
 */
public interface DataAuthManagerService {
    /**
     * 权限维护 - 权限数据 （角色） - 新建 or 更新
     *
     * @param tenantId        租户id
     * @param roleAuthDataDTO 新建权限信息
     * @return
     */
    RoleAuthDataDTO saveRoleAuthData(Long tenantId,
                                     RoleAuthDataDTO roleAuthDataDTO);

    /**
     * 权限维护 - 权限数据 （用户） - 新建 or 更新
     *
     * @param tenantId             租户id
     * @param userAuthorityDTOList 授权列表
     * @return
     */
    List<UserAuthorityDTO> saveUserAuthData(Long tenantId,
                                            List<UserAuthorityDTO> userAuthorityDTOList);
}
