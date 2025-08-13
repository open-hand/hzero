package org.hzero.iam.app.service;

/**
 * 服务Swagger信息描述文档服务
 *
 * @author bojiangzhou 2018/12/12
 */
public interface IDocumentService {

    String NULL_VERSION = "null_version";
    String METADATA_VERSION = "VERSION";

    /**
     * 手动刷新API权限
     *
     * @param serviceName     服务名称
     * @param metaVersion     标记版本
     * @param cleanPermission 是否清楚过期权限
     */
    void refreshPermission(String serviceName, String metaVersion, Boolean cleanPermission);

    /**
     * 异步刷新API权限，并确保刷新成功
     *
     * @param serviceName     服务名称
     * @param metaVersion     标记版本
     * @param cleanPermission 是否清楚过期权限
     */
    void refreshPermissionAsync(String serviceName, String metaVersion, Boolean cleanPermission);

}
