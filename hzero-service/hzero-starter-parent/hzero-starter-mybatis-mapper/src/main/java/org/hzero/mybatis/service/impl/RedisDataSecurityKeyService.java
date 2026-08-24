package org.hzero.mybatis.service.impl;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.service.DataSecurityKeyService;
import org.springframework.util.StringUtils;

/**
 * @author 废柴 2020/9/2 10:23
 */
public class RedisDataSecurityKeyService implements DataSecurityKeyService {
    private static final String CACHE_KEY = "mybatis:data-security:key";
    private static final String DEFAULT_SECURITY_KEY = "RGarqXE1wpAnW6V5hQs0Lg==";
    private RedisHelper redisHelper;
    private volatile String securityKey;

    public RedisDataSecurityKeyService(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public synchronized void storeSecurityKey(String securityKey, boolean shared) {
        if (StringUtils.isEmpty(securityKey)) {
            return;
        }
        this.securityKey = securityKey;
        if (shared) {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            redisHelper.strSet(CACHE_KEY, securityKey);
            redisHelper.clearCurrentDatabase();
        }
    }

    @Override
    public String readSecurityKey() {
        if (StringUtils.isEmpty(securityKey)) {
            synchronized (this) {
                if (StringUtils.isEmpty(securityKey)) {
                    redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
                    securityKey = redisHelper.strGet(CACHE_KEY);
                    redisHelper.clearCurrentDatabase();
                    if (StringUtils.isEmpty(securityKey)) {
                        securityKey = DEFAULT_SECURITY_KEY;
                    }
                }
            }
        }
        return securityKey;
    }
}
