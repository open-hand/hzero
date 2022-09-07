package org.hzero.core.jackson.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.core.jackson.sensitive.SensitiveHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import java.io.IOException;

/**
 * <p>
 * Jackson 序列化 ObjectMapper Bean
 * </p>
 *
 * @author qingsheng.chen 2018/8/27 星期一 9:22
 */
@Configuration
public class ObjectMapperConfiguration {

    @Bean
    public ObjectMapperPostProcess objectMapperPostProcess() {
        return new ObjectMapperPostProcess();
    }

    @Bean
    @ConditionalOnBean(ObjectMapperPostProcess.class)
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnBean(ObjectMapperPostProcess.class)
    public SensitiveAutoCloseFilter sensitiveAutoCloseFilter() {
        return new SensitiveAutoCloseFilter();
    }

    public static class SensitiveAutoCloseFilter implements Filter {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            // Do nothing
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            try {
                chain.doFilter(request, response);
            } finally {
                SensitiveHelper.remove();
            }
        }

        @Override
        public void destroy() {
            // Do nothing
        }
    }
}
