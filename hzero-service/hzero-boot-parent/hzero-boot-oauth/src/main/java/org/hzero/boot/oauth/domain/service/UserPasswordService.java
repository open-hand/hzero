package org.hzero.boot.oauth.domain.service;

/**
 * 用户密码服务
 *
 * @author bojiangzhou 2019/08/06
 */
public interface UserPasswordService {

    /**
     * 更新用户密码
     * 
     * @param userId 用户ID
     * @param newPassword 新密码
     */
    void updateUserPassword(Long userId, String newPassword);

    /**
     * 重置密码用户密码
     *
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void resetUserPassword(Long userId, Long tenantId, boolean ldapUpdatable);

    /**
     * 更新用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param ldapUpdatable Ldap 用户是否可更新密码
     */
    void updateUserPassword(Long userId, String newPassword, boolean ldapUpdatable);

    /**
     * 获取租户密码策略的默认密码
     * 
     * @param tenantId 租户ID
     * @return 默认密码
     */
    String getTenantDefaultPassword(Long tenantId);

}
