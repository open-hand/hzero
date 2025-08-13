package org.hzero.boot.platform.encrypt.impl;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

import io.choerodon.core.exception.CommonException;

import org.hzero.boot.platform.encrypt.EncryptRepository;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;

/**
 * 加密资源库实现
 *
 * @author bojiangzhou 2019/12/05
 */
public class DefaultEncryptRepository implements EncryptRepository {

    private final RedisHelper redisHelper;

    private static final String PUBLIC_KEY = HZeroService.Platform.CODE + ":encrypt:public-key";
    private static final String PRIVATE_KEY = HZeroService.Platform.CODE + ":encrypt:private-key";

    public DefaultEncryptRepository(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public void savePublicKey(String publicKey) {
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> {
            redisHelper.strSet(PUBLIC_KEY, publicKey);
        });
    }

    @Override
    public void savePrivateKey(String privateKey) {
        SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper, () -> {
            redisHelper.strSet(PRIVATE_KEY, privateKey);
        });
    }

    @Override
    @Nonnull
    public String getPublicKey() {
        String publicKey = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                        () -> redisHelper.strGet(PUBLIC_KEY));

        if (StringUtils.isBlank(publicKey)) {
            throw new CommonException("hpfm.warn.encrypt.publicKeyIsEmpty");
        }

        return publicKey;
    }

    @Override
    @Nonnull
    public String getPrivateKey() {
        String privateKey = SafeRedisHelper.execute(HZeroService.Platform.REDIS_DB, redisHelper,
                        () -> redisHelper.strGet(PRIVATE_KEY));

        if (StringUtils.isBlank(privateKey)) {
            throw new CommonException("hpfm.warn.encrypt.privateKeyIsEmpty");
        }
        return privateKey;
    }
}
