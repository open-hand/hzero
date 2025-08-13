package org.hzero.iam.domain.service.user.interceptor;

import java.util.List;

import org.springframework.stereotype.Component;

import org.hzero.core.interceptor.AbstractInterceptorChainManager;
import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.core.interceptor.InterceptorChainBuilder;
import org.hzero.core.interceptor.InterceptorChainConfigurer;
import org.hzero.iam.domain.entity.User;

/**
 *
 * @author bojiangzhou 2020/06/05
 */
@Component
public class UserInterceptorChainManager extends AbstractInterceptorChainManager<User> {

    public UserInterceptorChainManager(List<HandlerInterceptor<User>> handlerInterceptors,
                                       List<InterceptorChainConfigurer<User, InterceptorChainBuilder<User>>> interceptorChainConfigurers) {
        super(handlerInterceptors, interceptorChainConfigurers);
    }
}
