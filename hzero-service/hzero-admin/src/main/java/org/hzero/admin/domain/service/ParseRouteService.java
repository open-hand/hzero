package org.hzero.admin.domain.service;

import org.hzero.register.event.event.InstanceAddedEvent;

import java.util.Map;

/**
 * 解析路由
 *
 * @author bojiangzhou 2019/01/04
 */
public interface ParseRouteService {

    void init();

    /**
     * 解析swagger的文档树
     *
     * @param payload 接受的消息
     */
    void parser(InstanceAddedEvent payload);

    void parser(String serviceName, String version);

}
