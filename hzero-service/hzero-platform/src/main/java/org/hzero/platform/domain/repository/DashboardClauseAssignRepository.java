package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DashboardClauseAssign;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 工作台条目分配租户资源库
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
public interface DashboardClauseAssignRepository extends BaseRepository<DashboardClauseAssign> {
    /**
     * 查询工作台条目分配租户
     *
     * @param dashboardClauseAssign 条目分配租户实体
     * @param pageRequest 分页参数
     * @return Page<DashboardClauseAssign>
     */
    Page<DashboardClauseAssign> queryDbdClauseAssign(DashboardClauseAssign dashboardClauseAssign, PageRequest pageRequest);
    
}
