package org.hzero.admin.domain.service;

import org.hzero.admin.domain.vo.ConfigParam;

import java.util.List;
import java.util.Map;

/**
 * 通知服务刷新配置
 *
 * @author bojiangzhou 2018/12/19
 */
public interface ConfigRefreshService {

    /**
     * 通知服务刷新配置
     */
    @Deprecated
    void notifyServiceRefresh(List<ConfigParam> params);

    /**
     * 通知服务刷新配置
     */
    void notifyServiceRefresh(ConfigParam param);

    /**
     * 通知网关刷新路由
     */
    void notifyGatewayRefresh();

    /**
     * 根据tag刷新指定网关
     * @param tags
     */
    void notifyGatewayRefresh(Map<String, String> tags);

    /**
     * 通知网关刷新路由
     */
    @Deprecated
    void notifyGatewayRefresh(List<ConfigParam> params);

}
