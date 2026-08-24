package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.iam.api.dto.SecGrpDclDimDTO;
import org.hzero.iam.api.dto.SecGrpDclDimLineDTO;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDim;
import org.hzero.iam.domain.repository.SecGrpDclDimRepository;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpDclDimDetailDTO;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.mapper.SecGrpDclDimMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.hzero.iam.infra.constant.Constants.DIM_UNIQUE_KEY_TEMPLATE;

/**
 * 安全组数据权限维度 资源库实现
 *
 * @author bojiangzhou 2020/02/18
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Component
public class SecGrpDclDimRepositoryImpl extends BaseRepositoryImpl<SecGrpDclDim> implements SecGrpDclDimRepository {
    @Autowired
    private SecGrpDclDimMapper secGrpDclDimMapper;

    @Override
    @ProcessLovValue
    public Page<SecGrpDclDimDTO> listSecGrpAssignableDim(Long tenantId, Long secGrpId, SecGrpDclDimDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setTenantId(tenantId);
        queryDTO.setSecGrpId(secGrpId);
        Page<SecGrpDclDimDTO> page = PageHelper.doPage(pageRequest, () -> this.secGrpDclDimMapper.listSecGrpAssignableDim(queryDTO));

        // 查询行数据
        if (CollectionUtils.isNotEmpty(page.getContent())) {
            Set<Long> docTypeIds = page.getContent().stream().map(SecGrpDclDimDTO::getDocTypeId).collect(toSet());
            if (CollectionUtils.isNotEmpty(docTypeIds)) {
                List<SecGrpDclDimLineDTO> lines = secGrpDclDimMapper.listSecGrpAssignableDimLine(secGrpId, docTypeIds);
                if (CollectionUtils.isNotEmpty(lines)) {
                    Map<String, List<SecGrpDclDimLineDTO>> docTypeDim = lines.stream()
                            .collect(Collectors.groupingBy(dimLine ->
                                    String.format(DIM_UNIQUE_KEY_TEMPLATE, dimLine.getDocTypeId(), dimLine.getDimensionType())));
                    page.forEach(item -> item.setSecGrpDclDimLineList(docTypeDim.get(String.format(DIM_UNIQUE_KEY_TEMPLATE,
                            item.getDocTypeId(), item.getAuthScopeCode()))));
                }
            }
        }

        // 返回查询结果
        return page;
    }

    @Override
    @ProcessLovValue
    public Page<SecGrpDclDimDTO> listSecGrpAssignedDim(Long tenantId, Long secGrpId, SecGrpDclDimDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setTenantId(tenantId);
        queryDTO.setSecGrpId(secGrpId);
        Page<SecGrpDclDimDTO> page = PageHelper.doPage(pageRequest, () -> this.secGrpDclDimMapper.listSecGrpAssignedDim(queryDTO));

        // 查询行数据
        if (CollectionUtils.isNotEmpty(page.getContent())) {
            Set<Long> docTypeIds = page.getContent().stream().map(SecGrpDclDimDTO::getDocTypeId).collect(toSet());
            if (CollectionUtils.isNotEmpty(docTypeIds)) {
                List<SecGrpDclDimLineDTO> lines = this.secGrpDclDimMapper.listSecGrpAssignedDimLine(secGrpId, docTypeIds);
                if (CollectionUtils.isNotEmpty(lines)) {
                    Map<String, List<SecGrpDclDimLineDTO>> docTypeDim = lines.stream()
                            .collect(Collectors.groupingBy(dimLine ->
                                    String.format(DIM_UNIQUE_KEY_TEMPLATE, dimLine.getDocTypeId(), dimLine.getDimensionType())));
                    page.forEach(item -> item.setSecGrpDclDimLineList(docTypeDim.get(String.format(DIM_UNIQUE_KEY_TEMPLATE,
                            item.getDocTypeId(), item.getAuthScopeCode()))));
                }
            }
        }

        // 返回结果
        return page;
    }

    @Override
    public Set<String> listSecGrpAssignedAuthTypeCode(long secGrpId) {
        return secGrpDclDimMapper.selectSecGrpAssignedAuthTypeCode(secGrpId);
    }

    @Override
    public List<SecGrpDclDimDetailDTO> listRoleSecGrpDim(Long roleId) {
        return secGrpDclDimMapper.selectRoleSecGrpDim(roleId);
    }

    @Override
    public List<SecGrpDclDim> listSecGrpDimByUniqueKeys(Long secGrpId, Set<String> authDocTypeUniqueKeys) {
        if (CollectionUtils.isEmpty(authDocTypeUniqueKeys)) {
            return Collections.emptyList();
        }

        // 查询并返回数据
        return this.secGrpDclDimMapper.listSecGrpDimByUniqueKeys(secGrpId, authDocTypeUniqueKeys);
    }

    @Override
    public List<SecGrpDclDimDetailDTO> selectSelfManagementDimDetailInGrp(Long secGrpId) {
        return secGrpDclDimMapper.selectSelfManagementDimDetailInGrp(secGrpId);
    }

    @Override
    public List<SecGrpDclDimDetailDTO> listSecGrpAssignableDim(List<Long> secGrpIds) {
        return secGrpDclDimMapper.selectSecGrpDim(secGrpIds);
    }

    @Override
    public Boolean isSelfManagementDim(Long secGrpId, Long roleId, String authorityTypeCode) {
        // 查询，判断，并返回结果
        return this.secGrpDclDimMapper.countSelfManagementDim(secGrpId, roleId, authorityTypeCode) != 0;
    }

    @Override
    public List<SecGrpDclDim> selectDifferentAssignedDimInRoleFromSecGrp(Long secGrpId, Long roleId) {
        return secGrpDclDimMapper.selectDifferentAssignedDimInRoleFromSecGrp(secGrpId, roleId);
    }

    @Override
    public List<SecGrpDclDim> selectBySecGrpId(Long secGrpId) {
        if (secGrpId == null) {
            return new ArrayList<>();
        }
        SecGrpDclDim secGrpDclDim = new SecGrpDclDim();
        secGrpDclDim.setSecGrpId(secGrpId);
        return secGrpDclDimMapper.select(secGrpDclDim);
    }

    @Override
    public boolean isRoleIncludedAuthDoc(Long roleId, Long excludeSecGrpId, Long authDocTypeId, String authScopeCode) {
        // 查询角色其他分配的安全组的单据
        Set<Long> dimIds = this.secGrpDclDimMapper.selectRoleIncludedAuthDoc(roleId, excludeSecGrpId, authDocTypeId, authScopeCode);

        // 返回结果
        return CollectionUtils.isNotEmpty(dimIds);
    }

    @Override
    public List<SecGrp> listRoleIncludedSecGrp(Long roleId, Long authDocTypeId, String authScopeCode) {
        return this.secGrpDclDimMapper.listRoleIncludedSecGrp(roleId, authDocTypeId, authScopeCode);
    }

    @Override
    public void batchDeleteEmptySecGrpDim(List<Long> secGrpIds, Long authDocTypeId, String authScopeCode) {
        // 查询维度范围是自己创建的，且维度是自己创建后，父级分配的维度IDs
        Set<Long> dimIds = this.secGrpDclDimMapper.selectDimId(secGrpIds, authDocTypeId,
                authScopeCode, Constants.SecGrpAssignTypeCode.PARENT);

        if (CollectionUtils.isNotEmpty(dimIds)) {
            // 更新数据
            this.secGrpDclDimMapper.batchDeleteBySql(dimIds);
        }
    }

    @Override
    public void batchUpdateEmptySecGrpDim(List<Long> secGrpIds, Long authDocTypeId, String authScopeCode) {
        // 查询维度范围是自己创建的，且维度是自己创建后，父级分配的维度IDs
        Set<Long> dimIds = this.secGrpDclDimMapper.selectDimId(secGrpIds, authDocTypeId,
                authScopeCode, Constants.SecGrpAssignTypeCode.SELF_PARENT);

        if (CollectionUtils.isNotEmpty(dimIds)) {
            // 更新数据
            this.secGrpDclDimMapper.batchUpdateBySql(dimIds, Constants.SecGrpAssignTypeCode.SELF);
        }
    }

    @Override
    public SecGrpDclDim selectDimByUniqueKey(@Nonnull Long authDocTypeId, @Nonnull String authScopeCode) {
        SecGrpDclDim condition = new SecGrpDclDim();
        condition.setAuthDocTypeId(authDocTypeId);
        condition.setAuthScopeCode(authScopeCode);

        // 查询并返回结果
        return this.selectOne(condition);
    }

    @Override
    public Map<Long, SecGrpDclDim> listRoleSecGrpIncludedDim(Set<Long> roleCreatedSecGrpIds, Long authDocTypeId, String authScopeCode) {
        if (CollectionUtils.isNotEmpty(roleCreatedSecGrpIds) && Objects.nonNull(authDocTypeId)
                && StringUtils.isNotBlank(authScopeCode)) {
            // 查询dim对象
            List<SecGrpDclDim> secGrpDclDims = this.selectByCondition(Condition.builder(SecGrpDclDim.class)
                    .where(Sqls.custom().andIn(SecGrpDclDim.FIELD_SEC_GRP_ID, roleCreatedSecGrpIds)
                            .andEqualTo(SecGrpDclDim.FIELD_AUTH_DOC_TYPE_ID, authDocTypeId)
                            .andEqualTo(SecGrpDclDim.FIELD_AUTH_SCOPE_CODE, authScopeCode)).build());
            if (CollectionUtils.isNotEmpty(secGrpDclDims)) {
                // 分组并返回结果
                return secGrpDclDims.stream().collect(toMap(SecGrpDclDim::getSecGrpId, t -> t));
            }
        }

        // 返回空Map
        return Collections.emptyMap();
    }

    @Override
    public Map<Long, SecGrpDclDim> listRoleSecGrpIncludedDim(List<SecGrp> roleCreatedSecGrps, Long authDocTypeId, String authScopeCode) {
        if (CollectionUtils.isNotEmpty(roleCreatedSecGrps) && Objects.nonNull(authDocTypeId)
                && StringUtils.isNotBlank(authScopeCode)) {
            // 解析安全组ID，并查询数据返回结果
            return this.listRoleSecGrpIncludedDim(roleCreatedSecGrps.stream().map(SecGrp::getSecGrpId).collect(toSet()),
                    authDocTypeId, authScopeCode);
        }

        // 返回空Map
        return Collections.emptyMap();
    }
}
