package org.hzero.platform.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DashboardClauseAssign;
import org.hzero.platform.domain.repository.DashboardClauseAssignRepository;
import org.hzero.platform.infra.mapper.DashboardClauseAssignMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 工作台条目分配租户 资源库实现
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
@Component
public class DashboardClauseAssignRepositoryImpl extends BaseRepositoryImpl<DashboardClauseAssign> implements DashboardClauseAssignRepository {
    @Autowired
    private DashboardClauseAssignMapper dashboardClauseAssignMapper;

    @Override
    public Page<DashboardClauseAssign> queryDbdClauseAssign(DashboardClauseAssign dashboardClauseAssign, PageRequest pageRequest) {
        dashboardClauseAssign.checkTimeValidity();
        return PageHelper.doPageAndSort(pageRequest,() -> dashboardClauseAssignMapper.queryDbdClauseAssign(dashboardClauseAssign));
    }
}
