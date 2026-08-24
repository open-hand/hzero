package org.hzero.sso.core.configuration;

import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.hzero.sso.core.constant.SsoAttributes;

/**
 * SSO属性配置
 *
 * @author bojiangzhou 2020/08/24
 */
public interface SsoPropertyService {

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

    /**
     * SSO 入口地址
     */
    default String getProcessUrl() {
        return SsoAttributes.SSO_DEFAULT_PROCESS_URI;
    }

    /**
     * 可通过此参数禁止跳转到 sso 页面
     */
    default String getDisableSsoParameter() {
        return "disable_sso";
    }

}

