package org.hzero.oauth.domain.vo;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * 认证结果封装
 *
 * @author bojiangzhou 2018/08/02
 */
public class AuthenticationResult {

    private boolean success;

    private String code;

    private String message;

    //
    // access_token
    // ------------------------------------------------------------------------------
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String scope;
    private String redirectUrl;
    private Long userId;

    /**
     * 错误次数
     */
    private long errorTimes;

    /**
     * 剩余次数
     */
    private long surplusTimes;

    public AuthenticationResult() {
    }

    public AuthenticationResult(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public AuthenticationResult authenticateSuccess() {
        this.success = true;
        this.code = "success";
        this.message = "success";
        return this;
    }

    public AuthenticationResult authenticateFail(String code, String message) {
        this.success = false;
        this.code = code;
        this.message = message;
        return this;
    }

    public AuthenticationResult setOAuth2AccessToken(OAuth2AccessToken oauth) {
        this.accessToken = oauth.getValue();
        if (oauth.getRefreshToken() != null) {
            this.refreshToken = oauth.getRefreshToken().getValue();
        }
        this.expiresIn = oauth.getExpiresIn();
        this.scope = StringUtils.join(oauth.getScope(), ",");
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public AuthenticationResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getCode() {
        return code;
    }

    public AuthenticationResult setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public AuthenticationResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public AuthenticationResult setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public long getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(long errorTimes) {
        this.errorTimes = errorTimes;
    }

    public long getSurplusTimes() {
        return surplusTimes;
    }

    public void setSurplusTimes(long surplusTimes) {
        this.surplusTimes = surplusTimes;
    }
}
