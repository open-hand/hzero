package org.hzero.iam.app.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import org.hzero.iam.app.service.HrUnitService;
import org.hzero.iam.domain.entity.HrUnit;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.HpfmHrUnitRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.constant.HiamResourceLevel;

/**
 *
 * @author mingwei.liu@hand-china.com 2018/7/15
 */
@Service
public class HrUnitServiceImpl implements HrUnitService {
    @Autowired
    HpfmHrUnitRepository hpfmHrUnitRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public List<HrUnit> listWholeHrUnitTreeOfTenant(Long tenantId, Long userId) {
        return hpfmHrUnitRepository.queryWholeHrUnitTreeByTenantId(tenantId, userId);
    }

    @Override
    public List<HrUnit> listAssignableHrUnitTreeByRoleId(Long roleId, String unitCode, String unitName) {
        Role role = roleRepository.selectByPrimaryKey(roleId);
        Assert.notNull(role, "role cannot be invalid");

        if (HiamResourceLevel.SITE.value().equals(role.getParentRoleAssignLevel()) ||
                HiamResourceLevel.ORGANIZATION.value().equals(role.getParentRoleAssignLevel())) {
            // 角色所在租户的HR组织机构树
            return hpfmHrUnitRepository.queryHrUnitSubTree(role.getTenantId(), null, unitCode, unitName);
        } else {
            return Collections.emptyList();
        }
    }
}
