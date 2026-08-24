package org.hzero.platform.app.service.impl;

import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.app.service.DashboardLayoutService;
import org.hzero.platform.domain.entity.DashboardLayout;
import org.hzero.platform.domain.repository.DashboardLayoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 工作台配置应用服务默认实现
 *
 * @author zhiying.dong@hand-china.com 2018-09-25 10:51:53
 * @author xiaoyu.zhao@hand-china.com
 */
@Service
public class DashboardLayoutServiceImpl implements DashboardLayoutService {

    @Autowired
    private DashboardLayoutRepository dashboardLayoutRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<DashboardLayout> createDashboardLayout(List<DashboardLayout> dashboardLayouts) {
        // 先清除当前用户下原有的layout面板，再插入
        List<DashboardLayout> delDashboardLayouts = dashboardLayoutRepository.selectByCondition(Condition.builder(DashboardLayout.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DashboardLayout.FIELD_USER_ID, DetailsHelper.getUserDetails().getUserId())
                        .andEqualTo(DashboardLayout.FIELD_ROLE_ID, DetailsHelper.getUserDetails().getRoleId())
                        .andEqualTo(DashboardLayout.FIELD_TENANT_ID, DetailsHelper.getUserDetails().getTenantId())
                )
                .build());
        if (CollectionUtils.isNotEmpty(delDashboardLayouts)) {
            dashboardLayoutRepository.batchDeleteByPrimaryKey(delDashboardLayouts);
        }
        dashboardLayoutRepository.batchInsertSelective(dashboardLayouts);
        return dashboardLayouts;
    }

}
