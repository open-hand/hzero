package org.hzero.iam.app.service;

/**
 * 租户访问审计应用服务
 *
 * @author qingsheng.chen@hand-china.com 2019-03-06 13:53:40
 */
public interface TenantAccessService {

    /**
     * 存储用户访问租户信息
     * @param userId 用户ID
     * @param tenantId 租户ID
     */
    void storeUserTenant(long userId, Long tenantId);
}
