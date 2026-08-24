package org.hzero.iam.domain.service.secgrp.observer.field;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 数据维度变更角色订阅者接口的抽象实现
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public abstract class AbstractRoleSecGrpFieldObserver extends AbstractSecGrpFieldObserver implements RoleSecGrpFieldObserver {
    @Autowired
    private SecGrpRepository secGrpRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignSecGrpField(@Nonnull SecGrp secGrp, List<SecGrpAclField> fields) {
        // 查询安全组分配的角色
        List<Role> roles = this.secGrpRepository.listSecGrpAssignedRole(secGrp.getSecGrpId());

        // 分配角色字段权限
        this.assignRolesField(roles, fields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleSecGrpField(@Nonnull SecGrp secGrp, List<SecGrpAclField> fields) {
        // 查询安全组分配的角色
        List<Role> roles = this.secGrpRepository.listSecGrpAssignedRole(secGrp.getSecGrpId());

        // 回收角色字段权限
        this.recycleRolesField(roles, fields);
    }
}
