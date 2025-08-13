package org.hzero.starter.social.qq.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.hzero.starter.social.core.common.api.SocialUser;

/**
 * QQ 用户信息
 *
 * @author bojiangzhou 2019/08/29
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QQUser implements SocialUser {

    private String ret;

    private String msg;

    private String openId;

    private String nickname;

    private String figureurl;

    private String gender;

    private String unionId;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

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

    public String getFigureurl() {
        return figureurl;
    }

    public void setFigureurl(String figureurl) {
        this.figureurl = figureurl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
