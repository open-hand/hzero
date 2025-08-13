package org.hzero.iam.domain.service.secgrp.observer.dcl;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 数据权限变更角色订阅者接口抽象实现
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public abstract class AbstractRoleSecGrpDclObserver implements RoleSecGrpDclObserver {
    @Autowired
    private SecGrpRepository secGrpRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        //获取安全组所分配的所有角色
        List<Role> roles = this.secGrpRepository.listSecGrpAssignedRole(secGrp.getSecGrpId());

        // 分配角色Dcl
        this.assignRolesDcl(roles, dcl, dclLines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        //获取安全组所分配的所有角色
        List<Role> roles = this.secGrpRepository.listSecGrpAssignedRole(secGrp.getSecGrpId());

        // 回收角色Dcl
        this.recycleRolesDcl(roles, dcl, dclLines);
    }
}
