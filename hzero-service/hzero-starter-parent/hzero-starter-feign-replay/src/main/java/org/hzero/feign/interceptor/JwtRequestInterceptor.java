package org.hzero.feign.interceptor;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.properties.CoreProperties;
import org.hzero.core.variable.RequestVariableHolder;


/**
 * 拦截feign请求，为requestTemplate加上oauth token请求头
 *
 * @author bojiangzhou 2019/08/22
 */
public class JwtRequestInterceptor implements FeignRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestInterceptor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String OAUTH_TOKEN_PREFIX = "Bearer ";
    private Signer signer;

    private CoreProperties coreProperties;

    public JwtRequestInterceptor(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }

    @PostConstruct
    private void init() {
        signer = new MacSigner(coreProperties.getOauthJwtKey());
    }

    @Override
    public int getOrder() {
        return -1000;
    }

    @Override
    public void apply(RequestTemplate template) {
        String token = null;
        try {
            if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                    OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                    if (StringUtils.isNoneBlank(details.getTokenType(), details.getTokenValue())) {
                        token = details.getTokenType() + " " + details.getTokenValue();
                    } else if (details.getDecodedDetails() instanceof CustomUserDetails) {
                        token = OAUTH_TOKEN_PREFIX + JwtHelper.encode(OBJECT_MAPPER.writeValueAsString(details.getDecodedDetails()), signer).getEncoded();
                    }
                } else if (authentication.getPrincipal() instanceof CustomUserDetails) {
                    token = OAUTH_TOKEN_PREFIX + JwtHelper.encode(OBJECT_MAPPER.writeValueAsString(authentication.getPrincipal()), signer).getEncoded();
                }
            }

            if (token == null) {
                LOGGER.debug("Feign request set Header Jwt_Token, no member token found, use AnonymousUser default.");
                token = OAUTH_TOKEN_PREFIX + JwtHelper.encode(OBJECT_MAPPER.writeValueAsString(DetailsHelper.getAnonymousDetails()), signer).getEncoded();
            }
        } catch(Exception e){
            LOGGER.error("generate jwt token failed {}", e.getMessage());
        }

        template.header(RequestVariableHolder.HEADER_JWT, token);
        setLabel(template);
    }

    private void setLabel(RequestTemplate template) {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            String label = RequestVariableHolder.LABEL.get();
            if (label != null) {
                template.header(RequestVariableHolder.HEADER_LABEL, label);
            }
        }

    }
}
