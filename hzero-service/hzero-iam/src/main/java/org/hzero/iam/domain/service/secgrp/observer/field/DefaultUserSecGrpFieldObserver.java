package org.hzero.iam.domain.service.secgrp.observer.field;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.FieldPermission;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.entity.SecGrpAssign;
import org.hzero.iam.domain.repository.FieldPermissionRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 用户订阅数据维度的变化
 *
 * @author bojiangzhou 2020/02/27
 */
@Component
public class DefaultUserSecGrpFieldObserver extends AbstractUserSecGrpFieldObserver {

    @Autowired
    private FieldPermissionRepository fieldPermissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignUsersField(@Nonnull List<SecGrpAssign> secGrpAssigns, List<SecGrpAclField> fields) {
        if (CollectionUtils.isNotEmpty(secGrpAssigns) && CollectionUtils.isNotEmpty(fields)) {
            for (SecGrpAclField secGrpAclField : fields) {
                for (SecGrpAssign secGrpAssign : secGrpAssigns) {
                    FieldPermission fieldPermission = new FieldPermission();
                    fieldPermission.setPermissionDimension(Constants.SecGrpAssign.USER_DIMENSION);
                    fieldPermission.setDimensionValue(secGrpAssign.getDimensionValue());
                    fieldPermission.setTenantId(secGrpAssign.getTenantId());
                    fieldPermission.setFieldId(secGrpAclField.getFieldId());
                    fieldPermission.setPermissionRule(secGrpAclField.getPermissionRule());
                    fieldPermission.setPermissionType(secGrpAclField.getPermissionType());
                    fieldPermission.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
                    //保存字段权限
                    saveFieldPermission(fieldPermission);
                    //保存至缓存
                    fieldPermissionRepository.storePermission(fieldPermission);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleUsersField(@Nonnull List<SecGrpAssign> secGrpAssigns, List<SecGrpAclField> fields) {
        if (CollectionUtils.isNotEmpty(secGrpAssigns) && CollectionUtils.isNotEmpty(fields)) {
            for (SecGrpAclField secGrpAclField : fields) {
                for (SecGrpAssign secGrpAssign : secGrpAssigns) {
                    FieldPermission fieldPermission = new FieldPermission();
                    fieldPermission.setPermissionDimension(Constants.SecGrpAssign.USER_DIMENSION);
                    fieldPermission.setDimensionValue(secGrpAssign.getDimensionValue());
                    fieldPermission.setTenantId(secGrpAssign.getTenantId());
                    fieldPermission.setFieldId(secGrpAclField.getFieldId());
                    //移除权限
                    removeFieldPermission(fieldPermission);
                    //清除缓存
                    fieldPermissionRepository.removePermission(fieldPermission);
                }
            }
        }
    }
}
