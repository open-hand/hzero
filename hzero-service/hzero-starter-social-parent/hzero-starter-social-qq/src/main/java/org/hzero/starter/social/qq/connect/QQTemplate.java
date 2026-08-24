package org.hzero.starter.social.qq.connect;

import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.hzero.starter.social.core.common.connect.SocialTemplate;
import org.hzero.starter.social.core.provider.Provider;

/**
 * QQ定制 OAuth2Template
 *
 * @author bojiangzhou 2018/10/26
 */
public class QQTemplate extends SocialTemplate {

    private static final Logger LOGGER = LoggerFactory.getLogger(QQTemplate.class);

    public QQTemplate(Provider provider) {
        super(provider);
        // 设置带上 client_id、client_secret
        setUseParametersForClientAuthentication(true);
    }

    /**
     * 解析 QQ 返回的令牌
     */
    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        // 返回格式：access_token=FE04********CCE2&expires_in=7776000&refresh_token=88E4***********BE14
        String result = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
        if (StringUtils.isBlank(result)) {
            throw new RestClientException("access token endpoint returned empty result");
        }
        LOGGER.debug("==> get qq access_token: " + result);
        String[] arr = StringUtils.split(result, "&");
        String accessToken = "", expireIn = "", refreshToken = "";
        for (String s : arr) {
            if (s.contains("access_token")) {
                accessToken = s.split("=")[1];
            } else if (s.contains("expires_in")) {
                expireIn = s.split("=")[1];
            } else if (s.contains("refresh_token")) {
                refreshToken = s.split("=")[1];
            }
        }
        return createAccessGrant(accessToken, null, refreshToken, Long.valueOf(expireIn), null);
    }

    /**
     * QQ 响应 ContentType=text/html;因此需要加入 text/html; 的处理器
     */
    @Override
    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = super.createRestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charsets.UTF_8));
        return restTemplate;
    }
}
