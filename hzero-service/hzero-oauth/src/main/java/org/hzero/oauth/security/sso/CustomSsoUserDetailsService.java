package org.hzero.oauth.security.sso;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.CollectionUtils;

import org.hzero.core.user.UserType;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.domain.vo.Role;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.service.UserDetailsBuilder;
import org.hzero.sso.core.domain.SsoTenant;
import org.hzero.sso.core.exception.SsoLoginExceptions;
import org.hzero.sso.core.service.SsoUserDetailsService;

/**
 * @author bojiangzhou 2020/08/24
 */
public class CustomSsoUserDetailsService implements SsoUserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSsoUserDetailsService.class);

    private final UserAccountService userAccountService;
    private final UserDetailsBuilder userDetailsBuilder;
    private final LoginRecordService loginRecordService;

    private static final ThreadLocal<UserDetails> LOCAL_USER_DETAILS = new ThreadLocal<>();

    public CustomSsoUserDetailsService(UserAccountService userAccountService, UserDetailsBuilder userDetailsBuilder,
                                       LoginRecordService loginRecordService) {
        this.userAccountService = userAccountService;
        this.userDetailsBuilder = userDetailsBuilder;
        this.loginRecordService = loginRecordService;
    }

    @Override
    public UserDetails retrieveUser(@Nonnull String username, List<SsoTenant> tenants) throws AuthenticationException {
        UserDetails userDetails = LOCAL_USER_DETAILS.get();
        if (userDetails != null) {
            return userDetails;
        }

        tenants = Optional.ofNullable(tenants).orElse(Collections.emptyList());

        User user = getUserAccountService().findLoginUser(username, UserType.ofDefault());

        if (user == null) {
            LOGGER.info("sso user not found for [{}]", username);
            throw new UsernameNotFoundException(SsoLoginExceptions.SSO_CLIENT_USER_NOT_FOUND.value());
        }

        Set<Long> accessTenantIds = checkSsoTenant(user, tenants);

        getUserAccountService().checkLoginUser(user);

        checkSsoTenantCompany(user, tenants);

        getLoginRecordService().saveLocalLoginUser(user);

        Long tenantId = accessTenantIds.contains(user.getOrganizationId()) ? user.getOrganizationId() : accessTenantIds.stream().findFirst().get();

        LOGGER.debug("sso user retrieveUser, username: [{}], tenantId: [{}]", user.getLoginName(), tenantId);

        userDetails = getUserDetailsBuilder().buildUserDetails(user, tenantId);
        LOCAL_USER_DETAILS.set(userDetails);

        return userDetails;
    }

    /**
     * 校验单点登录的租户
     *
     * @param user    登录用户
     * @param tenants 域名分配的租户及公司
     * @return 返回有权限访问的租户ID
     */
    protected Set<Long> checkSsoTenant(@Nonnull User user, List<SsoTenant> tenants) {
        Set<Long> accessTenantIds;

        // 域名分配的租户
        Set<Long> tenantIds = tenants.stream().map(SsoTenant::getTenantId).collect(Collectors.toSet());
        // 角色所属租户
        Set<Long> roleTenantIds = Optional.ofNullable(user.getRoles()).orElse(Collections.emptyList()).stream().map(Role::getTenantId).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(tenantIds)) {
            accessTenantIds = roleTenantIds;
        } else {
            accessTenantIds = roleTenantIds.stream().filter(tenantIds::contains).collect(Collectors.toSet());
        }

        if (accessTenantIds.isEmpty()) {
            LOGGER.info("sso user tenant mismatch, username: [{}], ssoTenants: [{}], userTenantIds: [{}]", user.getLoginName(), tenants, roleTenantIds);
            throw new UsernameNotFoundException(SsoLoginExceptions.SSO_CLIENT_USER_NOT_FOUND.value());
        }
        return accessTenantIds;
    }

    /**
     * 检查租户公司
     *
     * @param user    登录用户
     * @param tenants 域名分配的租户及公司
     */
    protected void checkSsoTenantCompany(@Nonnull User user, List<SsoTenant> tenants) {
        //
    }

    protected UserAccountService getUserAccountService() {
        return userAccountService;
    }

    protected UserDetailsBuilder getUserDetailsBuilder() {
        return userDetailsBuilder;
    }

    protected LoginRecordService getLoginRecordService() {
        return loginRecordService;
    }

    public static void clearLocalResource() {
        LOCAL_USER_DETAILS.remove();
    }
}
