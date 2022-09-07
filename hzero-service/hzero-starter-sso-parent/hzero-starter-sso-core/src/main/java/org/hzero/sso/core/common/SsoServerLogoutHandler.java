package org.hzero.sso.core.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 单点登出处理
 *
 * @author bojiangzhou 2020/08/19
 */
public interface SsoServerLogoutHandler {

    /**
     * 是否单点登出请求
     */
    default boolean isLogoutRequest(final HttpServletRequest request) {
        return false;
    }

    /**
     * 处理单点登出请求
     *
     * @return access_token
     */
    default String handleLogoutRequest(final HttpServletRequest request, final HttpServletResponse response) {
        return null;
    }
}
