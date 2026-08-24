package org.hzero.oauth.security.util;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.util.RedirectUrlBuilder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.base.BaseHeaders;
import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.constant.SecurityAttributes;

/**
 * 从 Request 中获取请求的默认值
 *
 * @author bojiangzhou 2019/05/24
 */
public class RequestUtil {


    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static String getParameterValueFromRequestOrSavedRequest(String parameterName, String defaultValue) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return getParameterValueFromRequestOrSavedRequest(request, parameterName, defaultValue);
    }

    /**
     * 获取请求中的参数，默认从 request 中获取，获取不到从 session 中保存的 request 中获取
     *
     * @param request       HttpServletRequest
     * @param parameterName 参数名称
     * @param defaultValue  默认值
     * @return 参数值
     */
    public static String getParameterValueFromRequestOrSavedRequest(HttpServletRequest request, String parameterName, String defaultValue) {
        String parameterValue = request.getParameter(parameterName);
        if (StringUtils.isNotBlank(parameterValue)) {
            return parameterValue;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return defaultValue;
        }
        DefaultSavedRequest saveRequest = (DefaultSavedRequest) session.getAttribute(SecurityAttributes.SECURITY_SAVED_REQUEST);
        if (saveRequest != null) {
            String[] values = saveRequest.getParameterValues(parameterName);
            if (values != null) {
                parameterValue = StringUtils.defaultIfBlank(values[0], defaultValue);
            }
        }
        parameterValue = StringUtils.defaultIfBlank(parameterValue, defaultValue);
        return parameterValue;
    }


    /**
     * 获取请求中的参数，默认从 request 中获取，获取不到从 session 中获取
     *
     * @param request       HttpServletRequest
     * @param parameterName 参数名称
     * @param defaultValue  默认值
     * @return 参数值
     */
    public static String getParameterValueFromRequestOrSession(HttpServletRequest request, String parameterName, String defaultValue) {
        String parameterValue = request.getParameter(parameterName);
        if (StringUtils.isNotBlank(parameterValue)) {
            return parameterValue;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return defaultValue;
        }
        if (session.getAttribute(parameterName) != null) {
            parameterValue = (String) session.getAttribute(parameterName);
        }
        parameterValue = StringUtils.defaultIfBlank(parameterValue, defaultValue);
        return parameterValue;
    }

    private static SecurityProperties properties;
    private static PortMapper portMapper;

    /**
     * 动态计算出请求的 baseUrl
     *
     * @param request HttpServletRequest
     * @return oauth baseUrl
     */
    public static String getBaseURL(HttpServletRequest request) {
        initResource();

        if (StringUtils.isNotBlank(properties.getBaseUrl())) {
            return properties.getBaseUrl();
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

        if (StringUtils.isNotBlank(properties.getBaseUrl())) {
            return getFullUrlInBaseUrl(adaptor, properties.getBaseUrl());
        }

        RedirectUrlBuilder urlBuilder = getBaseUrlBuilder(adaptor);
        urlBuilder.setServletPath(request.getServletPath());
        urlBuilder.setPathInfo(adaptor.getPathInfo());
        urlBuilder.setQuery(adaptor.getQueryString());

        return urlBuilder.getUrl();
    }

    /**
     * 获取完整的请求地址
     *
     * @param request HttpServletRequest
     * @return fullURL
     */
    public static String getFullURL(DefaultSavedRequest request) {
        initResource();

        RequestAdaptor adaptor = new DefaultSavedRequestAdaptor(request);

        if (StringUtils.isNotBlank(properties.getBaseUrl())) {
            return getFullUrlInBaseUrl(adaptor, properties.getBaseUrl());
        }

        RedirectUrlBuilder urlBuilder = getBaseUrlBuilder(adaptor);
        urlBuilder.setServletPath(request.getServletPath());
        urlBuilder.setPathInfo(adaptor.getPathInfo());
        urlBuilder.setQuery(adaptor.getQueryString());

        return urlBuilder.getUrl();
    }

    /**
     * @return 是否启用了 https
     */
    public static boolean isEnableHttps() {
        return properties.isEnableHttps();
    }

    private static void initResource() {
        if (properties == null || portMapper == null) {
            synchronized (RequestUtil.class) {
                properties = ApplicationContextHelper.getContext().getBean(SecurityProperties.class);
                portMapper = ApplicationContextHelper.getContext().getBean(PortMapper.class);
            }
        }
    }

    private static String getFullUrlInBaseUrl(RequestAdaptor request, String baseUrl) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (request.getServletPath() != null) {
            sb.append(request.getServletPath());
        }

        if (request.getPathInfo() != null) {
            sb.append(request.getPathInfo());
        }

        if (request.getQueryString() != null) {
            sb.append("?").append(request.getQueryString());
        }
        return sb.toString();
    }

    private static RedirectUrlBuilder getBaseUrlBuilder(RequestAdaptor request) {
        RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();
        int serverPort = request.getServerPort();

        if (properties.isEnableHttps()) {
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

    static class DefaultSavedRequestAdaptor implements RequestAdaptor {

        private final DefaultSavedRequest request;

        public DefaultSavedRequestAdaptor(DefaultSavedRequest request) {
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
            List<String> values = request.getHeaderValues(name);
            return values != null && values.size() > 0 ? values.get(0) : null;
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
