package org.hzero.platform.domain.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Server;

/**
 * 文件服务器资源库
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerRepository extends BaseRepository<Server> {
    
    List<Server>  selectServerList(Server server);
    /**
     * 获取所有的服务信息
     * @return
     */
    List<Server> selectDTOAll();

    List<Server> fetchCanAssignFileServers( Long tenantId,Long clusterId,Server server);

    List<Server> selectAllWithClusterInfo(Server server);

    /**
     * 获取集群下的文件服务器
     *
     * @param clusterId 集群ID
     * @return 集群下的文件服务器
     */
    List<Server> findCluster(Long clusterId);
    
    List<Server> selectByServerIdList(List<Long> serverIds);
    
    List<Server> selectByClusterIdList(List<Long> clusterIds);
    
    Server selectByServerId(Long serverId,Long tenantId);
    
}
