package org.hzero.file.infra.repository.impl;

import org.hzero.file.domain.entity.ServerConfig;
import org.hzero.file.domain.repository.ServerConfigRepository;
import org.hzero.file.infra.mapper.ServerConfigMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 服务器上传配置 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-03 20:38:55
 */
@Component
public class ServerConfigRepositoryImpl extends BaseRepositoryImpl<ServerConfig> implements ServerConfigRepository {

    @Autowired
    private ServerConfigMapper serverConfigMapper;

    @Override
    public Page<ServerConfig> pageConfig(PageRequest pageRequest, Long tenantId, String configCode, String description, Integer enabledFlag) {
        return PageHelper.doPageAndSort(pageRequest, () -> serverConfigMapper.listConfig(tenantId, configCode, description, enabledFlag));
    }

    @Override
    public ServerConfig detailConfig(Long tenantId, Long configId) {
        return serverConfigMapper.detailConfig(tenantId, configId);
    }
}
