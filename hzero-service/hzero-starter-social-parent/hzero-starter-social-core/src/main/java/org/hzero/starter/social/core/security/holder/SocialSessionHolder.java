package org.hzero.starter.social.core.security.holder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author bojiangzhou 2019/09/02
 */
public class SocialSessionHolder {

    public static void add(HttpServletRequest request, String prefix, String state, String value) {
        if (StringUtils.isAnyBlank(prefix, state)) {
            return;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(getKey(prefix, state), value);
        }
    }

    public static String get(HttpServletRequest request, String prefix, String state) {
        if (StringUtils.isAnyBlank(prefix, state)) {
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(getKey(prefix, state));
        }
        return null;
    }

    public static String remove(HttpServletRequest request, String prefix, String state) {
        if (StringUtils.isAnyBlank(prefix, state)) {
            return null;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            String value = (String) session.getAttribute(getKey(prefix, state));
            session.removeAttribute(getKey(prefix, state));
            return value;
        }
        return null;
    }

    private static String getKey(String prefix, String state) {
        return prefix + ":" + state;
    }

}
