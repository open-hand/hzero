package org.hzero.platform.infra.repository.impl;

import java.util.List;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.Server;
import org.hzero.platform.domain.repository.ServerRepository;
import org.hzero.platform.infra.mapper.ServerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertHelper;

/**
 * 文件服务器 资源库实现
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Component
public class ServerRepositoryImpl extends BaseRepositoryImpl<Server> implements ServerRepository {

    @Autowired
    private ServerMapper serverMapper;

    @Override
    public List<Server> selectDTOAll() {
        List<Server> fileServerList = serverMapper.selectAll();
        return ConvertHelper.convertList(fileServerList, Server.class);
    }

    @Override
    public List<Server> fetchCanAssignFileServers(Long tenantId,Long clusterId,Server server) {
        return serverMapper.fetchCanAssignFileServers(tenantId,clusterId,server.getServerName(),server.getServerDescription());
    }

    @Override
    public List<Server> selectAllWithClusterInfo(Server server) {
        return serverMapper.selectAllWithClusterInfo(server);
    }

    @Override
    public List<Server> findCluster(Long clusterId) {
        return serverMapper.findCluster(clusterId);
    }

    @Override
    public List<Server> selectServerList(Server server) {
        return serverMapper.selectServerList(server);
    }

    @Override
    public List<Server> selectByServerIdList(List<Long> serverIds) {
        return serverMapper.selectByServerIdList(serverIds);
    }

    @Override
    public List<Server> selectByClusterIdList(List<Long> clusterIds) {
        return serverMapper.selectByClusterIdList(clusterIds);
    }

    @Override
    public Server selectByServerId(Long serverId, Long tenantId) {
        return serverMapper.selectByServerId(serverId, tenantId);
    }
  
}
