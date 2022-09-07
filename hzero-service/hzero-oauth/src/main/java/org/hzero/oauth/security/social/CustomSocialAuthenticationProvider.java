package org.hzero.oauth.security.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialAuthenticationToken;

import org.hzero.core.user.UserType;
import org.hzero.oauth.domain.entity.User;
import org.hzero.oauth.security.service.LoginRecordService;
import org.hzero.oauth.security.service.UserAccountService;
import org.hzero.oauth.security.util.RequestUtil;
import org.hzero.starter.social.core.exception.SocialErrorCode;
import org.hzero.starter.social.core.provider.SocialUserProviderRepository;
import org.hzero.starter.social.core.security.SocialAuthenticationProvider;
import org.hzero.starter.social.core.security.SocialUserDetailsService;

/**
 *
 * @author bojiangzhou 2019/09/01
 */
public class CustomSocialAuthenticationProvider extends SocialAuthenticationProvider {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private LoginRecordService loginRecordService;

    public CustomSocialAuthenticationProvider(SocialUserProviderRepository userProviderRepository,
                                              SocialUserDetailsService userDetailsService) {
        super(userProviderRepository, userDetailsService);
    }

    @Override
    protected UserDetails retrieveUser(String username, SocialAuthenticationToken authentication) throws AuthenticationException {
        String userType = RequestUtil.getParameterValueFromRequestOrSavedRequest(UserType.PARAM_NAME, UserType.DEFAULT_USER_TYPE);
        User user = userAccountService.findLoginUser(username, UserType.ofDefault(userType));
        if (user == null) {
            throw new UsernameNotFoundException(SocialErrorCode.USER_NOT_FOUND);
        }
        // 校验用户账户有效性
        userAccountService.checkLoginUser(user);

        // 缓存登录用户信息
        loginRecordService.saveLocalLoginUser(user);

        return getUserDetailsService().loadUserByUsername(username);
    }
}
