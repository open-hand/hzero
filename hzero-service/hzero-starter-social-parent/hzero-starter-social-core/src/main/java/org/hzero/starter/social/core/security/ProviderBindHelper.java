package org.hzero.starter.social.core.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 绑定账户
 *
 * @author bojiangzhou 2019/08/30
 */
public class ProviderBindHelper {

    public static final String SESSION_ATTRIBUTE = ProviderBindHelper.class.getName();

	private static SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

	public static void setConnection(HttpServletRequest request, Connection connection) {
        sessionStrategy.setAttribute(new ServletWebRequest(request), ProviderSignInAttempt.SESSION_ATTRIBUTE, connection);
    }

    public static Connection<?> getConnection(HttpServletRequest request) {
        return (Connection<?>) sessionStrategy.getAttribute(new ServletWebRequest(request), ProviderSignInAttempt.SESSION_ATTRIBUTE);
    }

    public static void removeConnection(HttpServletRequest request) {
        sessionStrategy.removeAttribute(new ServletWebRequest(request), ProviderSignInAttempt.SESSION_ATTRIBUTE);
    }

}
