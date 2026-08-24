package org.hzero.iam.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.PermissionFieldResponse;
import org.hzero.iam.domain.entity.Field;
import org.hzero.iam.domain.repository.FieldRepository;
import org.hzero.iam.infra.mapper.FieldMapper;
import org.hzero.iam.infra.mapper.PermissionMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 接口字段维护 资源库实现
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
@Component
public class FieldRepositoryImpl extends BaseRepositoryImpl<Field> implements FieldRepository {
    private FieldMapper fieldMapper;
    private PermissionMapper permissionMapper;

    @Autowired
    public FieldRepositoryImpl(FieldMapper fieldMapper, PermissionMapper permissionMapper) {
        this.fieldMapper = fieldMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public Page<PermissionFieldResponse> pageApi(String serviceName, String method, String path, String description, boolean includeAll, Long roleId, Long userId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> permissionMapper.listApi(serviceName, method, path, description, includeAll, roleId, userId));
    }

    @Override
    public Field queryField(long fieldId) {
        return fieldMapper.queryField(fieldId);
    }

    @Override
    public List<Field> listField(List<Long> fieldIds) {
        return fieldMapper.listFieldByPrimaryKeys(fieldIds);
    }

    @Override
    public Page<Field> pageField(long permissionId, String fieldName, String fieldType, String fieldDescription, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> fieldMapper.listField(permissionId, fieldName, fieldType, fieldDescription));
    }
}
