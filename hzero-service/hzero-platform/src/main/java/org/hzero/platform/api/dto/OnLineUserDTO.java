package org.hzero.platform.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.platform.domain.entity.AuditLogin;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.hzero.websocket.vo.SessionVO;

/**
 * 在线用户
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/12 14:25
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OnLineUserDTO {

    @ApiModelProperty("用户ID")
    @Encrypt
    private Long userId;
    @ApiModelProperty("角色ID")
    @Encrypt
    private Long roleId;
    @ApiModelProperty("用户token")
    private String accessToken;
    @ApiModelProperty("当前租户ID")
    private Long tenantId;
    @ApiModelProperty("当前租户")
    private String tenantName;
    @ApiModelProperty("所属租户ID")
    private Long organizationId;
    @ApiModelProperty("所属租户名称")
    private String organizationName;
    @ApiModelProperty("登录名")
    private String loginName;
    @ApiModelProperty("电话")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("登录时间")
    private Date loginDate;
    @ApiModelProperty("登录地址")
    private String loginIp;
    @ApiModelProperty("长连接的sessionId,用于发送长连接消息")
    private String sessionId;
    @ApiModelProperty("在线时长")
    private Long duration;

    public OnLineUserDTO setUser(SessionVO userVO) {
        this.tenantId = userVO.getTenantId();
        this.roleId = userVO.getRoleId();
        this.accessToken = userVO.getAccessToken();
        this.sessionId = userVO.getSessionId();
        return this;
    }

    public OnLineUserDTO setAuditLogin(AuditLogin auditLogin) {
        this.userId = auditLogin.getUserId();
        this.organizationId = auditLogin.getTenantId();
        this.organizationName = auditLogin.getTenantName();
        this.loginName = auditLogin.getLoginName();
        this.phone = auditLogin.getPhone();
        this.email = auditLogin.getEmail();
        this.loginDate = auditLogin.getLoginDate();
        this.loginIp = auditLogin.getLoginIp();
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getDuration() {
        return duration;
    }

    public OnLineUserDTO setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public String toString() {
        return "OnLineUserDTO{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                ", accessToken='" + accessToken + '\'' +
                ", tenantId=" + tenantId +
                ", tenantName='" + tenantName + '\'' +
                ", organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", loginDate=" + loginDate +
                ", loginIp='" + loginIp + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", duration=" + duration +
                '}';
    }
}
