package org.hzero.sso.core.domain.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.domain.DomainRepository;

/**
 * Domain 资源库
 *
 * @author bojiangzhou 2020/08/18
 */
public class DefaultDomainRepository implements DomainRepository {

    private static final String DOMAIN_PREFIX = HZeroService.Oauth.CODE + ":domain";

    private static final Domain NULL = new Domain();

    private final Cache<String, Domain> cache;

    private final RedisHelper redisHelper;

    public DefaultDomainRepository(RedisHelper redisHelper, SsoProperties ssoProperties) {
        this.redisHelper = redisHelper;

        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder()
                .initialCapacity(64)
                .concurrencyLevel(4)
                .softValues();

        if (ssoProperties.getDomainCacheTimeout() > 0) {
            builder.expireAfterWrite(ssoProperties.getDomainCacheTimeout(), TimeUnit.SECONDS);
        }

        this.cache = builder.build();
    }

    @Nullable
    @Override
    public Domain selectByHost(String host) {
        if (StringUtils.isBlank(host)) {
            return null;
        }
        Domain domain = null;
        try {
            domain = cache.getIfPresent(host);
            if (domain != null) {
                if (NULL == domain) {
                    return null;
                }
                return domain;
            } else {
                cache.invalidate(host);
            }

            domain = cache.get(host, () -> {
                String str = SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, () -> redisHelper.hshGet(DOMAIN_PREFIX, host));
                Domain obj = redisHelper.fromJson(str, Domain.class);
                if (obj != null) {
                    obj.setHost(host);
                } else  {

                    obj = NULL;
                }
                return obj;
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (domain == null || NULL == domain) {
            return null;
        }
        return domain;
    }
}
