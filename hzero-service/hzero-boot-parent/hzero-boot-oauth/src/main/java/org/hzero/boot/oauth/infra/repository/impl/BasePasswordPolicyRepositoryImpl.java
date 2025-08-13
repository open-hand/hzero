package org.hzero.boot.oauth.infra.repository.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
@Component
public class BasePasswordPolicyRepositoryImpl extends BaseRepositoryImpl<BasePasswordPolicy> implements BasePasswordPolicyRepository {

    private static final String PASSWORD_POLICY_KEY = HZeroService.Oauth.CODE + ":password_policy";

    private final RedisHelper redisHelper;

    public BasePasswordPolicyRepositoryImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public BasePasswordPolicy selectPasswordPolicy(Long tenantId) {
        Assert.notNull(tenantId, "tenantId not be null.");

        String str = SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB,
                () -> redisHelper.hshGet(PASSWORD_POLICY_KEY, tenantId.toString()));
        BasePasswordPolicy passwordPolicy;
        if (StringUtils.isNotBlank(str)) {
            passwordPolicy = redisHelper.fromJson(str, BasePasswordPolicy.class);
        } else {
            BasePasswordPolicy param = new BasePasswordPolicy();
            param.setOrganizationId(tenantId);
            passwordPolicy = this.selectOne(param);
            if (passwordPolicy != null) {
                savePasswordPolicy(passwordPolicy);
            } else {
                // 取不到默认取平台
                str = SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB,
                        () -> redisHelper.hshGet(PASSWORD_POLICY_KEY, BaseConstants.DEFAULT_TENANT_ID.toString()));
                passwordPolicy = redisHelper.fromJson(str, BasePasswordPolicy.class);
            }
        }
        return Optional.ofNullable(passwordPolicy).orElseThrow(() -> new AuthenticationServiceException("hoth.warn.password.passwordPolicy.notFound"));
    }

    @Override
    public void savePasswordPolicy(BasePasswordPolicy basePasswordPolicy) {
        Assert.notNull(basePasswordPolicy, "basePasswordPolicy not be null.");
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper,
                () -> redisHelper.hshPut(PASSWORD_POLICY_KEY, basePasswordPolicy.getOrganizationId().toString(), redisHelper.toJson(basePasswordPolicy)));
    }

    @Override
    public void batchSavePasswordPolicy(List<BasePasswordPolicy> basePasswordPolicyList) {
        if (CollectionUtils.isEmpty(basePasswordPolicyList)) {
            return;
        }

        Map<String, String> map = basePasswordPolicyList.stream()
                .collect(Collectors.toMap(pp -> pp.getOrganizationId().toString(), redisHelper::toJson));

        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper,
                () -> redisHelper.hshPutAll(PASSWORD_POLICY_KEY, map));
    }
}
