package org.hzero.file.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.StorageConfig;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 文件存储配置Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-06-29 12:58:25
 */
public interface StorageConfigMapper extends BaseMapper<StorageConfig> {

    /**
     * 重置默认存储配置
     *
     * @param tenantId 租户Id
     */
    void resetDefaultStorageConfig(@Param("tenantId") Long tenantId);

    /**
     * 查询
     *
     * @param tenantId    租户Id
     * @param storageCode 存储编码
     * @return 配置列表
     */
    List<StorageConfig> selectByUnique(@Param("tenantId") Long tenantId,
                                       @Param("storageCode") String storageCode);
}
