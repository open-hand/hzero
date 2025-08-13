package org.hzero.oauth.security.sso;

import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.sso.core.service.SsoLogoutUrlRecordService;

/**
 *
 * @author bojiangzhou 2020/08/24
 */
public class CustomSsoLogoutUrlRecordService implements SsoLogoutUrlRecordService {

    private final LoginRecordService loginRecordService;

    public CustomSsoLogoutUrlRecordService(LoginRecordService loginRecordService) {
        this.loginRecordService = loginRecordService;
    }

    @Override
    public void recordLogoutUrl(String token, String logoutRedirectUrl) {
        loginRecordService.recordLogoutUrl(token, logoutRedirectUrl);
    }

    @Override
    public void clearLogoutUrl(String token) {
        loginRecordService.removeLogoutUrl(token);
    }
}
