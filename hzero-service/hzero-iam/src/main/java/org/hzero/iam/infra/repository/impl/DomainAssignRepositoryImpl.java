package org.hzero.iam.infra.repository.impl;

import org.hzero.iam.domain.entity.DomainAssign;
import org.hzero.iam.domain.repository.DomainAssignRepository;
import org.hzero.iam.infra.mapper.DomainAssignMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单点二级域名分配 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2020-09-02 15:34:46
 */
@Component
public class DomainAssignRepositoryImpl extends BaseRepositoryImpl<DomainAssign> implements DomainAssignRepository {

    @Autowired
    private DomainAssignMapper assignMapper;

    @Override
    public Page<DomainAssign> pageDomainAssign(DomainAssign domainAssign, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> assignMapper.selectDomainAssign(domainAssign));
    }

    @Override
    public DomainAssign getDomainAssignDetail(Long domainId, Long domainAssignId) {
        return assignMapper.selectDomainAssignDetail(domainId, domainAssignId);
    }
}
