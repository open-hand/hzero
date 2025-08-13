package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DashboardClause;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 工作台条目配置资源库
 *
 * @author jun.gao@hand-china.com 2019-01-27 19:10:25
 */
public interface DashboardClauseRepository extends BaseRepository<DashboardClause> {

    /**
     * 卡片条目配置查询
     * @param clause 卡片条目实体
     * @param pageRequest 分页参数
     * @return Page<DashboardClause>
     */
    Page<DashboardClause> queryDashboardClause(DashboardClause clause, PageRequest pageRequest);

    /**
     * 查询卡片条目明细
     *
     * @param clauseId 条目Id
     * @return 条目明细
     */
    DashboardClause queryDashboardClauseDetails(Long clauseId);
}
