package org.hzero.starter.social.sina.connection;

import com.google.common.base.Charsets;
import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Sina Oauth2 Template 用于获取三方应用 access_token 等等
 *
 * @author liufanghan 2019/09/18 12:51
 */
public class SinaTemplate extends SocialTemplate {

    public SinaTemplate(Provider provider) {
        super(provider);
        // 设置带上 client_id、client_secret
        setUseParametersForClientAuthentication(true);
    }

    /**
     * Sina 响应 ContentType=text/html;因此需要加入 text/html; 的处理器
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charsets.UTF_8));
        return restTemplate;
    }

    /**
     * 解析新浪返回的access_token 新浪同时返回了uid(open_id)
     */
    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        //格式：{"access_token": "SlAV32hkKG","remind_in": 3600,"expires_in": 3600}

        Map result = getRestTemplate().postForObject(accessTokenUrl, parameters, Map.class);

        if (result == null) {
            throw new RestClientException("access token endpoint returned empty result");
        }
        String accessToken = (String) result.get("access_token");
        Long expiresIn = Long.valueOf(String.valueOf(result.get("expires_in")));
        String uid = (String) result.get("uid");

        return new SinaAccessGrant(accessToken,expiresIn,uid);
    }


}
