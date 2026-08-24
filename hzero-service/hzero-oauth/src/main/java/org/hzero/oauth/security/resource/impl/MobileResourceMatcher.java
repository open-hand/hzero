package org.hzero.oauth.security.resource.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hzero.oauth.security.resource.ResourceMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 移动端资源匹配器，对 /oauth/authorize 放行
 *
 * @author bojiangzhou 2019/01/09
 */
public class MobileResourceMatcher implements ResourceMatcher {

    private final AntPathRequestMatcher apiMatcher;
    private final AntPathRequestMatcher authMatcher;

    public MobileResourceMatcher() {
        apiMatcher = new AntPathRequestMatcher("/api/**");
        authMatcher = new AntPathRequestMatcher("/oauth/authorize", "GET");
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");
        String authorization = request.getHeader("Authorization");

        boolean tokenExist = StringUtils.isNotBlank(accessToken)
                || (StringUtils.isNotBlank(authorization) && authorization.toLowerCase().startsWith("bearer"));

        return apiMatcher.matches(request) || (tokenExist && authMatcher.matches(request));
    }
}
