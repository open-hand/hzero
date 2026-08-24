package org.hzero.sso.core.common;

import java.util.Set;

import org.hzero.sso.core.standard.StandardServerLogoutHandler;

/**
 * 提供单点认证相关对象
 *
 * @author bojiangzhou 2020/08/15
 */
public interface SsoAuthenticationFactory {

    /**
     * @return 支持的SSO类型
     */
    Set<String> supportiveSsoType();

    /**
     * @return 认证服务路由器
     */
    SsoAuthenticationRouter getAuthenticationRouter();

    /**
     * @return 用户认证器
     */
    SsoAuthenticationProvider getAuthenticationProvider();

    /**
     * @return 单点类型判断器
     */
    default SsoServerLogoutHandler getLogoutHandler() {
        return new StandardServerLogoutHandler();
    }

    /**
     * @return 登录成功处理器(用户认证成功)
     */
    default SsoAuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return new SsoAuthenticationSuccessHandler();
    }

    /**
     * @return 认证失败处理器
     */
    default SsoAuthenticationFailureHandler getAuthenticationFailureHandler() {
        return new SsoAuthenticationFailureHandler();
    }

    /**
     * @return 自定义SSO过滤器
     */
    default SsoExtendedFilter getExtendedFilter() {
        return null;
    }

}
