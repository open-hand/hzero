package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.Server;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 文件服务器Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerMapper extends BaseMapper<Server> {
    
    List<Server> fetchCanAssignFileServers(@Param("tenantId") Long tenantId
                                          ,@Param("clusterId") Long clusterId
                                          ,@Param("serverName") String serverName
                                          ,@Param("serverDescription") String serverDescription
                                          );
    
    List<Server>  selectServerList(Server server);
    
    Server selectByServerId(@Param("serverId") Long serverId,@Param("tenantId") Long tenantId);

    /**
     * 根据条件查询对应的文件服务器
     *
     * @param fileServerDTO
     * @return
     */
    List<Server> selectAllWithClusterInfo(Server server);

    /**
     * 查询集群下的文件服务器
     *
     * @param clusterId 集群id
     * @return 集群下的文件服务器
     */
    List<Server> findCluster(@Param("clusterId") Long clusterId);
    
    List<Server> selectByServerIdList(@Param("serverIds") List<Long> serverIds);
    
    List<Server> selectByClusterIdList(@Param("clusterIds") List<Long> clusterIds);

}
