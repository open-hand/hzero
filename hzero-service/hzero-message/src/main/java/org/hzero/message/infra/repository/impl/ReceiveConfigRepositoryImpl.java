package org.hzero.message.infra.repository.impl;

import org.hzero.message.api.dto.ReceiveConfigDTO;
import org.hzero.message.domain.entity.ReceiveConfig;
import org.hzero.message.domain.repository.ReceiveConfigRepository;
import org.hzero.message.infra.mapper.ReceiveConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 接收配置 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2018-11-19 20:42:53
 */
@Component
public class ReceiveConfigRepositoryImpl extends BaseRepositoryImpl<ReceiveConfig> implements ReceiveConfigRepository {

    private ReceiveConfigMapper receiveConfigMapper;

    @Autowired
    public ReceiveConfigRepositoryImpl(ReceiveConfigMapper receiveConfigMapper) {
        this.receiveConfigMapper = receiveConfigMapper;
    }

    @Override
    public List<ReceiveConfigDTO> listConfig(Long tenantId) {
        return receiveConfigMapper.listConfig(tenantId);
    }

    @Override
    public List<ReceiveConfigDTO> listParentReceive(String parentReceiveCode, Long tenantId) {
        return receiveConfigMapper.listParentReceive(parentReceiveCode, tenantId);
    }

}
