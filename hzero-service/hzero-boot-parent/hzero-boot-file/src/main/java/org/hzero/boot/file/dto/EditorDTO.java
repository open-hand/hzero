package org.hzero.boot.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 编辑人员信息
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:44:54
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditorDTO {

    /**
     * 回调地址
     */
    private String callbackUrl;
    /**
     * 用户Id
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public EditorDTO setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public EditorDTO setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public EditorDTO setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @Override
    public String toString() {
        return "Editor{" +
                "callbackUrl='" + callbackUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
