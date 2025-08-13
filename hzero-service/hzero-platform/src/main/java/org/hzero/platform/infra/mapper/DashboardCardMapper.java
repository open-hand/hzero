package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.DashboardCard;

import java.util.List;

/**
 * 卡片Mapper
 * @author xiaoyu.zhao@hand-china.com
 */
public interface DashboardCardMapper extends BaseMapper<DashboardCard> {

    /**
     * 查询卡片列表信息
     *
     * @param dashboardCard 卡片实体
     * @return List<DashboardCard>
     */
    List<DashboardCard> selectDashboardCard(DashboardCard dashboardCard);

    /**
     * 获取卡片信息明细
     *
     * @param dashboardCardId 卡片主键id
     * @return DashboardCard
     */
    DashboardCard selectDashboardCardDetails(@Param("dashboardCardId") Long dashboardCardId);

    /**
     * 获取可分配卡片列表(平台级条目)
     *
     * @param dashboardCard 查询条件
     * @return 查询结果
     */
    List<DashboardCard> selectSiteAssignableDashboardCard(DashboardCard dashboardCard);

    /**
     * 获取可分配卡片列表(租户级条目)
     * 使用DISTINCT是因为当租户级的卡片和条目均分配多个相同租户时查询数据会出现重复
     *
     * @param dashboardCard 查询条件
     * @return 查询结果
     */
    List<DashboardCard> selectOrgAssignableDashboardCard(DashboardCard dashboardCard);
}
