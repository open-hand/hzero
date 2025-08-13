package org.hzero.starter.social.apple.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hzero.starter.social.core.common.api.SocialUser;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-parent
 * @PackageName: org.hzero.starter.social.apple.api
 * @Date: 2020/4/27
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleUser implements SocialUser {
    private String openId;
    private String userName;
    private String mobile;
    private String email;


    public String getOpenId() {
        return openId;
    }

    public AppleUser setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AppleUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public AppleUser setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AppleUser setEmail(String email) {
        this.email = email;
        return this;
    }
}
