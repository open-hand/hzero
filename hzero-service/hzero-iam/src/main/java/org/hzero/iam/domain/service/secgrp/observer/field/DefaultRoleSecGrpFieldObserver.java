package org.hzero.iam.domain.service.secgrp.observer.field;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.FieldPermission;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.repository.FieldPermissionRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 角色订阅数据维度的变化
 *
 * @author bojiangzhou 2020/02/27
 */
@Component
public class DefaultRoleSecGrpFieldObserver extends AbstractRoleSecGrpFieldObserver {

    @Autowired
    private FieldPermissionRepository fieldPermissionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignRolesField(@Nonnull List<Role> roles, List<SecGrpAclField> fields) {
        if (CollectionUtils.isNotEmpty(roles) && CollectionUtils.isNotEmpty(fields)) {
            for (SecGrpAclField secGrpAclField : fields) {
                for (Role role : roles) {
                    FieldPermission fieldPermission = new FieldPermission();
                    fieldPermission.setPermissionDimension(Constants.SecGrpAssign.ROLE_DIMENSION);
                    fieldPermission.setDimensionValue(role.getId());
                    fieldPermission.setTenantId(role.getTenantId());
                    fieldPermission.setFieldId(secGrpAclField.getFieldId());
                    fieldPermission.setPermissionRule(secGrpAclField.getPermissionRule());
                    fieldPermission.setPermissionType(secGrpAclField.getPermissionType());
                    fieldPermission.setDataSource(Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);

                    saveFieldPermission(fieldPermission);
                    //保存至缓存
                    fieldPermissionRepository.storePermission(fieldPermission);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleRolesField(@Nonnull List<Role> roles, List<SecGrpAclField> fields) {
        if (CollectionUtils.isNotEmpty(roles) && CollectionUtils.isNotEmpty(fields)) {
            for (SecGrpAclField secGrpAclField : fields) {
                for (Role role : roles) {
                    FieldPermission fieldPermission = new FieldPermission();
                    fieldPermission.setPermissionDimension(Constants.SecGrpAssign.ROLE_DIMENSION);
                    fieldPermission.setDimensionValue(role.getId());
                    fieldPermission.setTenantId(role.getTenantId());
                    fieldPermission.setFieldId(secGrpAclField.getFieldId());

                    removeFieldPermission(fieldPermission);
                    //清除缓存
                    fieldPermissionRepository.removePermission(fieldPermission);
                }
            }
        }
    }
}
