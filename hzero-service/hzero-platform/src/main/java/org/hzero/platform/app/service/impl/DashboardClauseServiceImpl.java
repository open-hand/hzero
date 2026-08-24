package org.hzero.platform.app.service.impl;


import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.common.Criteria;
import org.hzero.platform.app.service.DashboardCardClauseService;
import org.hzero.platform.app.service.DashboardClauseService;
import org.hzero.platform.domain.entity.DashboardCardClause;
import org.hzero.platform.domain.entity.DashboardClause;
import org.hzero.platform.domain.repository.DashboardClauseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 工作台条目配置应用服务默认实现
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 * @author xiaoyu.zhao@hand-china.com
 */
@Service
public class DashboardClauseServiceImpl implements DashboardClauseService {

    @Autowired
    private DashboardClauseRepository dashboardClauseRepository;
    @Autowired
    private DashboardCardClauseService dashboardCardClauseService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DashboardClause saveDashboardClause(DashboardClause dashboardClause) {
        // FIX 新建条目时需要保存后才可以选择租户
        dashboardClause.validate(dashboardClauseRepository);
        dashboardClauseRepository.insertSelective(dashboardClause);
        Long idParam = dashboardClause.getClauseId();
        List<DashboardCardClause> cardClauses = dashboardClause.getDashboardCardClauseList();
        if (CollectionUtils.isNotEmpty(cardClauses)) {
            // 新建条目时直接分配了卡片，组装插入条目卡片配置参数
            cardClauses.forEach(cardClause -> {
                cardClause.setClauseId(idParam);
                cardClause.setTenantId(dashboardClause.getTenantId());
            });
        }
        dashboardCardClauseService.batchInsertDashboardCardClause(cardClauses);
        return dashboardClause;
    }

    @Override
    public DashboardClause updateDashboardClause(DashboardClause dashboardClause) {
        DashboardClause checkClause = dashboardClauseRepository.selectOneOptional(dashboardClause, new Criteria()
                .unSelect(DashboardClause.FIELD_CREATED_BY, DashboardClause.FIELD_CREATION_DATE, DashboardClause.FIELD_LAST_UPDATE_DATE,
                        DashboardClause.FIELD_LAST_UPDATED_BY)
                .where(DashboardClause.FIELD_CLAUSE_CODE)
        );
        checkClause.judgeClauseValidity(dashboardClause.getTenantId());
        List<DashboardCardClause> dashboardCardClauseList = dashboardClause.getDashboardCardClauseList();
        if (CollectionUtils.isNotEmpty(dashboardCardClauseList)) {
            dashboardCardClauseList.forEach(cardClause -> {
                cardClause.setClauseId(dashboardClause.getClauseId());
                cardClause.setTenantId(dashboardClause.getTenantId());
            });
        }
        // 更新条目分配卡片信息
        dashboardCardClauseService.batchInsertDashboardCardClause(dashboardCardClauseList);
        // 更新条目信息
        dashboardClauseRepository.updateOptional(dashboardClause, DashboardClause.FIELD_CLAUSE_NAME,
                DashboardClause.FIELD_DATA_TENANT_LEVEL, DashboardClause.FIELD_DOC_REMARK_EXPRESSION,
                DashboardClause.FIELD_ENABLED_FLAG, DashboardClause.FIELD_MENU_CODE,
                DashboardClause.FIELD_ROUTE, DashboardClause.FIELD_STATS_EXPRESSION,
                DashboardClause.FIELD_REMARK);
        return dashboardClause;
    }

}
