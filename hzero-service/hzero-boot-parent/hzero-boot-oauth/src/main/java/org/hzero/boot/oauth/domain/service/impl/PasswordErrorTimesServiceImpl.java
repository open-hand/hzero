package org.hzero.boot.oauth.domain.service.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.boot.oauth.domain.service.PasswordErrorTimesService;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
public class PasswordErrorTimesServiceImpl implements PasswordErrorTimesService {

    private static final String LOGIN_ERROR_TIMES_KEY = HZeroService.Oauth.CODE + ":login_error_times:";

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public long increaseErrorTimes(Long userId) {
        return SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, () -> {
            long times = redisHelper.strIncrement(LOGIN_ERROR_TIMES_KEY + userId, 1L);
            redisHelper.setExpire(LOGIN_ERROR_TIMES_KEY + userId, 24, TimeUnit.HOURS);
            return times;
        });
    }

    @Override
    public void clearErrorTimes(Long userId) {
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, () -> {
            redisHelper.delKey(LOGIN_ERROR_TIMES_KEY + userId);
        });
    }

    @Override
    public long getErrorTimes(Long userId) {
        return SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, () -> {
            String str = redisHelper.strGet(LOGIN_ERROR_TIMES_KEY + userId);
            return StringUtils.isNotBlank(str) ? Long.parseLong(str) : 0L;
        });
    }
}
