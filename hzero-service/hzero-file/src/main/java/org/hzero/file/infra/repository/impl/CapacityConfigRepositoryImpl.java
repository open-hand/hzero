package org.hzero.file.infra.repository.impl;

import org.hzero.file.domain.entity.CapacityConfig;
import org.hzero.file.domain.repository.CapacityConfigRepository;
import org.hzero.file.infra.mapper.CapacityConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件容量配置 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
@Component
public class CapacityConfigRepositoryImpl extends BaseRepositoryImpl<CapacityConfig> implements CapacityConfigRepository {

    @Autowired
    private CapacityConfigMapper capacityConfigMapper;

    @Override
    public CapacityConfig selectCapacityConfig(Long tenantId) {
        return capacityConfigMapper.selectByTenantId(tenantId);
    }
}
