package org.hzero.boot.platform.permission.tenant;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * <p>
 * 临时可访问租户
 * </p>
 *
 * @author qingsheng.chen 2019/2/26 星期二 10:40
 */
public class TemporaryTenantHelper {
    private static final Logger logger = LoggerFactory.getLogger(TemporaryTenantHelper.class);
    private final String keyPrefix;
    private RedisHelper redisHelper;
    private TemporaryTenantProperties temporaryTenantProperties;
    private static final long NOT_LOGIN = Long.MIN_VALUE;

    public TemporaryTenantHelper(String serviceName, RedisHelper redisHelper, TemporaryTenantProperties temporaryTenantProperties) {
        keyPrefix = serviceName + ":TEMPORARY_TENANT:";
        this.redisHelper = redisHelper;
        this.temporaryTenantProperties = temporaryTenantProperties;
    }

    private static long getCurrentUserId() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null || customUserDetails.getUserId() == null) {
            return NOT_LOGIN;
        }
        return customUserDetails.getUserId();
    }

    /**
     * 临时可访问租户
     *
     * @param tenantId 租户ID
     * @see DetailsHelper 当前用户
     * @see TemporaryTenantProperties 配置默认超时时间
     */
    public void allowAccess(long tenantId) {
        allowAccess(getCurrentUserId(), tenantId, temporaryTenantProperties.getTimeout(), temporaryTenantProperties.getTimeUnit());
    }

    /**
     * 临时可访问租户，取默认超时时间
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @see TemporaryTenantProperties 配置默认超时时间
     */
    public void allowAccess(long userId, long tenantId) {
        allowAccess(userId, tenantId, temporaryTenantProperties.getTimeout(), temporaryTenantProperties.getTimeUnit());
    }

    /**
     * 临时可访问租户
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @param timeout  超时限制
     * @param timeUnit 超时单位
     */
    public void allowAccess(long userId, long tenantId, long timeout, TimeUnit timeUnit) {
        allowAccess(userId, Collections.singleton(tenantId), timeout, timeUnit);
    }

    /**
     * 临时可访问租户
     *
     * @param tenantIds 租户ID列表
     * @see DetailsHelper 当前用户
     * @see TemporaryTenantProperties 配置默认超时时间
     */
    public void allowAccess(Collection<Long> tenantIds) {
        allowAccess(getCurrentUserId(), tenantIds, temporaryTenantProperties.getTimeout(), temporaryTenantProperties.getTimeUnit());
    }

    /**
     * 临时可访问租户
     *
     * @param userId    用户ID
     * @param tenantIds 租户ID列表
     * @see TemporaryTenantProperties 配置默认超时时间
     */
    public void allowAccess(long userId, Collection<Long> tenantIds) {
        allowAccess(userId, tenantIds, temporaryTenantProperties.getTimeout(), temporaryTenantProperties.getTimeUnit());
    }

    /**
     * 临时可访问租户
     *
     * @param userId    用户ID
     * @param tenantIds 租户ID列表
     * @param timeout   超时限制
     * @param timeUnit  超时单位
     */
    public void allowAccess(long userId, Collection<Long> tenantIds, long timeout, TimeUnit timeUnit) {
        if (userId == NOT_LOGIN || CollectionUtils.isEmpty(tenantIds)) {
            logger.error("Temporary accessible tenant assignment failed because the user is not logged in or assigned an empty list.");
            return;
        }
        tenantIds.forEach(tenantId -> {
            String key = getKey(userId, tenantId);
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            redisHelper.strSet(key, timeout + timeUnit.name(), timeout, timeUnit);
            redisHelper.clearCurrentDatabase();
        });
    }

    private String getKey(long userId, long tenantId) {
        return keyPrefix + userId + ":" + tenantId;
    }

    /**
     * 判断用户是否有访问该租户的临时权限
     *
     * @param tenantId 租户ID
     * @return 是否用户权限访问
     */
    public boolean isAllowedAccess(long tenantId) {
        return isAllowedAccess(getCurrentUserId(), tenantId);
    }

    /**
     * 判断用户是否有访问该租户的临时权限
     *
     * @param userId   用户ID
     * @param tenantId 租户ID
     * @return 是否用户权限访问
     */
    public boolean isAllowedAccess(long userId, long tenantId) {
        if (userId == NOT_LOGIN) {
            return false;
        }
        String key = getKey(userId, tenantId);
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String value = redisHelper.strGet(key);
        redisHelper.clearCurrentDatabase();
        return StringUtils.hasText(value);
    }
}
