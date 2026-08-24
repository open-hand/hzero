package org.hzero.scheduler.util;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * 模拟用户登录工具
 *
 * @author bojiangzhou 2018/07/12
 */
public class UserLogin {

    /**
     * 模拟登陆
     *
     * @param loginName 登录名
     * @param userId 用户ID
     * @param organizationId 租户ID
     */
    public static void login(String loginName, Long userId, Long organizationId) {
        CustomUserDetails details = new CustomUserDetails(loginName, "", Collections.EMPTY_LIST);
        details.setUserId(userId);
        details.setLanguage("zh_CN");
        details.setTimeZone("CTT");
        details.setEmail("hand@hand-china.com");
        details.setOrganizationId(organizationId);

        AbstractAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(details, "", Collections.EMPTY_LIST);
        authentication.setDetails(details);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
