package org.hzero.boot.imported.infra.redis;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;

/**
 * 缓存用于标识模板是否被更新
 * bei更新的模板需要feign查询同步到本地
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/08 19:32
 */
public class TemplateRedis {

    private TemplateRedis() {
    }

    /**
     * 生成redis存储key
     */
    private static String getCacheKey(Long tenantId, String templateCode) {
        return HZeroService.Import.CODE + ":template:" + templateCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     */
    public static void refreshCache(RedisHelper redisHelper, Long tenantId, String templateCode, String templateString) {
        redisHelper.strSet(getCacheKey(tenantId, templateCode), templateString);
    }

    /**
     * 查询缓存
     */
    public static Template getCache(RedisHelper redisHelper, Long tenantId, String templateCode) {
        String result = redisHelper.strGet(getCacheKey(tenantId, templateCode));
        if (StringUtils.isBlank(result)) {
            return null;
        }
        return redisHelper.fromJson(result, Template.class);
    }

    /**
     * 清除缓存
     */
    public static void clearCache(RedisHelper redisHelper, Long tenantId, String templateCode) {
        redisHelper.delKey(getCacheKey(tenantId, templateCode));
    }
}
