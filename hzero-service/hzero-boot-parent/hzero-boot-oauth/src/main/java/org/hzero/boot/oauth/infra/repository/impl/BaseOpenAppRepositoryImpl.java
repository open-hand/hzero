package org.hzero.boot.oauth.infra.repository.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.oauth.domain.entity.BaseOpenApp;
import org.hzero.boot.oauth.domain.repository.BaseOpenAppRepository;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 *
 * @author xiaoyu.zhao@hand-china.com 2019/08/28 9:08
 */
@Component
public class BaseOpenAppRepositoryImpl extends BaseRepositoryImpl<BaseOpenApp> implements BaseOpenAppRepository {
    /**
     * 登录方式缓存key
     */
    private static final String CACHE_OPEN_LOGIN_WAY = HZeroService.Oauth.CODE+":open-login-way:";
    private static final String CHANNEL_NOT_NULL = "open-app.channel.not-null";
    private static final String APP_CODE_NOT_NULL = "open-app.app-code.not-null";
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOpenAppRepositoryImpl.class);

    private final RedisHelper redisHelper;

    public BaseOpenAppRepositoryImpl(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public void saveOpenApp(BaseOpenApp baseOpenApp) {
        Assert.notNull(baseOpenApp.getChannel(), CHANNEL_NOT_NULL);
        String cacheKey = StringUtils.join(CACHE_OPEN_LOGIN_WAY, baseOpenApp.getChannel());
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper, () ->
                redisHelper.hshPut(cacheKey, baseOpenApp.getAppCode(), redisHelper.toJson(baseOpenApp)));
    }

    @Override
    public void removeOpenApp(String channel, String appCode) {
        Assert.notNull(channel, CHANNEL_NOT_NULL);
        Assert.notNull(appCode, APP_CODE_NOT_NULL);
        String cacheKey = StringUtils.join(CACHE_OPEN_LOGIN_WAY, channel);
        SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper, () -> redisHelper.hshDelete(cacheKey, appCode));
    }

    @Override
    public BaseOpenApp getOpenApp(String channel, String appCode) {
        if (StringUtils.isAnyBlank(channel, appCode)) {
            return null;
        }
        String cacheKey = StringUtils.join(CACHE_OPEN_LOGIN_WAY, channel);
        return SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper, () -> {
            String redisValue = redisHelper.hshGet(cacheKey, appCode);
            if (redisValue != null) {
                BaseOpenApp baseOpenApp = redisHelper.fromJson(redisValue, BaseOpenApp.class);
                // 将 appKey 解密返回
                baseOpenApp.setAppKey(decryptAppKey(baseOpenApp.getAppKey()));
                return baseOpenApp;
            } else {
                return null;
            }
        });
    }

    @Override
    public List<BaseOpenApp> getOpenApps(String channel) {
        if (StringUtils.isAnyBlank(channel)) {
            return null;
        }
        String cacheKey = StringUtils.join(CACHE_OPEN_LOGIN_WAY, channel);
        return SafeRedisHelper.execute(HZeroService.Oauth.REDIS_DB, redisHelper, () -> {
            List<String> redisValueList = redisHelper.hshVals(cacheKey);
            List<BaseOpenApp> resultList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(redisValueList)) {
                redisValueList.forEach(redisValue -> {
                    BaseOpenApp baseOpenApp = redisHelper.fromJson(redisValue, BaseOpenApp.class);
                    // 将 appKey 解密返回
                    baseOpenApp.setAppKey(decryptAppKey(baseOpenApp.getAppKey()));
                    resultList.add(baseOpenApp);
                });
            }
            return resultList.stream().sorted(Comparator.comparing(BaseOpenApp::getOrderSeq))
                    .collect(Collectors.toList());
        });
    }

    /**
     * 解密授权码参数
     *
     * @param encryptAppKey 加密后的appKey
     * @return 解密后的授权码
     */
    private String decryptAppKey(String encryptAppKey) {
        try{
            // 返回解密后的参数
            return DataSecurityHelper.decrypt(encryptAppKey);
        }catch(Exception e) {
            LOGGER.info("appKey is already decrypted and does not need to be decrypted again!");
        }
        // 原有的参数就是解密的，返回原有参数即可
        return encryptAppKey;
    }
}
