package org.hzero.boot.platform.entity.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hzero.boot.platform.entity.dto.EntityTableDTO;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;

/**
 * entity Redis帮助类
 *
 * @author xingxing.wu@hand-china.com 2019/07/10 16:18
 */
public class EntityRedisService {
    /**
     * 数据配置缓存key前缀
     */
    public static final String DATA_MONITOR_CONFIG_PREFIX = HZeroService.Platform.CODE + ":entity";

    /**
     * 构造catchKey
     *
     * @param serverName 服务名称
     * @return
     */
    public static String buildCatchKey(String serverName) {
        return DATA_MONITOR_CONFIG_PREFIX + ":" + serverName;
    }

    /**
     * 缓存中获取EntityTable对象
     *
     * @param serverName  服务名称
     * @param tableName   表名
     * @param redisHelper redisHelper
     * @return EntityTable对象
     */
    public static EntityTableDTO getEntityTableFromRedis(String serverName, String tableName, RedisHelper redisHelper) {
        try {
            redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
            String catchKey = buildCatchKey(serverName);
            return redisHelper.fromJson(redisHelper.hshGet(catchKey, tableName), EntityTableDTO.class);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * @param serverName    服务名称
     * @param tableNameList 表名List
     * @param redisHelper   redisHelper
     */
    public static void deleteEntityTableFromRedis(String serverName, List<String> tableNameList, RedisHelper redisHelper) {
        if (CollectionUtils.isEmpty(tableNameList)) {
            return;
        }
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String catchKey = buildCatchKey(serverName);
        for (String tableName : tableNameList) {
            redisHelper.hshDelete(catchKey, tableName);
        }
        redisHelper.clearCurrentDatabase();
    }

    /**
     * 缓存中获取当前服务下所有的EntityTable对象
     *
     * @param serverName  服务名称
     * @param redisHelper redisHelper
     * @return EntityTable对象
     */
    public static Map<String, EntityTableDTO> getCurrencyServiceEntityTableMapFromRedis(String serverName, RedisHelper redisHelper) {
        Map<String, EntityTableDTO> result = new HashMap<>();
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String catchKey = buildCatchKey(serverName);

        Map<String, String> entityTableMap = redisHelper.hshGetAll(catchKey);
        if (MapUtils.isEmpty(entityTableMap)) {
            return result;
        }

        for (Map.Entry<String, String> entry : entityTableMap.entrySet()) {
            result.put(entry.getKey(), redisHelper.fromJson(entry.getValue(), EntityTableDTO.class));
        }
        redisHelper.clearCurrentDatabase();
        return result;

    }

    /**
     * 添加entityTable对象到缓存
     *
     * @param serverName  服务名称
     * @param tableName   表名
     * @param redisHelper redisHelper
     */
    public static void addEntityTableToRedis(String serverName, String tableName, RedisHelper redisHelper, String entityTableJson) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String catchKey = buildCatchKey(serverName);
        redisHelper.hshPut(catchKey, tableName, entityTableJson);
        redisHelper.clearCurrentDatabase();
    }

    /**
     * 添加entityTable对象到缓存
     *
     * @param serverName  服务名称
     * @param redisHelper redisHelper
     */
    public static void addEntityTableToRedis(String serverName, RedisHelper redisHelper, Map<String, String> entityTableJsonMap) {
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        entityTableJsonMap.forEach((tableName, value) -> {
            String catchKey = buildCatchKey(serverName);
            redisHelper.hshPut(catchKey, tableName, value);
        });
        redisHelper.clearCurrentDatabase();
    }
}
