package org.hzero.scheduler.infra.repository.impl;

import java.time.LocalDate;
import java.util.List;

import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.scheduler.domain.entity.Concurrent;
import org.hzero.scheduler.domain.repository.ConcurrentRepository;
import org.hzero.scheduler.infra.mapper.ConcurrentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/09 16:24
 */
@Component
public class ConcurrentRepositoryImpl extends BaseRepositoryImpl<Concurrent> implements ConcurrentRepository {

    @Autowired
    private ConcurrentMapper concurrentMapper;

    @Override
    public Page<Concurrent> pageConcurrent(Long tenantId, String concCode, String concName, String concDescription, Integer enabledFlag, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> concurrentMapper.listConcurrent(tenantId, concCode, concName, concDescription, enabledFlag));
    }

    @Override
    public Concurrent detailConcurrent(Long concurrentId, Long tenantId) {
        Concurrent concurrent = concurrentMapper.queryConcurrentById(concurrentId, tenantId);
        Assert.notNull(concurrent, BaseConstants.ErrorCode.NOT_FOUND);
        return concurrent;
    }

    @Override
    public List<Concurrent> listConcurrentByTenantId(Long tenantId, List<Long> roleIds, String concCode, String concName, LocalDate nowDate, boolean flag) {
        return concurrentMapper.listConcurrentByTenantId(tenantId, roleIds, concCode, concName, nowDate, flag);
    }

    @Override
    public List<Concurrent> adminListConcurrentByTenantId(Long tenantId, String concCode, String concName, LocalDate nowDate) {
        return concurrentMapper.adminListConcurrentByTenantId(tenantId, concCode, concName, nowDate);
    }
}
