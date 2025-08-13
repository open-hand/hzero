package org.hzero.boot.oauth.infra.repository.impl;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.boot.oauth.domain.repository.BaseClientRepository;
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
public class BaseClientRepositoryImpl extends BaseRepositoryImpl<BaseClient> implements BaseClientRepository {

    private static final String CLIENT_KEY = HZeroService.Oauth.CODE + ":client";

    private final RedisHelper redisHelper;

    public BaseClientRepositoryImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public BaseClient selectClient(String clientName) {
        Assert.notNull(clientName, "clientName not be null.");

        String str = SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB,
                () -> redisHelper.hshGet(CLIENT_KEY, clientName));
        BaseClient client = null;
        if (StringUtils.isNotBlank(str)) {
            client = redisHelper.fromJson(str, BaseClient.class);
        } else {
            BaseClient param = new BaseClient();
            param.setName(clientName);
            client = this.selectOne(param);
            if (client != null) {
                BaseClient finalClient = client;
                SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB,
                        () -> redisHelper.hshPut(CLIENT_KEY, clientName, redisHelper.toJson(finalClient)));
            }
        }
        Assert.notNull(client, "error.client.notFound");
        if (Objects.equals(client.getEnabledFlag(), BaseConstants.Flag.NO)) {
            throw new AuthenticationServiceException("hoth.warn.password.client.disabled");
        }
        return client;
    }

    @Override
    public void saveClient(BaseClient client) {
        Assert.notNull(client, "client not be null.");
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper,
                () -> redisHelper.hshPut(CLIENT_KEY, client.getName(), redisHelper.toJson(client)));
    }

    @Override
    public void removeClient(String clientName) {
        Assert.notNull(clientName, "clientName not be null.");
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB,
                () -> redisHelper.hshDelete(CLIENT_KEY, clientName));
    }
}
