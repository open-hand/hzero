package org.hzero.scheduler.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.api.dto.RequestQueryDTO;
import org.hzero.scheduler.domain.entity.ConcurrentRequest;
import org.hzero.scheduler.domain.repository.ConcurrentRequestRepository;
import org.hzero.scheduler.infra.mapper.ConcurrentRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/10 16:07
 */
@Component
public class ConcurrentRequestRepositoryImpl extends BaseRepositoryImpl<ConcurrentRequest> implements ConcurrentRequestRepository {

    @Autowired
    private ConcurrentRequestMapper requestMapper;

    @Override
    public Page<ConcurrentRequest> pageRequest(PageRequest pageRequest, RequestQueryDTO requestQueryDTO) {
        return PageHelper.doPageAndSort(pageRequest, () -> requestMapper.listRequest(requestQueryDTO));
    }

    @Override
    public ConcurrentRequest selectById(Long tenantId, Long requestId) {
        return requestMapper.queryRequestById(tenantId, requestId);
    }
}
