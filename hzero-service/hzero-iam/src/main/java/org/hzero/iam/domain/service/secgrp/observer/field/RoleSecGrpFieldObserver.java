package org.hzero.iam.domain.service.secgrp.observer.field;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrpAclField;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 字段权限变更角色订阅者
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public interface RoleSecGrpFieldObserver extends SecGrpFieldObserver {
    /**
     * 向角色分配字段权限
     *
     * @param roles  处理的角色
     * @param fields 字段权限
     */
    void assignRolesField(@Nonnull List<Role> roles, List<SecGrpAclField> fields);

    /**
     * 向角色分配回收字段权限
     *
     * @param roles  处理的角色
     * @param fields 字段权限
     */
    void recycleRolesField(@Nonnull List<Role> roles, List<SecGrpAclField> fields);
}
