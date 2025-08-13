package org.hzero.platform.app.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hzero.platform.app.service.DashboardRoleCardService;
import org.hzero.platform.domain.entity.DashboardLayout;
import org.hzero.platform.domain.entity.DashboardRoleCard;
import org.hzero.platform.domain.repository.DashboardLayoutRepository;
import org.hzero.platform.domain.repository.DashboardRoleCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色卡片表应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 19:58:17
 */
@Service
public class DashboardRoleCardServiceImpl implements DashboardRoleCardService {

    @Autowired
    private DashboardRoleCardRepository roleCardRepository;
    @Autowired
    private DashboardLayoutRepository dashboardLayoutRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<DashboardRoleCard> batchCreateOrUpdateRoleCard(List<DashboardRoleCard> dashboardRoleCards) {
        Map<Boolean, List<DashboardRoleCard>> updateAndInsertMap = dashboardRoleCards.stream()
                .distinct()
                .collect(Collectors.partitioningBy(roleCard -> roleCard.getObjectVersionNumber() == null));
        List<DashboardRoleCard> insertList = updateAndInsertMap.get(true);
        List<DashboardRoleCard> updateList = updateAndInsertMap.get(false);
        insertList.forEach(roleCard -> {
            Long roleTenantId = roleCardRepository.selectRoleTenant(roleCard.getRoleId());
            roleCard.setTenantId(roleTenantId);
            roleCard.validate(roleCardRepository);
        });
        // 校验完成，保存和更新数据
        roleCardRepository.batchInsertSelective(insertList);
        roleCardRepository.batchUpdateByPrimaryKeySelective(updateList);
        return dashboardRoleCards;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchRemoveRoleCard(List<DashboardRoleCard> dashboardRoleCards) {
        List<DashboardLayout> delLayouts = new LinkedList<>();
        // 清除已经分配到用户层的卡片数据
        for (DashboardRoleCard dashboardRoleCard : dashboardRoleCards) {
            List<Long> delRoleIds = roleCardRepository.selectSubRoleIds(dashboardRoleCard.getRoleId(), dashboardRoleCard.getCardId());
            for (Long delRoleId : delRoleIds) {
                DashboardLayout layout = new DashboardLayout();
                layout.setCardId(dashboardRoleCard.getCardId());
                layout.setRoleId(delRoleId);
                delLayouts.add(layout);
            }
        }
        dashboardLayoutRepository.batchDelete(delLayouts);
        // 直接删除
        roleCardRepository.batchDeleteByPrimaryKey(dashboardRoleCards);
    }
}
