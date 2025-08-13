package org.hzero.sso.core.support;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.hzero.sso.core.domain.Domain;

/**
 * SSO 上下文环境
 *
 * @author bojiangzhou 2020/08/17
 */
public class SsoContextHolder {

    private static final String SESSION_ATTRIBUTE_DOMAIN = "SESSION_ATTRIBUTE_DOMAIN";
    private static final String SESSION_ATTRIBUTE_SSO_TYPE = "SESSION_ATTRIBUTE_SSO_TYPE";

    public static void setDomain(Domain domain) {
        HttpSession session = getSession();
        if (session == null) {
            return;
        }

        session.setAttribute(SESSION_ATTRIBUTE_DOMAIN, domain);
        session.setAttribute(SESSION_ATTRIBUTE_SSO_TYPE, domain.getSsoTypeCode().toLowerCase());
    }

    public static Domain getDomain() {
        HttpSession session = getSession();
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute(SESSION_ATTRIBUTE_DOMAIN);
        if (obj instanceof Domain) {
            return (Domain) obj;
        }
        return null;
    }

    public static Domain getNonNullDomain() {
        HttpSession session = getSession();
        Domain domain = null;
        if (session != null) {
            Object obj = session.getAttribute(SESSION_ATTRIBUTE_DOMAIN);
            if (obj instanceof Domain) {
                domain = (Domain) obj;
            }
        }
        if (domain == null) {
            throw new NullPointerException("domain is null");
        }

        return domain;
    }

    public static void setSsoType(String ssoType) {
        HttpSession session = getSession();
        if (session == null) {
            return;
        }

        session.setAttribute(SESSION_ATTRIBUTE_SSO_TYPE, ssoType.toLowerCase());
    }

    public static String getSsoType() {
        HttpSession session = getSession();
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute(SESSION_ATTRIBUTE_SSO_TYPE);
        if (obj instanceof String) {
            return (String) obj;
        }
        return null;
    }

    public static void clear(ServletRequest request) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(SESSION_ATTRIBUTE_DOMAIN);
        session.removeAttribute(SESSION_ATTRIBUTE_SSO_TYPE);
    }

    private static HttpSession getSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getSession();
    }

}
