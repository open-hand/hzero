package org.hzero.starter.social.wechat.enterprise.connection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.google.common.base.Charsets;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 微信定制 OAuth2Template，微信认证流程与 OAuth2 标准流程有些不一样：参数 client_id -> appid，client_secret -> secret.
 * <br/>
 * 微信获取code地址：https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=CORPID&agentid=AGENTID&redirect_uri=REDIRECT_URI&state=STATE <br/>
 * 微信获取令牌地址：https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ID&corpsecret=SECRET <br/>
 * 微信获取获取用户id地址：https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=ACCESS_TOKEN&code=CODE
 * 微信获取用户信息地址：https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&userid=USERID
 *
 * @author wanshun.zhang@hand-china.com 2019/11/12
 */
public class WechatEnterpriseTemplate extends SocialTemplate {

    private String clientId;

    private String agentId;

    private String clientSecret;

    private String accessTokenUrl;

    private String authorizeUrl;

    public WechatEnterpriseTemplate(Provider provider) {
        super(provider);
        // 设置带上 client_id、client_secret
        setUseParametersForClientAuthentication(true);

        this.clientId = provider.getAppId();
        this.agentId = provider.getSubAppId();
        this.clientSecret = provider.getAppKey();
        this.authorizeUrl = provider.getAuthorizeUrl() + "?appid=" + clientId + "&agentid=" + agentId;
        this.accessTokenUrl = provider.getAccessTokenUrl() + "?corpid=" + clientId + "&corpsecret="+ clientSecret;
    }

    @Override
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
        StringBuilder authUrl = new StringBuilder(authorizeUrl);
        for (Iterator<Map.Entry<String, List<String>>> additionalParams = parameters.entrySet().iterator(); additionalParams.hasNext(); ) {
            Map.Entry<String, List<String>> param = additionalParams.next();
            String name = formEncode(param.getKey());
            for (Iterator<String> values = param.getValue().iterator(); values.hasNext(); ) {
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
    @SuppressWarnings("unchecked")
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        Map<String, Object> result = getRestTemplate().getForObject(accessTokenUrl, Map.class);
        if (result == null) {
            throw new RestClientException("access token endpoint returned empty result");
        }
        int errCode = (int) result.get("errcode");
        String errMsg = (String) result.get("errmsg");
        if (errCode != 0) {
            throw new RestClientException(errMsg);
        }
        // accessToken拼接上code 用于获取userID(openId)
        String accessToken = result.get("access_token") + BaseConstants.Symbol.WELL + authorizationCode;
        Long expireIn = Long.valueOf(String.valueOf(result.get("expires_in")));
        return createAccessGrant(accessToken, null, null, expireIn, null);
    }

    /**
     * 响应 ContentType=text/html;因此需要加入 text/html; 的处理器
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charsets.UTF_8));
        return restTemplate;
    }

    private String formEncode(String data) {
        try {
            return URLEncoder.encode(data, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
