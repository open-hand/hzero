package org.hzero.platform.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DashboardCardClause;
import org.hzero.platform.domain.repository.DashboardCardClauseRepository;
import org.hzero.platform.infra.mapper.DashboardCardClauseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 *  资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-03-06 17:55:53
 */
@Component
public class DashboardCardClauseRepositoryImpl extends BaseRepositoryImpl<DashboardCardClause> implements DashboardCardClauseRepository {

    @Autowired
    private DashboardCardClauseMapper dashboardCardClauseMapper;

    @Override
    public Page<DashboardCardClause> selectDashboardCardClauseList(DashboardCardClause dashboardCardClause, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> dashboardCardClauseMapper.selectDashboardCardClauseList(dashboardCardClause));
    }

    @Override
    public Integer selectMaxRankValue(Long cardId) {
        return dashboardCardClauseMapper.selectMaxRankValue(cardId);
    }

    @Override
    public int checkRepeat(Long clauseId, Long cardId) {
        return dashboardCardClauseMapper.checkRepeat(clauseId, cardId);
    }
}
