package org.hzero.file.infra.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.hzero.file.infra.util.DeleteFileContext;

/**
 * 文件处理过滤器
 *
 * @author berg-turing 2025/02/24
 */
@Component
public class DeleteFileFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteFileFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            String physicalDelete = servletRequest.getHeader(DeleteFileContext.HEADER_PHYSICAL_DELETE);
            if (physicalDelete != null) {
                LOGGER.debug("enabled origin flag");
                DeleteFileContext.setOrigin(Boolean.TRUE);
            }
            if (BooleanUtils.toBoolean(physicalDelete)) {
                LOGGER.debug("enabled physical delete file");
                // 启用物理删除
                DeleteFileContext.enablePhysical();
            }
            // 执行逻辑
            chain.doFilter(request, response);
        } finally {
            // 清理上下文
            DeleteFileContext.removeOrigin();
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
