package org.hzero.iam.domain.service.user.interceptor;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.hzero.core.interceptor.InterceptorChainBuilder;
import org.hzero.core.interceptor.InterceptorChainConfigurer;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.user.interceptor.interceptors.*;

/**
 *
 * @author bojiangzhou 2020/06/05
 */
@Order(0)
@Component
public class UserInterceptorChainConfigurer implements InterceptorChainConfigurer<User, InterceptorChainBuilder<User>> {

    @Override
    public void configure(InterceptorChainBuilder<User> builder) {
        builder
                .selectChain(UserOperation.CREATE_USER)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .async()
                .addInterceptor(CommonMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(SendMessageInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.UPDATE_USER)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .addInterceptor(CommonMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.REGISTER_USER)
                .post()
                .async()
                .addInterceptor(RegisterMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.CREATE_USER_INTERNAL)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .async()
                .addInterceptor(InternalMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.UPDATE_USER_INTERNAL)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.IMPORT_USER)
                .post()
                .addInterceptor(InternalMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);
    }
}
