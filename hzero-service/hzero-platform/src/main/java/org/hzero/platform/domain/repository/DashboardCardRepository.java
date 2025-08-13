package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DashboardCard;

/**
 * 卡片表资源库
 * @author xiaoyu.zhao@hand-china.com
 */
public interface DashboardCardRepository extends BaseRepository<DashboardCard> {

    /**
     * 查询卡片列表信息
     *
     * @param dashboardCard 卡片实体
     * @param pageRequest 分页参数
     * @return Page<DashboardCard>
     */
    Page<DashboardCard> getDashboardCards(DashboardCard dashboardCard, PageRequest pageRequest);

    /**
     * 获取卡片详细信息
     *
     * @param dashboardCardId 主键
     * @return DashboardCard
     */
    DashboardCard getDashboardCardDetails(Long dashboardCardId);

    /**
     * 获取可分配卡片列表--条目配置分配卡片时调用
     *
     * @param dashboardCard 查询条件
     * @param pageRequest 分页参数
     * @return 查询结果
     */
    Page<DashboardCard> getAssignableDashboardCard(DashboardCard dashboardCard, PageRequest pageRequest);
}
