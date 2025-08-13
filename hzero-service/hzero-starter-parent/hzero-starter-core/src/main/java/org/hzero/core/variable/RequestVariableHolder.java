package org.hzero.core.variable;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

import org.hzero.core.base.TokenConstants;

/**
 * Request 常量等
 *
 * @author bojiangzhou 2019/08/23
 */
public class RequestVariableHolder {

    private RequestVariableHolder() {
    }

    public static final String HEADER_LABEL = "X-Eureka-Label";
    public static final String HEADER_JWT = TokenConstants.HEADER_JWT;
    public static final String HEADER_BEARER = TokenConstants.HEADER_BEARER;
    public static final String HEADER_AUTH = TokenConstants.HEADER_AUTH;
    public static final String ACCESS_TOKEN = TokenConstants.ACCESS_TOKEN;

    public static final HystrixRequestVariableDefault<String> LABEL = new HystrixRequestVariableDefault<>();
    public static final HystrixRequestVariableDefault<Long> TENANT_ID = new HystrixRequestVariableDefault<>();
    public static final HystrixRequestVariableDefault<Long> USER_ID = new HystrixRequestVariableDefault<>();

}
