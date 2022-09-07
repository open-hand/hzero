package org.hzero.starter.social.core.common.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import org.hzero.starter.social.core.common.api.SocialApi;

/**
 * SocialApi 适配器
 *
 * @author bojiangzhou 2019/08/29
 */
public abstract class SocialApiAdapter implements ApiAdapter<SocialApi> {

    private String providerUserId;

    public SocialApiAdapter() { }

    public SocialApiAdapter(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    @Override
    public abstract void setConnectionValues(SocialApi api, ConnectionValues values);

    @Override
    public boolean test(SocialApi api) {
        return true;
    }

    @Override
    public UserProfile fetchUserProfile(SocialApi api) {
        return  null;
    }

    @Override
    public void updateStatus(SocialApi api, String message) {

    }

    public String getProviderUserId() {
        return providerUserId;
    }
}
