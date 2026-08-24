package org.hzero.iam.domain.service.secgrp.observer.dcl;


import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.RoleAuthDataLineRepository;
import org.hzero.iam.domain.repository.RoleAuthDataRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;


/**
 * 角色订阅数据维度的变化
 *
 * @author bojiangzhou 2020/02/27
 */
@Component
public class DefaultRoleSecGrpDclObserver extends AbstractRoleSecGrpDclObserver {

    @Autowired
    private RoleAuthDataRepository roleAuthDataRepository;
    @Autowired
    private RoleAuthDataLineRepository roleAuthDataLineRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignRolesDcl(@Nonnull List<Role> roles, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role role : roles) {
                RoleAuthData roleAuthData = new RoleAuthData();
                roleAuthData.setRoleId(role.getId());
                roleAuthData.setTenantId(role.getTenantId());
                roleAuthData.setAuthorityTypeCode(dcl.getAuthorityTypeCode());
                roleAuthData.setIncludeAllFlag(Constants.Authority.NOT_INCLUDE);
                roleAuthData.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);

                saveRoleAuthDataForSecGrp(roleAuthData);

                if (dclLines != null && roleAuthData.getAuthDataId() != null) {
                    for (SecGrpDclLine dclLine : dclLines) {

                        RoleAuthDataLine authDataLine = new RoleAuthDataLine();
                        authDataLine.setAuthDataId(roleAuthData.getAuthDataId());
                        authDataLine.setDataId(dclLine.getDataId());
                        authDataLine.setDataCode(dclLine.getDataCode());
                        authDataLine.setTenantId(role.getTenantId());
                        authDataLine.setDataName(dclLine.getDataName());
                        authDataLine.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);

                        saveRoleAuthDataLineForSecGrp(authDataLine);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleRolesDcl(@Nonnull List<Role> roles, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role role : roles) {
                RoleAuthData roleAuthData = new RoleAuthData();
                roleAuthData.setRoleId(role.getId());
                roleAuthData.setTenantId(role.getTenantId());
                roleAuthData.setAuthorityTypeCode(dcl.getAuthorityTypeCode());

                roleAuthData = roleAuthDataRepository.selectOne(roleAuthData);
                if (Objects.nonNull(roleAuthData)) {
                    // 开始删除行
                    if (CollectionUtils.isNotEmpty(dclLines)) {
                        for (SecGrpDclLine dclLine : dclLines) {
                            RoleAuthDataLine authDataLine = new RoleAuthDataLine();
                            authDataLine.setAuthDataId(roleAuthData.getAuthDataId());
                            authDataLine.setDataId(dclLine.getDataId());

                            // 删除行
                            this.removeRoleAuthDataLineForSecGrp(authDataLine);
                        }
                    }

                    // 删除头
                    this.removeRoleAuthDataForSecGrp(roleAuthData);
                }
            }
        }
    }

    /**
     * 保存角色数据权限
     *
     * @param roleAuthData 角色数据权限
     */
    private void saveRoleAuthDataForSecGrp(RoleAuthData roleAuthData) {
        Assert.notNull(roleAuthData, RoleAuthData.ROLE_AUTH_DATA_NOT_NULL);
        Assert.notNull(roleAuthData.getRoleId(), RoleAuthData.ROLE_ID_REQUIRED);
        Assert.notNull(roleAuthData.getTenantId(), RoleAuthData.TENATN_ID_REQUIRED);
        Assert.notNull(roleAuthData.getAuthorityTypeCode(), RoleAuthData.AUTHORITY_TYPE_CODE_REQUIRED);

        RoleAuthData uniqueAuthData = new RoleAuthData();
        uniqueAuthData.setRoleId(roleAuthData.getRoleId());
        uniqueAuthData.setTenantId(roleAuthData.getTenantId());
        uniqueAuthData.setAuthorityTypeCode(roleAuthData.getAuthorityTypeCode());

        RoleAuthData hasRoleAuthData = roleAuthDataRepository.selectOne(uniqueAuthData);
        if (hasRoleAuthData != null) {
            roleAuthData.setAuthDataId(hasRoleAuthData.getAuthDataId());
            if (hasRoleAuthData.containDefaultDataSource()) {
                hasRoleAuthData.setDataSource(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
                roleAuthDataRepository.updateByPrimaryKeySelective(hasRoleAuthData);
            }
        } else {
            roleAuthDataRepository.insertSelective(roleAuthData);
        }
    }

    /**
     * 保存角色数据权限行
     *
     * @param authDataLine 角色数据权限行
     */
    private void saveRoleAuthDataLineForSecGrp(RoleAuthDataLine authDataLine) {
        Assert.notNull(authDataLine, RoleAuthorityLine.ROLE_AUTHORITY_LINE_NOT_NULL);
        Assert.notNull(authDataLine.getAuthDataId(), RoleAuthorityLine.AUTH_DATA_ID_REQUIRED);
        Assert.notNull(authDataLine.getDataId(), RoleAuthorityLine.DATA_ID_REQUIRED);

        RoleAuthDataLine uniqueDataLine = new RoleAuthDataLine();
        uniqueDataLine.setAuthDataId(authDataLine.getAuthDataId());
        uniqueDataLine.setDataId(authDataLine.getDataId());

        RoleAuthDataLine hasDataLine = roleAuthDataLineRepository.selectOne(uniqueDataLine);
        if (hasDataLine != null) {
            authDataLine.setAuthDataLineId(hasDataLine.getAuthDataLineId());
            if (hasDataLine.containDefaultDataSource()) {
                hasDataLine.setDataSource(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
                roleAuthDataLineRepository.updateByPrimaryKeySelective(hasDataLine);
            }
        } else {
            roleAuthDataLineRepository.insertSelective(authDataLine);
        }
    }

    /**
     * 删除角色数据权限
     *
     * @param roleAuthData 角色数据权限
     */
    private void removeRoleAuthDataForSecGrp(RoleAuthData roleAuthData) {
        if (Objects.nonNull(roleAuthData)) {
            if (roleAuthData.equalDefaultSecGrpDataSource()) {
                // 更新成默认状态
                roleAuthData.setDataSource(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
                this.roleAuthDataRepository.updateByPrimaryKeySelective(roleAuthData);
            } else if (roleAuthData.equalSecGrpDataSource()) {
                RoleAuthDataLine roleAuthDataLine = new RoleAuthDataLine();
                roleAuthDataLine.setAuthDataId(roleAuthData.getAuthDataId());
                // 没有行才开始处理删除头的逻辑
                if (this.roleAuthDataLineRepository.selectCount(roleAuthDataLine) == 0) {
                    this.roleAuthDataRepository.deleteByPrimaryKey(roleAuthData);
                }
            }
        }
    }

    /**
     * 删除角色数据权限行
     *
     * @param authDataLine 角色数据权限行
     */
    private void removeRoleAuthDataLineForSecGrp(RoleAuthDataLine authDataLine) {
        if (authDataLine != null
                && authDataLine.getAuthDataId() != null
                && authDataLine.getDataId() != null) {

            RoleAuthDataLine uniqueDataLine = new RoleAuthDataLine();
            uniqueDataLine.setAuthDataId(authDataLine.getAuthDataId());
            uniqueDataLine.setDataId(authDataLine.getDataId());

            RoleAuthDataLine hasDataLine = roleAuthDataLineRepository.selectOne(uniqueDataLine);
            if (hasDataLine != null) {
                if (hasDataLine.equalDefaultSecGrpDataSource()) {
                    hasDataLine.setDataSource(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
                    roleAuthDataLineRepository.updateByPrimaryKeySelective(hasDataLine);
                } else if (hasDataLine.equalSecGrpDataSource()) {
                    roleAuthDataLineRepository.deleteByPrimaryKey(hasDataLine);
                }
            }
        }
    }
}
