package org.hzero.iam.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.app.service.SecGrpAclService;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAcl;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.domain.service.secgrp.authority.impl.SecGrpAclAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 安全组访问权限应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@Service
public class SecGrpAclServiceImpl implements SecGrpAclService {

    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    private SecGrpAclAuthorityService aclAuthorityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSecGrpAcl(Long tenantId, Long secGrpId, List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }

        SecGrp secGrp = secGrpRepository.querySecGrp(tenantId, secGrpId);

        secGrp.checkOperable();

        List<SecGrpAcl> secGrpAcls = SecGrpAcl.toList(secGrp, permissionIds);

        // 访问权限添加到安全组
        aclAuthorityService.addSecGrpAuthority(secGrpId, secGrpAcls);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSecGrpAcl(Long tenantId, Long secGrpId, List<Long> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return;
        }

        SecGrp secGrp = secGrpRepository.querySecGrp(tenantId, secGrpId);

        secGrp.checkOperable();

        List<SecGrpAcl> secGrpAcls = SecGrpAcl.toList(secGrp, permissionIds);

        // 移除安全组中的权限
        aclAuthorityService.removeSecGrpAuthority(secGrpId, secGrpAcls);
    }

}
