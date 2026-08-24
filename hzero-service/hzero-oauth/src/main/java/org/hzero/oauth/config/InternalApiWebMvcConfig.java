package org.hzero.oauth.config;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.hzero.oauth.security.config.SecurityProperties;
import org.hzero.oauth.security.filter.InternalPathInterceptor;

/**
 * Web MVC 配置
 * 用于注册拦截器
 *
 * @author codebuddy
 */
@Configuration
public class InternalApiWebMvcConfig implements WebMvcConfigurer {

    /**
     * 需要拦截的内部接口路径
     */
    private static final List<String> DEFAULT_INTERNAL_PATHS = Arrays.asList(
            "/admin/**",
            "/api/**",
            "/v2/market/**",
            "/v1/**",
            "/v2/choerodon/api-docs"
    );
    private static final List<String> EXCLUDE_INTERNAL_PATHS = Arrays.asList(
            "/api/user",
            "/api/user/"
    );
    private final Set<String> internalPaths;


    /**
     * JWT Token 校验拦截器
     */
    private final InternalPathInterceptor internalPathInterceptor;

    public InternalApiWebMvcConfig(SecurityProperties securityProperties, InternalPathInterceptor internalPathInterceptor) {
        this.internalPaths = new HashSet<>();
        internalPaths.addAll(DEFAULT_INTERNAL_PATHS);
        if (CollectionUtils.isNotEmpty(securityProperties.getInternalPath())) {
            internalPaths.addAll(securityProperties.getInternalPath());
        }
        this.internalPathInterceptor = internalPathInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        registry.addInterceptor(internalPathInterceptor)
                .addPathPatterns(new ArrayList<>(internalPaths))
                .excludePathPatterns(EXCLUDE_INTERNAL_PATHS);
    }
}
