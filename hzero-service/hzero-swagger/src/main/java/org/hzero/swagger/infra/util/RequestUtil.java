package org.hzero.swagger.infra.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.base.BaseHeaders;
import org.hzero.swagger.config.SwaggerProperties;

/**
 *
 * @author bojiangzhou 2019/05/24
 */
public class RequestUtil {

    public static final String PROTOCOL_HTTP = "http://";
    public static final String PROTOCOL_HTTPS = "https://";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    private static SwaggerProperties properties;

    public static String getRequestDomain() {
        if (properties == null) {
            properties = ApplicationContextHelper.getContext().getBean(SwaggerProperties.class);
        }

        if (StringUtils.isNotBlank(properties.getBaseUrl())) {
            return properties.getBaseUrl();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String rootPath = request.getHeader(BaseHeaders.H_ROOT_PATH);
        if (org.springframework.util.StringUtils.isEmpty(rootPath) || "/".equals(rootPath)) {
            rootPath = "";
        } else if (!rootPath.startsWith("/")) {
            rootPath = "/" + rootPath;
        }

        String domain = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        if (properties.isEnableHttps()) {
            domain = domain.replace("http://", "https://");
        }

        domain = domain + rootPath;

        return domain;
    }

    public static String getProtocol() {
        if (properties == null) {
            properties = ApplicationContextHelper.getContext().getBean(SwaggerProperties.class);
        }

        String baseUrl = properties.getBaseUrl();
        if (StringUtils.isNotBlank(baseUrl)) {
            if (baseUrl.startsWith(PROTOCOL_HTTP)) {
                return HTTP;
            } else if (baseUrl.startsWith(PROTOCOL_HTTPS)) {
                return HTTPS;
            }
        }

        if (properties.isEnableHttps()) {
            return HTTPS;
        }
        return HTTP;
    }

}
