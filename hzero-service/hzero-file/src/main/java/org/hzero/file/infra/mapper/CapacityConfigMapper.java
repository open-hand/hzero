package org.hzero.file.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.CapacityConfig;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 文件容量配置Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
public interface CapacityConfigMapper extends BaseMapper<CapacityConfig> {

    /**
     * 查询头
     *
     * @param tenantId 租户Id
     * @return 配置列表
     */
    CapacityConfig selectByTenantId(@Param("tenantId") Long tenantId);
}
