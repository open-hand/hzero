package org.hzero.core.base;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.core.properties.CoreProperties;

/**
 * Token 相关常量
 *
 * @author bojiangzhou 2020/06/24
 */
public final class TokenConstants {

    public static final String HEADER_JWT = "Jwt_Token";
    public static final String HEADER_BEARER = "Bearer";
    public static final String HEADER_AUTH = getAuthHeaderName();
    public static final String ACCESS_TOKEN = "access_token";
    public static final String JWT_TOKEN = "jwt_token";

    private static CoreProperties coreProperties;

    /**
     * @return 认证令牌请求头名称
     */
    public static String getAuthHeaderName() {
        if (coreProperties == null) {
            synchronized (BaseHeaders.class) {
                coreProperties = ApplicationContextHelper.getContext().getBean(CoreProperties.class);
            }
        }
        return coreProperties.getResource().getAuthHeaderName();
    }

}
