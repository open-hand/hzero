package org.hzero.starter.social.core.provider;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * 三方应用信息
 *
 * @author bojiangzhou 2019/08/29
 */
public class Provider implements Serializable {
    private static final long serialVersionUID = 2160189747536987135L;

    private static final String SPLITTER = "@";

    private String providerId;
    private String channel;

    private String appId;
    // 应用ID 企业微信
    private String subAppId;
    private String appKey;
    private String providerName;

    private String scope;
    private String authorizeUrl;
    private String accessTokenUrl;
    private String openIdUrl;
    private String userInfoUrl;
    private String refreshTokenUrl;

    public Provider() {
    }

    /**
     *
     * @param providerId 三方登录编码
     * @param channel 登录渠道
     * @param appId 三方平台 APP ID
     * @param appKey 三方平台 APP KEY
     */
    public Provider(String providerId, String channel, String appId, String appKey,String subAppId) {
        this.providerId = providerId;
        this.channel = channel;
        this.appId = appId;
        this.subAppId = subAppId;
        this.appKey = appKey;
    }

    public static String uniqueProviderId(String providerId, String channel) {
        return providerId + SPLITTER + channel;
    }

    public static String realProviderId(String providerId) {
        if (!providerId.contains(SPLITTER)) {
            return providerId;
        }
        String[] arr = StringUtils.split(providerId, SPLITTER);
        return arr[0];
    }

    public String getProviderId() {
        return providerId;
    }

    public String getChannel() {
        return channel;
    }

    public String getAppId() {
        return appId;
    }

    public String getSubAppId() {
        return subAppId;
    }

    public void setSubAppId(String subAppId) {
        this.subAppId = subAppId;
    }

    public String getAppKey() {
        return appKey;
    }


    public String getProviderName() {
        return providerName;
    }

    public Provider setProviderName(String providerName) {
        this.providerName = providerName;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public Provider setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public Provider setAuthorizeUrl(String authorizeUrl) {
        this.authorizeUrl = authorizeUrl;
        return this;
    }

    public String getAccessTokenUrl() {
        return accessTokenUrl;
    }

    public Provider setAccessTokenUrl(String accessTokenUrl) {
        this.accessTokenUrl = accessTokenUrl;
        return this;
    }

    public String getOpenIdUrl() {
        return openIdUrl;
    }

    public void setOpenIdUrl(String openIdUrl) {
        this.openIdUrl = openIdUrl;
    }

    public String getUserInfoUrl() {
        return userInfoUrl;
    }

    public Provider setUserInfoUrl(String userInfoUrl) {
        this.userInfoUrl = userInfoUrl;
        return this;
    }

    public String getRefreshTokenUrl() {
        return refreshTokenUrl;
    }

    public Provider setRefreshTokenUrl(String refreshTokenUrl) {
        this.refreshTokenUrl = refreshTokenUrl;
        return this;
    }


}
