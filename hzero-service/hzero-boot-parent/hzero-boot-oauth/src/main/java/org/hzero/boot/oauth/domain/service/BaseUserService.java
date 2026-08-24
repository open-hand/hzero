package org.hzero.boot.oauth.domain.service;

public interface BaseUserService {

    /**
     * 锁定用户
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void lockUser(Long userId, Long tenantId);

    /**
     * 解锁用户
     * 
     * @param userId 用户ID
     */
    void unLockUser(Long userId, Long tenantId);

}
