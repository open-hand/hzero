package org.hzero.oauth.security.social;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.exception.CustomAuthenticationException;
import org.hzero.oauth.security.exception.LoginExceptions;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.service.UserDetailsBuilder;
import org.hzero.starter.social.core.security.SocialUserDetailsService;

/**
 *
 * @author bojiangzhou 2019/09/01
 */
public class CustomSocialUserDetailsService implements SocialUserDetailsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomSocialUserDetailsService.class);

    private final UserAccountService userAccountService;
    private final UserDetailsBuilder userDetailsBuilder;
    private final LoginRecordService loginRecordService;

    public CustomSocialUserDetailsService(UserAccountService userAccountService,
                                    UserDetailsBuilder userDetailsBuilder,
                                    LoginRecordService loginRecordService) {
        this.userAccountService = userAccountService;
        this.userDetailsBuilder = userDetailsBuilder;
        this.loginRecordService = loginRecordService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getLoginRecordService().getLocalLoginUser();
        if (user == null) {
            user = getUserAccountService().findLoginUser(username);
            if (user == null) {
                throw new CustomAuthenticationException(LoginExceptions.USERNAME_OR_PASSWORD_ERROR.value());
            }

            userAccountService.checkLoginUser(user);

            getLoginRecordService().saveLocalLoginUser(user);
        }
        LOGGER.debug("load custom user, username={}", username);
        return getUserDetailsBuilder().buildUserDetails(user);
    }

    protected UserAccountService getUserAccountService() {
        return userAccountService;
    }

    protected UserDetailsBuilder getUserDetailsBuilder() {
        return userDetailsBuilder;
    }

    protected LoginRecordService getLoginRecordService() {
        return loginRecordService;
    }
}
