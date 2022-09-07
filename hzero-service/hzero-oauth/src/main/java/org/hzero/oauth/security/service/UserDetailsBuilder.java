package org.hzero.oauth.security.service;

import io.choerodon.core.oauth.CustomUserDetails;

import org.hzero.oauth.domain.entity.User;

/**
 * 构建 CustomUserDetails
 *
 * @author bojiangzhou 2019/07/24
 */
public interface UserDetailsBuilder {

    /**
     * 构建 CustomUserDetails
     *
     * @param user User
     * @return CustomUserDetails
     */
    CustomUserDetails buildUserDetails(User user);

    /**
     * 构建 CustomUserDetails
     *
     * @param user     User
     * @param tenantId 当前租户ID
     * @return CustomUserDetails
     */
    CustomUserDetails buildUserDetails(User user, Long tenantId);

}
