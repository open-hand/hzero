package org.hzero.boot.oauth.domain.repository;

import java.util.List;

import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.mybatis.base.BaseRepository;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
public interface BasePasswordPolicyRepository extends BaseRepository<BasePasswordPolicy> {

    /**
     * 查询租户的密码策略
     *
     * @param tenantId 租户ID
     */
    BasePasswordPolicy selectPasswordPolicy(Long tenantId);

    /**
     * 缓存密码策略
     * @param basePasswordPolicy 密码策略
     */
    void savePasswordPolicy(BasePasswordPolicy basePasswordPolicy);

    /**
     * 批量缓存密码策略
     */
    void batchSavePasswordPolicy(List<BasePasswordPolicy> basePasswordPolicyList);

}
