package org.hzero.sso.core.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户退出，令牌清理
 *
 * @author bojiangzhou 2020/08/19
 */
public interface SsoTokenClearService {

    /**
     * SSO 用户退出时，清理令牌
     *
     * @param token 令牌
     */
    void clearTokenOnLogout(final HttpServletRequest request, final HttpServletResponse response, final String token);

}
