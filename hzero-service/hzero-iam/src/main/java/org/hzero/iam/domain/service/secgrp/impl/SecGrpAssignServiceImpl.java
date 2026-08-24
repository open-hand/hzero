package org.hzero.iam.domain.service.secgrp.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.service.secgrp.SecGrpAssignService;
import org.hzero.iam.domain.service.secgrp.SecGrpCoreService;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 安全组分配接口实现
 *
 * @author bojiangzhou 2020/02/20
 * @author xingxingwu.hand-china.com 2019/12/10 10:51
 */
@Service
public class SecGrpAssignServiceImpl implements SecGrpAssignService {

    @Autowired
    private SecGrpCoreService secGrpCoreService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void roleAssignSecGrp(Long roleId, List<Long> secGrpIds) {
        if (CollectionUtils.isEmpty(secGrpIds) || roleId == null) {
            return;
        }

        this.secGrpCoreService.assignRoleSecGrp(roleId, secGrpIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void roleRecycleSecGrp(Long roleId, List<Long> secGrpIds) {
        if (CollectionUtils.isEmpty(secGrpIds) || roleId == null) {
            return;
        }

        secGrpCoreService.recycleRoleSecGrp(roleId, secGrpIds);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void secGrpAssignRole(Long secGrpId, List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds) || secGrpId == null) {
            return;
        }
        List<Long> secGrpIds = Collections.singletonList(secGrpId);
        roleIds.forEach(roleId -> roleAssignSecGrp(roleId, secGrpIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void secGrpRecycleRole(Long secGrpId, List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds) || secGrpId == null) {
            return;
        }
        List<Long> secGrpIds = Collections.singletonList(secGrpId);
        roleIds.forEach(roleId -> roleRecycleSecGrp(roleId, secGrpIds));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, String authorityType) {
        secGrpCoreService.shieldRoleSecGrpAuthority(roleId, secGrpId, authorityId, SecGrpAuthorityType.typeOf(authorityType));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShieldRoleSecGrpAuthority(Long roleId, Long secGrpId, Long authorityId, String authorityType) {
        secGrpCoreService.cancelShieldRoleSecGrpAuthority(roleId, secGrpId, authorityId, SecGrpAuthorityType.typeOf(authorityType));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userAssignSecGrp(Long userId, List<Long> secGrpIds) {
        secGrpCoreService.assignUserSecGrp(userId, secGrpIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userRecycleSecGrp(Long userId, List<Long> secGrpIds) {
        secGrpCoreService.recycleUserSecGrp(userId, secGrpIds);
    }

}
