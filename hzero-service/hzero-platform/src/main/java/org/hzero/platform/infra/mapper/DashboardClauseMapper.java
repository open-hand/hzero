package org.hzero.platform.infra.mapper;

import java.util.List;

import org.hzero.platform.domain.entity.DashboardClause;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 工作台条目配置Mapper
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
public interface DashboardClauseMapper extends BaseMapper<DashboardClause> {
    /**
     * 查询卡片条目列表
     *
     * @param clause 卡片条目实体
     * @return List<DashboardClause>
     */
    List<DashboardClause> queryDashboardClause(DashboardClause clause);
}
