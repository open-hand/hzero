package org.hzero.starter.social.core.common.connect;

import org.springframework.social.oauth2.OAuth2Template;

import org.hzero.starter.social.core.provider.Provider;

/**
 * OAuth2Template
 *
 * @author bojiangzhou 2019/08/29
 */
public class SocialTemplate extends OAuth2Template {

    public SocialTemplate(Provider provider) {
        super(provider.getAppId(), provider.getAppKey(), provider.getAuthorizeUrl(), provider.getAccessTokenUrl());
    }

}
