package org.hzero.starter.social.wechat.api;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import org.hzero.starter.social.core.common.api.AbstractSocialApi;
import org.hzero.starter.social.core.exception.ProviderUserNotFoundException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;

/**
 * 微信 API 默认实现，微信可以直接通过 access_token 获取到 openId 以及用户信息
 *
 * @author bojiangzhou 2018/10/16
 */
public class DefaultWechatApi extends AbstractSocialApi implements WechatApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWechatApi.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private String userInfoUrl;


    public DefaultWechatApi(String accessToken, Provider provider) {
        super(accessToken);
        this.userInfoUrl = provider.getUserInfoUrl() + "?openid={openId}";
    }

    @Override
    public WechatUser getUser(String providerUserId) {
        String result = getRestTemplate().getForObject(userInfoUrl, String.class, providerUserId);
        WechatUser user = null;
        try {
            user = mapper.readValue(result, WechatUser.class);
        } catch (Exception e) {
            LOGGER.error("parse wechat UserInfo error. result : {}", result);
        }
        if (user == null || StringUtils.isBlank(user.getNickname())) {
            LOGGER.info("not found provider user, result user={}", user);
            throw new ProviderUserNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 由于默认添加的 StringHttpMessageConverter 编码使用 {@code "ISO-8859-1"}，而微信要求 {@code "UTF-8"}，
     * 因此需要重新添加 UTF-8 的 StringHttpMessageConverter
     */
    @Override
    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> converters = super.getMessageConverters();
        converters.remove(0);
        converters.add(new StringHttpMessageConverter(Charsets.UTF_8));
        return converters;
    }

}
