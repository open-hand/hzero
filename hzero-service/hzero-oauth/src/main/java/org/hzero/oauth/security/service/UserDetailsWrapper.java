package org.hzero.oauth.security.service;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * 加载 UserDetails 后的业务处理
 *
 * @author bojiangzhou 2019/02/27
 */
public interface UserDetailsWrapper {

    void warp(CustomUserDetails details, Long userId, Long tenantId, boolean login);

    /**
     * 只更新角色信息
     */
    default void warpRoleInfo(CustomUserDetails details, Long roleId) {
    }
}
