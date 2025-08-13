package org.hzero.iam.infra.repository.impl;

import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDimLine;
import org.hzero.iam.domain.repository.SecGrpDclDimLineRepository;
import org.hzero.iam.domain.service.secgrp.dto.SecGrpDclDimDetailDTO;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.iam.infra.mapper.SecGrpDclDimLineMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 * 安全组数据权限维度行 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Component
public class SecGrpDclDimLineRepositoryImpl extends BaseRepositoryImpl<SecGrpDclDimLine> implements SecGrpDclDimLineRepository {
    @Autowired
    private SecGrpDclDimLineMapper secGrpDclDimLineMapper;

    @Override
    public List<SecGrpDclDimDetailDTO> selectBuildDclDimDetailBindAuthTypeCodeInRoleAndSubRole(Long roleId, List<String> secGrpDclDimKeys) {
        List<SecGrpDclDimDetailDTO> dclDimDetailDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(secGrpDclDimKeys)) {
            dclDimDetailDTOList = secGrpDclDimLineMapper.selectBuildDclDimDetailBindAuthTypeCodeInRoleAndSubRole(roleId, secGrpDclDimKeys);
        }

        return dclDimDetailDTOList;
    }

    @Override
    public List<SecGrpDclDimDetailDTO> selectAssignedDclDimDetailBindAuthTypeCodeInRoleAndSubRole(Long roleId, List<String> secGrpDclDimKeys) {
        List<SecGrpDclDimDetailDTO> dclDimDetailDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(secGrpDclDimKeys)) {
            secGrpDclDimLineMapper.selectAssignedDclDimDetailBindAuthTypeCodeInRoleAndSubRole(roleId, secGrpDclDimKeys);
        }
        return dclDimDetailDTOList;
    }

    @Override
    public SecGrpDclDimDetailDTO selectDclDimDetailById(Long secGrpDclDimLineId) {
        return secGrpDclDimLineMapper.selectDclDimDetailById(secGrpDclDimLineId);
    }

    @Override
    public List<SecGrpDclDimLine> select(Long secGrpId, Long secGrpDclDimId) {
        if (secGrpDclDimId == null || secGrpId == null) {
            return new ArrayList<>();
        }
        SecGrpDclDimLine secGrpDclDimLine = new SecGrpDclDimLine();
        secGrpDclDimLine.setSecGrpId(secGrpId);
        secGrpDclDimLine.setSecGrpDclDimId(secGrpDclDimId);
        return secGrpDclDimLineMapper.select(secGrpDclDimLine);
    }

    @Override
    public List<SecGrpDclDimLine> listSecGrpDimLine(Long secGrpId, Long secGrpDclDimId) {
        return selectByCondition(Condition.builder(SecGrpDclDimLine.class)
                .select(
                        SecGrpDclDimLine.FIELD_SEC_GRP_DCL_DIM_LINE_ID,
                        SecGrpDclDimLine.FIELD_SEC_GRP_DCL_DIM_ID,
                        SecGrpDclDimLine.FIELD_AUTH_TYPE_CODE,
                        SecGrpDclDimLine.FIELD_AUTO_ASSIGN_FLAG,
                        SecGrpDclDimLine.FIELD_ASSIGN_TYPE_CODE
                )
                .where(
                        Sqls.custom()
                                .andEqualTo(SecGrp.FIELD_SEC_GRP_ID, secGrpId)
                                .andEqualTo(SecGrpDclDimLine.FIELD_SEC_GRP_DCL_DIM_ID, secGrpDclDimId)
                )
                .build()
        );
    }

    @Override
    public Set<String> listRoleNotIncludedAuthType(Long roleId, Long excludeSecGrpId, Long authDocTypeId,
                                                   String authScopeCode, Set<String> authTypeCodes) {
        if (CollectionUtils.isEmpty(authTypeCodes)) {
            return Collections.emptySet();
        }

        // 查询其他安全组分配的权限维度
        List<String> includedDimLines = this.secGrpDclDimLineMapper.selectRoleSecGrpIncludedDimLine(roleId, excludeSecGrpId,
                authDocTypeId, authScopeCode, authTypeCodes);

        // 排除其他安全组分配的权限维度
        return authTypeCodes.stream().filter(item -> !includedDimLines.contains(item)).collect(Collectors.toSet());
    }

    @Override
    public Set<String> listUserNotIncludedAuthType(Long userId, Long excludeSecGrpId, Long authDocTypeId,
                                                   String authScopeCode, Set<String> authTypeCodes) {
        if (CollectionUtils.isEmpty(authTypeCodes)) {
            return Collections.emptySet();
        }

        // 查询其他安全组分配的权限维度
        List<String> includedDimLines = this.secGrpDclDimLineMapper.selectRoleSecGrpIncludedDimLine(userId, excludeSecGrpId,
                authDocTypeId, authScopeCode, authTypeCodes);

        // 排除其他安全组分配的权限维度
        return authTypeCodes.stream().filter(item -> !includedDimLines.contains(item)).collect(Collectors.toSet());
    }

    @Override
    public Map<Long, Set<String>> queryRoleIncludedAuthType(Set<Long> roleIds, Long excludeSecGrpId, Long authDocTypeId,
                                                            String authScopeCode, Set<String> assignAuthTypeCodes) {
        // 参数处理
        if (CollectionUtils.isNotEmpty(roleIds) && Objects.nonNull(excludeSecGrpId) && Objects.nonNull(authDocTypeId)
                && StringUtils.isNotBlank(authScopeCode) && CollectionUtils.isNotEmpty(assignAuthTypeCodes)) {
            // 查询角色包含的权限码
            List<SecGrpDclDimLine> secGrpDclDimLines = this.secGrpDclDimLineMapper.queryRoleIncludedDimLines(roleIds, excludeSecGrpId,
                    authDocTypeId, authScopeCode, assignAuthTypeCodes);
            if (CollectionUtils.isNotEmpty(secGrpDclDimLines)) {
                // 查询数据，并进行分组
                return secGrpDclDimLines.stream()
                        .collect(groupingBy(SecGrpDclDimLine::getRoleId, mapping(SecGrpDclDimLine::getAuthTypeCode, toSet())));
            }
        }

        // 空Map
        return Collections.emptyMap();
    }

    @Override
    public Map<Long, Set<String>> queryRoleIncludedAuthType(List<Role> roles, Long excludeSecGrpId, Long authDocTypeId,
                                                            String authScopeCode, Set<String> assignAuthTypeCodes) {
        if (CollectionUtils.isNotEmpty(roles)) {
            // 处理数据，查询并返回结果
            return this.queryRoleIncludedAuthType(roles.stream().map(Role::getId).collect(Collectors.toSet()),
                    excludeSecGrpId, authDocTypeId, authScopeCode, assignAuthTypeCodes);
        } else {
            // 返回空Map
            return Collections.emptyMap();
        }
    }

    @Override
    public List<SecGrp> listRoleIncludedAuthTypeSecGrp(Long roleId, Long authDocTypeId,
                                                       String authScopeCode, Set<String> authTypeCodes) {
        return secGrpDclDimLineMapper.selectRoleIncludedDimLineSecGrp(roleId, authDocTypeId, authScopeCode, authTypeCodes);
    }

    @Override
    public Set<String> selectRecycleAuthTypeCodes(Long secGrpId, Long authDocTypeId,
                                                  String authScopeCode, Set<String> authTypeCodes) {
        // 查询数据，并返回结果
        // 查询自己拥有的权限类型码
        Set<String> ownAuthTypeCodes = this.secGrpDclDimLineMapper.selectAuthTypeCodes(secGrpId, authDocTypeId,
                authScopeCode, authTypeCodes, Constants.SecGrpAssignTypeCode.SELF);

        // 筛选已经回收的权限类型码
        return authTypeCodes.stream()
                .filter(authTypeCode -> !ownAuthTypeCodes.contains(authTypeCode)).collect(toSet());
    }

    @Override
    public void batchRemoveSecGrpDimLine(List<Long> secGrpIds, Long authDocTypeId,
                                         String authScopeCode, Set<String> authTypeCodes) {
        // 查询父安全组分配的数据权限维度
        List<Long> ids = this.secGrpDclDimLineMapper.selectDimLineId(secGrpIds, authDocTypeId,
                authScopeCode, authTypeCodes, Constants.SecGrpAssignTypeCode.PARENT);

        if (CollectionUtils.isNotEmpty(ids)) {
            // 删除数据
            this.secGrpDclDimLineMapper.batchDeleteBySql(ids);
        }
    }

    @Override
    public void batchUpdateSecGrpDimLine(List<Long> secGrpIds, Long authDocTypeId,
                                         String authScopeCode, Set<String> authTypeCodes) {
        // 查询父安全组分配的数据权限维度
        List<Long> ids = this.secGrpDclDimLineMapper.selectDimLineId(secGrpIds, authDocTypeId,
                authScopeCode, authTypeCodes, Constants.SecGrpAssignTypeCode.SELF_PARENT);

        if (CollectionUtils.isNotEmpty(ids)) {
            // 更新数据
            this.secGrpDclDimLineMapper.batchUpdateBySql(ids, Constants.SecGrpAssignTypeCode.SELF);
        }
    }

    @Override
    public Map<String, SecGrpDclDimLine> listDimIncludedDimLine(@Nonnull Long secGrpDclDimId,
                                                                @Nonnull Set<String> notIncludedAuthTypes) {
        if (CollectionUtils.isNotEmpty(notIncludedAuthTypes)) {
            // 查询DimLine
            List<SecGrpDclDimLine> secGrpDclDimLines = this.selectByCondition(Condition.builder(SecGrpDclDimLine.class)
                    .where(Sqls.custom().andEqualTo(SecGrpDclDimLine.FIELD_SEC_GRP_DCL_DIM_ID, secGrpDclDimId)
                            .andIn(SecGrpDclDimLine.FIELD_AUTH_TYPE_CODE, notIncludedAuthTypes)).build());
            if (CollectionUtils.isNotEmpty(secGrpDclDimLines)) {
                // 转换成Map并返回结果
                return secGrpDclDimLines.stream().collect(toMap(SecGrpDclDimLine::getAuthTypeCode, t -> t));
            }
        }

        // 返回空Map
        return Collections.emptyMap();
    }

    @Override
    public int countDimLineByDimIdAndAssignTypeCode(@Nonnull Long secGrpDclDimId,
                                                    @Nonnull String assignTypeCode) {
        // 查询条件对象
        SecGrpDclDimLine condition = new SecGrpDclDimLine();
        condition.setSecGrpDclDimId(secGrpDclDimId);
        condition.setAssignTypeCode(assignTypeCode);

        // 返回查询结果
        return this.selectCount(condition);
    }
}
