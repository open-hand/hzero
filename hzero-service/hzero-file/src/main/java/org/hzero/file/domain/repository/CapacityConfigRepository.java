package org.hzero.file.domain.repository;

import org.hzero.file.domain.entity.CapacityConfig;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 文件容量配置资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
public interface CapacityConfigRepository extends BaseRepository<CapacityConfig> {

    /**
     * 查询头
     *
     * @param tenantId 租户Id
     * @return 头
     */
    CapacityConfig selectCapacityConfig(Long tenantId);
}
