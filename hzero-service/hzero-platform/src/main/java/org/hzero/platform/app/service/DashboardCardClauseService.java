package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.DashboardCardClause;

import java.util.List;

/**
 * 条目关联卡片信息 应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-03-06 17:55:53
 */
public interface DashboardCardClauseService {

    /**
     * 批量创建条目卡片关联配置信息
     *
     * @param dashboardCardClauses 插入的数据
     * @return 关联信息集合
     */
    List<DashboardCardClause> batchInsertDashboardCardClause(List<DashboardCardClause> dashboardCardClauses);

    /**
     * 批量删除条目卡片关联配置信息
     *
     * @param dashboardCardClauses 批量删除的数据
     */
    void batchDeleteDashboardCardClause(List<DashboardCardClause> dashboardCardClauses);
}
