package org.hzero.starter.social.core.common.connect;

import java.io.Serializable;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.social.connect.ConnectionData;

/**
 *
 * @author bojiangzhou 2019/09/04
 */
public class SocialUserData implements Serializable {
    private static final long serialVersionUID = 6485078605648791564L;

    private String providerId;

    private String providerUserId;

    private String providerUnionId;

    private String displayName;

    private String profileUrl;

    private String imageUrl;

    private String accessToken;

    private String secret;

    private String refreshToken;

    private Long expireTime;

    public SocialUserData() {
    }

    public SocialUserData(ConnectionData data) {
        this.providerId = data.getProviderId();
        this.providerUserId = data.getProviderUserId();
        this.providerUnionId = StringUtils.defaultIfBlank(data.getProviderUnionId(), null);
        this.displayName = data.getDisplayName();
        this.profileUrl = data.getProfileUrl();
        this.imageUrl = data.getImageUrl();
        this.accessToken = data.getAccessToken();
        this.secret = data.getSecret();
        this.refreshToken = data.getRefreshToken();
        this.expireTime = data.getExpireTime();
    }

    public String getProviderId() {
        return providerId;
    }

    public SocialUserData setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public SocialUserData setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
        return this;
    }

    public String getProviderUnionId() {
        return providerUnionId;
    }

    public void setProviderUnionId(String providerUnionId) {
        this.providerUnionId = providerUnionId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SocialUserData setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public SocialUserData setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public SocialUserData setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public SocialUserData setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public SocialUserData setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public SocialUserData setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public SocialUserData setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SocialUserData.class.getSimpleName() + "[", "]")
                .add("providerId='" + providerId + "'")
                .add("providerUserId='" + providerUserId + "'")
                .add("providerUnionId='" + providerUnionId + "'")
                .add("displayName='" + displayName + "'")
                .add("accessToken='" + accessToken + "'")
                .toString();
    }
}
