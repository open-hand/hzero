package org.hzero.sso.core.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

/**
 * 用户认证器，SSO服务端认证成功后，通过此认证器认证用户有效性
 *
 * @author bojiangzhou 2020/08/18
 */
public interface SsoAuthenticationProvider {

    /**
     * 认证SSO请求，返回已认证的 Authentication
     */
    Authentication authenticate(final HttpServletRequest request, final HttpServletResponse response);

}
