package org.hzero.platform.app.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.platform.app.service.ServerAssignService;
import org.hzero.platform.domain.entity.ServerAssign;
import org.hzero.platform.domain.repository.ServerAssignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 文件服务器的集群分配应用服务默认实现
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Service
public class ServerAssignServiceImpl implements ServerAssignService {
    
    @Autowired
    private ServerAssignRepository serverAssignRepository;

    @Override
    public List<ServerAssign> batchInsertDTOFilterExist(Long tenantId,List<ServerAssign> serverAssignList) {
        if(tenantId != null){
            for(ServerAssign dto : serverAssignList){
                dto.setTenantId(tenantId);
            }
        }
        if (CollectionUtils.isNotEmpty(serverAssignList)) {
            return serverAssignRepository.batchInsertDTOFilterExist(serverAssignList);
        }
        return serverAssignList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAll(Long tenantId,List<ServerAssign> serverAssignList) {
        for(ServerAssign dto : serverAssignList){
            if(tenantId != null ){
                dto.setTenantId(tenantId);
            }
            ServerAssign serverAssign = serverAssignRepository.selectOne(dto);
            if(serverAssign!=null){
                serverAssignRepository.deleteByPrimaryKey(serverAssign.getAssignId());
            }
        }
        return 0;
    }
}
