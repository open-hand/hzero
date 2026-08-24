package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.DashboardCard;

import java.util.List;

/**
 * 卡片配置服务
 * @author xiaoyu.zhao@hand-china.com
 */
public interface DashboardCardService {

    /**
     * 创建卡片
     *
     * @param dashboardCard 卡片信息
     * @return DashboardCard
     */
    DashboardCard createDashboardCard(DashboardCard dashboardCard);

    /**
     * 修改卡片信息
     *
     * @param dashboardCard 卡片信息
     * @return DashboardCard
     */
    DashboardCard updateDashboardCard(DashboardCard dashboardCard);
}
