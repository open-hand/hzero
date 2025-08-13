package org.hzero.message.domain.vo;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/30 11:29
 */
public class UserCacheVO {

    private Long id;
    private String loginName;
    private String realName;
    private String userType;
    private String language;

    public Long getId() {
        return id;
    }

    public UserCacheVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getLoginName() {
        return loginName;
    }

    public UserCacheVO setLoginName(String loginName) {
        this.loginName = loginName;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public UserCacheVO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getUserType() {
        return userType;
    }

    public UserCacheVO setUserType(String userType) {
        this.userType = userType;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public UserCacheVO setLanguage(String language) {
        this.language = language;
        return this;
    }

    @Override
    public String toString() {
        return "UserCacheVO{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", realName='" + realName + '\'' +
                ", userType='" + userType + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
