package org.hzero.iam.domain.service.secgrp.observer.dcl;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 数据权限变更角色订阅者接口
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public interface RoleSecGrpDclObserver extends SecGrpDclObserver {
    /**
     * 给角色分配数据权限
     *
     * @param roles    待操作的角色
     * @param dcl      数据权限头
     * @param dclLines 数据权限行
     */
    void assignRolesDcl(@Nonnull List<Role> roles, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines);

    /**
     * 取消角色分配的数据权限
     *
     * @param roles    待操作的角色
     * @param dcl      数据权限头
     * @param dclLines 数据权限行
     */
    void recycleRolesDcl(@Nonnull List<Role> roles, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines);
}
