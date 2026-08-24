package org.hzero.iam.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.PermissionFieldResponse;
import org.hzero.iam.app.service.FieldPermissionService;
import org.hzero.iam.app.service.FieldService;
import org.hzero.iam.domain.entity.Field;
import org.hzero.iam.domain.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 接口字段维护应用服务默认实现
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
@Service
public class FieldServiceImpl implements FieldService {

    private FieldRepository fieldRepository;
    private FieldPermissionService fieldPermissionService;

    @Autowired
    public FieldServiceImpl(FieldRepository fieldRepository,
                            FieldPermissionService fieldPermissionService) {
        this.fieldRepository = fieldRepository;
        this.fieldPermissionService = fieldPermissionService;
    }

    @Override
    public Page<PermissionFieldResponse> pageApi(String serviceName, String method, String path, String description, boolean includeAll, Long roleId, Long userId, PageRequest pageRequest) {
        return fieldRepository.pageApi(serviceName, method, path, description, includeAll, roleId, userId, pageRequest);
    }

    @Override
    public Page<Field> pageField(long permissionId, String fieldName, String fieldType, String fieldDescription, PageRequest pageRequest) {
        return fieldRepository.pageField(permissionId, fieldName, fieldType, fieldDescription, pageRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Field createField(Field field) {
        fieldRepository.insertSelective(field);
        return field;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Field> createField(List<Field> fieldList) {
        if (CollectionUtils.isEmpty(fieldList)) {
            return fieldList;
        }
        fieldList.forEach(this::createField);
        return fieldList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Field updateField(Field field) {
        fieldRepository.updateOptional(field, Field.getUpdatableField());
        return field;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Field> updateField(List<Field> fieldList) {
        if (CollectionUtils.isEmpty(fieldList)) {
            return fieldList;
        }
        fieldList.forEach(this::updateField);
        return fieldList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteField(Field field) {
        fieldPermissionService.deletePermission(fieldPermissionService.listFieldPermission(field.getFieldId()));
        fieldRepository.delete(field);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteField(List<Field> fieldList) {
        if (CollectionUtils.isEmpty(fieldList)) {
            return;
        }
        fieldList.forEach(this::deleteField);
    }
}
