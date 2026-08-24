package org.hzero.iam.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.app.service.SecGrpAclDashboardService;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAclDashboard;
import org.hzero.iam.domain.repository.SecGrpAclDashboardRepository;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.domain.service.secgrp.authority.impl.SecGrpDashAuthorityService;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 安全组工作台配置应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@Service
public class SecGrpAclDashboardServiceImpl implements SecGrpAclDashboardService {
    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private SecGrpAclDashboardRepository secGrpAclDashboardRepository;
    @Autowired
    private SecGrpDashAuthorityService dashAuthorityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSecGrpDashboard(Long tenantId, Long secGrpId, List<SecGrpAclDashboard> secGrpDashboards) {
        if (CollectionUtils.isEmpty(secGrpDashboards)) {
            return;
        }

        secGrpDashboards.forEach((dash) -> {
            dash.setSecGrpId(secGrpId);
            dash.setTenantId(tenantId);
        });

        dashAuthorityService.addSecGrpAuthority(secGrpId, secGrpDashboards);
    }

    @Override
    public void updateSecGrpDashboard(Long tenantId, Long secGrpId, List<SecGrpAclDashboard> secGrpDashboards) {
        if (CollectionUtils.isEmpty(secGrpDashboards)) {
            return;
        }
        SecGrp secGrp = secGrpRepository.querySecGrp(tenantId, secGrpId);
        Assert.notNull(secGrp, "SecGrp not found.");

        secGrpAclDashboardRepository.batchUpdateOptional(secGrpDashboards,
                SecGrpAclDashboard.FIELD_X,
                SecGrpAclDashboard.FIELD_Y,
                SecGrpAclDashboard.FIELD_REMARK,
                SecGrpAclDashboard.FIELD_DEFAULT_DISPLAY_FLAG
        );
    }

    @Override
    public void removeSecGrpDashboard(Long tenantId, Long secGrpId, List<SecGrpAclDashboard> secGrpDashboards) {
        dashAuthorityService.removeSecGrpAuthority(secGrpId, secGrpDashboards);
    }
}
