package org.hzero.platform.app.service;

import org.hzero.platform.domain.entity.DashboardTenantCard;

import java.util.List;

/**
 * 租户卡片分配应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 09:30:19
 */
public interface DashboardTenantCardService {

    /**
     * 卡片分配租户
     *
     * @param tenantCards 租户卡片实体集合
     * @return List<DashboardTenantCard>
     */
    List<DashboardTenantCard> createAssignCardToTenants(List<DashboardTenantCard> tenantCards);

    /**
     * 批量删除租户卡片
     *
     * @param tenantCards 租户卡片实体集合
     */
    void batchRemoveAssignTenantCards(List<DashboardTenantCard> tenantCards);
}
