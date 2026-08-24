package org.hzero.admin.infra.repository.impl;

import com.alibaba.fastjson.JSON;
import org.hzero.admin.domain.repository.MaintainServiceRepository;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Properties;

/**
 * @author XCXCXCXCX
 * @date 2020/6/5 11:51 上午
 */
@Repository
public class RedisMaintainServiceRepository implements MaintainServiceRepository {

    private static final String REDIS_KEY_PREFIX = HZeroService.Admin.CODE + ":service-maintain-rules:";

    @Autowired
    private RedisHelper redisHelper;

    @Override
    public void put(String serviceName, Properties properties) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.Admin.REDIS_DB);
            redisHelper.strSet(REDIS_KEY_PREFIX + serviceName, serialize(properties));
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    @Override
    public void remove(String serviceName) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.Admin.REDIS_DB);
            redisHelper.delKey(REDIS_KEY_PREFIX + serviceName);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    @Override
    public Properties getConfig(String serviceName) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.Admin.REDIS_DB);
            return deserialize(redisHelper.strGet(REDIS_KEY_PREFIX + serviceName));
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    private String serialize(Properties properties) {
        return JSON.toJSONString(properties);
    }

    private Properties deserialize(String json) {
        return JSON.parseObject(json, Properties.class);
    }

}
