package org.hzero.iam.domain.service.secgrp.observer.dim;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDim;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

/**
 * 数据维度变更角色订阅者接口抽象实现
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public abstract class AbstractRoleSecGrpDimObserver extends AbstractSecGrpDimObserver implements RoleSecGrpDimObserver {
    /**
     * 安全组仓库对象
     */
    @Autowired
    private SecGrpRepository secGrpRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignSecGrpDim(@Nonnull SecGrp secGrp, @Nonnull SecGrpDclDim dim, Set<String> authTypeCodes) {
        //获取安全组所分配的所有角色
        List<Role> roles = this.secGrpRepository.listSecGrpAssignedRole(secGrp.getSecGrpId());

        // 分配角色维度权限
        this.assignRolesDim(roles, dim, authTypeCodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleSecGrpDim(@Nonnull SecGrp secGrp, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes) {
        //获取安全组所分配的所有角色
        List<Role> roles = this.secGrpRepository.listSecGrpAssignedRole(secGrp.getSecGrpId());

        // 回收角色维度权限
        this.recycleRolesDim(roles, dim, authTypeCodes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleSecGrpDimLine(@Nonnull SecGrp secGrp, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes) {
        //获取安全组所分配的所有角色
        List<Role> roles = this.secGrpRepository.listSecGrpAssignedRole(secGrp.getSecGrpId());

        // 回收角色维度行权限
        this.recycleRolesDimLine(roles, dim, authTypeCodes);
    }
}
