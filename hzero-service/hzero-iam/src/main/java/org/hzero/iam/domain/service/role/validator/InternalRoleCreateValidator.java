package org.hzero.iam.domain.service.role.validator;

import org.springframework.stereotype.Component;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;

/**
 * @author bojiangzhou 2020/07/16
 */
@Component
public class InternalRoleCreateValidator extends DefaultRoleCreateValidator {

    /**
     * 内部创建角色 无需校验管理角色
     */
    @Override
    public void checkAdminRole(Role role, User adminUser) {
        // not check ...
    }

    @Override
    public void checkRoleIsSubOfParentRole(Long parentRoleId, Long roleId) {
        // not check ...
    }

    @Override
    public void checkRoleBuiltIn(Role role, User adminUser) {
        // not check ...
    }
}
