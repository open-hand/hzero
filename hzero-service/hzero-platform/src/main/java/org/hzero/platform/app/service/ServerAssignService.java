package org.hzero.platform.app.service;

import java.util.List;
import org.hzero.platform.domain.entity.ServerAssign;

/**
 * 文件服务器的集群分配应用服务
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerAssignService {
    
    List<ServerAssign> batchInsertDTOFilterExist(Long tenantId,List<ServerAssign> serverAssignList);

    int deleteAll(Long tenantId,List<ServerAssign> serverAssignList);

}
