package org.hzero.sso.auth;

import static org.hzero.sso.auth.AuthAttributes.HEADER_AUTHORIZATION;
import static org.hzero.sso.auth.AuthAttributes.HEADER_BEARER;
import static org.hzero.sso.core.constant.OAuthParameters.*;
import static org.hzero.sso.core.constant.SsoAttributes.formEncode;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.UUIDUtils;
import org.hzero.sso.core.common.SsoAuthenticationProviderAdapter;
import org.hzero.sso.core.constant.SsoAttributes;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.exception.SsoServerException;
import org.hzero.sso.core.exception.SsoUserNotFoundException;
import org.hzero.sso.core.security.SsoAuthenticationToken;
import org.hzero.sso.core.service.SsoUserDetailsService;

/**
 * @author bojiangzhou 2020/08/19
 */
public class AuthAuthenticationProvider extends SsoAuthenticationProviderAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAuthenticationProvider.class);

    private static final String EXPRESSION_START_WITH = "#{";
    private static final String EXPRESSION_END_WITH = "}";

    /**
     * 表达式解析对象
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private final RestTemplate restTemplate = new RestTemplate();

    public AuthAuthenticationProvider(SsoUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    @Override
    protected Authentication extractAuthentication(final HttpServletRequest request, final HttpServletResponse response, final Domain domain) throws AuthenticationException {
        String accessToken = exchangeForToken(request, domain);

        if (StringUtils.isBlank(accessToken)) {
            throw new SsoServerException("Exchange for token failure");
        }

        String username = exchangeForUsername(request, domain, accessToken);

        if (StringUtils.isBlank(username)) {
            LOGGER.warn("auth exchange for username from sso server failure");
            throw new SsoUserNotFoundException();
        }

        return new SsoAuthenticationToken(username, SsoAttributes.UNKNOWN);
    }

    /**
     * 向认证服务器请求令牌
     */
    protected String exchangeForToken(final HttpServletRequest request, final Domain domain) {
        // 授权码
        String code = request.getParameter(CODE);
        if (StringUtils.isBlank(code)) {
            LOGGER.error("authorization code not found in request parameter");
            throw new SsoServerException("authorization code not found");
        }

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add(GRANT_TYPE, "authorization_code");
        parameters.add(CLIENT_ID, domain.getSsoClientId());
        parameters.add(CLIENT_SECRET, domain.getSsoClientPwd());
        parameters.add(REDIRECT_URI, domain.getClientHostUrl());
        parameters.add(DEVICE_ID, UUIDUtils.generateUUID());
        parameters.add(CODE, code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Object> formEntity = new HttpEntity<>(parameters, headers);

        // 获取 token 的地址
        String tokenUrl = domain.getSsoServerUrl() + "/oauth/token";

        Map<String, Object> map = exchange(tokenUrl, HttpMethod.POST, formEntity);

        return (String) map.get(ACCESS_TOKEN);
    }

    /**
     * 向认证服务器获取登录用户信息
     */
    protected String exchangeForUsername(final HttpServletRequest request, Domain domain, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, HEADER_BEARER + accessToken);

        HttpEntity<Object> formEntity = new HttpEntity<>(null, headers);

        Map<String, Object> userInfo = exchange(domain.getSsoUserInfo(), HttpMethod.GET, formEntity);

        String username = null;
        String usernameField = domain.getLoginNameField();
        if (StringUtils.isNotBlank(usernameField)) {
            String usernameExpression = usernameField;
            if (!usernameExpression.startsWith(EXPRESSION_START_WITH)) {
                usernameExpression = EXPRESSION_START_WITH + usernameExpression;
            }
            if (!usernameExpression.endsWith(EXPRESSION_END_WITH)) {
                usernameExpression = usernameExpression + EXPRESSION_END_WITH;
            }

            StandardEvaluationContext context = new StandardEvaluationContext(userInfo);
            context.addPropertyAccessor(new MapAccessor());
            try {
                username = String.valueOf(PARSER.parseExpression(usernameExpression, new TemplateParserContext()).getValue(context, Object.class));
            } catch (ParseException e) {
                LOGGER.debug("parse username by expression {} from {} failure.", usernameExpression, userInfo);
            }
        }

        if (StringUtils.isBlank(username)) {
            if (userInfo.containsKey("username")) {
                username = String.valueOf(userInfo.get("username"));
            } else {
                for (String key : userInfo.keySet()) {
                    if (key.contains("name")) {
                        username = String.valueOf(userInfo.get(key));
                        break;
                    }
                }
            }
        }

        return username;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> exchange(String url, HttpMethod httpMethod, HttpEntity<Object> httpEntity) {
        LOGGER.debug("sso auth exchange for: [{}] - [{}]", httpMethod.name(), url);
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, httpMethod, httpEntity, Map.class);
            LOGGER.debug("auth exchange result: {}", responseEntity.getBody());
            return responseEntity.getBody();
        } catch (RestClientException e) {
            LOGGER.warn("auth sso request error", e);
            throw new SsoServerException(e);
        }
    }

    private String buildTokenUrl(final HttpServletRequest request, final Domain domain, String code) {
        StringBuilder url = new StringBuilder(domain.getSsoServerUrl()).append("/oauth/token?")
                .append(GRANT_TYPE).append("=").append(formEncode("authorization_code"))
                .append("&").append(CLIENT_ID).append("=").append(formEncode(domain.getSsoClientId()))
                .append("&").append(CLIENT_SECRET).append("=").append(formEncode(domain.getSsoClientPwd()))
                .append("&").append(REDIRECT_URI).append("=").append(formEncode(domain.getClientHostUrl()))
                .append("&").append(CODE).append("=").append(formEncode(code));

        return url.toString();
    }

}
