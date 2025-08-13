package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.DashboardLayout;

import java.util.List;

/**
 * 工作台配置应用服务
 *
 * @author zhiying.dong@hand-china.com 2018-09-25 10:51:53
 * @author xiaoyu.zhao@hand-china.com
 */
public interface DashboardLayoutService {

    /**
     * 创建工作台配置
     *
     * @param dashboardLayouts 配置信息
     * @return List<DashboardLayout>
     */
    List<DashboardLayout> createDashboardLayout(List<DashboardLayout> dashboardLayouts);
}
