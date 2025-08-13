package org.hzero.starter.social.hippius.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.starter.social.core.common.api.AbstractSocialApi;
import org.hzero.starter.social.core.exception.CommonSocialException;
import org.hzero.starter.social.core.exception.ProviderUserNotFoundException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-hippius
 * @PackageName: org.hzero.starter.social.hippius.api
 * @Date: 2020/4/5
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
public class DefaultHippiusApi extends AbstractSocialApi implements HippiusApi {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHippiusApi.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private String userInfoUrl;
    private String openIdUrl;


    private String openId;
    private String appId;


    public DefaultHippiusApi(String accessToken, Provider provider) {
        super(accessToken);
        appId = provider.getAppId();
        this.userInfoUrl = provider.getUserInfoUrl()+"?clientKey="+appId;
        this.openIdUrl = provider.getOpenIdUrl();

    }


    @Override
    public synchronized HippiusUser getUser() {

        if (!isAuthorized()) {
            throw new CommonSocialException(SocialErrorCode.SOCIAL_USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(openId)) {
            HippiusMe me = getMe();
            this.openId = me.getOpenId();
        }

        String result = getRestTemplate().getForObject(userInfoUrl, String.class);

        HippiusUser user = null;
        try {
            user = mapper.readValue(result, HippiusUser.class);
        } catch (Exception e) {
            logger.error("parse Hippius UserInfo error. result : {}", result);
        }
        if (user == null || StringUtils.isBlank(user.getNickname())) {
            logger.info("not found provider user, result user={}", user);
            throw new ProviderUserNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        user.setOpenId(openId);
        return user;

    }

    /**
     * 获取openId
     */
    private HippiusMe getMe() {

        // 返回结构：callback( {"openId":"31477667","nickname":"xxx","headimgurl":"xxx.png"} );
        String openIdResult = getRestTemplate().getForObject(openIdUrl, String.class);
        if (StringUtils.isBlank(openIdResult) || openIdResult.contains("error")) {
            logger.warn("request social user's openId return error, result={}", openIdResult);
            throw new CommonSocialException(SocialErrorCode.OPEN_ID_NOT_FOUND);
        }
        // 解析 openId
        String[] arr = StringUtils.substringBetween(openIdResult, "{", "}")
                .replace("\"", "")
                .split(",");

        String openid = null;
        for (String s : arr) {
            if (s.contains("openId")) {
                openid = s.split(":")[1];
            }
        }
        return new HippiusMe(openid);

    }


    private static class HippiusMe {
        private String openId;

        public HippiusMe(String openId) {
            this.openId = openId;
        }

        public String getOpenId() {
            return openId;
        }

    }


}
