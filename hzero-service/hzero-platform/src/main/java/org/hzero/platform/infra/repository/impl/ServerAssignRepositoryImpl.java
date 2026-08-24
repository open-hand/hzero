package org.hzero.platform.infra.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.ServerAssign;
import org.hzero.platform.domain.repository.ServerAssignRepository;
import org.hzero.platform.infra.mapper.ServerAssignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 文件服务器的集群分配 资源库实现
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Component
public class ServerAssignRepositoryImpl extends BaseRepositoryImpl<ServerAssign> implements ServerAssignRepository {

    @Autowired
    private ServerAssignMapper serverAssignMapper;

    @Override
    public List<ServerAssign> batchInsertDTOFilterExist(List<ServerAssign> serverAssignList) {
        //数量不会很多,直接循环判断
        List<ServerAssign> newList = serverAssignList.stream().filter(it ->
                it.getClusterId() != null && it.getServerId() != null && serverAssignMapper.exists(it).size() == 0
        ).collect(Collectors.toList());
        return this.batchInsertSelective(newList);
    }

    @Override
    public int deleteAll(List<ServerAssign> serverAssignList) {
        int count = 0;
        List<ServerAssign> collect = serverAssignList.stream().filter(it -> 
                it.getAssignId() != null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            count += this.batchDeleteByPrimaryKey(collect);
        }
        collect = serverAssignList.stream().filter(it ->
                it.getAssignId() == null
                        && it.getClusterId() != null
                        && it.getServerId() != null
        ).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            for (ServerAssign it : collect) {
                count += serverAssignMapper.deleteByServerIdAndClusterId(it);
            }
        }
        return count;
    }
  
}
