package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.DashboardRoleCard;

import java.util.List;

/**
 * 角色卡片表应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 19:58:17
 */
public interface DashboardRoleCardService {

    /**
     * 批量创建或更新角色卡片信息
     *
     * @param dashboardRoleCards 角色卡片信息
     * @return List<DashboardRoleCard>
     */
    List<DashboardRoleCard> batchCreateOrUpdateRoleCard(List<DashboardRoleCard> dashboardRoleCards);

    /**
     * 批量删除角色卡片信息
     *
     * @param dashboardRoleCards 角色卡片信息
     */
    void batchRemoveRoleCard(List<DashboardRoleCard> dashboardRoleCards);
}
