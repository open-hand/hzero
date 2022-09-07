package org.hzero.starter.social.wechat.enterprise.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.starter.social.core.common.api.AbstractSocialApi;
import org.hzero.starter.social.core.exception.CommonSocialException;
import org.hzero.starter.social.core.exception.ProviderUserNotFoundException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

/**
 * 企业微信 API 默认实现，微信需要通过access_token 和 code 获取到 userId
 * 通过 userId 和 token
 *
 * @author wanshun.zhang@hand-china.com 2019/11/12
 */
public class DefaultWechatEnterpriseApi extends AbstractSocialApi implements WechatEnterpriseApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWechatEnterpriseApi.class);

    private String userInfoUrl;
    private String openIdUrl;
    private String code;

    public DefaultWechatEnterpriseApi(String accessToken, Provider provider) {
        // 用#切分
        super(accessToken.split(BaseConstants.Symbol.WELL)[0]);
        if (accessToken.contains(BaseConstants.Symbol.WELL)) {
            String[] arr = accessToken.split(BaseConstants.Symbol.WELL);
            if (arr.length > 1) {
                this.code = arr[1];
            }
        }
        // 获取用户ID
        this.openIdUrl = provider.getOpenIdUrl() + "?code={code}";
        // 获取用户信息
        this.userInfoUrl = provider.getUserInfoUrl() + "?userid={userId}";
    }

    @Override
    @SuppressWarnings("unchecked")
    public WechatEnterpriseUser getUser(String providerUserId) {
        if (!isAuthorized()) {
            throw new CommonSocialException(SocialErrorCode.SOCIAL_USER_NOT_AUTHORIZED);
        }
        String userId;
        if (StringUtils.isBlank(providerUserId)) {
            userId = getUserId();
        } else {
            userId = providerUserId;
        }
        HashMap<String, String> userInfo = getRestTemplate().getForObject(this.userInfoUrl, HashMap.class, userId);

        if (userInfo == null || StringUtils.isBlank(userInfo.get(WechatEnterpriseUser.NAME))) {
            LOGGER.info("not found provider user, result user={}", userInfo);
            throw new ProviderUserNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        WechatEnterpriseUser user = new WechatEnterpriseUser();
        user.setName(userInfo.get(WechatEnterpriseUser.NAME));
        user.setAvatar(userInfo.get(WechatEnterpriseUser.AVATAR));
        user.setMobile(userInfo.get(WechatEnterpriseUser.MOBILE));

        user.setUserid(userId);
        return user;
    }

    /**
     * 获取用户 OpenId
     */
    @SuppressWarnings("unchecked")
    private String getUserId() {
        // 返回结构：callback( {"errcode": 0,"errmsg": "ok","UserId":"USERID"} );
        Map<String, String> openIdResult = getRestTemplate().getForObject(openIdUrl, Map.class, code);

        if (openIdResult == null || openIdResult.isEmpty()) {
            LOGGER.warn("request social user's openId return error, result={}", openIdResult);
            throw new CommonSocialException(SocialErrorCode.OPEN_ID_NOT_FOUND);
        }
        // 返回 openId
        return openIdResult.get("UserId");
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
