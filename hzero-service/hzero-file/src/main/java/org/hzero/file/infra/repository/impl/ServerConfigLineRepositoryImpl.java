package org.hzero.file.infra.repository.impl;

import java.util.List;

import org.hzero.file.domain.entity.ServerConfigLine;
import org.hzero.file.domain.repository.ServerConfigLineRepository;
import org.hzero.file.domain.vo.ServerVO;
import org.hzero.file.infra.mapper.ServerConfigLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务器上传配置明细 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-04 10:10:21
 */
@Component
public class ServerConfigLineRepositoryImpl extends BaseRepositoryImpl<ServerConfigLine> implements ServerConfigLineRepository {

    @Autowired
    private ServerConfigLineMapper serverConfigLineMapper;

    @Override
    public List<ServerConfigLine> listConfigLineWithServer(Long configId) {
        return serverConfigLineMapper.listConfigLineWithServer(configId);
    }

    @Override
    public ServerConfigLine getConfigLineWithServer(Long configLineId) {
        return serverConfigLineMapper.getConfigLineWithServer(configLineId);
    }

    @Override
    public List<ServerConfigLine> listConfigLineWithCluster(Long configId) {
        return serverConfigLineMapper.listConfigLineWithCluster(configId);
    }

    @Override
    public ServerConfigLine getConfigLineWithCluster(Long configLineId) {
        return serverConfigLineMapper.getConfigLineWithCluster(configLineId);
    }

    @Override
    public ServerVO getServer(String serverCode, Long tenantId) {
        return serverConfigLineMapper.getServer(serverCode, tenantId);
    }
}
