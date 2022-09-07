package org.hzero.core.hystrix;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

public class RequestAttributeCallableWrapper implements HystrixCallableWrapper {

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new RequestAttributeCallable<>(callable, RequestContextHolder.getRequestAttributes(), SecurityContextHolder.getContext() != null ? SecurityContextHolder.getContext().getAuthentication() : null);
    }

    public static class RequestAttributeCallable<T> extends AbstractCallable<T> {
        private final RequestAttributes requestAttributes;
        private final Authentication authentication;

        public RequestAttributeCallable(Callable<T> target, RequestAttributes requestAttributes, Authentication authentication) {
            super(target);
            this.requestAttributes = requestAttributes;
            this.authentication = authentication;
        }

        @Override
        public T call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                if (SecurityContextHolder.getContext() != null && authentication != null && authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                return super.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }
}
