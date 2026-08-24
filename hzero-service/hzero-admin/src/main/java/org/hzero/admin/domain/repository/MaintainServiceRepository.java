package org.hzero.admin.domain.repository;

import java.util.Properties;

/**
 * @author XCXCXCXCX
 * @date 2020/6/5 11:40 上午
 */
public interface MaintainServiceRepository {

    /**
     * 保存运维服务的配置
     * @param serviceName
     * @param properties
     */
    void put(String serviceName, Properties properties);

    /**
     * 移除运维服务的配置
     * @param serviceName
     */
    void remove(String serviceName);

    /**
     * 获取运维服务的配置
     * @param serviceName
     * @return
     */
    Properties getConfig(String serviceName);

}
