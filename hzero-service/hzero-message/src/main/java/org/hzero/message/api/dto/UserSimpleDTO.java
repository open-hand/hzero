package org.hzero.message.api.dto;

/**
 * 用户信息
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/08 15:56
 */
public class UserSimpleDTO {


    private String loginName;
    private String realName;

    public String getLoginName() {
        return loginName;
    }

    public UserSimpleDTO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public UserSimpleDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    @Override
    public String toString() {
        return  realName + " (" + loginName + ")";
    }
}
