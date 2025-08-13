package org.hzero.starter.social.hippius.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hzero.starter.social.core.common.api.SocialUser;

/**
 * @Copyright (c) 2020. Hand Enterprise Solution Company. All right reserved.
 * @ProjectName: hzero-starter-social-hippius
 * @PackageName: org.hzero.starter.social.hippius.api
 * @Date: 2020/4/5
 * @author: ajisun
 * @Email: jiguang.sun@hand-china.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HippiusUser implements SocialUser {
    private String openId;
    private String nickname;
    private String headimgurl;
    private String mobile;
    private String email;
    private String countryName;
    private String regionName;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getMobile() {
        return mobile;
    }

    public HippiusUser setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public HippiusUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getCountryName() {
        return countryName;
    }

    public HippiusUser setCountryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public String getRegionName() {
        return regionName;
    }

    public HippiusUser setRegionName(String regionName) {
        this.regionName = regionName;
        return this;
    }
}
