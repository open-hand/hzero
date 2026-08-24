package org.hzero.iam.domain.service.secgrp.observer.field;

import org.hzero.iam.domain.entity.FieldPermission;
import org.hzero.iam.domain.repository.FieldPermissionRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/3/4 17:01
 */
public abstract class AbstractSecGrpFieldObserver {

    @Autowired
    protected FieldPermissionRepository fieldPermissionRepository;

    /**
     * 保存字段权限
     *
     * @param fieldPermission 字段权限
     */
    protected void saveFieldPermission(FieldPermission fieldPermission) {
        if (fieldPermission != null) {
            Assert.notNull(fieldPermission.getPermissionDimension(), FieldPermission.PERMISSION_DIMENSION_REQUIRED);
            Assert.notNull(fieldPermission.getDimensionValue(), FieldPermission.DIMENSION_VALUE_REQUIRED);
            Assert.notNull(fieldPermission.getFieldId(), FieldPermission.FIELD_ID_REQUIRED);
            Assert.notNull(fieldPermission.getTenantId(), FieldPermission.TENANT_ID_REQUIRED);

            FieldPermission uniqueField = new FieldPermission();
            uniqueField.setDimensionValue(fieldPermission.getDimensionValue());
            uniqueField.setPermissionDimension(fieldPermission.getPermissionDimension());
            uniqueField.setFieldId(fieldPermission.getFieldId());
            uniqueField.setTenantId(fieldPermission.getTenantId());

            FieldPermission hasFiledPermission = fieldPermissionRepository.selectOne(uniqueField);
            if (hasFiledPermission != null) {
                //外部使用
                fieldPermission.setFieldPermissionId(hasFiledPermission.getFieldPermissionId());

                if (hasFiledPermission.containDefaultDataSource()) {
                    hasFiledPermission.setDataSource(Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
                    fieldPermissionRepository.updateByPrimaryKeySelective(hasFiledPermission);
                }
            } else {
                fieldPermissionRepository.insertSelective(fieldPermission);
            }
        }
    }

    /**
     * 移除字段权限
     *
     * @param fieldPermission
     */
    protected void removeFieldPermission(FieldPermission fieldPermission) {
        if (fieldPermission != null) {
            Assert.notNull(fieldPermission.getPermissionDimension(), FieldPermission.PERMISSION_DIMENSION_REQUIRED);
            Assert.notNull(fieldPermission.getDimensionValue(), FieldPermission.DIMENSION_VALUE_REQUIRED);
            Assert.notNull(fieldPermission.getFieldId(), FieldPermission.FIELD_ID_REQUIRED);
            Assert.notNull(fieldPermission.getTenantId(), FieldPermission.TENANT_ID_REQUIRED);

            FieldPermission uniqueField = new FieldPermission();
            uniqueField.setDimensionValue(fieldPermission.getDimensionValue());
            uniqueField.setPermissionDimension(fieldPermission.getPermissionDimension());
            uniqueField.setFieldId(fieldPermission.getFieldId());
            uniqueField.setTenantId(fieldPermission.getTenantId());

            FieldPermission hasFiledPermission = fieldPermissionRepository.selectOne(uniqueField);
            if (hasFiledPermission != null) {
                if (hasFiledPermission.equalDefaultSecGrpDataSource()) {
                    //更新掉安全组的权限
                    hasFiledPermission.setDataSource(Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
                    fieldPermissionRepository.updateByPrimaryKeySelective(hasFiledPermission);
                } else if (hasFiledPermission.equalSecGrpDataSource()) {
                    //删除权限
                    fieldPermissionRepository.delete(hasFiledPermission);
                }
            }
        }
    }
}

