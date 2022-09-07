package org.hzero.iam.domain.service.secgrp.observer.dcl;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.UserAuthorityLineRepository;
import org.hzero.iam.domain.repository.UserAuthorityRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 用户订阅数据维度的变化
 *
 * @author bojiangzhou 2020/02/27
 */
@Component
public class DefaultUserSecGrpDclObserver extends AbstractUserSecGrpDclObserver {

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;
    @Autowired
    private UserAuthorityLineRepository userAuthorityLineRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignUsersDcl(@Nonnull List<SecGrpAssign> secGrpAssigns, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        if (CollectionUtils.isNotEmpty(secGrpAssigns)) {
            for (SecGrpAssign secGrpAssign : secGrpAssigns) {
                Long userId = secGrpAssign.getDimensionValue();
                Long tenantId = secGrpAssign.getTenantId();
                UserAuthority userAuthority = new UserAuthority(userId, tenantId, dcl.getAuthorityTypeCode());
                userAuthority.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
                userAuthority.setIncludeAllFlag(Constants.Authority.NOT_INCLUDE);

                saveUserAuthForSecGrp(userAuthority);

                if (dclLines != null && userAuthority.getAuthorityId() != null) {
                    for (SecGrpDclLine dclLine : dclLines) {
                        UserAuthorityLine authorityLine = new UserAuthorityLine();
                        authorityLine.setAuthorityId(userAuthority.getAuthorityId());
                        authorityLine.setTenantId(tenantId);
                        authorityLine.setDataId(dclLine.getDataId());
                        authorityLine.setDataCode(dclLine.getDataCode());
                        authorityLine.setDataName(dclLine.getDataName());
                        authorityLine.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);

                        saveUserAuthLineForSecGrp(authorityLine);
                    }
                }
            }
            // 用户权限处理完成后添加用户权限缓存标识
            for (SecGrpAssign secGrpAssign : secGrpAssigns) {
                Long userId = secGrpAssign.getDimensionValue();
                Long tenantId = secGrpAssign.getTenantId();
                userAuthorityRepository.processUserAuthorityCache(tenantId, userId, dcl.getAuthorityTypeCode());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleUsersDcl(@Nonnull List<SecGrpAssign> secGrpAssigns, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        if (CollectionUtils.isNotEmpty(secGrpAssigns)) {
            for (SecGrpAssign secGrpAssign : secGrpAssigns) {
                Long userId = secGrpAssign.getDimensionValue();
                Long tenantId = secGrpAssign.getTenantId();
                UserAuthority userAuthority = new UserAuthority(userId, tenantId, dcl.getAuthorityTypeCode());

                removeUserAuthForSecGrp(userAuthority);

                if (dclLines != null && userAuthority.getAuthorityId() != null) {
                    for (SecGrpDclLine dclLine : dclLines) {
                        UserAuthorityLine authorityLine = new UserAuthorityLine(userAuthority.getAuthorityId(), dclLine.getDataId());

                        removeUserAuthLineForSecGrp(authorityLine);
                    }
                }
            }
            // 用户权限移除完成后判断用户下是否仍存在权限，不存在则清除用户权限缓存标识
            for (SecGrpAssign secGrpAssign : secGrpAssigns) {
                Long userId = secGrpAssign.getDimensionValue();
                Long tenantId = secGrpAssign.getTenantId();
                userAuthorityRepository.processUserAuthorityCache(tenantId, userId, dcl.getAuthorityTypeCode());
            }
        }
    }

    /**
     * 保存用户权限
     *
     * @param userAuthority 用户权限信息
     */
    private void saveUserAuthForSecGrp(UserAuthority userAuthority) {
        Assert.notNull(userAuthority, UserAuthority.USER_AUTHORITY_NOT_NULL);
        Assert.notNull(userAuthority.getUserId(), UserAuthority.USER_ID_REQUIRED);
        Assert.notNull(userAuthority.getTenantId(), UserAuthority.TENANT_ID_REQUIRED);
        Assert.notNull(userAuthority.getAuthorityTypeCode(), UserAuthority.AUTHORITY_TYPE_CODE_REQUIRED);

        UserAuthority uniqueUserAuth = new UserAuthority();
        uniqueUserAuth.setUserId(userAuthority.getUserId());
        uniqueUserAuth.setAuthorityTypeCode(userAuthority.getAuthorityTypeCode());
        uniqueUserAuth.setTenantId(userAuthority.getTenantId());

        UserAuthority hasUserAuthority = userAuthorityRepository.selectOne(uniqueUserAuth);
        if (hasUserAuthority != null) {
            //供外部使用
            userAuthority.setAuthorityId(hasUserAuthority.getAuthorityId());

            if (hasUserAuthority.containDefaultDataSource()) {
                hasUserAuthority.setDataSource(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
                userAuthorityRepository.updateByPrimaryKeySelective(hasUserAuthority);
            }
        } else {
            userAuthorityRepository.insertSelective(userAuthority);
        }
    }

    /**
     * 保存用户权限行
     *
     * @param userAuthorityLine 用户权限行
     */
    private void saveUserAuthLineForSecGrp(UserAuthorityLine userAuthorityLine) {
        Assert.notNull(userAuthorityLine, UserAuthorityLine.USER_AUTHORITY_LINE_NOT_NULL);
        Assert.notNull(userAuthorityLine.getAuthorityId(), UserAuthorityLine.AUTHORITY_ID_REQUIRED);
        Assert.notNull(userAuthorityLine.getDataId(), UserAuthorityLine.DATA_ID_REQUIRED);

        UserAuthorityLine uniqueAuthLine = new UserAuthorityLine();
        uniqueAuthLine.setAuthorityId(userAuthorityLine.getAuthorityId());
        uniqueAuthLine.setDataId(userAuthorityLine.getDataId());

        UserAuthorityLine hasAuthLine = userAuthorityLineRepository.selectOne(uniqueAuthLine);
        if (hasAuthLine != null) {
            //外部使用
            userAuthorityLine.setAuthorityLineId(hasAuthLine.getAuthorityLineId());

            if (hasAuthLine.containDefaultDataSource()) {
                hasAuthLine.setDataSource(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
                userAuthorityLineRepository.updateByPrimaryKeySelective(hasAuthLine);
            }
        } else {
            userAuthorityLineRepository.insertSelective(userAuthorityLine);
        }
    }

    /**
     * 移除用户权限
     *
     * @param userAuthority 用户权限
     */
    private void removeUserAuthForSecGrp(UserAuthority userAuthority) {
        if (userAuthority != null && userAuthority.getUserId() != null
                && userAuthority.getTenantId() != null
                && StringUtils.isNotEmpty(userAuthority.getAuthorityTypeCode())) {

            UserAuthority uniqueUserAuth = new UserAuthority();
            uniqueUserAuth.setUserId(userAuthority.getUserId());
            uniqueUserAuth.setTenantId(userAuthority.getTenantId());
            uniqueUserAuth.setAuthorityTypeCode(userAuthority.getAuthorityTypeCode());

            UserAuthority hasUserAuthority = userAuthorityRepository.selectOne(uniqueUserAuth);
            if (hasUserAuthority != null) {
                if (hasUserAuthority.equalDefaultSecGrpDataSource()) {
                    hasUserAuthority.setDataSource(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
                    userAuthorityRepository.updateByPrimaryKeySelective(hasUserAuthority);
                    // 外部使用
                    userAuthority.setAuthorityId(hasUserAuthority.getAuthorityId());
                } else if (hasUserAuthority.equalSecGrpDataSource()) {
                    userAuthorityRepository.delete(userAuthority);
                    // 外部使用
                    userAuthority.setAuthorityId(hasUserAuthority.getAuthorityId());
                }
            }
        }
    }

    /**
     * 移除用户权限行
     *
     * @param authorityLine 用户权限行
     */
    private void removeUserAuthLineForSecGrp(UserAuthorityLine authorityLine) {
        if (authorityLine != null && authorityLine.getAuthorityId() != null
                && authorityLine.getDataId() != null) {

            UserAuthorityLine userAuthorityLine = new UserAuthorityLine();
            userAuthorityLine.setAuthorityId(authorityLine.getAuthorityId());
            userAuthorityLine.setDataId(authorityLine.getDataId());

            UserAuthorityLine hasAuthorityLine = userAuthorityLineRepository.selectOne(userAuthorityLine);
            if (hasAuthorityLine != null) {
                if (hasAuthorityLine.equalDefaultSecGrpDataSource()) {
                    hasAuthorityLine.setDataSource(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
                    userAuthorityLineRepository.updateByPrimaryKeySelective(hasAuthorityLine);
                    // 外部使用
                    authorityLine.setAuthorityLineId(hasAuthorityLine.getAuthorityLineId());
                } else if (hasAuthorityLine.equalSecGrpDataSource()) {
                    userAuthorityLineRepository.delete(authorityLine);
                    // 外部使用
                    authorityLine.setAuthorityLineId(hasAuthorityLine.getAuthorityLineId());
                }
            }
        }
    }
}
