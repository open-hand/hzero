package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DashboardCardClause;

/**
 * 条目关联卡片资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-03-06 17:55:53
 */
public interface DashboardCardClauseRepository extends BaseRepository<DashboardCardClause> {

    /**
     * 分页查询条目关联卡片信息
     *
     * @param dashboardCardClause 条目信息
     * @param pageRequest 分页参数
     * @return 查询结果
     */
    Page<DashboardCardClause> selectDashboardCardClauseList(DashboardCardClause dashboardCardClause, PageRequest pageRequest);

    /**
     * 查询当前卡片下条目的最大rank值
     * @param cardId 卡片Id
     * @return max rank value
     */
    Integer selectMaxRankValue(Long cardId);

    /**
     * 判断是否重复
     *
     * @param clauseId 条目Id
     * @param cardId 卡片Id
     * @return 是否重复
     */
    int checkRepeat(Long clauseId, Long cardId);
}
