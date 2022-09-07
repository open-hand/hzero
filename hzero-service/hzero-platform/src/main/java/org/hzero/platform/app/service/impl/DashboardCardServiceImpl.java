package org.hzero.platform.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.common.Criteria;
import org.hzero.platform.app.service.DashboardCardService;
import org.hzero.platform.domain.entity.DashboardCard;
import org.hzero.platform.domain.entity.DashboardRoleCard;
import org.hzero.platform.domain.repository.DashboardCardRepository;
import org.hzero.platform.domain.repository.DashboardRoleCardRepository;
import org.hzero.platform.domain.repository.DashboardTenantCardRepository;
import org.hzero.platform.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.mybatis.service.BaseServiceImpl;

/**
 * 卡片信息配置实现
 * @author xiaoyu.zhao@hand-china.com
 */
@Service
public class DashboardCardServiceImpl extends BaseServiceImpl<DashboardCard> implements DashboardCardService {

    @Autowired
    private DashboardCardRepository cardRepository;
    @Autowired
    private DashboardTenantCardRepository tenantCardRepository;
    @Autowired
    private DashboardRoleCardRepository roleCardRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DashboardCard createDashboardCard(DashboardCard dashboardCard) {
        // 判断code是否重复
        dashboardCard.validate(cardRepository);
        cardRepository.insertSelective(dashboardCard);
        // 自动分配到超级管理员下
        // FIX 03-22 修复oracle无法插入数据问题
        List<DashboardRoleCard> roleCards = new ArrayList<>();
        List<Long> superRoleIds = roleCardRepository.selectSuperRoleIds();
        if (CollectionUtils.isNotEmpty(superRoleIds)) {
            superRoleIds.forEach(roleId -> {
                DashboardRoleCard dashboardRoleCard = new DashboardRoleCard(roleId, dashboardCard.getId(),
                        BaseConstants.Digital.ZERO, BaseConstants.Digital.ZERO, BaseConstants.Flag.NO, dashboardCard.getTenantId());
                roleCards.add(dashboardRoleCard);
            });
        }
        roleCardRepository.batchInsertSelective(roleCards);
        return dashboardCard;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DashboardCard updateDashboardCard(DashboardCard dashboardCard) {
        DashboardCard oldDashboard = cardRepository.selectOneOptional(dashboardCard, new Criteria()
                .unSelect(DashboardCard.FIELD_CREATED_BY, DashboardCard.FIELD_CREATION_DATE, DashboardCard.FIELD_LAST_UPDATE_DATE,
                        DashboardCard.FIELD_LAST_UPDATED_BY)
                .where(DashboardCard.FIELD_ID)
        );
        oldDashboard.judgeCardValidity(dashboardCard.getTenantId());
        // 判断是否是由租户级修改为平台级
        if (!Objects.equals(dashboardCard.getLevel(), oldDashboard.getLevel()) &&
                Objects.equals(Constants.SITE_LEVEL_UPPER_CASE, dashboardCard.getLevel())) {
            // 修改了卡片的层级并且卡片层级修改为平台级， 是租户级修改为平台级，则删除该卡片分配的租户信息
            tenantCardRepository.deleteByCardId(dashboardCard.getId());
        }
        // 更新卡片信息
        // 2019-04-16 FIX 修复updateOptional生成的sql中无法拼接宽度和高度（w,h）的问题
        cardRepository.updateByPrimaryKeySelective(dashboardCard);
        return dashboardCard;
    }
}
