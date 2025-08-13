package org.hzero.platform.infra.mapper;

import java.util.List;

import org.hzero.platform.domain.entity.DashboardClauseAssign;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 工作台条目分配租户Mapper
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 * @author xiaoyu.zhao@hand-china.com
 */
public interface DashboardClauseAssignMapper extends BaseMapper<DashboardClauseAssign> {

    /**
     * 查询工作台条目分配租户
     *
     * @param dashboardClauseAssign 条目分配租户实体
     * @return Page<DashboardClauseAssign>
     */
    List<DashboardClauseAssign> queryDbdClauseAssign(DashboardClauseAssign dashboardClauseAssign);
}
