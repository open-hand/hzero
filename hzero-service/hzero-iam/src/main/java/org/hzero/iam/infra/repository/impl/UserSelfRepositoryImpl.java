package org.hzero.iam.infra.repository.impl;

import java.util.concurrent.TimeUnit;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.iam.domain.repository.UserSelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description
 *
 * @author fanghan.liu 2020/06/03 16:42
 */
@Component
public class UserSelfRepositoryImpl implements UserSelfRepository {

    private static final String MODIFY_PASSWORD_ERROR_TIMES_KEY = HZeroService.Iam.CODE + ":pass_error_times:";

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public long increaseErrorTimes(Long userId) {
        long times = redisHelper.strIncrement(MODIFY_PASSWORD_ERROR_TIMES_KEY + userId, 1L);
        redisHelper.setExpire(MODIFY_PASSWORD_ERROR_TIMES_KEY + userId, 24, TimeUnit.HOURS);
        return times;
    }

    @Override
    public void clearErrorTimes(Long userId) {
        redisHelper.delKey(MODIFY_PASSWORD_ERROR_TIMES_KEY + userId);
    }
}
