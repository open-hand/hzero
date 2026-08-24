package org.hzero.iam.domain.service.role.observer;

import java.util.List;

import org.hzero.iam.domain.entity.MemberRole;

/**
 * 成员角色分配观察者
 *
 * @author bojiangzhou 2020/05/25
 */
public interface RoleAssignObserver {

    /**
     * 分配角色成员事件
     *
     * @param memberRoleList 角色成员关系
     */
    void assignMemberRole(List<MemberRole> memberRoleList);

    /**
     * 回收角色成员事件
     *
     * @param memberRoleList 角色成员关系
     */
    void revokeMemberRole(List<MemberRole> memberRoleList);
}
