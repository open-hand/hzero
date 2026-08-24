package org.hzero.starter.social.core.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.util.RedirectUrlBuilder;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.base.BaseHeaders;
import org.hzero.starter.social.core.configuration.SocialPropertyService;

/**
 * 从 Request 中获取请求信息
 *
 * @author bojiangzhou 2020/11/05
 */
public class SocialRequestUtil {

    private static SocialPropertyService propertyService;
    private static PortMapper portMapper;

    /**
     * 动态计算出请求的 baseUrl
     *
     * @param request HttpServletRequest
     * @return oauth baseUrl
     */
    public static String getBaseURL(HttpServletRequest request) {
        initResource();

        if (StringUtils.isNotBlank(propertyService.getBaseUrl())) {
            return propertyService.getBaseUrl();
        }

        RequestAdaptor adaptor = new ServletRequestAdaptor(request);
        RedirectUrlBuilder urlBuilder = getBaseUrlBuilder(adaptor);

        return urlBuilder.getUrl();
    }

    /**
     * 获取完整的请求地址
     *
     * @param request HttpServletRequest
     * @return fullURL
     */
    public static String getFullURL(HttpServletRequest request) {
        initResource();

        RequestAdaptor adaptor = new ServletRequestAdaptor(request);

        if (StringUtils.isNotBlank(propertyService.getBaseUrl())) {
            StringBuilder sb = new StringBuilder(propertyService.getBaseUrl());
            if (request.getServletPath() != null) {
                sb.append(request.getServletPath());
            }

            if (request.getPathInfo() != null) {
                sb.append(request.getPathInfo());
            }
            return sb.toString();
        }

        RedirectUrlBuilder urlBuilder = getBaseUrlBuilder(adaptor);
        urlBuilder.setServletPath(request.getServletPath());

        return urlBuilder.getUrl();
    }

    private static void initResource() {
        if (propertyService == null || portMapper == null) {
            synchronized (SocialRequestUtil.class) {
                propertyService = ApplicationContextHelper.getContext().getBean(SocialPropertyService.class);
                portMapper = ApplicationContextHelper.getContext().getBean(PortMapper.class);
            }
        }
    }

    private static RedirectUrlBuilder getBaseUrlBuilder(RequestAdaptor request) {
        RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
        int serverPort = request.getServerPort();

        if (propertyService.isEnableHttps()) {
            urlBuilder.setScheme("https");
            Integer portLookup = portMapper.lookupHttpsPort(serverPort);
            if (portLookup != null) {
                urlBuilder.setPort(portLookup);
            } else {
                urlBuilder.setPort(serverPort);
            }
        } else {
            urlBuilder.setScheme("http");
            urlBuilder.setPort(serverPort);
        }

        // if nginx config root path
        String rootPath = request.getHeader(BaseHeaders.H_ROOT_PATH);
        if (StringUtils.isBlank(rootPath) || "/".equals(rootPath)) {
            rootPath = "";
        } else if (!rootPath.startsWith("/")) {
            rootPath = "/" + rootPath;
        }
        urlBuilder.setServerName(request.getServerName());

        String contextPath = request.getContextPath();
        if (StringUtils.isNotBlank(rootPath) && !request.getServerName().endsWith(rootPath)) {
            contextPath = rootPath + contextPath;
        }
        urlBuilder.setContextPath(contextPath);

        return urlBuilder;
    }

    interface RequestAdaptor {
        String getScheme();

        int getServerPort();

        String getHeader(String name);

        String getServerName();

        String getContextPath();

        String getServletPath();

        String getPathInfo();

        String getQueryString();
    }

    static class ServletRequestAdaptor implements RequestAdaptor {

        private final HttpServletRequest request;

        public ServletRequestAdaptor(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public String getScheme() {
            return request.getScheme();
        }

        @Override
        public int getServerPort() {
            return request.getServerPort();
        }

        @Override
        public String getHeader(String name) {
            return request.getHeader(name);
        }

        @Override
        public String getServerName() {
            return request.getServerName();
        }

        @Override
        public String getContextPath() {
            return request.getContextPath();
        }

        @Override
        public String getServletPath() {
            return request.getServletPath();
        }

        @Override
        public String getPathInfo() {
            return request.getPathInfo();
        }

        @Override
        public String getQueryString() {
            return request.getQueryString();
        }
    }

}
