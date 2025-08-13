package org.hzero.iam.domain.service.user.interceptor.interceptors;

import org.springframework.stereotype.Component;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;

/**
 * 最终的处理器
 *
 * @author bojiangzhou 2020/05/28
 */
@Component
public class LastHandlerInterceptor implements HandlerInterceptor<User> {


    @Override
    public void interceptor(User user) {
        user.clearPassword();
    }
}
