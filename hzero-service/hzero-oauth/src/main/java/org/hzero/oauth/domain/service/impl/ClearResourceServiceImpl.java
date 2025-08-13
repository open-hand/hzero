package org.hzero.oauth.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.oauth.domain.service.ClearResourceService;
import org.hzero.oauth.security.custom.CustomClientDetailsService;
import org.hzero.oauth.security.custom.CustomUserDetailsService;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.sso.CustomSsoUserDetailsService;

/**
 * @author bojiangzhou 2020/05/25
 */
public class ClearResourceServiceImpl implements ClearResourceService {

    @Autowired
    private LoginRecordService loginRecordService;

    @Override
    public void cleaningResource() {
        // 清理登录用户
        loginRecordService.clearLocalLoginUser();

        // 清理 UserDetails
        CustomUserDetailsService.clearLocalResource();

        // 清理 ClientDetails
        CustomClientDetailsService.clearLocalResource();

        // 清理 SSO UserDetails
        CustomSsoUserDetailsService.clearLocalResource();
    }
}
