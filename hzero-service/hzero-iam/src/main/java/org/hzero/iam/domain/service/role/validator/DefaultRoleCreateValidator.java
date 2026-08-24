package org.hzero.iam.domain.service.role.validator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.LabelRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.HiamError;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;

/**
 *
 * @author bojiangzhou 2020/07/16
 */
@Component
public class DefaultRoleCreateValidator implements RoleCreateValidator {

    @Autowired
    protected TenantRepository tenantRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected LabelRepository labelRepository;

    @Override
    public void checkAdminUser(Role role, User adminUser) {
        if (adminUser == null || adminUser.getId() == null) {
            throw new CommonException("hiam.warn.role.adminUserIsNull");
        }

        User tmp = userRepository.selectSimpleUserWithTenant(adminUser.getId());
        if (tmp == null) {
            throw new CommonException("hiam.warn.role.userWithTenantNotFound");
        }
        adminUser.setTenantNum(tmp.getTenantNum());
        adminUser.setOrganizationId(tmp.getOrganizationId());

        role.setCreatedByTenantId(adminUser.getOrganizationId());
        role.setCreatedByTenantNum(adminUser.getTenantNum());
    }

    @Override
    public void checkCreateType(Role role, boolean inherited, boolean duplicate) {
        // 继承角色创建
        if (inherited) {
            if (role.getInheritRoleId() == null) {
                throw new CommonException("hiam.warn.role.inheritedRoleIdMustNotBeNull");
            }
            role.setCopyFromRoleId(null);
        }
        // 复制角色创建
        else if (duplicate) {
            if (role.getCopyFromRoleId() == null) {
                throw new CommonException("hiam.warn.role.duplicateRoleIdMustNotBeNull");
            }
            role.setInheritRoleId(null);
        }
        // 直接创建角色
        else {
            role.setCopyFromRoleId(null);
            role.setInheritRoleId(null);
        }
    }


    @Override
    public void checkParentRole(Role role, User adminUser) {
        if (role.getParentRoleId() == null) {
            throw new CommonException("hiam.warn.role.parentRoleNotFound");
        }
        // 父级角色
        Role parentRole = roleRepository.selectByPrimaryKey(role.getParentRoleId());

        if (parentRole == null) {
            throw new CommonException("hiam.warn.role.parentRoleNotFound");
        }

        role.setParentRole(parentRole);
    }

    @Override
    public void checkInheritRole(Role role, User adminUser) {
        if (role.getInheritRoleId() == null) {
            return;
        }

        Role inheritRole = roleRepository.selectByPrimaryKey(role.getInheritRoleId());
        if (inheritRole == null) {
            throw new CommonException("hiam.warn.role.inheritedRoleNotFound");
        }
        role.setInheritRole(inheritRole);
    }

    @Override
    public void checkCopyRole(Role role, User adminUser) {
        if (role.getCopyFromRoleId() == null) {
            return;
        }

        Role copyRole = roleRepository.selectByPrimaryKey(role.getCopyFromRoleId());
        if (copyRole == null) {
            throw new CommonException("hiam.warn.role.duplicateRoleNotFound");
        }
        role.setCopyRole(copyRole);
    }

    @Override
    public void checkAdminRole(Role role, User adminUser) {
        // 父级角色
        Role parentRole = role.getParentRole();

        // 当前用户的管理角色
        RoleVO params = new RoleVO();
        params.setId(parentRole.getId());
        params.setUserId(adminUser.getId());

        // 校验是否是管理角色
        List<RoleVO> selfAdminRoles = roleRepository.selectUserAdminRoles(params);

        if (CollectionUtils.isEmpty(selfAdminRoles)) {
            throw new CommonException("hiam.warn.role.parentRoleIsNotAdminRole");
        }
    }

    @Override
    public void checkRoleTenant(Role role) {
        Role adminRole = role.getParentRole();
        // 0 租户的租户级角色可以选择其它租户
        if (Objects.equals(adminRole.getTenantId(), BaseConstants.DEFAULT_TENANT_ID) &&
                StringUtils.equals(adminRole.getLevel(), HiamResourceLevel.ORGANIZATION.value())) {
            // 继承创建，租户ID=继承角色租户ID
            Role inheritRole = role.getInheritRole();
            if (inheritRole != null && !BaseConstants.DEFAULT_TENANT_ID.equals(inheritRole.getTenantId())) {
                if (!Objects.equals(role.getTenantId(), inheritRole.getTenantId())) {
                    throw new CommonException("hiam.warn.role.roleTenantIdEqualsInheritRoleTenantId");
                }
            }
        } else {
            // 非继承，租户ID=父级角色ID
            if (!Objects.equals(role.getTenantId(), adminRole.getTenantId())) {
                throw new CommonException("hiam.warn.role.roleTenantIdEqualsParentRoleTenantId");
            }
        }
        // 默认平台租户
        if (role.getTenantId() == null) {
            role.setTenantId(BaseConstants.DEFAULT_TENANT_ID);
        }
        // 租户
        if (StringUtils.isBlank(role.getTenantNum())) {
            Tenant tenant = tenantRepository.selectByPrimaryKey(role.getTenantId());
            tenant = Optional.ofNullable(tenant).orElseThrow(() -> new CommonException("hiam.warn.role.tenantNotFound"));
            role.setTenantNum(tenant.getTenantNum());
        }
    }

    @Override
    public void checkRoleLevel(Role role) {
        Role adminRole = role.getParentRole();

        if (StringUtils.isNotBlank(role.getLevel()) &&
                !StringUtils.equalsIgnoreCase(role.getLevel(), adminRole.getLevel())) {
            throw new CommonException("hiam.warn.role.roleLevelEqualsAdminRoleLevel");
        } else {
            role.setLevel(adminRole.getLevel());
        }
    }

    @Override
    public void checkTemplateRoleUnique(Role role) {
        List<Label> labels = role.getRoleLabels();
        if (CollectionUtils.isEmpty(labels)) {
            return;
        }

        Set<Long> ids = labels.stream().map(Label::getId).collect(Collectors.toSet());
        if (!labelRepository.containsTplRoleLabel(ids)) {
            return;
        }

        long count = roleRepository.selectCountByCondition(Condition.builder(Role.class).andWhere(Sqls.custom().andEqualTo(Role.FIELD_CODE, role.getCode())).build());
        if (count > 0) {
            throw new CommonException("hiam.warn.role.tplRoleCodeUnique");
        }
    }

    @Override
    public void checkRoleExists(Role role, User adminUser) {
        Role params = new Role();

        params.setTenantId(role.getTenantId());
        params.setCode(role.getCode());
        params.setParentRoleId(role.getParentRoleId());
        params.setParentRoleAssignLevel(role.getParentRoleAssignLevel());
        params.setParentRoleAssignLevelValue(role.getParentRoleAssignLevelValue());
        params.setCreatedByTenantId(adminUser.getOrganizationId());

        // 判断编码是否重复
        if (roleRepository.selectCount(params) > 0) {
            throw new CommonException(HiamError.ROLE_CODE_EXISTS);
        }

        checkRoleBuiltIn(role, adminUser);
    }

    @Override
    public void checkRoleBuiltIn(Role role, User adminUser) {
        // 不能与内置角色编码重复
        long count = roleRepository.selectCountByCondition(Condition.builder(Role.class)
                .where(Sqls.custom().andEqualTo(Role.FIELD_CODE, role.getCode()).andEqualTo(Role.FIELD_BUILD_IN, Boolean.TRUE)).build());

        if (count > 0) {
            throw new CommonException("hiam.warn.role.builtInRoleCodeUnique");
        }
    }

    @Override
    public void checkRoleIsSubOfParentRole(Long parentRoleId, Long roleId) {
        if (Objects.equals(parentRoleId, roleId)) {
            return;
        }

        // 检查是否是父级角色的子角色
        Long count = roleRepository.countSubRole(parentRoleId, roleId);

        if (count == 0) {
            throw new CommonException("hiam.warn.role.noAuthority");
        }
    }

}
