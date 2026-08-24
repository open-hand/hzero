package org.hzero.iam.domain.service.secgrp.authority.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAclDashboard;
import org.hzero.iam.domain.repository.SecGrpAclDashboardRepository;
import org.hzero.iam.domain.service.secgrp.authority.AbstractSecGrpAuthorityService;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 安全组权限服务——工作台权限
 *
 * @author bojiangzhou 2020/02/12
 */
@Component
public class SecGrpDashAuthorityService extends AbstractSecGrpAuthorityService<SecGrpAclDashboard> {

    private final Logger logger = LoggerFactory.getLogger(SecGrpDashAuthorityService.class);

    @Autowired
    private SecGrpAclDashboardRepository secGrpAclDashboardRepository;

    @Override
    public boolean support(@Nonnull SecGrpAuthorityType authorityType) {
        return SecGrpAuthorityType.ACL_DASHBOARD.equals(authorityType);
    }

    @Override
    public void copySecGrpAuthority(@Nonnull List<SecGrp> sourceSecGrps, @Nonnull SecGrp targetSecGrp) {
        List<Long> secGrpIds = sourceSecGrps.stream().map(SecGrp::getSecGrpId).collect(Collectors.toList());
        List<SecGrpAclDashboard> secGrpAclDashboards = secGrpAclDashboardRepository.listDashboardBySecGrpIds(secGrpIds);

        if (CollectionUtils.isEmpty(secGrpAclDashboards)) {
            return;
        }

        Map<Long, SecGrpAclDashboard> secGrpAclDashboardMap = new HashMap<>(16);
        secGrpAclDashboards.forEach(item -> {
            SecGrpAclDashboard dashboard = secGrpAclDashboardMap.get(item.getCardId());
            if (dashboard == null || item.getCreationDate().after(dashboard.getCreationDate())) {
                // 复制自建的安全组字段权限只需要修改安全组ID即可
                item.setSecGrpId(targetSecGrp.getSecGrpId());
                item.setSecGrpAclDashboardId(null);
                item.setTenantId(targetSecGrp.getTenantId());
                secGrpAclDashboardMap.put(item.getCardId(), item);
            }
        });
        secGrpAclDashboardMap.values().parallelStream().forEach(dash -> secGrpAclDashboardRepository.insertSelective(dash));
    }

    @Override
    public void deleteAuthorityBySecGrpId(@Nonnull Long secGrpId) {
        secGrpAclDashboardRepository.delete(new SecGrpAclDashboard(secGrpId));
    }

    @Override
    protected void addSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpAclDashboard> authorities) {
        // 插入数据
        this.secGrpAclDashboardRepository.batchInsertSelective(authorities);
    }

    @Override
    protected void removeSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpAclDashboard> authorities) {
        // 删除数据
        this.secGrpAclDashboardRepository.batchDeleteByPrimaryKey(authorities);
    }
}
