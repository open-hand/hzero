package org.hzero.iam.app.service.impl;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.SecGrpDclDTO;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.app.service.SecGrpDclService;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.iam.domain.repository.SecGrpDclRepository;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.domain.service.secgrp.authority.dcl.DclServiceManager;
import org.hzero.iam.domain.service.secgrp.authority.impl.SecGrpDclAuthorityService;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 安全组数据权限应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Service
public class SecGrpDclServiceImpl implements SecGrpDclService {
    public static final Logger logger = LoggerFactory.getLogger(SecGrpDclServiceImpl.class);
    @Autowired
    private SecGrpDclRepository secGrpDclRepository;
    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private SecGrpDclAuthorityService dclAuthorityService;
    /**
     * 数据权限服务管理器
     */
    @Autowired
    private DclServiceManager dclServiceManager;

    @Override
    public SecGrpDclDTO querySecGrpDclAuthority(Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setSecGrpId(secGrpId);

        queryDTO.validateSecGrpParam();
        // 查询安全组数据权限详细，并返回结果
        return this.dclServiceManager.querySecGrpDclAuthority(queryDTO.getAuthorityTypeCode(), queryDTO, pageRequest);
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignableAuthority(Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest) {
        Assert.notNull(queryDTO.getRoleId(), "Param roleId should not be null.");
        CustomUserDetails self = UserUtils.getUserDetails();
        queryDTO.setSecGrpId(secGrpId);
        queryDTO.setTenantId(self.getTenantId());

        queryDTO.validateSecGrpParam();
        // 查询安全组可分配的数据权限，并返回结果
        return this.dclServiceManager.querySecGrpDclAssignableAuthority(queryDTO.getAuthorityTypeCode(), queryDTO, pageRequest);
    }

    @Override
    public SecGrpDclDTO querySecGrpDclAssignedAuthority(Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setSecGrpId(secGrpId);

        queryDTO.validateSecGrpParam();
        // 查询安全组已分配的数据权限，并返回结果
        return this.dclServiceManager.querySecGrpDclAssignedAuthority(queryDTO.getAuthorityTypeCode(), queryDTO, pageRequest);
    }

    @Override
    public SecGrpDclDTO queryRoleSecGrpDclAuthority(Long roleId, Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setAuthorityTypeCode(queryDTO.getAuthorityTypeCode());
        queryDTO.setSecGrpId(secGrpId);
        queryDTO.setRoleId(roleId);

        queryDTO.validateRoleParam();
        // 查询角色安全组已分配的数据权限，并返回结果
        return this.dclServiceManager.queryRoleSecGrpDclAuthority(queryDTO.getAuthorityTypeCode(), roleId, queryDTO, pageRequest);
    }

    @Override
    public SecGrpDclDTO queryUserSecGrpDclAuthority(Long userId, Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setSecGrpId(secGrpId);

        queryDTO.validateUserParam();
        // 查询安全组已分配的数据权限，并返回结果
        return this.dclServiceManager.querySecGrpDclAssignedAuthority(queryDTO.getAuthorityTypeCode(), queryDTO, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSecGrpDclAuthority(Long secGrpId, String authorityTypeCode, List<SecGrpDclLine> dclLines) {
        // 处理数据
        this.dclServiceManager.saveSecGrpDclAuthority(authorityTypeCode, secGrpId, dclLines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSecGrpDclAuthority(Long secGrpId, String authorityTypeCode, List<SecGrpDclLine> dclLines) {
        // 处理数据
        this.dclServiceManager.deleteSecGrpDclAuthority(authorityTypeCode, secGrpId, dclLines);
    }
}
