package org.hzero.core.request.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.servlet.HandlerMapping;

/**
 * 获取当前请求最佳匹配路径
 *
 * @author qingsheng.chen@hand-china.com
 */
public class BestMatchHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private static ThreadLocal<String> bestMatchPattern = new ThreadLocal<>();

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request 当前请求
     * @throws IllegalArgumentException if the request is null
     */
    public BestMatchHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public void setAttribute(String name, Object o) {
        super.setAttribute(name, o);
        if (HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE.equals(name)) {
            bestMatchPattern.set(String.valueOf(o));
        }
    }

    public static void remove() {
        bestMatchPattern.remove();
    }

    public static String getBestMatchPattern() {
        String bestMatchPattern = BestMatchHttpServletRequestWrapper.bestMatchPattern.get();
        remove();
        return bestMatchPattern;
    }
}
