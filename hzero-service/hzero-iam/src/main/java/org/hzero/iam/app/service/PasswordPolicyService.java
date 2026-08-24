package org.hzero.iam.app.service;

import org.hzero.iam.domain.entity.PasswordPolicy;

/**
 * 密码策略服务
 *
 * @author bojiangzhou 2019/08/05
 */
public interface PasswordPolicyService {

    /**
     * 创建租户密码策略
     * 
     * @param tenantId 租户ID
     * @param passwordPolicy 密码策略
     */
    PasswordPolicy createPasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy);

    /**
     * 创建租户密码策略
     *
     * @param tenantId 租户ID
     * @param passwordPolicy 密码策略
     */
    PasswordPolicy updatePasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy);


}
