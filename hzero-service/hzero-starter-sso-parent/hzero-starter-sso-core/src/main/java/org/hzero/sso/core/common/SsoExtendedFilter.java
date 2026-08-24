package org.hzero.sso.core.common;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * SSO 自定义扩展的过滤器
 *
 * @author bojiangzhou 2020/08/30
 */
public interface SsoExtendedFilter {

    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;

    /**
     * @return 是否能处理此请求
     */
    boolean requiresFilter(HttpServletRequest request);

}
