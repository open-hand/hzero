package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.ServerCluster;

/**
 * 服务器集群设置表资源库
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerClusterRepository extends BaseRepository<ServerCluster> {
    
    List<ServerCluster> selectClusterList(ServerCluster serverCluster);
    
}
