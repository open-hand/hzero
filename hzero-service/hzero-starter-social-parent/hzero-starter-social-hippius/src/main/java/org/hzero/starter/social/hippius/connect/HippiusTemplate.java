package org.hzero.starter.social.hippius.connect;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-hippius
 * @PackageName: org.hzero.starter.social.hippius.connect
 * @Date: 2020/4/5
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class HippiusTemplate extends SocialTemplate {
    private static final Logger logger = LoggerFactory.getLogger(HippiusTemplate.class);

    private String clientId;

    private String clientSecret;

    private String accessTokenUrl;

    private String authorizeUrl;


    public HippiusTemplate(Provider provider) {
        super(provider);
        setUseParametersForClientAuthentication(true);

        this.clientId = provider.getAppId();
        this.clientSecret = provider.getAppKey();
        String clientInfo = "?client_id=" + clientId;
        this.authorizeUrl = provider.getAuthorizeUrl() + clientInfo;
        this.accessTokenUrl = provider.getAccessTokenUrl();
    }


    @Override
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
        StringBuilder authUrl = new StringBuilder(authorizeUrl);
        authUrl.append('&').append("response_type").append('=').append("code");
        for (Iterator<Map.Entry<String, List<String>>> additionalParams = parameters.entrySet().iterator(); additionalParams.hasNext();) {
            Map.Entry<String, List<String>> param = additionalParams.next();
            String name = param.getKey();
            for (Iterator<String> values = param.getValue().iterator(); values.hasNext();) {
                authUrl.append('&').append(name);
                String value = values.next();
                if (org.springframework.util.StringUtils.hasLength(value)) {
                    authUrl.append('=').append(value);
                }
            }
        }
        return authUrl.toString();
    }


    @Override
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("client_id", clientId);
        params.set("client_secret", clientSecret);
        params.set("code", authorizationCode);
        params.set("grant_type", "authorization_code");
        if (additionalParameters != null) {
            params.putAll(additionalParameters);
        }
        return postForAccessGrant(accessTokenUrl, params);
    }

    /**
     * 解析 海马汇 返回的令牌
     * 海马汇返回结构
     * {
     * "access_token": "d5d9a856-fe31-47ea-9b0a-fd5934c16626",
     * "token_type": "bearer",
     * "refresh_token": "c5c29315-dfc2-4c32-8854-d6c38b52f215",
     * "expires_in": 7189,
     * "scope": "default"
     * }
     */
    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        Map result = getRestTemplate().postForObject(accessTokenUrl, parameters, Map.class);
        if (result == null) {
            throw new RestClientException("access token endpoint returned empty result");
        }
        logger.debug("==> get hippius access_token: " + result);

        String accessToken = (String) result.get("access_token");
        String scope = (String) result.get("scope");
        String refreshToken = (String) result.get("refresh_token");
        Long expireIn = Long.valueOf(String.valueOf(result.get("expires_in")));

        return createAccessGrant(accessToken, scope, refreshToken, expireIn, null);
    }


}
