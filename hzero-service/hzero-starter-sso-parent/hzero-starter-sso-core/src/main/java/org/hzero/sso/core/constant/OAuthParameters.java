package org.hzero.sso.core.constant;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;

/**
 *
 * @author bojiangzhou 2020/08/28
 */
public class OAuthParameters {

    public static final String CLIENT_ID = OAuth2Utils.CLIENT_ID;
    public static final String CLIENT_SECRET = "client_secret";

    public static final String RESPONSE_TYPE = OAuth2Utils.RESPONSE_TYPE;
    public static final String GRANT_TYPE = OAuth2Utils.GRANT_TYPE;
    public static final String REDIRECT_URI = OAuth2Utils.REDIRECT_URI;
    public static final String SCOPE = OAuth2Utils.SCOPE;
    public static final String STATE = OAuth2Utils.STATE;
    public static final String CODE = "code";
    public static final String ACCESS_TOKEN = OAuth2AccessToken.ACCESS_TOKEN;

    public static final String DEVICE_ID = "device_id";
}
