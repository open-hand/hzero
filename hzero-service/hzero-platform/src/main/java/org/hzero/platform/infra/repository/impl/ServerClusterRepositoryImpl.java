package org.hzero.platform.infra.repository.impl;

import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.ServerCluster;
import org.hzero.platform.domain.repository.ServerClusterRepository;
import org.hzero.platform.infra.mapper.ServerClusterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务器集群设置表 资源库实现
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Component
public class ServerClusterRepositoryImpl extends BaseRepositoryImpl<ServerCluster> implements ServerClusterRepository {
    
    @Autowired
    ServerClusterMapper serverClusterMapper;

    @Override
    public List<ServerCluster> selectClusterList(ServerCluster serverCluster) {
        return serverClusterMapper.selectClusterList(serverCluster);
    }

  
}
