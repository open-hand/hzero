package org.hzero.oauth.security.custom;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import org.hzero.oauth.security.constant.SecurityAttributes;
import org.hzero.oauth.security.util.LoginUtil;

/**
 * 生成 access_token key，移动端加入设备ID区分唯一，web端加入sessionId区分唯一.
 *
 * @author bojiangzhou
 */
public class CustomAuthenticationKeyGenerator implements AuthenticationKeyGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationKeyGenerator.class);

    private LoginUtil loginUtil;

    public CustomAuthenticationKeyGenerator(LoginUtil loginUtil) {
        this.loginUtil = loginUtil;
    }


    private static final String CLIENT_ID = "client_id";

    private static final String SCOPE = "scope";

    private static final String USERNAME = SecurityAttributes.SECURITY_LOGIN_USERNAME;

    private static final String SESSION = "session";

    @Override
    public String extractKey(OAuth2Authentication authentication) {
        if (authentication == null) {
            LOGGER.warn("extractKey authentication is null.");
            return null;
        }
        Map<String, String> values = new LinkedHashMap<>();
        OAuth2Request authorizationRequest = authentication.getOAuth2Request();
        if (!authentication.isClientOnly()) {
            values.put(USERNAME, authentication.getName());
        }
        values.put(CLIENT_ID, authorizationRequest.getClientId());
        if (authorizationRequest.getScope() != null) {
            values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<>(authorizationRequest.getScope())));
        }

        String deviceIdParameter = loginUtil.getDeviceIdParameter();
        if (loginUtil.isMobileDeviceLogin(authentication)) { // 移动设备登录加入设备ID
            Map<String, String> parameters = authentication.getOAuth2Request().getRequestParameters();
            String deviceId = parameters.get(deviceIdParameter);
            if (StringUtils.isEmpty(deviceId)) {
                throw new IllegalArgumentException("the parameter device_id should not be empty when login in mobile device.");
            }
            values.put(deviceIdParameter, deviceId);
        } else { // web登录加入sessionID
            Authentication auth = authentication.getUserAuthentication();
            if (auth != null && auth.getDetails() instanceof WebAuthenticationDetails) {
                String sessionId = ((WebAuthenticationDetails) auth.getDetails()).getSessionId();
                LOGGER.info("sessionId : {}", sessionId);
                if (!StringUtils.isEmpty(sessionId)) {
                    values.put(SESSION, sessionId);
                }
            } else if (auth != null && auth.getDetails() instanceof Map) {
                Map parameters = (Map) auth.getDetails();
                if (parameters.containsKey(deviceIdParameter)) {
                    String deviceId = Optional.ofNullable(parameters.get(deviceIdParameter)).map(Object::toString).orElse("");
                    if (StringUtils.isNotBlank(deviceId)){
                        values.put(deviceIdParameter, deviceId);
                    }
                }
            } else {
                Map<String, String> parameters = authorizationRequest.getRequestParameters();
                String deviceId;
                if (parameters != null && (deviceId = parameters.get(deviceIdParameter)) != null) {
                    values.put(deviceIdParameter, deviceId);
                }
            }
        }

        return generateKey(values);
    }

    private String generateKey(Map<String, String> values) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(values.toString().getBytes(StandardCharsets.UTF_8.displayName()));
            return String.format("%032x", new BigInteger(1, bytes));
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).", nsae);
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).", uee);
        }
    }

    protected LoginUtil getLoginUtil() {
        return loginUtil;
    }
}
