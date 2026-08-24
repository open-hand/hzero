package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DashboardTenantCard;

import java.util.List;

/**
 * 租户卡片分配资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 09:30:19
 */
public interface DashboardTenantCardRepository extends BaseRepository<DashboardTenantCard> {

    /**
     * 获取卡片分配信息
     *
     * @param dashboardTenantCard 卡片实体
     * @param pageRequest 分页参数
     * @return Page<DashboardTenantCard>
     */
    Page<DashboardTenantCard> getAssignCardToTenantsList(DashboardTenantCard dashboardTenantCard, PageRequest pageRequest);

    /**
     * 禁用卡片时清除与租户关联的数据信息
     */
    void deleteByCardId(Long cardId);
}
