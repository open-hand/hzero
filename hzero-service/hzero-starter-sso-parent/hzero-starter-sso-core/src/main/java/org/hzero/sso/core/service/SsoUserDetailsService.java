package org.hzero.sso.core.service;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import org.hzero.sso.core.domain.SsoTenant;

/**
 * 获取SSO用户
 *
 * @author bojiangzhou 2020/08/18
 */
public interface SsoUserDetailsService {

    /**
     * 根据用户名和租户查询 UserDetails，也可以在此方法中对用户进行校验
     *
     * @param username username/phone/email
     * @param tenants  单点登录域名配置的租户和公司
     */
    UserDetails retrieveUser(@Nonnull String username, List<SsoTenant> tenants) throws AuthenticationException;

}
