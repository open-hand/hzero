package org.hzero.platform.infra.repository.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.DashboardLayout;
import org.hzero.platform.domain.entity.DashboardRoleCard;
import org.hzero.platform.domain.repository.DashboardLayoutRepository;
import org.hzero.platform.domain.repository.DashboardRoleCardRepository;
import org.hzero.platform.domain.vo.DashboardLayoutVO;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.mapper.DashboardLayoutMapper;
import org.hzero.platform.infra.properties.PlatformProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 工作台配置 资源库实现
 *
 * @author zhiying.dong@hand-china.com 2018-09-25 10:51:53
 * @author xiaoyu.zhao@hand-china.com
 */
@Component
public class DashboardLayoutRepositoryImpl extends BaseRepositoryImpl<DashboardLayout> implements DashboardLayoutRepository {

    @Autowired
    private DashboardRoleCardRepository roleCardRepository;
    @Autowired
    private DashboardLayoutMapper layoutMapper;
    @Autowired
    private PlatformProperties platformProperties;

    @Override
    @ProcessLovValue
    public List<DashboardLayoutVO> selectDashboardLayoutCards() {
        List<Long> roleIds = DetailsHelper.getUserDetails().roleMergeIds();
        // 判断当前角色是否继承租户管理员模板，如果符合条件说明是租户管理员,可直接使用所有平台级的卡片
        Long resultTmpAdminId = this.checkOrgAdminUser(roleIds);
        if (resultTmpAdminId != null) {
            // io.choerodon.core.oauth.CustomUserDetails.roleMergeIds() 方法的返回结果可能是
            // java.util.Collections.singletonList() 结果对象(内部数据不可变)，因此需要在此处处理
            roleIds = new ArrayList<>(roleIds);
            roleIds.add(resultTmpAdminId);
            // 是租户管理员，查询获取当前角色以及租户管理员模板角色以及所有平台级的卡片信息,需要去重，无法保证自身、模板以及平台级卡片是否存在重复
            List<DashboardLayoutVO> layouts = layoutMapper.selectOrgAdminRoleCards(roleIds);
            return layouts.stream().filter(layout -> layout.getRoleId() != null || Objects
                    .equals(Constants.SITE_LEVEL_UPPER_CASE, layout.getLevel())
            ).distinct().collect(Collectors.toList());
        } else {
            // 查询获取当前角色（若继承自销售员模板或专家模板则获取模板分配的卡片）卡片信息，需要去重，无法保证自身及模板是否存在重复
            List<DashboardLayoutVO> resultList = layoutMapper.selectRoleCardsByRoleId(roleIds, platformProperties.getRoleTemplateCodes());
            return resultList.stream().distinct().collect(Collectors.toList());
        }
    }

    @Override
    public List<DashboardLayoutVO> selectDashboardLayout() {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        // 查询当前用户下的布局信息
        List<DashboardLayoutVO> resultList = layoutMapper.selectCurrentLayouts(userDetails.getUserId(), userDetails.roleMergeIds(), userDetails.getTenantId());
        if (CollectionUtils.isEmpty(resultList)) {
            // 无卡片配置信息，查询判断当前角色下是否存在初始化布局信息
            // FIX 2019-09-30 添加逻辑，初始化卡片不可删除
            List<DashboardRoleCard> dashboardRoleCards = roleCardRepository.selectCurrentRoleCards(userDetails.roleMergeIds());
            if (CollectionUtils.isNotEmpty(dashboardRoleCards)) {
                // 存在初始化布局信息
                getDashboardCardList(dashboardRoleCards, resultList);
            } else {
                // 判断是否存在父级角色以及父级角色中是否存在初始化卡片
                List<DashboardRoleCard> initLayout = getInitLayout(userDetails.roleMergeIds());
                if (CollectionUtils.isNotEmpty(initLayout)) {
                    getDashboardCardList(initLayout, resultList);
                }
            }
        }
        return resultList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 组装返回的list参数
     *
     * @param roleCards 角色卡片集合
     * @param resultList 返回的集合
     */
    private void getDashboardCardList(List<DashboardRoleCard> roleCards, List<DashboardLayoutVO> resultList) {
        roleCards.forEach(roleCard -> {
            if (!Objects.isNull(roleCard.getCardId()) && 0L != roleCard.getCardId()) {
                DashboardLayoutVO layoutVO = new DashboardLayoutVO(roleCard.getCode(), roleCard.getW(), roleCard.getH(),
                        roleCard.getX(), roleCard.getY(), roleCard.getDefaultDisplayFlag(), roleCard.getCatalogType(),
                        roleCard.getName(), 1, roleCard.getCardParams(), roleCard.getCardId());
                resultList.add(layoutVO);
            }
        });
    }

    /**
     * 获取父级初始化面板信息
     *
     * @return List<DashboardRoleCard>
     */
    private List<DashboardRoleCard> getInitLayout(List<Long> roleIds) {
        List<DashboardRoleCard> initLayouts = new ArrayList<>();
        for (Long roleId : roleIds) {
            // 查询获取levelPath下所有父级角色Id
            List<Long> parentRoleList = layoutMapper.selectParentRole(roleId);
            if (CollectionUtils.isNotEmpty(parentRoleList)) {
                List<DashboardRoleCard> resultList = roleCardRepository.selectByRoleIds(parentRoleList);
                Map<Long, List<DashboardRoleCard>> parentRoleCardMap =
                        resultList.stream().collect(Collectors.toMap(DashboardRoleCard::getRoleId, lst -> {
                            // 将相同key的数据组装为集合返回
                            List<DashboardRoleCard> tmpList = new ArrayList<>();
                            tmpList.add(lst);
                            return tmpList;
                        }, (List<DashboardRoleCard> lst1, List<DashboardRoleCard> lst2) -> {
                            lst1.addAll(lst2);
                            return lst1;
                        }));
                // 取出当前角色的父级角色判断是否存在初始化信息
                for (Long aLong : parentRoleList) {
                    if (parentRoleCardMap.containsKey(aLong)) {
                        initLayouts.addAll(parentRoleCardMap.get(aLong));
                        // 取出第一个存在默认卡片的父级角色数据
                        break;
                    }
                }
            }
        }
        return initLayouts;
    }

    /**
     * 判断是否是租户管理员用户
     *
     * 判断当前角色是否继承租户管理员模板，如果符合条件说明是租户管理员,可直接使用所有平台级的卡片
     * @param roleIds 角色IDs
     * @return 租户管理员模板Id
     */
    private Long checkOrgAdminUser(List<Long> roleIds) {
        // 返回租户管理员模板Id
        return layoutMapper.checkOrgAdminUser(roleIds);
    }
}
