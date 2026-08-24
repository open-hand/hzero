package org.hzero.iam.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.exception.IllegalOperationException;
import org.hzero.iam.api.dto.SecGrpQueryDTO;
import org.hzero.iam.app.service.SecGrpService;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.domain.service.secgrp.SecGrpCoreService;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpAuthorityDTO;
import org.hzero.mybatis.common.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * 安全组应用服务
 *
 * @author bojiangzhou 2020/02/12 代码重构
 * @author xingxing.wu@hand-china.com 2019-10-17 11:26:19
 */
@Service
public class SecGrpServiceImpl implements SecGrpService {

    @Autowired
    private SecGrpCoreService secGrpCoreService;
    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private SecGrpAclRepository secGrpAclRepository;
    @Autowired
    private SecGrpDclLineRepository secGrpDclLineRepository;
    @Autowired
    private SecGrpAclFieldRepository secGrpAclFieldRepository;
    @Autowired
    private SecGrpDclDimRepository secGrpDclDimRepository;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public SecGrp createSecGrp(Long tenantId, SecGrp secGrp) {
        secGrp.setTenantId(tenantId);
        secGrp.setupCurrentRole(null);

        // 检查安全组是否已存在
        secGrpCoreService.checkSecGrpExists(secGrp.getTenantId(), secGrp.getSecGrpLevel(), secGrp.getSecGrpCode());
        // 插入数据
        secGrpRepository.insertSelective(secGrp);
        // 初始化安全组相关权限
        secGrpCoreService.initSecGrpAuthority(secGrp);
        return secGrp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SecGrp copySecGrp(Long tenantId, Long sourceSecGrpId, SecGrp secGrp) {
        secGrp.setupCurrentRole(null);

        // 查询源安全组
        List<SecGrp> sourceSecGrps = listSourceSecGrp(Collections.singletonList(sourceSecGrpId), secGrp);

        // 插入新安全组
        secGrp.setTenantId(tenantId);
        secGrpRepository.insertSelective(secGrp);

        // 复制安全组权限
        secGrpCoreService.copySecGrpAuthority(sourceSecGrps, secGrp);

        return secGrp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SecGrp quickCreateSecGrp(Long tenantId, List<Long> sourceSecGrpIds, SecGrp secGrp, Long roleId) {
        if (CollectionUtils.isEmpty(sourceSecGrpIds)) {
            return secGrp;
        }
        secGrp.setupCurrentRole(roleId);

        // 查询源安全组
        List<SecGrp> sourceSecGrps = listSourceSecGrp(sourceSecGrpIds, secGrp);

        // 插入目标安全组
        secGrp.setTenantId(Optional.ofNullable(tenantId).orElse(secGrp.getTenantId()));
        // 快速创建的安全组是草稿状态
        secGrp.setDraftFlag(BaseConstants.Flag.YES);
        secGrpRepository.insertSelective(secGrp);

        // 复制安全组权限
        secGrpCoreService.copySecGrpAuthority(sourceSecGrps, secGrp);

        return secGrp;

    }

    private List<SecGrp> listSourceSecGrp(List<Long> sourceSecGrpIds, SecGrp secGrp) {
        // 查询源安全组
        List<SecGrp> sourceSecGrps = secGrpRepository.selectRoleAuthorizedSecGrp(sourceSecGrpIds, secGrp.getRoleId());
        if (CollectionUtils.isEmpty(sourceSecGrps)) {
            throw new CommonException("hiam.error.secgrp.noAuthority");
        }

        for (SecGrp sourceSecGrp : sourceSecGrps) {
            if (!StringUtils.equals(sourceSecGrp.getSecGrpLevel(), secGrp.getSecGrpLevel())) {
                throw new CommonException("hiam.warn.secGrp.copySecGrpLevelDiff");
            }
        }
        return sourceSecGrps;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SecGrp updateSecGrp(Long tenantId, SecGrp secGrp) {
        SecGrp oldSecGrp = secGrpRepository.querySecGrp(tenantId, secGrp.getSecGrpId());

        // 不允许将安全组更新为草稿状态，因为草稿状态是可以删除的
        if (BaseConstants.Flag.YES.equals(secGrp.getDraftFlag())) {
            throw new IllegalOperationException("Draft security group can't update.");
        }

        secGrpRepository.updateOptional(secGrp,
                SecGrp.FIELD_ENABLED_FLAG,
                SecGrp.FIELD_REMARK,
                SecGrp.FIELD_SEC_GRP_NAME,
                SecGrp.FIELD_DRAFT_FLAG
        );

        // 判断是否为启用操作
        if (oldSecGrp.isEnabled(secGrp)) {
            secGrpCoreService.enableSecGrp(secGrp.getSecGrpId());
        }
        // 判断是否为禁用操作
        else if (oldSecGrp.isDisabled(secGrp)) {
            secGrpCoreService.disableSecGrp(secGrp.getSecGrpId());
        }

        return secGrp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDraftSecGrp(Long tenantId, Long secGrpId) {
        SecGrp params = new SecGrp();
        params.setSecGrpId(secGrpId);
        params.setTenantId(tenantId);
        SecGrp secGrp = secGrpRepository.selectOneOptional(params, new Criteria()
                .select(SecGrp.FIELD_SEC_GRP_ID, SecGrp.FIELD_DRAFT_FLAG)
                .where(SecGrp.FIELD_TENANT_ID, SecGrp.FIELD_SEC_GRP_ID)
        );

        Assert.isTrue(secGrp != null, BaseConstants.ErrorCode.DATA_EXISTS);

        if (!BaseConstants.Flag.YES.equals(secGrp.getDraftFlag())) {
            throw new CommonException("hiam.warn.secGrp.onlyDraftCanDelete");
        }

        // 删除安全组
        secGrpCoreService.deleteSecGrp(secGrpId);
    }

    @Override
    public void checkDuplicate(Long tenantId, String level, String secGrpCode) {
        if (null == tenantId || StringUtils.isAnyBlank(level, secGrpCode)) {
            throw new CommonException("Param tenantId, level and secGrpCode must not blank.");
        }
        // 检查安全组是否已存在
        secGrpCoreService.checkSecGrpExists(tenantId, level, secGrpCode);
    }

    @Override
    public SecGrpAuthorityDTO selectSecGrpAuthorityInSecGrp(Long secGrpId) {
        SecGrpAuthorityDTO secGrpAuthority = new SecGrpAuthorityDTO();

        // 查询安全组访问权限
        secGrpAuthority.setSecGrpAclList(secGrpAclRepository.selectAclInGrp(secGrpId));
        // 查询安全组数据权限
        secGrpAuthority.setSecGrpDclLineList(secGrpDclLineRepository.selectSecGrpDclInGrp(secGrpId));
        // 查询安全组下能自我管理的，不受父级限制的安全组访问字段
        secGrpAuthority.setSecGrpAclFieldList(secGrpAclFieldRepository.selectSelfManagementFieldInGrp(secGrpId));
        // 查询安全组下能自我管理的，不受父级限制的单据维度
        secGrpAuthority.setSecGrpDclDimDetailList(secGrpDclDimRepository.selectSelfManagementDimDetailInGrp(secGrpId));
        return secGrpAuthority;
    }

	@Override
    @ProcessLovValue
	public Page<SecGrp> pageUserAssignedSecGrp(Long userId, SecGrpQueryDTO secGrpQueryDTO, PageRequest pageRequest) {
		return secGrpRepository.listUserAssignedSecGrp(userId,secGrpQueryDTO,pageRequest);
	}

}
