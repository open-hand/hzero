package org.hzero.sso.core.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;

/**
 * 登出成功处理器
 *
 * @author bojiangzhou 2020/08/18
 */
public interface SsoClientLogoutHandler {

    void onLogoutSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication);
}
