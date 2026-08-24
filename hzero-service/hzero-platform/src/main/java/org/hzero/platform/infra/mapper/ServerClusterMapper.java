package org.hzero.platform.infra.mapper;

import java.util.List;

import org.hzero.platform.domain.entity.ServerCluster;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 服务器集群设置表Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerClusterMapper extends BaseMapper<ServerCluster> {
    
    List<ServerCluster> selectClusterList(ServerCluster serverCluster);

}
