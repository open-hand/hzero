package org.hzero.oauth.security.service.impl;

import io.choerodon.core.oauth.CustomUserDetails;
import org.hzero.boot.oauth.domain.entity.BaseClient;
import org.hzero.boot.oauth.user.UserPostProcessor;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.service.UserDetailsBuilder;
import org.hzero.oauth.security.service.UserDetailsWrapper;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 构建 CustomUserDetails
 *
 * @author bojiangzhou 2019/07/24
 */
public class DefaultUserDetailsBuilder implements UserDetailsBuilder {
    private static final String LANG = "lang";

    private final UserDetailsWrapper userDetailsWrapper;
    private final UserAccountService userAccountService;
    private final List<UserPostProcessor> userPostProcessorList;

    public DefaultUserDetailsBuilder(UserDetailsWrapper userDetailsWrapper,
                                     UserAccountService userAccountService,
                                     List<UserPostProcessor> userPostProcessorList) {
        this.userDetailsWrapper = userDetailsWrapper;
        this.userAccountService = userAccountService;
        this.userPostProcessorList = Optional.ofNullable(userPostProcessorList).orElseGet(Collections::emptyList);
        Collections.sort(this.userPostProcessorList);
    }

    @Override
    public CustomUserDetails buildUserDetails(User user) {
        return buildUserDetails(user, null);
    }

    @Override
    public CustomUserDetails buildUserDetails(User user, Long tenantId) {
        tenantId = Optional.ofNullable(tenantId).orElse(user.getOrganizationId());

        CustomUserDetails details = new CustomUserDetails(user.getLoginName(), user.getPassword(), user.getUserType(), Collections.emptyList());
        details.setUserId(user.getId());
        details.setLanguage(Optional.ofNullable(getLanguageFromSession()).orElse(user.getLanguage()));
        details.setTimeZone(user.getTimeZone());
        details.setEmail(user.getEmail());
        details.setOrganizationId(user.getOrganizationId());
        details.setAdmin(user.getAdmin());
        details.setRealName(user.getRealName());
        details.setImageUrl(user.getImageUrl());

        BaseClient client = userAccountService.findCurrentClient();
        if (client != null) {
            // 接口加密标识
            details.setApiEncryptFlag(client.getApiEncryptFlag());
            // api防重放标识
            details.setApiReplayFlag(client.getApiReplayFlag());
        }

        userPostProcessorList.forEach(item -> item.beforeWrapper(details));
        getUserDetailsWrapper().warp(details, user.getId(), tenantId, true);
        userPostProcessorList.forEach(item -> item.afterWrapper(details));

        return details;
    }

    private String getLanguageFromSession() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpSession session = ((ServletRequestAttributes) requestAttributes).getRequest().getSession(false);
            if (session != null) {
                String attribute = (String) session.getAttribute(LANG);
                if (StringUtils.hasText(attribute)) {
                    return attribute;
                }
            }
        }
        return null;
    }

    public UserDetailsWrapper getUserDetailsWrapper() {
        return userDetailsWrapper;
    }
}
