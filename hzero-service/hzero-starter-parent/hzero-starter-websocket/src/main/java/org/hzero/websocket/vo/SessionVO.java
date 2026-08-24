package org.hzero.websocket.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/14 9:45
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SessionVO {

    /**
     * websocketSession id
     */
    private String sessionId;
    /**
     * 用户Id
     */
    private Long userId;
    /**
     * 当前租户
     */
    private Long tenantId;
    /**
     * 当前角色
     */
    private Long roleId;
    /**
     * token
     */
    private String accessToken;
    /**
     * brokerId
     */
    private String brokerId;


    /**
     * group
     */
    private String group;

    public SessionVO() {

    }

    public SessionVO(String sessionId, Long userId, Long tenantId, Long roleId, String accessToken, String brokerId) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.tenantId = tenantId;
        this.roleId = roleId;
        this.accessToken = accessToken;
        this.brokerId = brokerId;
    }

    public SessionVO(String sessionId, String group, String brokerId) {
        this.sessionId = sessionId;
        this.group = group;
        this.brokerId = brokerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public SessionVO setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public SessionVO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public SessionVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getRoleId() {
        return roleId;
    }

    public SessionVO setRoleId(Long roleId) {
        this.roleId = roleId;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public SessionVO setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public SessionVO setBrokerId(String brokerId) {
        this.brokerId = brokerId;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public SessionVO setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "sessionId='" + sessionId + '\'' +
                ", userId=" + userId +
                ", tenantId=" + tenantId +
                ", roleId=" + roleId +
                ", accessToken='" + accessToken + '\'' +
                ", brokerId='" + brokerId + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
