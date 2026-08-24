package org.hzero.platform.domain.repository;

import java.util.List;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.ServerAssign;

/**
 * 文件服务器的集群分配资源库
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerAssignRepository extends BaseRepository<ServerAssign> {
    
    List<ServerAssign> batchInsertDTOFilterExist(List<ServerAssign> serverAssignList);

    int deleteAll(List<ServerAssign> serverAssignList);
    
}
