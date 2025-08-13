package org.hzero.starter.social.wechat.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;

/**
 * 微信定制 OAuth2Template，微信认证流程与 OAuth2 标准流程有些不一样：参数 client_id -> appid，client_secret -> secret.
 * <br/>
 * 微信获取code地址：https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect <br/>
 * 微信获取令牌地址：https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code <br/>
 * 微信refresh_token地址：https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN <br/>
 * @author bojiangzhou 2018/10/26
 */
public class WechatTemplate extends SocialTemplate {

    private String clientId;

    private String clientSecret;

    private String accessTokenUrl;

    private String authorizeUrl;

    public WechatTemplate(Provider provider) {
        super(provider);
        // 设置带上 client_id、client_secret
        setUseParametersForClientAuthentication(true);

        this.clientId = provider.getAppId();
        this.clientSecret = provider.getAppKey();
        String clientInfo = "?appid=" + formEncode(clientId);
        this.authorizeUrl = provider.getAuthorizeUrl() + clientInfo;
        this.accessTokenUrl = provider.getAccessTokenUrl();
    }

    @Override
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
        StringBuilder authUrl = new StringBuilder(authorizeUrl);
        authUrl.append('&').append("response_type").append('=').append("code");
        for (Iterator<Map.Entry<String, List<String>>> additionalParams = parameters.entrySet().iterator(); additionalParams.hasNext();) {
            Map.Entry<String, List<String>> param = additionalParams.next();
            String name = formEncode(param.getKey());
            for (Iterator<String> values = param.getValue().iterator(); values.hasNext();) {
                authUrl.append('&').append(name);
                String value = values.next();
                if (org.springframework.util.StringUtils.hasLength(value)) {
                    authUrl.append('=').append(formEncode(value));
                }
            }
        }
        return authUrl.toString();
    }

    @Override
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("appid", clientId); // OAuth2 标准为 client_id
        params.set("secret", clientSecret); // OAuth2 标准为 client_secret
        params.set("code", authorizationCode);
        //params.set("redirect_uri", redirectUri); // 微信获取令牌不需要传 redirect_uri
        params.set("grant_type", "authorization_code");
        if (additionalParameters != null) {
            params.putAll(additionalParameters);
        }
        return postForAccessGrant(accessTokenUrl, params);
    }

    @Override
    public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("appid", clientId); // OAuth2 标准为 client_id
        //params.set("secret", clientSecret); // 微信 不需要传 secret
        params.set("refresh_token", refreshToken);
        params.set("grant_type", "refresh_token");
        if (additionalParameters != null) {
            params.putAll(additionalParameters);
        }
        return postForAccessGrant(accessTokenUrl, params);
    }

    /**
     * 解析微信返回的令牌，微信同时将 openId 和 unionid 都返回来了
     */
    @Override
    @SuppressWarnings("unchecked")
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        Map<String, Object> result = getRestTemplate().postForObject(accessTokenUrl, parameters, Map.class);
        if (result == null) {
            throw new RestClientException("access token endpoint returned empty result");
        }
        String accessToken = (String) result.get("access_token");
        String scope = (String) result.get("scope");
        String refreshToken = (String) result.get("refresh_token");
        Long expireIn = Long.valueOf(String.valueOf(result.get("expires_in")));
        String openId = (String) result.get("openid");
        String unionId = (String) result.get("unionid");

        return new WechatAccessGrant(accessToken, scope, refreshToken, expireIn, openId, unionId);
    }

    private String formEncode(String data) {
        try {
            return URLEncoder.encode(data, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
