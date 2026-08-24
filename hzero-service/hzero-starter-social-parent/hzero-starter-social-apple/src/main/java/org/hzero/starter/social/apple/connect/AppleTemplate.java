package org.hzero.starter.social.apple.connect;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-parent
 * @PackageName: org.hzero.starter.social.apple.connect
 * @Date: 2020/4/27
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class AppleTemplate extends SocialTemplate {

    private static final Logger logger = LoggerFactory.getLogger(AppleTemplate.class);

    private String clientId;

    private String clientSecret;

    private String accessTokenUrl;

    private String authorizeUrl;

    public AppleTemplate(Provider provider) {
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
        authUrl.append('&').append("response_mode").append('=').append("form_post");
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

    /**
     * 解析 apple 返回的令牌
     */
    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        /**
         * {
         *     ""data:{
                 "access_token": "一个token，此处省略",
                 "token_type": "Bearer",
                 "expires_in": 3600,
                 "refresh_token": "一个token，此处省略",
                 "id_token": "结果是JWT，字符串形式，此处省略"
                 }
         * }
         */
        String result = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
        if (StringUtils.isBlank(result)) {
            throw new RestClientException("access token endpoint returned empty result");
        }
        logger.debug("==> get apple access_token: " + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject token = jsonObject.getJSONObject("data");
        String accessToken = "", expireIn = "", refreshToken = "";
        accessToken = token.getString("access_token");
        refreshToken = token.getString("refresh_token");
        expireIn = token.getString("expires_in");

        return createAccessGrant(accessToken, null, refreshToken, Long.valueOf(expireIn), null);
    }




}
