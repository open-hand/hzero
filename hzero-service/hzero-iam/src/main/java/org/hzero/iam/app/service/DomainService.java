package org.hzero.iam.app.service;

import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.domain.entity.Domain;

/**
 * 门户分配应用服务
 *
 * @author minghui.qiu@hand-china.com 2019-06-27 20:50:16
 */
public interface DomainService {
    
    /**
     * 根据可选条件查询单据类型
     * 
     * @param pageRequest 分页参数
     * @param docType 查询数据实体
     * @return 分页之后的数据
     */
    Page<Domain> selectByOptions(PageRequest pageRequest, Domain domain);
    
    Domain selectByDomainId(Long domainId);
    
    int insertDomain(Domain domain);
    
    int updateDomain(Domain domain);
    
    int deleteDomain(Domain domain);

    Map<String, Object> fixSsoDomain();
}
