package org.hzero.boot.platform.lov.handler.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.feign.LovFeignClient;
import org.hzero.boot.platform.lov.handler.LovSqlGetter;
import org.hzero.common.HZeroCacheKey;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.springframework.util.Assert;

/**
 * 基于缓存和Feign的SQL语句获取器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月28日上午9:53:26
 */
public class CacheAndFeignLovSqlGetter implements LovSqlGetter {

    private final RedisHelper redisHelper;
    private final LovFeignClient lovFeignClient;

    public CacheAndFeignLovSqlGetter(RedisHelper redisHelper, LovFeignClient lovFeignClient) {
        this.redisHelper = redisHelper;
        this.lovFeignClient = lovFeignClient;
    }

    @Override
    public String getCustomSql(String lovCode, Long tenantId, String lang, Boolean publicQuery) {
        Assert.notNull(lovCode, String.format(LovConstants.ErrorMessage.ERROR_NULL, "lov code"));
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        String customSql;
        this.redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        customSql = this.redisHelper.hshGet(HZeroCacheKey.Lov.SQL_KEY_PREFIX + lovCode, String.valueOf(tenantId));
        this.redisHelper.clearCurrentDatabase();
        if (StringUtils.isEmpty(customSql)) {
            if (!publicQuery) {
                customSql = this.lovFeignClient.queryLovSql(lovCode, tenantId);
            } else {
                customSql = this.lovFeignClient.queryLovSql(lovCode, tenantId, lang);
            }
        }
        return customSql;
    }

    @Override
    public String getTranslationSql(String lovCode, Long tenantId, String lang, Boolean publicQuery) {
        Assert.notNull(lovCode, String.format(LovConstants.ErrorMessage.ERROR_NULL, "lov code"));
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        String translationSql;
        this.redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        translationSql = this.redisHelper.hshGet(HZeroCacheKey.Lov.TRANSLATION_SQL_KEY_PREFIX + lovCode, String.valueOf(tenantId));
        this.redisHelper.clearCurrentDatabase();
        if (StringUtils.isEmpty(translationSql)) {
            if (!publicQuery) {
                translationSql = this.lovFeignClient.queryTranslationSql(lovCode, tenantId);
            } else {
                translationSql = this.lovFeignClient.queryTranslationSql(lovCode, tenantId, lang);
            }
        }
        return translationSql;
    }
}
