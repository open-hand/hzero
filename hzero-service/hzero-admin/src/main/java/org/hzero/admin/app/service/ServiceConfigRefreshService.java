package org.hzero.admin.app.service;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/2/21 4:19 下午
 */
public interface ServiceConfigRefreshService {
    void refreshApplicationContext(String serviceName, String version);
}
