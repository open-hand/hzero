package org.hzero.file.app.service;

/**
 * 租户剩余容量缓存控制
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/27 9:31
 */
public interface CapacityUsedService {

    /**
     * 生成redis存储key
     *
     * @param tenantId 租户Id
     * @return key
     */
    String getCacheKey(Long tenantId);

    /**
     * 初始化缓存
     *
     * @param tenantId 租户Id
     * @param capacity 租户容量
     */
    void initCache(Long tenantId, Long capacity);

    /**
     * 刷新缓存
     *
     * @param tenantId 租户Id
     * @param fileSize 文件大小单位B
     * @return 已使用大小
     */
    Long refreshCache(Long tenantId, Long fileSize);

    /**
     * 查询缓存
     *
     * @param tenantId 租户Id
     * @return 已使用大小
     */
    Long getCache(Long tenantId);

    /**
     * 清除缓存
     *
     * @param tenantId 租户Id
     */
    void clearRedisCache(Long tenantId);

    /**
     * 启动服务写入租户已使用容量
     */
    void initUsedRedis();

}
