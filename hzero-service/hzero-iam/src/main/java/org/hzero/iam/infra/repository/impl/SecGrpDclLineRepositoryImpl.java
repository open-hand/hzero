package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.api.dto.SecGrpDclQueryDTO;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.iam.domain.repository.SecGrpDclLineRepository;
import org.hzero.iam.domain.repository.SecGrpRevokeRepository;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityRevokeType;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.SecGrpDclLineMapper;
import org.hzero.iam.infra.util.BatchSqlHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 安全组数据权限行 资源库实现
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
@Component
public class SecGrpDclLineRepositoryImpl extends BaseRepositoryImpl<SecGrpDclLine> implements SecGrpDclLineRepository {

    @Autowired
    private SecGrpDclLineMapper secGrpDclLineMapper;

    /**
     * 安全组回收表仓库对象
     */
    @Autowired
    private SecGrpRevokeRepository secGrpRevokeRepository;

    @Override
    public Page<SecGrpDclLine> listSecGrpDclLine(SecGrpDclQueryDTO query, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpDclLineMapper.listSecGrpDclLine(query));
    }

    @Override
    public List<SecGrpDclLine> listRoleNotIncludedDclLine(Long roleId, Long excludeSecGrpId, String authorityTypeCode, List<SecGrpDclLine> dclLines) {
        if (CollectionUtils.isEmpty(dclLines)) {
            return Collections.emptyList();
        }
        Set<Long> dataIds = dclLines.stream().map(SecGrpDclLine::getDataId).collect(Collectors.toSet());

        List<SecGrpDclLine> includedDclLines = secGrpDclLineMapper.selectRoleIncludedDclLine(roleId, excludeSecGrpId, authorityTypeCode, dataIds);

        Set<Long> includedDataIds = includedDclLines.stream().map(SecGrpDclLine::getDataId).collect(Collectors.toSet());

        return dclLines.stream().filter(item -> !includedDataIds.contains(item.getDataId())).collect(Collectors.toList());
    }

    @Override
    public Page<SecGrpDclLine> listRoleSecGrpDcl(Long roleId, Long secGrpId, SecGrpDclQueryDTO queryDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpDclLineMapper.listRoleSecGrpDcl(queryDTO));
    }

    @Override
    public List<SecGrpDclLine> selectSecGrpDclInGrp(Long secGrpId) {
        return secGrpDclLineMapper.selectSecGrpDclInGrp(secGrpId);
    }

    @Override
    public List<SecGrpDclLine> selectSecGrpDclInRole(Long roleId) {
        return secGrpDclLineMapper.selectSecGrpDclInRole(roleId);
    }

    @Override
    public List<SecGrpDclLine> listRoleSecGrpDcl(Long roleId, List<Long> secGrpIds) {
        return secGrpDclLineMapper.selectRoleSecGrpDcl(roleId, secGrpIds);
    }

    @Override
    public List<SecGrpDclLine> selectSecGrpDclBindPermissionIdInRoleAndSubRole(Long roleId, String authorityType,
                                                                               List<Long> dataIds, Integer includeRevokeFlag) {
        return secGrpDclLineMapper.selectSecGrpDclBindPermissionIdInRoleAndSubRole(roleId, authorityType, dataIds, includeRevokeFlag);
    }

    @Override
    public List<SecGrpDclLine> selectSecGrpDclBindPermissionIdInRole(Long roleId, String authorityType, List<Long> dataIds, Integer includeRevokeFlag) {
        return secGrpDclLineMapper.selectSecGrpDclBindPermissionIdInRole(roleId, authorityType, dataIds, includeRevokeFlag);

    }

    @Override
    public SecGrpDclLine selectSecGrpDclLineDetailById(Long secGrpDclLineId) {
        return secGrpDclLineMapper.selectSecGrpDclLineDetailById(secGrpDclLineId);
    }

    @Override
    public List<SecGrpDclLine> selectBySecGrpId(Long secGrpId) {
        if (secGrpId == null) {
            return new ArrayList<>();
        }
        SecGrpDclLine line = new SecGrpDclLine();
        line.setSecGrpId(secGrpId);
        return secGrpDclLineMapper.select(line);
    }

    @Override
    public List<SecGrpDclLine> listRoleNotIncludeSecGrpDclLine(Long secGrpId, Long roleId, String authorityType, Set<Long> dataIds) {
        if (CollectionUtils.isNotEmpty(dataIds)) {
            // 查询数据
            return this.secGrpDclLineMapper.selectRoleNotIncludeSecGrpDclLine(secGrpId, roleId, authorityType, dataIds);
        } else {
            // 返回空集合对象
            return Collections.emptyList();
        }
    }

    @Override
    public List<SecGrpDclLine> listUserNotIncludeSecGrpDclLine(Long secGrpId, Long userId, String authorityType, Set<Long> dataIds) {
        if (CollectionUtils.isNotEmpty(dataIds)) {
            // 查询数据
            return this.secGrpDclLineMapper.selectUserNotIncludeSecGrpDclLine(secGrpId, userId, authorityType, dataIds);
        } else {
            // 返回空集合对象
            return Collections.emptyList();
        }
    }

    @Override
    public void batchAdd(List<SecGrpDclLine> addList) {
        if (CollectionUtils.isEmpty(addList)) {
            return;
        }
        UserUtils.setDataUser(addList);

        BatchSqlHelper.batchExecute(addList, 8,
                (dataList) -> secGrpDclLineMapper.batchInsertBySql(dataList),
                "BatchInsertSecGrpDclLine");
    }

    @Override
    public void batchRemove(Long secGrpId, String authorityType, Set<Long> dataIds) {
        if (CollectionUtils.isNotEmpty(dataIds)) {
            // 查询需要删除的数据权限行数据
            Set<Long> dclLineIds = secGrpDclLineMapper.selectDeletedDclLineId(secGrpId, authorityType, dataIds);
            // 删除数据
            this.batchDeleteBySql(secGrpId, dclLineIds);
        }
    }

    @Override
    public void batchDeleteBySql(Long secGrpId, Set<Long> dclLineIds) {
        if (CollectionUtils.isNotEmpty(dclLineIds)) {
            // 移除即将删除的数据关联的回屏蔽权限的数据
            this.secGrpRevokeRepository.batchRemove(null, secGrpId, dclLineIds,
                    SecGrpAuthorityRevokeType.SHIELD, SecGrpAuthorityType.DCL);
            // 移除即将删除的数据关联的回收权限的数据
            this.secGrpRevokeRepository.batchRemove(null, secGrpId, dclLineIds,
                    SecGrpAuthorityRevokeType.REVOKE, SecGrpAuthorityType.DCL);
            // 删除数据
            this.secGrpDclLineMapper.batchDeleteBySql(dclLineIds);
        }
    }

    @Override
    public Page<SecGrpDclLine> listSecGrpAssignableDclLine(SecGrpDclQueryDTO queryDTO, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpDclLineMapper.selectAssignableSecGrpDclLine(queryDTO));
    }


    @Override
    public List<SecGrpDclLine> selectDclLineByDclId(@Nonnull Long dclId) {
        // 查询数据权限行
        SecGrpDclLine secGrpDclLineCondition = new SecGrpDclLine();
        secGrpDclLineCondition.setSecGrpDclId(dclId);

        // 返回查询结果
        return this.secGrpDclLineMapper.select(secGrpDclLineCondition);
    }
}
