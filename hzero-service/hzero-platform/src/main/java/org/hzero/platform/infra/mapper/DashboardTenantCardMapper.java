package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.DashboardTenantCard;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 租户卡片分配Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 09:30:19
 */
public interface DashboardTenantCardMapper extends BaseMapper<DashboardTenantCard> {

    /**
     * 查询分配卡片信息
     *
     * @param dashboardTenantCard 卡片实体
     * @return List<DashboardCard>
     */
    List<DashboardTenantCard> selectAssignCardToTenantsList(DashboardTenantCard dashboardTenantCard);

    /**
     * 禁用卡片时删除租户下引用该卡片的信息
     *
     * @param cardId 卡片Id
     */
    void deleteByCardId(Long cardId);
}
