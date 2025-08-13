package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.ServerCluster;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 服务器集群设置表应用服务
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerClusterService {
    
    /**
     * 查询集群信息
     * @param serverCluster
     * @param pageRequest
     * @return
     */
    Page<ServerCluster> serverClusterList(PageRequest pageRequest, ServerCluster serverCluster);

    ServerCluster serverClusterById(Long tenantId,Long serverClusterId);
    
    ServerCluster updateServerCluster(ServerCluster dto);

    /**
     * 删除服务器集群（若集群已经被应用则不允许删除）
     *
     * @param serverCluster 删除集群
     */
    void deleteServerCluster(ServerCluster serverCluster);
}
