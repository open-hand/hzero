package org.hzero.core.net;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.base.TokenConstants;
import org.hzero.core.properties.CoreProperties;

/**
 * RestTemplate自动复制请求头信息
 *
 * @author gaokuo.dai@hand-china.com 2018年9月4日下午7:05:06
 */
public class RequestHeaderCopyInterceptor implements ClientHttpRequestInterceptor {

    private static final String CACHE_CONTROL_NO_CACHE = "no-cache";

    private static final Logger logger = LoggerFactory.getLogger(RequestHeaderCopyInterceptor.class);

    private static final String OAUTH_TOKEN_PREFIX = TokenConstants.HEADER_BEARER + " ";
    private final Set<String> ignoreHeader = new CopyOnWriteArraySet<>();

    @Override
    @Nonnull
    public ClientHttpResponse intercept(@Nonnull HttpRequest request, @Nonnull byte[] body,
                                        @Nonnull ClientHttpRequestExecution execution) throws IOException {
        try {
            ServletRequestAttributes originRequestAttribute = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (originRequestAttribute != null) {
                HttpServletRequest originRequest = originRequestAttribute.getRequest();
                Enumeration<String> originHeaderNames = originRequest.getHeaderNames();
                HttpHeaders aimHttpHeaders = request.getHeaders();
                if (originHeaderNames != null) {
                    while (originHeaderNames.hasMoreElements()) {
                        String key = originHeaderNames.nextElement();
                        if (ignoreHeader.contains(key) || aimHttpHeaders.containsKey(key)) {
                            continue;
                        }
                        aimHttpHeaders.add(key, originRequest.getHeader(key));
                    }
                }
                // 没有token的话，补充token，若没有登录信息，补充匿名用户
                if (!aimHttpHeaders.containsKey(TokenConstants.JWT_TOKEN)) {
                    String token = buildJwtWithUserDetail();
                    aimHttpHeaders.add(TokenConstants.JWT_TOKEN, token);
                }
                if (!aimHttpHeaders.containsKey(HttpHeaders.CACHE_CONTROL)){
                    aimHttpHeaders.add(HttpHeaders.CACHE_CONTROL, CACHE_CONTROL_NO_CACHE);
                }
            } else {
                // 判断当前线程是否有用户上下文，有的话，组装token
                String token = buildJwtWithUserDetail();
                request.getHeaders().add(TokenConstants.JWT_TOKEN, token);
            }
        } catch (Exception e) {
            logger.warn("can not copy header info automatic", e.getCause());
        }
        return execution.execute(request, body);
    }

    private String buildJwtWithUserDetail() throws JsonProcessingException {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            // 获取不到使用匿名用户
            customUserDetails = DetailsHelper.getAnonymousDetails();
        }
        ApplicationContext applicationContext = ApplicationContextHelper.getContext();
        ObjectMapper objectMapper = applicationContext.getBean(ObjectMapper.class);
        CoreProperties coreProperties = applicationContext.getBean(CoreProperties.class);
        Signer signer = new MacSigner(coreProperties.getOauthJwtKey());
        return OAUTH_TOKEN_PREFIX + JwtHelper.encode(objectMapper.writeValueAsString(customUserDetails), signer).getEncoded();
    }

    public void addIgnoreHeader(String key) {
        ignoreHeader.add(key);
    }
}
