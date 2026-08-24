package org.hzero.sso.cas;

import static org.hzero.sso.cas.CasAttributes.*;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

import org.hzero.core.redis.RedisHelper;
import org.hzero.sso.core.common.SsoAuthorizeSuccessHandler;

/**
 * @author bojiangzhou 2020/08/19
 */
public class CasAuthorizeSuccessHandler extends SsoAuthorizeSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasAuthorizeSuccessHandler.class);

    private final RedisHelper redisHelper;

    public CasAuthorizeSuccessHandler(RedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    protected Set<String> supportiveSsoType() {
        return CasAttributes.SSO_TYPE;
    }

    @Override
    public void onAuthorizeSuccessHandler(HttpServletRequest request, final AuthorizationRequest authorizationRequest, OAuth2AccessToken oAuth2AccessToken) {
        HttpSession session = request.getSession(false);
        String ticket;
        if (session != null && (ticket = (String) session.getAttribute(ATTRIBUTE_CAS_TICKET)) != null) {
            String accessToken = oAuth2AccessToken.getValue();

            // 记录 ticket_token，便于SSO请求退出时清理 token
            redisHelper.strSet(KEY_CAS_TICKET_TOKEN + ticket, accessToken, 24, TimeUnit.HOURS);
            redisHelper.strSet(KEY_CAS_TOKEN_TICKET + accessToken, ticket, 24, TimeUnit.HOURS);

            session.removeAttribute(ATTRIBUTE_CAS_TICKET);

            LOGGER.debug("cas login success, ticket: [{}], access_token: [{}]", ticket, accessToken);
        }
        super.onAuthorizeSuccessHandler(request, authorizationRequest, oAuth2AccessToken);
    }
}
