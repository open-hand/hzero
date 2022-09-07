package org.hzero.boot.message.entity;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * websocket消息对象
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/19 15:49
 */
public class Msg {

    private Long userId;
    /**
     * websocketSession Id
     */
    private String sessionId;
    /**
     * 服务
     */
    private String service;
    /**
     * 话题
     */
    private String key;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 发送方式
     */
    private String type;
    /**
     * 用户信息
     */
    private CustomUserDetails userDetails;

    public Long getUserId() {
        return userId;
    }

    public Msg setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Msg setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getService() {
        return service;
    }

    public Msg setService(String service) {
        this.service = service;
        return this;
    }

    public String getKey() {
        return key;
    }

    public Msg setKey(String key) {
        this.key = key;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Msg setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getType() {
        return type;
    }

    public Msg setType(String type) {
        this.type = type;
        return this;
    }

    public CustomUserDetails getUserDetails() {
        return userDetails;
    }

    public Msg setUserDetails(CustomUserDetails userDetails) {
        this.userDetails = userDetails;
        return this;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "userId=" + userId +
                ", sessionId='" + sessionId + '\'' +
                ", service='" + service + '\'' +
                ", key='" + key + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", userDetails=" + userDetails +
                '}';
    }
}
