package org.hzero.sso.saml.metadata;

import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.configuration.SsoPropertyService;

/**
 * @author bojiangzhou 2020/11/04
 */
public class CustomMetadataGenerator extends MetadataGenerator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SsoPropertyService propertyService;
    private final SsoProperties properties;

    public static final String DEFAULT_BASE_URL = "http://local.unknown:8020/oauth";

    public CustomMetadataGenerator(SsoPropertyService ssoPropertyService, SsoProperties properties) {
        this.propertyService = ssoPropertyService;
        this.properties = properties;
    }

    @Override
    public String getEntityBaseURL() {
        String baseUrl = null;
        if (DEFAULT_BASE_URL.equals(super.getEntityBaseURL())) {
            Function<HttpServletRequest, String> fun = propertyService.getDynamicBaseUrlFunction();
            if (fun != null) {
                HttpServletRequest request = getHttpServletRequest();
                if (request != null) {
                    baseUrl = fun.apply(request);
                }
            }
        } else {
            baseUrl = super.getEntityBaseURL();
        }

        if (StringUtils.isBlank(baseUrl)) {
            baseUrl = DEFAULT_BASE_URL;
            logger.warn("Saml baseUrl is null, so use default baseUrl: {}", baseUrl);
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }
        String ssoPath;
        if (properties.isEnableCompatibilityMode()) {
            ssoPath = "";
        } else {
            ssoPath = properties.getProcessUrl().replace("/**", "");
        }
        baseUrl = baseUrl + ssoPath;

        logger.debug("sso saml medata baseURL is [{}]", baseUrl);

        return baseUrl;
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
