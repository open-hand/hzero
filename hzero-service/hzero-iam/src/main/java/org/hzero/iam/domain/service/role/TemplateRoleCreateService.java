package org.hzero.iam.domain.service.role;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.service.role.validator.InternalRoleCreateValidator;
import org.hzero.iam.domain.service.role.validator.RoleCreateValidator;
import org.hzero.iam.infra.constant.HiamError;

/**
 * 模板角色创建服务
 *
 * @author bojiangzhou 2020/04/29
 */
public class TemplateRoleCreateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateRoleCreateService.class);

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleCreateService roleCreateService;
    @Autowired
    private InternalRoleCreateValidator internalRoleCreateValidator;

    /**
     * 继承模板角色创建一个新的角色，创建用户默认为系统匿名用户
     *
     * @param parentRole 父级角色
     * @param tplRole    模板角色
     * @param tenant     角色所属租户
     * @return 创建的角色
     */
    public Role createRoleByTpl(@Nonnull Role parentRole, @Nonnull Role tplRole, @Nonnull Tenant tenant) {
        return createRoleByTpl(parentRole, tplRole, tenant, null, internalRoleCreateValidator);
    }

    /**
     * 继承模板角色创建一个新的角色
     *
     * @param parentRole 父级角色
     * @param tplRole    模板角色
     * @param tenant     角色所属租户
     * @param adminUser  创建角色的用户；为空时，如果存在当前登录用户则用登录用户，否则默认使用系统匿名用户
     * @param validator  角色校验器
     * @return 创建的角色
     */
    public Role createRoleByTpl(@Nonnull Role parentRole, @Nonnull Role tplRole, @Nonnull Tenant tenant, @Nullable User adminUser, RoleCreateValidator validator) {
        adminUser = getAdminUser(adminUser);

        Role role = new Role();
        role.setTenantId(tenant.getTenantId());
        role.setTenantNum(tenant.getTenantNum());
        role.setInheritRoleId(tplRole.getId());
        role.setParentRoleId(parentRole.getId());
        role.setLevel(tplRole.getLevel());

        setupRoleCode(role, tplRole, adminUser);
        setupRoleName(role, tplRole, adminUser);

        try {
            return roleCreateService.createRole(role, adminUser, true, false, Optional.ofNullable(validator).orElse(internalRoleCreateValidator));
        } catch (CommonException e) {
            if (HiamError.ROLE_CODE_EXISTS.equalsIgnoreCase(e.getCode())) {
                LOGGER.warn("createRoleByRoleTpl: role code exists, role={}", role);

                Role param = new Role();
                param.setTenantId(tenant.getTenantId());
                param.setCode(role.getCode());
                param.setLevel(role.getLevel());
                return roleRepository.selectOne(param);
            }
            throw e;
        }
    }

    /**
     * 返回管理用户，如果传入的用户为 null，则返回系统匿名用户
     *
     * @param adminUser 创建角色的用户
     * @return 创建角色的用户
     */
    @Nonnull
    protected User getAdminUser(@Nullable User adminUser) {
        if (null != adminUser) {
            return adminUser;
        }

        CustomUserDetails self = DetailsHelper.getUserDetails();
        if (self == null) {
            self = DetailsHelper.getAnonymousDetails();
            LOGGER.info("use anonymous user to create role from tpl.");
        }

        adminUser = new User();
        adminUser.setId(self.getUserId());
        adminUser.setOrganizationId(self.getOrganizationId());
        adminUser.setLoginName(self.getUsername());
        adminUser.setUserType(self.getUserType());
        adminUser.setLanguage(self.getLanguage());

        return adminUser;
    }

    /**
     * 设置角色名称，默认使用模板角色的子角色名称
     *
     * @param role    新角色
     * @param tplRole 模板角色
     */
    protected void setupRoleName(Role role, Role tplRole, User adminUser) {
        Map<String, String> tls = roleRepository.selectTplRoleNameById(tplRole.getId());
        if (MapUtils.isEmpty(tls)) {
            LOGGER.warn("Template role's name is empty, then use tepRole's name. tplRole is {}", tplRole.getCode());
            tls = new HashMap<>();
            tls.put(LanguageHelper.getDefaultLanguage(), tplRole.getName());
            tls.put("en_US", tplRole.getName());
        }
        Map<String, Map<String, String>> _tls = new HashMap<>(2);
        _tls.put(Role.FIELD_NAME, tls);
        role.set_tls(_tls);
        role.setName(tls.get(adminUser.getLanguage()));
    }

    /**
     * 默认取模板角色编码的最后一段
     *
     * @param role    新角色
     * @param tplRole 模板角色
     */
    protected void setupRoleCode(Role role, Role tplRole, User adminUser) {
        String[] arr = StringUtils.split(tplRole.getCode(), BaseConstants.Symbol.SLASH);
        role.setCode(arr[arr.length - 1]);
    }
}
