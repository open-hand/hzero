package org.hzero.oauth.domain.service;

import javax.servlet.http.HttpServletRequest;

import org.hzero.oauth.domain.entity.User;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * 插入登录登出记录
 *
 * @author xingxing.wu@hand-china.com 2018/11/26 8:57
 */
public interface AuditLoginService {

    /**
     * 记录登陆信息
     *
     * @param request     request
     * @param accessToken token
     * @param clientId    客户端
     * @param user        用户
     */
    void addLoginRecord(HttpServletRequest request, String accessToken, String clientId, CustomUserDetails user);

    /**
     * 记录登出信息
     *
     * @param request     请求
     * @param user        user
     * @param clientId    clientId
     */
    void addLogOutRecord(HttpServletRequest request, User user, String clientId);

    /**
     * 记录登录失败日志
     *
     * @param request 请求参数
     * @param user 登录用户信息
     * @param message 登录失败消息
     */
    void addLogFailureRecord(HttpServletRequest request, User user, String message);

    /**
     * 获取clientId
     *
     * @param servletRequest servletRequest
     * @return ClientId
     */
    String getClientId(HttpServletRequest servletRequest);
}
