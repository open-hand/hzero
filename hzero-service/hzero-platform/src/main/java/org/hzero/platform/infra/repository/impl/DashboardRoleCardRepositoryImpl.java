package org.hzero.platform.infra.repository.impl;

import java.util.List;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DashboardRoleCard;
import org.hzero.platform.domain.repository.DashboardRoleCardRepository;
import org.hzero.platform.infra.mapper.DashboardRoleCardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 角色卡片表 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-01-24 19:58:17
 */
@Component
public class DashboardRoleCardRepositoryImpl extends BaseRepositoryImpl<DashboardRoleCard> implements DashboardRoleCardRepository {

    @Autowired
    private DashboardRoleCardMapper roleCardMapper;

    @Override
    @ProcessLovValue
    public Page<DashboardRoleCard> getRoleCardList(DashboardRoleCard dashboardRoleCard, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> roleCardMapper.selectRoleCardList(dashboardRoleCard));
    }

    @Override
    @ProcessLovValue
    public Page<DashboardRoleCard> selectRoleAssignCard(DashboardRoleCard dashboardRoleCard, PageRequest pageRequest) {
        return PageHelper.doPage(pageRequest, () -> roleCardMapper.selectRoleAssignCardList(dashboardRoleCard));
    }

    @Override
    public List<Long> selectSuperRoleIds() {
        return roleCardMapper.selectSuperRoleIds();
    }

    @Override
    @ProcessLovValue
    public List<DashboardRoleCard> selectByRoleIds(List<Long> parentRoleList) {
        return roleCardMapper.selectParentInitRoleCard(parentRoleList);
    }

    @Override
    @ProcessLovValue
    public List<DashboardRoleCard> selectCurrentRoleCards(List<Long> roleIds) {
        return roleCardMapper.selectCurrentRoleCards(roleIds);
    }

    @Override
    public Long selectRoleTenant(Long roleId) {
        return roleCardMapper.selectRoleTenant(roleId);
    }

    @Override
    public List<Long> selectSubRoleIds(Long delCardRoleId, Long cardId) {
        return roleCardMapper.selectSubRoleIds(delCardRoleId, cardId);
    }
}
