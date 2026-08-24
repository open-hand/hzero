package org.hzero.core.request;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import org.hzero.core.request.wrapper.BestMatchHttpServletRequestWrapper;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class RequestMappingHelper {
    public static final String REFERRER = "referer";
    public static final String USER_AGENT = "user-agent";

    public static BestMatchRequestMapping getBestMatchRequestMapping(RequestMappingHandlerMapping requestMappingHandlerMapping) throws Exception {
        return getBestMatchRequestMapping(requestMappingHandlerMapping, RequestContextHolder.currentRequestAttributes());
    }

    public static BestMatchRequestMapping getBestMatchRequestMapping(RequestMappingHandlerMapping requestMappingHandlerMapping, RequestAttributes requestAttributes) throws Exception {
        if (requestAttributes instanceof ServletRequestAttributes) {
            String bestMatchPattern = BestMatchHttpServletRequestWrapper.getBestMatchPattern();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (StringUtils.isEmpty(bestMatchPattern)) {
                requestMappingHandlerMapping.getHandler(new BestMatchHttpServletRequestWrapper(request));
                bestMatchPattern = BestMatchHttpServletRequestWrapper.getBestMatchPattern();
            }
            if (StringUtils.hasText(bestMatchPattern)) {
                return new BestMatchRequestMapping().setMethod(request.getMethod().toLowerCase())
                                .setPath(bestMatchPattern).setIp(request.getLocalAddr())
                                .setReferrer(request.getHeader(REFERRER)).setUserAgent(request.getHeader(USER_AGENT));
            }
        }
        return null;
    }

    public static class BestMatchRequestMapping{
        private String method;
        private String path;
        private String ip;
        private String referrer;
        private String userAgent;


        public String getMethod() {
            return method;
        }

        public BestMatchRequestMapping setMethod(String method) {
            this.method = method;
            return this;
        }

        public String getPath() {
            return path;
        }

        public BestMatchRequestMapping setPath(String path) {
            this.path = path;
            return this;
        }

        public String getIp() {
            return ip;
        }

        public BestMatchRequestMapping setIp(String ip) {
            this.ip = ip;
            return this;
        }

        public String getReferrer() {
            return referrer;
        }

        public BestMatchRequestMapping setReferrer(String referrer) {
            this.referrer = referrer;
            return this;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public BestMatchRequestMapping setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
    }
}
