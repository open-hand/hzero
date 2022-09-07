package org.hzero.starter.social.core.common.api;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

/**
 * 用户API抽象类
 *
 * @author bojiangzhou 2019/08/29
 */
public abstract class AbstractSocialApi extends AbstractOAuth2ApiBinding implements SocialApi {

    protected AbstractSocialApi() {
        super();
    }

    protected AbstractSocialApi(String accessToken) {
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
    }

    protected AbstractSocialApi(String accessToken, TokenStrategy tokenStrategy) {
        super(accessToken, tokenStrategy);
    }
}
