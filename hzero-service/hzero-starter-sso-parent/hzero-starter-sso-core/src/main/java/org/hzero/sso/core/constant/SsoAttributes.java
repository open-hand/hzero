package org.hzero.sso.core.constant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * SSO 常量
 *
 * @author bojiangzhou 2020/08/17
 */
public class SsoAttributes {

    public static final String SESSION_ATTRIBUTE_LOGOUT_URL = "SESSION_ATTRIBUTE_LOGOUT_URL";

    public static final String UNKNOWN = "unknown";

    public static final String SSO_DEFAULT_PROCESS_URI = "/sso/**";

    public static String formEncode(String data) {
        try {
            return URLEncoder.encode(data, StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException ex) {
            // should not happen, UTF-8 is always supported
            throw new IllegalStateException(ex);
        }
    }

}
