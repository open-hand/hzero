package org.hzero.platform.app.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.platform.app.service.ServerClusterService;
import org.hzero.platform.domain.entity.ServerAssign;
import org.hzero.platform.domain.entity.ServerCluster;
import org.hzero.platform.domain.repository.ServerAssignRepository;
import org.hzero.platform.domain.repository.ServerClusterRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
/**
 * 服务器集群设置表应用服务默认实现
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Service
public class ServerClusterServiceImpl implements ServerClusterService {
    @Autowired
    private ServerClusterRepository serverClusterRepository;
    @Autowired
    private ServerAssignRepository serverAssignRepository;

    @Override
    public Page<ServerCluster> serverClusterList(PageRequest pageRequest, ServerCluster serverCluster) {
        return PageHelper.doPageAndSort(pageRequest, () -> serverClusterRepository.selectClusterList(serverCluster));
    }

    @Override
    public ServerCluster serverClusterById(Long tenantId,Long serverClusterId) {
        ServerCluster dto = new ServerCluster();
        if(tenantId != null){
            dto.setTenantId(tenantId);
        }
        dto.setClusterId(serverClusterId);
        List<ServerCluster> serverClusters = serverClusterRepository.selectClusterList(dto);
        Assert.notNull(serverClusters, String.format("serverCluster not exists with tenantId=%s and serverClusterId=%s", tenantId, serverClusterId));
        return serverClusters.get(0);
    }
    
    @Override
    public ServerCluster updateServerCluster(ServerCluster dto) {
        serverClusterRepository.updateOptional(dto, ServerCluster.FIELD_CLUSTER_NAME,ServerCluster.FIELD_CLUSTER_DESCRIPTION,ServerCluster.FIELD_ENABLED_FLAG);
        return dto;
    }

    @Override
    public void deleteServerCluster(ServerCluster serverCluster) {
        ServerAssign serverAssign = new ServerAssign();
        serverAssign.setClusterId(serverCluster.getClusterId());
        int count = serverAssignRepository.selectCount(serverAssign);
        if (count > 0) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_CLUSTER_ASSIGN_SERVERS);
        } else {
            serverClusterRepository.deleteByPrimaryKey(serverCluster.getClusterId());
        }
    }
}
