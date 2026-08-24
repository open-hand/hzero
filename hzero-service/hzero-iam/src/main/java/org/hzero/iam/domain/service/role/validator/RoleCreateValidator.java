package org.hzero.iam.domain.service.role.validator;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;

/**
 * 角色创建校验器
 *
 * @author bojiangzhou 2020/07/16
 */
public interface RoleCreateValidator {

    /**
     * 检查管理用户
     *
     * @param adminUser 管理用户
     */
    void checkAdminUser(Role role, User adminUser);

    /**
     * 检查创建类型
     *
     * @param role      创建角色
     * @param inherited 是否继承
     * @param duplicate 是否复制
     */
    void checkCreateType(Role role, boolean inherited, boolean duplicate);

    /**
     * 检查父级角色
     */
    void checkParentRole(Role role, User adminUser);

    /**
     * 检查继承的角色
     */
    void checkInheritRole(Role role, User adminUser);

    /**
     * 检查复制的角色
     */
    void checkCopyRole(Role role, User adminUser);

    /**
     * 检查有权限操作父级角色
     */
    void checkAdminRole(Role role, User adminUser);

    /**
     * 检查角色所属租户：
     * 1.0 租户的租户级角色可以选择其它租户
     * 2.非租户超级管理员，继承角色创建时，租户ID=继承角色租户ID；其它创建方式，租户ID=父级角色租户ID
     *
     * @param role 创建的角色
     */
    void checkRoleTenant(Role role);

    /**
     * 角色层级与父级角色层级一致
     *
     * @param role 角色
     */
    void checkRoleLevel(Role role);

    /**
     * 模板角色校验，如果创建的模板角色，要求编码全局唯一
     *
     * @param role 角色
     */
    void checkTemplateRoleUnique(Role role);

    /**
     * 检查角色是否存在
     * <p>
     * 租户ID、角色编码、父级角色ID、父级分配层级、父级分配层级值、创建者租户ID 构成组合唯一性索引
     *
     * @param role      角色
     * @param adminUser 管理用户
     */
    void checkRoleExists(Role role, User adminUser);

    /**
     * 检查角色编码是否与内置角色编码相同，避免输入和超级管理员等内置角色相同的角色编码
     *
     * @param role      角色
     * @param adminUser 管理用户
     */
    void checkRoleBuiltIn(Role role, User adminUser);

    /**
     * 角色必须是父级角色的子孙角色
     *
     * @param parentRoleId 父级角色ID
     * @param roleId       角色ID
     */
    void checkRoleIsSubOfParentRole(Long parentRoleId, Long roleId);

}
