package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.DashboardClause;

/**
 * 工作台条目配置应用服务
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
public interface DashboardClauseService {

    /**
     * 保存卡片条目配置信息
     * @param dashboardClause 卡片条目实体
     * @return DashboardClause
     */
    DashboardClause saveDashboardClause(DashboardClause dashboardClause);

    /**
     * 修改卡片条目配置信息
     *
     * @param dashboardClause 卡片条目实体
     * @return DashboardClause
     */
    DashboardClause updateDashboardClause(DashboardClause dashboardClause);
}
