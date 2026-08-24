package org.hzero.feign.interceptor;

import feign.RequestTemplate;


/**
 * Feign Request Interceptor Interface
 *
 * @author bojiangzhou 2019/08/22
 */
public interface FeignRequestInterceptor {

    /**
     * Called for every request. Add data using methods on the supplied {@link RequestTemplate}.
     */
    void apply(RequestTemplate template);

    /**
     * apply order
     */
    default int getOrder() {
        return 0;
    }

}
