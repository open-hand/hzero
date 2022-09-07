package org.hzero.iam.domain.service.role;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.hzero.core.observer.AsyncEventBus;
import org.hzero.core.observer.EventBus;
import org.hzero.core.observer.Observer;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.service.RootUserService;
import org.hzero.iam.domain.service.role.validator.DefaultRoleCreateValidator;
import org.hzero.iam.domain.service.role.validator.RoleCreateValidator;

/**
 * 角色创建相关核心业务抽象类
 *
 * @author bojiangzhou 2019/01/23
 */
public class RoleCreateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleCreateService.class);

    @Autowired
    protected TenantRepository tenantRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    private DefaultRoleCreateValidator defaultRoleCreateValidator;

    protected EventBus<Role> eventBus;

    public final Role createRole(Role role, User adminUser, boolean inherited, boolean duplicate) {
        return createRole(role, adminUser, inherited, duplicate, defaultRoleCreateValidator);
    }

    /**
     * 创建角色
     *
     * @param role      角色信息
     * @param adminUser 管理员用户
     * @param inherited 是否继承角色创建，如果为 false，则不是继承的
     * @param duplicate 是否复制角色创建，如果为 false，则不是复制的
     */
    public final Role createRole(Role role, User adminUser, boolean inherited, boolean duplicate, RoleCreateValidator validator) {
        LOGGER.info(">> Start create role: role={}, adminUser={}, inherited={}, duplicate={}",
                role, adminUser, inherited, duplicate);

        // 检查管理用户
        validator.checkAdminUser(role, adminUser);

        // 检查创建类型
        validator.checkCreateType(role, inherited, duplicate);

        // 检查父级角色
        validator.checkParentRole(role, adminUser);

        // 检查继承角色
        validator.checkInheritRole(role, adminUser);

        // 检查继承角色
        validator.checkCopyRole(role, adminUser);

        // 检查管理角色
        if (RootUserService.isRootUser()) {
            validator.checkAdminRole(role, adminUser);
        }

        // 检查角色所属租户
        validator.checkRoleTenant(role);

        // 检查角色层级
        validator.checkRoleLevel(role);

        // 如果创建的模板角色，检查编码是否全局唯一
        validator.checkTemplateRoleUnique(role);

        // 检查角色是否存在
        validator.checkRoleExists(role, adminUser);

        // 处理角色路径
        handleRoleLevelPath(role);

        // 创建前处理
        handleBeforeCreate(role, adminUser);

        // 创建角色
        persistRole(role);

        // 角色处理完成，最后处理的一些事情
        postHandle(role, adminUser, inherited, duplicate, validator);

        LOGGER.info(">> Finish create role: {}", role);

        return role;
    }

    /**
     * 处理 level_path
     *
     * @param role 角色
     */
    protected void handleRoleLevelPath(Role role) {
        role.buildCreatedRoleLevelPath(role.getParentRole());
        role.buildInheritRoleLevelPath(role.getInheritRole());
    }

    /**
     * 创建角色前的一些处理
     *
     * @param role 角色
     */
    protected void handleBeforeCreate(Role role, User adminUser) {
        role.setBuiltIn(false);
        role.setEnabled(true);
        role.setAssignable(false);
        role.setEnableForbidden(true);
        role.setModified(true);
        role.setCreatedBy(adminUser.getId());
        role.setCreatedByTenantId(adminUser.getOrganizationId());
        role.setupParentAssignLevel(role.getParentRole());
    }

    /**
     * 持久化角色到DB
     *
     * @param role 角色
     */
    protected void persistRole(Role role) {
        roleRepository.insertSelective(role);
    }


    /**
     * 角色处理完成后，最后处理的一些事情
     *
     * @param role 角色信息
     */
    protected void postHandle(Role role, User adminUser, boolean inherited, boolean duplicate, RoleCreateValidator validator) {
        role.setPermissionSets(null);

        // 异步通知其它处理器
        eventBus.notifyObservers(role, adminUser, inherited, duplicate, validator);
    }

    @Autowired
    private void setEventBus(List<Observer<Role>> roleObservers, @Qualifier("IamCommonAsyncTaskExecutor") ThreadPoolExecutor executor) {
        this.eventBus = new AsyncEventBus<>("CreateRole", roleObservers, executor);
    }
}
