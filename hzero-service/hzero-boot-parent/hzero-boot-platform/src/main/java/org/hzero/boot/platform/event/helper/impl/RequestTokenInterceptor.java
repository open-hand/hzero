package org.hzero.boot.platform.event.helper.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.platform.event.helper.EventRequestInterceptor;
import org.hzero.core.properties.CoreProperties;
import org.hzero.core.variable.RequestVariableHolder;


/**
 * 为RestTemplate加上oauth token请求头
 *
 * @author jiangzhou.bo@hand-china.com 2018/06/14 16:36
 */
public class RequestTokenInterceptor implements EventRequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTokenInterceptor.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String OAUTH_TOKEN_PREFIX = "Bearer ";
    private Signer signer;

    public RequestTokenInterceptor(CoreProperties properties) {
        signer = new MacSigner(properties.getOauthJwtKey());
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = request.getHeaders();
        if (SecurityContextHolder.getContext() != null
                && SecurityContextHolder.getContext().getAuthentication() != null) {
            OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getDetails();
            headers.add(RequestVariableHolder.HEADER_JWT, details.getTokenType() + " " + details.getTokenValue());
        } else {
            try {
                String jwtToken = OAUTH_TOKEN_PREFIX + JwtHelper
                        .encode(OBJECT_MAPPER.writeValueAsString(DetailsHelper.getAnonymousDetails()), signer).getEncoded();
                headers.add(RequestVariableHolder.HEADER_JWT, jwtToken);
            } catch (IOException e) {
                LOGGER.error("generate jwt token failed {}", e.getMessage());
            }
        }
        setLabel(headers);
        return execution.execute(request, body);
    }

    private void setLabel(HttpHeaders headers) {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            String label = RequestVariableHolder.LABEL.get();
            if (label != null) {
                headers.add(RequestVariableHolder.HEADER_LABEL, label);
            }
        }
    }
}
