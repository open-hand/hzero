package org.hzero.iam.domain.service.secgrp.observer.dim;


import static org.hzero.iam.infra.constant.Constants.SecGrpAssign.DEFAULT_DATA_SOURCE;
import static org.hzero.iam.infra.constant.Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.iam.domain.entity.SecGrpDclDim;
import org.hzero.iam.domain.repository.RoleAuthorityLineRepository;
import org.hzero.iam.domain.repository.RoleAuthorityRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色订阅数据维度的变化
 *
 * @author bojiangzhou 2020/02/27
 */
@Component
public class DefaultRoleSecGrpDimObserver extends AbstractRoleSecGrpDimObserver {
    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;
    @Autowired
    private RoleAuthorityLineRepository roleAuthorityLineRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignRolesDim(@Nonnull List<Role> roles, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes) {
        if (CollectionUtils.isNotEmpty(roles)) {
            Long roleId;
            for (Role role : roles) {
                roleId = role.getId();
                RoleAuthority roleAuthority = new RoleAuthority();
                roleAuthority.setRoleId(roleId);
                roleAuthority.setAuthDocTypeId(dim.getAuthDocTypeId());
                roleAuthority.setAuthScopeCode(dim.getAuthScopeCode());
                roleAuthority.setMsgFlag(0);
                roleAuthority.setTenantId(role.getTenantId());
                roleAuthority.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
                //分配维度头
                saveRoleAuthForSecGrp(roleAuthority);

                if (CollectionUtils.isNotEmpty(authTypeCodes)) {
                    //处理角色权限行
                    for (String authTypeCode : authTypeCodes) {
                        RoleAuthorityLine authLine = new RoleAuthorityLine();
                        authLine.setRoleAuthId(roleAuthority.getRoleAuthId());
                        authLine.setRoleId(roleId);
                        authLine.setAuthTypeCode(authTypeCode);
                        authLine.setTenantId(role.getTenantId());
                        authLine.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
                        //分配维度行
                        saveRoleAuthLineForSecGrp(authLine);
                    }
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleRolesDim(@Nonnull List<Role> roles, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes) {
        if (CollectionUtils.isNotEmpty(roles)) {
            Long roleId;
            for (Role role : roles) {
                roleId = role.getId();
                RoleAuthority roleAuth = new RoleAuthority();
                roleAuth.setRoleId(roleId);
                roleAuth.setAuthDocTypeId(dim.getAuthDocTypeId());
                roleAuth.setAuthScopeCode(dim.getAuthScopeCode());

                // 查询授权头
                roleAuth = this.roleAuthorityRepository.selectOne(roleAuth);
                if (Objects.nonNull(roleAuth)) {
                    if (CollectionUtils.isNotEmpty(authTypeCodes)) {
                        for (String authTypeCode : authTypeCodes) {
                            RoleAuthorityLine roleAuthorityLine = new RoleAuthorityLine();
                            roleAuthorityLine.setAuthTypeCode(authTypeCode);
                            roleAuthorityLine.setRoleAuthId(roleAuth.getRoleAuthId());

                            removeRoleAuthLineForSecGrp(roleAuthorityLine);
                        }
                    }

                    removeRoleAuthForSecGrp(roleAuth);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleRolesDimLine(@Nonnull List<Role> roles, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes) {
        if (CollectionUtils.isNotEmpty(roles)) {
            Long roleId;
            for (Role role : roles) {
                roleId = role.getId();
                RoleAuthority roleAuth = new RoleAuthority();
                roleAuth.setRoleId(roleId);
                roleAuth.setAuthDocTypeId(dim.getAuthDocTypeId());
                roleAuth.setAuthScopeCode(dim.getAuthScopeCode());

                // 查询授权头
                roleAuth = this.roleAuthorityRepository.selectOne(roleAuth);
                if (Objects.nonNull(roleAuth)) {
                    if (CollectionUtils.isNotEmpty(authTypeCodes)) {
                        for (String authTypeCode : authTypeCodes) {
                            RoleAuthorityLine roleAuthorityLine = new RoleAuthorityLine();
                            roleAuthorityLine.setAuthTypeCode(authTypeCode);
                            roleAuthorityLine.setRoleAuthId(roleAuth.getRoleAuthId());

                            removeRoleAuthLineForSecGrp(roleAuthorityLine);
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存由安全组分配过来的权限维度头
     *
     * @param roleAuthority 权限维度头
     */
    private void saveRoleAuthForSecGrp(RoleAuthority roleAuthority) {
        if (roleAuthority != null && roleAuthority.getRoleId() != null
                && roleAuthority.getAuthDocTypeId() != null) {

            RoleAuthority uniqueRoleAuth = new RoleAuthority();
            uniqueRoleAuth.setRoleId(roleAuthority.getRoleId());
            uniqueRoleAuth.setAuthDocTypeId(roleAuthority.getAuthDocTypeId());
            uniqueRoleAuth.setAuthScopeCode(roleAuthority.getAuthScopeCode());

            RoleAuthority hasRoleAuthority = roleAuthorityRepository.selectOne(uniqueRoleAuth);
            if (hasRoleAuthority != null) {
                if (hasRoleAuthority.equalDefaultDataSource()) {
                    hasRoleAuthority.setDataSource(DEFAULT_SEC_GRP_DATA_SOURCE);
                    roleAuthorityRepository.updateByPrimaryKeySelective(hasRoleAuthority);
                }
                //返回给外部使用
                roleAuthority.setRoleAuthId(hasRoleAuthority.getRoleAuthId());
            } else {
                roleAuthorityRepository.insertSelective(roleAuthority);
            }
        }
    }

    /**
     * 保存由安全组分配过来的权限维度行
     *
     * @param roleAuthorityLine 维度行
     */
    private void saveRoleAuthLineForSecGrp(RoleAuthorityLine roleAuthorityLine) {
        if (roleAuthorityLine != null && roleAuthorityLine.getRoleAuthId() != null
                && roleAuthorityLine.getAuthTypeCode() != null) {

            RoleAuthorityLine uniqueAuthLine = new RoleAuthorityLine();
            uniqueAuthLine.setRoleAuthId(roleAuthorityLine.getRoleAuthId());
            uniqueAuthLine.setAuthTypeCode(roleAuthorityLine.getAuthTypeCode());

            RoleAuthorityLine hasAuthorityLine = roleAuthorityLineRepository.selectOne(uniqueAuthLine);
            if (hasAuthorityLine != null) {
                if (hasAuthorityLine.equalDefaultDataSource()) {
                    hasAuthorityLine.setDataSource(DEFAULT_SEC_GRP_DATA_SOURCE);
                    roleAuthorityLineRepository.updateByPrimaryKeySelective(hasAuthorityLine);
                }
                //返回给外部使用
                roleAuthorityLine.setRoleAuthId(hasAuthorityLine.getRoleAuthId());
            } else {
                roleAuthorityLineRepository.insertSelective(roleAuthorityLine);
            }
        }
    }

    /**
     * 移除由安全组分配的角色权限
     *
     * @param roleAuthority 角色权限
     * @return 删除的权限Id
     */
    private void removeRoleAuthForSecGrp(RoleAuthority roleAuthority) {
        if (roleAuthority != null && roleAuthority.getRoleId() != null
                && roleAuthority.getAuthDocTypeId() != null) {

            RoleAuthority uniqueRoleAuth = new RoleAuthority();
            uniqueRoleAuth.setRoleId(roleAuthority.getRoleId());
            uniqueRoleAuth.setAuthDocTypeId(roleAuthority.getAuthDocTypeId());
            uniqueRoleAuth.setAuthScopeCode(roleAuthority.getAuthScopeCode());

            RoleAuthority hasRoleAuth = roleAuthorityRepository.selectOne(uniqueRoleAuth);
            if (hasRoleAuth != null) {
                if (hasRoleAuth.equalDefaultSecGrpDataSource()) {
                    if (this.countAuthorityLine(hasRoleAuth.getRoleAuthId()) == 0) {
                        hasRoleAuth.setDataSource(DEFAULT_DATA_SOURCE);
                        roleAuthorityRepository.updateByPrimaryKeySelective(hasRoleAuth);
                        //供外部使用
                        roleAuthority.setRoleAuthId(hasRoleAuth.getRoleAuthId());
                    }
                } else if (hasRoleAuth.equalSecGrpDataSource()) {
                    if (this.countAuthorityLine(hasRoleAuth.getRoleAuthId()) == 0) {
                        roleAuthorityRepository.deleteByPrimaryKey(hasRoleAuth);
                        //供外部使用
                        roleAuthority.setRoleAuthId(hasRoleAuth.getRoleAuthId());
                    }
                }
            }
        }
    }

    /**
     * 根据授权头ID和数据来源查询授权行数量
     *
     * @param authorityId 授权头ID
     * @return 授权行数量
     */
    private int countAuthorityLine(Long authorityId) {
        RoleAuthorityLine condition = new RoleAuthorityLine();
        condition.setRoleAuthId(authorityId);

        return this.roleAuthorityLineRepository.selectCount(condition);
    }


    /**
     * 移除由安全组分配的角色权限行
     *
     * @param roleAuthorityLine 角色权限行
     * @return 删除的权限行Id
     */
    private void removeRoleAuthLineForSecGrp(RoleAuthorityLine roleAuthorityLine) {
        if (roleAuthorityLine != null && roleAuthorityLine.getRoleAuthId() != null
                && roleAuthorityLine.getAuthTypeCode() != null) {

            RoleAuthorityLine uniqueRoleAuth = new RoleAuthorityLine();
            uniqueRoleAuth.setRoleAuthId(roleAuthorityLine.getRoleAuthId());
            uniqueRoleAuth.setAuthTypeCode(roleAuthorityLine.getAuthTypeCode());

            RoleAuthorityLine hasAuthLine = roleAuthorityLineRepository.selectOne(uniqueRoleAuth);
            if (hasAuthLine != null) {
                if (hasAuthLine.equalDefaultSecGrpDataSource()) {
                    hasAuthLine.setDataSource(DEFAULT_DATA_SOURCE);
                    roleAuthorityLineRepository.updateByPrimaryKeySelective(hasAuthLine);
                    //供外部使用
                    roleAuthorityLine.setRoleAuthLineId(hasAuthLine.getRoleAuthLineId());
                } else if (hasAuthLine.equalSecGrpDataSource()) {
                    roleAuthorityLineRepository.deleteByPrimaryKey(hasAuthLine);
                    //供外部使用
                    roleAuthorityLine.setRoleAuthLineId(hasAuthLine.getRoleAuthLineId());
                }
            }
        }
    }
}
