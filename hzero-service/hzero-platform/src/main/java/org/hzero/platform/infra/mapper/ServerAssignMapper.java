package org.hzero.platform.infra.mapper;

import java.util.List;
import org.hzero.platform.domain.entity.ServerAssign;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 文件服务器的集群分配Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerAssignMapper extends BaseMapper<ServerAssign> {
    
    List<Long> exists(ServerAssign it);

    int deleteByServerIdAndClusterId(ServerAssign it);

}
