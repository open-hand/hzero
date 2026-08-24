package org.hzero.starter.social.qq.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.starter.social.core.common.api.AbstractSocialApi;
import org.hzero.starter.social.core.configuration.SocialProperties;
import org.hzero.starter.social.core.exception.CommonSocialException;
import org.hzero.starter.social.core.exception.ProviderUserNotFoundException;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.Provider;

/**
 * QQ API 默认实现。
 * 由于 Api 会使用得到的令牌来获取信息，每个用户的令牌是不同的，所以该类不是一个单例对象，每次访问 Api 都需要新建实例。
 *
 * @author bojiangzhou 2019/08/29
 */
public class DefaultQQApi extends AbstractSocialApi implements QQApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQQApi.class);

    private String userInfoUrl;
    private String openIdUrl;

    /**
     * 客户端 appId
     */
    private String appId;
    /**
     * openId
     */
    private String openId;
    /**
     * openId
     */
    private String unionId;

    private static final ObjectMapper mapper = new ObjectMapper();

    public DefaultQQApi(String accessToken, Provider provider) {
        super(accessToken);
        this.appId = provider.getAppId();
        this.userInfoUrl = provider.getUserInfoUrl() + "?oauth_consumer_key={appId}&openid={openId}";
        this.openIdUrl = provider.getOpenIdUrl();
        if (isGetUnionId()) {
            this.openIdUrl += "?unionid=1";
        }
    }

    @Override
    public synchronized QQUser getUser() {
        if (!isAuthorized()) {
            throw new CommonSocialException(SocialErrorCode.SOCIAL_USER_NOT_AUTHORIZED);
        }
        if (StringUtils.isBlank(openId)) {
            QQMe me = getMe();
            this.openId = me.getOpenId();
            this.unionId = me.getUnionId();
        }

        String result = getRestTemplate().getForObject(userInfoUrl, String.class, this.appId, this.openId);

        QQUser user = null;
        try {
            user = mapper.readValue(result, QQUser.class);
        } catch (Exception e) {
            LOGGER.error("parse qq UserInfo error. result : {}", result);
        }
        if (user == null || StringUtils.isBlank(user.getNickname())) {
            LOGGER.info("not found provider user, result user={}", user);
            throw new ProviderUserNotFoundException(SocialErrorCode.PROVIDER_USER_NOT_FOUND);
        }
        user.setOpenId(openId);
        user.setUnionId(unionId);
        return user;
    }

    /**
     * 获取用户 OpenId
     */
    private QQMe getMe() {
        // 返回结构：callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID","unionid":"YOUR_UNIONID"} );
        String openIdResult = getRestTemplate().getForObject(openIdUrl, String.class);
        if (StringUtils.isBlank(openIdResult) || openIdResult.contains("error")) {
            LOGGER.warn("request social user's openId return error, result={}", openIdResult);
            throw new CommonSocialException(SocialErrorCode.OPEN_ID_NOT_FOUND);
        }
        // 解析 openId
        String[] arr = StringUtils.substringBetween(openIdResult, "{", "}").replace("\"", "").split(",");

        String openid = null;
        String unionid = null;
        for (String s : arr) {
            if (s.contains("openid")) {
                openid = s.split(":")[1];
            } else if (s.contains("unionid")) {
                unionid = s.split(":")[1];
            }
        }
        return new QQMe(openid, unionid);
    }

    private boolean isGetUnionId() {
        SocialProperties socialProperties = ApplicationContextHelper.getContext().getBean(SocialProperties.class);
        return socialProperties.getQq().isGetUnionId();
    }

    private static class QQMe {
        private String openId;
        private String unionId;

        public QQMe(String openId, String unionId) {
            this.openId = openId;
            this.unionId = unionId;
        }

        public String getOpenId() {
            return openId;
        }

        public String getUnionId() {
            return unionId;
        }
    }

}
