package org.hzero.sso.cas;

import static org.hzero.sso.cas.CasAttributes.KEY_CAS_TICKET_TOKEN;
import static org.hzero.sso.cas.CasAttributes.KEY_CAS_TOKEN_TICKET;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.TokenUtils;
import org.hzero.sso.core.common.SsoClientLogoutHandler;

/**
 *
 * @author bojiangzhou 2020/08/25
 */
public class CasClientLogoutHandler implements SsoClientLogoutHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasClientLogoutHandler.class);

    private final RedisHelper redisHelper;

    public CasClientLogoutHandler(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = TokenUtils.getToken(request);

        String ticket = redisHelper.strGet(KEY_CAS_TOKEN_TICKET + accessToken);
        if (ticket != null) {
            redisHelper.delKey(KEY_CAS_TICKET_TOKEN + ticket);
            redisHelper.delKey(KEY_CAS_TOKEN_TICKET + accessToken);
            LOGGER.debug("logout clear cas info: ticket: [{}], access_token: [{}]", ticket, accessToken);
        }
    }
}
