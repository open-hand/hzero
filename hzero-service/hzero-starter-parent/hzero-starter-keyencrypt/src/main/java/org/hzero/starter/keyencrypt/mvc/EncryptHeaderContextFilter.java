package org.hzero.starter.keyencrypt.mvc;

import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author : peng.yu01@hand-china.com 2019/10/21 11:48
 */
public class EncryptHeaderContextFilter implements Filter, PriorityOrdered {

    private static final Logger logger = LoggerFactory.getLogger(EncryptHeaderContextFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            String encrypt = servletRequest.getHeader(EncryptContext.HEADER_ENCRYPT);
            if (EncryptContext.isAllowedEncrypt() && StringUtils.isNotEmpty(encrypt)) {
                EncryptContext.setEncryptType(encrypt);
            }
            chain.doFilter(request, response);
        } finally {
            EncryptContext.clear();
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 50;
    }
}
