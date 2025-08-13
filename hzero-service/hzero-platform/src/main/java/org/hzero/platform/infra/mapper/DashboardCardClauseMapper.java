package org.hzero.platform.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.DashboardCardClause;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-03-06 17:55:53
 */
public interface DashboardCardClauseMapper extends BaseMapper<DashboardCardClause> {

    /**
     * 分页查询条目关联卡片信息
     *
     * @param dashboardCardClause 查询条件
     * @return 返回结果
     */
    List<DashboardCardClause> selectDashboardCardClauseList(DashboardCardClause dashboardCardClause);

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
    int checkRepeat(@Param("clauseId") Long clauseId, @Param("cardId") Long cardId);
}
