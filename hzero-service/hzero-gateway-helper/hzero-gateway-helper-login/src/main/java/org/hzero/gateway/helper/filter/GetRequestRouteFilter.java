package org.hzero.gateway.helper.filter;

import org.hzero.gateway.helper.api.HelperFilter;
import org.hzero.gateway.helper.config.GatewayPropertiesWrapper;
import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.CommonRoute;
import org.hzero.gateway.helper.entity.RequestContext;
import org.hzero.gateway.helper.util.ServerRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 根据请求前缀获取对应的zuul路由
 * 如果为/zuul/开头的文件上传请求，则去除之后再获取
 */
@Component
public class GetRequestRouteFilter implements HelperFilter {
    private static final Logger logger = LoggerFactory.getLogger(GetRequestRouteFilter.class);

    private static final String ZUUL_SERVLET_PATH = "zuul/";

    public static final String REQUEST_KEY_SEPARATOR = ":::";

    private static final String URI_SQL_DATA = "/lovs/sql/data";
    private static final String URI_SQL_MEANING = "/lovs/sql/meaning";

    private final AntPathMatcher matcher = new AntPathMatcher();

    private GatewayPropertiesWrapper properties;

    public GetRequestRouteFilter(GatewayPropertiesWrapper properties) {
        this.properties = properties;
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        //如果是文件上传的url，以/zuul/开否，则去除了/zuul再进行校验权限
        String requestUri = context.request.uri;
        if (requestUri.startsWith("/" + ZUUL_SERVLET_PATH)) {
            requestUri = requestUri.substring(5);

        }
        //根据请求uri获取zuulRoute
        CommonRoute route = getRoute(requestUri, properties.getRoutes());
        if (route == null) {
            context.response.setStatus(CheckState.PERMISSION_SERVICE_ROUTE);
            try {
                context.response.setMessage("This request mismatch any routes, uri: " + URLEncoder.encode(requestUri, StandardCharsets.UTF_8.displayName()));
            } catch (UnsupportedEncodingException e) {
                logger.error("Error encode uri.", e);
                context.response.setMessage("This request mismatch any routes.");
            }
            return false;
        } else {
            final String trueUri = getRequestTruePath(requestUri, route.getPath());
            if (trueUri.endsWith(URI_SQL_DATA) || trueUri.endsWith(URI_SQL_MEANING)) {
                Object servletRequest = context.getServletRequest();
                String lovCode = null;
                if(servletRequest instanceof HttpServletRequest){
                    lovCode = ((HttpServletRequest)servletRequest).getParameter("lovCode");
                }else if (servletRequest instanceof ServerHttpRequest){
                    lovCode = ServerRequestUtils.resolveParam((ServerHttpRequest)servletRequest, "lovCode");
                }
                context.setLovCode(lovCode);
            }
            context.setTrueUri(trueUri);
            context.setRoute(route);
            context.setRequestKey(generateKey(trueUri, context.request.method, route.getServiceId()));
            return true;
        }
    }

    private String generateKey(String uri, String method, String service) {
        return uri + REQUEST_KEY_SEPARATOR + method + REQUEST_KEY_SEPARATOR + service;
    }


    private String getRequestTruePath(String uri, String routePath) {
        return "/" + matcher.extractPathWithinPattern(routePath, uri);
    }

    private CommonRoute getRoute(final String requestUri,
                                 final Map<String, CommonRoute> routeMap) {
        for (CommonRoute zuulRoute : routeMap.values()) {
            if (matcher.match(zuulRoute.getPath(), requestUri)) {
                return zuulRoute;
            }
        }
        return null;
    }
}
