package org.hzero.sso.core.service;

/**
 * 认证成功后，记录单点登出地址
 *
 * @author bojiangzhou 2020/08/24
 */
public interface SsoLogoutUrlRecordService {

    /**
     * 记录单点登出地址
     *
     * @param token             access_token
     * @param logoutRedirectUrl sso logoutUrl
     */
    void recordLogoutUrl(String token, String logoutRedirectUrl);

    /**
     * 清除单点退出地址
     *
     * @param token access_token
     */
    void clearLogoutUrl(String token);

}
