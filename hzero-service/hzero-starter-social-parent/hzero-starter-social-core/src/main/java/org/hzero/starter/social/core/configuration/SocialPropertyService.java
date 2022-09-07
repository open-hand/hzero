package org.hzero.starter.social.core.configuration;

import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;

/**
 * SSO属性配置
 *
 * @author bojiangzhou 2020/08/24
 */
public interface SocialPropertyService {

    /**
     * 服务基础URL
     */
    default String getBaseUrl() {
        return null;
    }

    /**
     * @return 获取动态计算 baseUrl 的函数
     */
    default Function<HttpServletRequest, String> getDynamicBaseUrlFunction() {
        return null;
    }

    /**
     * 是否启用 https
     */
    default boolean isEnableHttps() {
        return false;
    }

    /**
     * 登录页面地址
     */
    default String getLoginPage() {
        return "/login";
    }

}

