
package org.hzero.oauth.domain.service;

import java.io.IOException;
import javax.servlet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 清理资源的过滤器，用于在请求结束后清理一些环境中的资源，如 ThreadLocal
 *
 * @author bojiangzhou 2020/05/25
 */
public class ClearResourceFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClearResourceFilter.class);

    private final ClearResourceService clearResourceService;

    public ClearResourceFilter(ClearResourceService clearResourceService) {
        this.clearResourceService = clearResourceService;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Throwable e) {
            LOGGER.error("clear resource filters error", e);
        } finally {
            try {
                clearResourceService.cleaningResource();
            } catch (Throwable e) {
                LOGGER.error("clear resource error", e);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
