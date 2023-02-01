package org.hzero.export.redis;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;

/**
 * 导入模板获取缓存
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/08 19:32
 */
public class ImportTemplateRedis {

    public static final String NO = "$";

    private ImportTemplateRedis() {
    }

    /**
     * 生成redis存储key
     */
    private static String getCacheKey(Long tenantId, String templateCode) {
        return HZeroService.Import.CODE + ":templates:" + templateCode + ":" + tenantId;
    }

    /**
     * 刷新缓存
     */
    public static void refreshCache(RedisHelper redisHelper, Long tenantId, String templateCode, String lang, String templateString) {
        redisHelper.hshPut(getCacheKey(tenantId, templateCode), lang, templateString);
    }

    /**
     * 查询缓存
     */
    public static String getTemplateStr(RedisHelper redisHelper, Long tenantId, String templateCode, String lang) {
        return redisHelper.hshGet(getCacheKey(tenantId, templateCode), lang);
    }

    /**
     * 清除缓存
     */
    public static void clearCache(RedisHelper redisHelper, Long tenantId, String templateCode) {
        redisHelper.delKey(getCacheKey(tenantId, templateCode));
    }
}
