package org.hzero.starter.social.sina.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hzero.starter.social.core.common.api.SocialUser;

/**
 * Sina 用户信息
 *
 * @author liufanghan 2019/09/18 10:51
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SinaUser implements SocialUser {

    /**
     * 用户UID
     */
    private Long id;

    /**
     * 字符串型的用户UID
     */
    private String idstr;

    /**
     * 用户昵称
     */
    private String screen_name;

    /**
     * 友好显示名称
     */
    private String name;

    /**
     * 用户所在省级ID
     */
    private String province;

    /**
     * 用户所在城市ID
     */
    private String city;

    /**
     * 用户所在地
     */
    private String location;

    /**
     * 用户个人描述
     */
    private String description;

    /**
     * 用户博客地址
     */
    private String url;

    /**
     * 用户头像地址（中图），50×50像素
     */
    private String profile_image_url;

    /**
     * 用户的微博统一URL地址
     */
    private String profile_url;

    /**
     * 用户的个性化域名
     */
    private String domain;
    /**
     * 性别，m：男、f：女、n：未知
     */
    private String gender;
    /**
     * 用户的微号
     */
    private String weihao;

    public SinaUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeihao() {
        return weihao;
    }

    public void setWeihao(String weihao) {
        this.weihao = weihao;
    }
}
