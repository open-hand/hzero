package org.hzero.starter.social.sina.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.starter.social.core.common.api.AbstractSocialApi;
import org.hzero.starter.social.core.common.api.SocialUser;
import org.hzero.starter.social.core.exception.ProviderUserNotFoundException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;

/**
 * Sina API 默认实现 openId在sina的api中名字为uid
 *
 * @author liufanghan 2019/09/18 11:21
 */
public class DefaultSinaApi extends AbstractSocialApi implements SinaApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSinaApi.class);

    /**
     * 查询用户信息的url
     */
    private String userInfoUrl;

    private static final ObjectMapper mapper = new ObjectMapper();


    public DefaultSinaApi(String accessToken, Provider provider) {
        super(accessToken);
        //获取用户信息url
        this.userInfoUrl = provider.getUserInfoUrl()+"?uid={openId}";
    }

    /**
     * 获取用户信息
     * @param providerUserId 三方用户ID(openId)
     * @return org.hzero.starter.social.core.common.api.SocialUser
     */
    @Override
    public SocialUser getUser(String providerUserId) {
        String result = getRestTemplate().getForObject(userInfoUrl, String.class, providerUserId);
        SinaUser user = null;
        try {
            user = mapper.readValue(result, SinaUser.class);
        } catch (Exception e) {
            LOGGER.error("parse sina UserInfo error. result : {}", result);
        }
        if (user == null || StringUtils.isBlank(user.getIdstr())) {
            LOGGER.info("not found provider user, result user={}", user);
            throw new ProviderUserNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        return user;
    }

}
