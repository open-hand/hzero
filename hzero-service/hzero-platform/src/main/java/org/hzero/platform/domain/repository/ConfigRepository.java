package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Config;

/**
 * <p>
 * 系统配置repository
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/19 11:50
 */
public interface ConfigRepository extends BaseRepository<Config> {

    /**
     * 批量更新，并向消息队列中发送更新信息
     *
     * @param configList 系统配置list
     * @return 系统配置list
     */
    List<Config> batchUpdate(List<Config> configList);

    /**
     * 启动时刷新缓存
     */
    void initAllConfigToRedis();

    /**
     * 根据租户id查询租户级的配置，如果没有配置则引用平台级的配置，如果有则采用自己的
     *
     * @param tenantId 租户id
     * @return 系统配置list
     */
    List<Config> selectConfigByTenantId(Long tenantId);

    /**
     * 新增或者更新租户级系统配置
     *
     * @param configList 配置列表
     * @param tenantId   租户Id
     * @return 配置
     */
    List<Config> insertOrUpdateConfig(List<Config> configList, Long tenantId);
}
