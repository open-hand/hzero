package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DashboardTenantCard;
import org.hzero.platform.domain.repository.DashboardTenantCardRepository;
import org.hzero.platform.infra.mapper.DashboardTenantCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 租户卡片分配 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 09:30:19
 */
@Component
public class DashboardTenantCardRepositoryImpl extends BaseRepositoryImpl<DashboardTenantCard> implements DashboardTenantCardRepository {

    @Autowired
    private DashboardTenantCardMapper mapper;

    @Override
    public Page<DashboardTenantCard> getAssignCardToTenantsList(DashboardTenantCard dashboardTenantCard, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> mapper.selectAssignCardToTenantsList(dashboardTenantCard));
    }

    @Override
    public void deleteByCardId(Long cardId) {
        mapper.deleteByCardId(cardId);
    }
}
