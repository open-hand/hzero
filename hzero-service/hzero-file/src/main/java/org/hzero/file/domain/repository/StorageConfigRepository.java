package org.hzero.file.domain.repository;

import java.util.List;

import org.hzero.file.domain.entity.StorageConfig;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 文件存储配置资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-06-29 12:58:25
 */
public interface StorageConfigRepository extends BaseRepository<StorageConfig> {

    /**
     * 查询
     *
     * @param tenantId    租户Id
     * @param storageCode 存储编码
     * @return 配置列表
     */
    List<StorageConfig> selectByUnique(Long tenantId, String storageCode);

    /**
     * 获取存储配置
     *
     * @param tenantId 租户Id
     * @return StorageConfig
     */
    StorageConfig getStorageConfig(Long tenantId);

    /**
     * 新增或更新数据
     *
     * @param storageConfig StorageConfig
     */
    void insertOrUpdateStorageConfig(StorageConfig storageConfig);

    /**
     * 查询文件存储配置信息
     *
     * @param record StorageConfig
     * @return StorageConfig
     */
    List<StorageConfig> selectStorageConfig(StorageConfig record);

    /**
     * 删除配置
     *
     * @param config 存储配置
     */
    void deleteConfig(StorageConfig config);
}
