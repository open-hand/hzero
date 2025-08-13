package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.vo.PasswordPolicyVO;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 密码策略资源库
 *
 * @author bojiangzhou 2019/08/05
 */
public interface PasswordPolicyRepository extends BaseRepository<PasswordPolicy> {

    /**
     * 查询租户密码策略
     *
     * @param tenantId 租户ID
     * @return 租户密码策略
     */
    PasswordPolicy selectTenantPasswordPolicy(Long tenantId);

    /**
     * 初始化缓存缓存密码策略
     */
    void initCachePasswordPolicy();

    /**
     *密码策略缓存
     */
    void cachePasswordPolicy(PasswordPolicy passwordPolicy);

    /**
     * 查询密码策略
     *
     * @param tenantId 租户ID
     * @return 密码策略视图对象
     */
    PasswordPolicyVO queryPasswordPolicy(Long tenantId);
}
