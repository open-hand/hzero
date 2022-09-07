package org.hzero.boot.oauth.infra.repository.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.hzero.boot.oauth.domain.entity.BaseLdap;
import org.hzero.boot.oauth.domain.repository.BaseLdapRepository;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
@Component
public class BaseLdapRepositoryImpl extends BaseRepositoryImpl<BaseLdap> implements BaseLdapRepository {

    private static final String LDAP_KEY = HZeroService.Oauth.CODE + ":ldap";

    private final RedisHelper redisHelper;

    public BaseLdapRepositoryImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public BaseLdap selectLdap(Long tenantId) {
        Assert.notNull(tenantId, "tenantId not be null.");

        String str = SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB,
                () -> redisHelper.hshGet(LDAP_KEY, tenantId.toString()));
        BaseLdap ldap = null;
        if (StringUtils.isNotBlank(str)) {
            ldap = redisHelper.fromJson(str, BaseLdap.class);
        } else {
            BaseLdap param = new BaseLdap();
            param.setOrganizationId(tenantId);
            ldap = this.selectOne(param);
            if (ldap != null) {
                BaseLdap finalLdap = ldap;
                SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB,
                        () -> redisHelper.hshPut(LDAP_KEY, tenantId.toString(), redisHelper.toJson(finalLdap)));
            }
        }
        return Optional.ofNullable(ldap).orElseThrow(() -> new AuthenticationServiceException("hoth.warn.password.ldap.notFound"));
    }

    @Override
    public void saveLdap(BaseLdap ldap) {
        Assert.notNull(ldap, "ldap not be null.");
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper,
                () -> redisHelper.hshPut(LDAP_KEY, ldap.getOrganizationId().toString(), redisHelper.toJson(ldap)));
    }

    @Override
    public void deleteLdap(BaseLdap ldap) {
        Assert.notNull(ldap, "ldap not be null.");
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper,
                () -> redisHelper.hshDelete(LDAP_KEY, ldap.getOrganizationId().toString()));
    }
}
