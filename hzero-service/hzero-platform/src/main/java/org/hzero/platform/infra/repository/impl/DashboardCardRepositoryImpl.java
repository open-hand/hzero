package org.hzero.platform.infra.repository.impl;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DashboardCard;
import org.hzero.platform.domain.repository.DashboardCardRepository;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.mapper.DashboardCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Objects;

/**
 * 卡片表  资源库实现
 * @author xiaoyu.zhao@hand-china.com
 */
@Component
public class DashboardCardRepositoryImpl extends BaseRepositoryImpl<DashboardCard> implements DashboardCardRepository {

    @Autowired
    private DashboardCardMapper dashboardCardMapper;

    @Override
    @ProcessLovValue
    public Page<DashboardCard> getDashboardCards(DashboardCard dashboardCard, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> dashboardCardMapper.selectDashboardCard(dashboardCard));
    }

    @Override
    @ProcessLovValue
    public DashboardCard getDashboardCardDetails(Long dashboardCardId) {
        return dashboardCardMapper.selectDashboardCardDetails(dashboardCardId);
    }

    @Override
    public Page<DashboardCard> getAssignableDashboardCard(DashboardCard dashboardCard, PageRequest pageRequest) {
        // 判断获取可分配卡片的条目层级
        if (Objects.equals(Constants.TENANT_LEVEL_UPPER_CASE, dashboardCard.getClauseLevel())) {
            // 租户级条目可以查询获取所有平台级卡片以及与当前租户级条目分配租户所匹配的租户级卡片
            return PageHelper.doPageAndSort(pageRequest, () -> dashboardCardMapper.selectOrgAssignableDashboardCard(dashboardCard));
        } else {
            // 平台级条目可以查询获取所有平台级卡片以及当前租户所具有的租户级卡片以及分配给当前租户的卡片
            return PageHelper.doPageAndSort(pageRequest, () -> dashboardCardMapper.selectSiteAssignableDashboardCard(dashboardCard));
        }
    }
}
