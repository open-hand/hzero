package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.platform.domain.entity.DashboardClauseAssign;

/**
 * 工作台条目分配租户应用服务
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
public interface DashboardClauseAssignService {

    /**
     * 保存工作台条目分配租户
     *
     * @param dashboardClauseAssigns 工作台条目分配租户
     * @return 工作台条目分配租户Id
     */
    List<DashboardClauseAssign> saveDbdClauseAssigns(List<DashboardClauseAssign> dashboardClauseAssigns);

    /**
     * 删除工作台条目分配租户
     *
     * @param dashboardClauseAssigns 待删除的分配租户
     */
    void deleteDbdClauseAssign(List<DashboardClauseAssign> dashboardClauseAssigns);
}
