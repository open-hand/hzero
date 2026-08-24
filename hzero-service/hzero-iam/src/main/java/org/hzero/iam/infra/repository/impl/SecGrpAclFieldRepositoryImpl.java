package org.hzero.iam.infra.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.util.CommonStream;
import org.hzero.iam.api.dto.SecGrpAclApiDTO;
import org.hzero.iam.api.dto.SecGrpAclFieldDTO;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.repository.SecGrpAclFieldRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.PermissionMapper;
import org.hzero.iam.infra.mapper.SecGrpAclFieldMapper;
import org.hzero.iam.infra.util.BatchSqlHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;

/**
 * 安全组字段权限 资源库实现
 *
 * @author bojiangzhou 2020/02/18
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
@Component
public class SecGrpAclFieldRepositoryImpl extends BaseRepositoryImpl<SecGrpAclField> implements SecGrpAclFieldRepository {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private SecGrpAclFieldMapper secGrpAclFieldMapper;

    @Override
    public Page<Permission> listAssignableSecGrpApi(Long tenantId, Long secGrpId, SecGrpAclApiDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setSecGrpId(secGrpId);
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.listSecGrpAssignableApi(queryDTO));
    }

    @Override
    public Page<Permission> listAssignedSecGrpApi(Long tenantId, Long secGrpId, SecGrpAclApiDTO queryDTO, PageRequest pageRequest) {
        queryDTO.setSecGrpId(secGrpId);
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.listSecGrpAssignedApi(queryDTO));
    }

    @Override
    @ProcessLovValue
    public Page<SecGrpAclField> listSecGrpApiField(Long tenantId, Long secGrpId, Long permissionId,
                                                   SecGrpAclFieldDTO dto, PageRequest pageRequest) {
        dto.setSecGrpId(secGrpId);
        dto.setPermissionId(permissionId);
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpAclFieldMapper.selectSecGrpApiField(dto));
    }

    @Override
    @ProcessLovValue
    public Page<SecGrpAclField> listRoleAssignedApiField(
            Long roleId, Long secGrpId, Long permissionId, SecGrpAclFieldDTO queryDTO, PageRequest pageRequest) {

        queryDTO.setPermissionId(permissionId);
        queryDTO.setSecGrpId(secGrpId);
        return PageHelper.doPageAndSort(pageRequest, () -> secGrpAclFieldMapper.selectRoleSecGrpAssignedApiField(roleId, queryDTO));
    }

    @Override
    public List<SecGrpAclField> listRoleSecGrpField(Long roleId) {
        return secGrpAclFieldMapper.selectRoleSecGrpField(roleId)
                .parallelStream()
                // 根据 fieldId 去重
                .filter(CommonStream.distinctByKey(SecGrpAclField::getFieldId))
                .collect(Collectors.toList());
    }

    @Override
    public List<SecGrpAclField> selectSelfManagementFieldInGrp(Long secGrpId) {
        return secGrpAclFieldMapper.selectSelfManagementFieldInGrp(secGrpId);
    }

    @Override
    public List<SecGrpAclField> selectBuildAclFieldBindFieldIdInRoleAndSubRole(Long roleId, List<Long> fieldIds) {
        return secGrpAclFieldMapper.selectBuildAclFieldBindFieldIdInRoleAndSubRole(roleId, fieldIds);
    }

    @Override
    public List<SecGrpAclField> selectAssignedAclFieldBindFieldIdInRoleAndSubRole(Long roleId, List<Long> fieldIds) {
        return secGrpAclFieldMapper.selectAssignedAclFieldBindFieldIdInRoleAndSubRole(roleId, fieldIds);
    }

    @Override
    public List<SecGrpAclField> listSecGrpFields(List<Long> secGrpIds, Set<Long> secGrpFieldIds) {
        if (CollectionUtils.isEmpty(secGrpIds)) {
            return Collections.emptyList();
        }
        return secGrpAclFieldMapper.selectSecGrpFields(secGrpIds, secGrpFieldIds);
    }

    @Override
    public List<SecGrpAclField> select(Long secGrpId, Long tenantId) {
        if(secGrpId == null || tenantId == null) {
            return new ArrayList<>();
        }
        SecGrpAclField secGrpAclField = new SecGrpAclField();
        secGrpAclField.setSecGrpId(secGrpId);
        secGrpAclField.setTenantId(tenantId);
        return secGrpAclFieldMapper.select(secGrpAclField);
    }

    @Override
    public List<SecGrpAclField> listRoleNotIncludedFields(Long roleId, Long excludeSecGrpId, List<SecGrpAclField> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return Collections.emptyList();
        }
        Set<Long> fieldIds = fields.stream().map(SecGrpAclField::getFieldId).collect(Collectors.toSet());
        List<SecGrpAclField> aclFields = secGrpAclFieldMapper.selectRoleSecGrpIncludedField(roleId, excludeSecGrpId, fieldIds);

        Set<Long> includedFieldIds = aclFields.stream().map(SecGrpAclField::getFieldId).collect(Collectors.toSet());

        return fields.stream().filter(item -> !includedFieldIds.contains(item.getFieldId())).collect(Collectors.toList());
    }

    @Override
    public List<SecGrpAclField> listUserNotIncludedFields(Long userId, Long excludeSecGrpId, List<SecGrpAclField> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return Collections.emptyList();
        }
        Set<Long> fieldIds = fields.stream().map(SecGrpAclField::getFieldId).collect(Collectors.toSet());
        List<SecGrpAclField> aclFields = this.secGrpAclFieldMapper.selectUserSecGrpIncludedField(userId, excludeSecGrpId, fieldIds);

        Set<Long> includedFieldIds = aclFields.stream().map(SecGrpAclField::getFieldId).collect(Collectors.toSet());

        return fields.stream().filter(item -> !includedFieldIds.contains(item.getFieldId())).collect(Collectors.toList());
    }

    @Override
    public void batchRemove(List<Long> secGrpIds, List<SecGrpAclField> fields, Integer autoAssignFlag) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        Set<Long> fieldIds = fields.stream().map(SecGrpAclField::getFieldId).collect(Collectors.toSet());
        secGrpAclFieldMapper.batchDeleteBySql(secGrpIds, fieldIds, autoAssignFlag);
    }

    @Override
    public void batchAdd(List<SecGrpAclField> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return;
        }
        UserUtils.setDataUser(fields);

        BatchSqlHelper.batchExecute(fields, 10,
                (dataList) -> secGrpAclFieldMapper.batchInsertBySql(dataList),
                "BatchInsertSecGrpAclField");
    }

    @Override
    public List<SecGrpAclField> listSecGrpAclField(List<Long> secGrpIds) {
        return secGrpAclFieldMapper.listSecGrpAclField(secGrpIds);
    }

    @Override
    public List<SecGrpAclField> listSecGrpAclField(@Nonnull Long secGrpId) {
        // 创建查询条件对象
        SecGrpAclField condition = new SecGrpAclField();
        condition.setSecGrpId(secGrpId);

        // 查询并返回结果
        return this.secGrpAclFieldMapper.select(condition);
    }
}
