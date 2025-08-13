package org.hzero.platform.infra.repository.impl;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.hzero.platform.domain.entity.DashboardCard;
import org.hzero.platform.domain.entity.DashboardCardClause;
import org.hzero.platform.domain.entity.DashboardClause;
import org.hzero.platform.domain.repository.DashboardClauseRepository;
import org.hzero.platform.infra.mapper.DashboardClauseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 工作台条目配置 资源库实现
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
@Component
public class DashboardClauseRepositoryImpl extends BaseRepositoryImpl<DashboardClause> implements DashboardClauseRepository {
    @Autowired
    private DashboardClauseMapper dashboardClauseMapper;

    @Override
    @ProcessLovValue
    public Page<DashboardClause> queryDashboardClause(DashboardClause clause, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,() -> dashboardClauseMapper.queryDashboardClause(clause));
    }

    @Override
    public DashboardClause queryDashboardClauseDetails(Long clauseId) {
        DashboardClause dashboardClause = new DashboardClause();
        dashboardClause.setClauseId(clauseId);
        return this.selectOneOptional(dashboardClause, new Criteria()
                .unSelect(DashboardCard.FIELD_LAST_UPDATED_BY, DashboardCard.FIELD_LAST_UPDATE_DATE,
                        DashboardCardClause.FIELD_CREATED_BY, DashboardCard.FIELD_CREATION_DATE
                )
        );
    }
}
