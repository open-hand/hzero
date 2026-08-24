package org.hzero.sso.idm;

import static org.hzero.sso.core.constant.SsoAttributes.UNKNOWN;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.StringUtils;

import org.hzero.sso.core.common.SsoAuthenticationProviderAdapter;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.exception.SsoUserNotFoundException;
import org.hzero.sso.core.security.SsoAuthenticationToken;
import org.hzero.sso.core.service.SsoUserDetailsService;

/**
 *
 * @author bojiangzhou 2020/08/20
 */
public class IdmAuthenticationProvider extends SsoAuthenticationProviderAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdmAuthenticationProvider.class);

    public IdmAuthenticationProvider(SsoUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    protected Authentication extractAuthentication(HttpServletRequest request, HttpServletResponse response, Domain domain) throws AuthenticationException {
        // Idm 默认从 header 获取 USER_NAME
        String usernameFiled = domain.getLoginNameField();
        if (StringUtils.isEmpty(usernameFiled)) {
            usernameFiled = "USER_NAME";
        }
        String username = request.getHeader(usernameFiled);
        if (StringUtils.isEmpty(username)) {
            LOGGER.warn("sso idm username not found from request header");
            throw new SsoUserNotFoundException();
        }

        return new SsoAuthenticationToken(username, UNKNOWN);
    }
}
