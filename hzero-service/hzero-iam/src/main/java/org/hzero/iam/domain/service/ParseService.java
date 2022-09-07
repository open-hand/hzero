package org.hzero.iam.domain.service;

import org.springframework.cloud.client.ServiceInstance;

/**
 * @author XCXCXCXCX
 * @since 1.0
 */
public interface ParseService {

    /**
     * 解析服务文档
     *
     * <ul>
     * <h3>解析权限步骤：</h3>
     * <li>判断是否要跳过解析服务权限，默认跳过 <i>register, gateway, oauth</i></li>
     * <li>调用服务接口 <i>/v2/choerodon/api-docs</i> 获取服务 swagger json 文档</li>
     * <li>从 json 中解析权限</li>
     * <li>如果权限编码重复，加上 HttpMethod 后缀</li>
     * <li>保存权限，编码存在则更新，不存在则新增</li>
     * <li>如果要清除过期权限，则清除过期权限</li>
     * <li>缓存权限到Redis，默认存储到 db4>gateway:permissions</li>
     * </ul>
     *
     * @param serviceName     服务名称
     * @param instance        服务实例
     * @param cleanPermission 是否清除过期权限：主要用于解决多版本应用同时在线时，低版本应用在高版本应用之后启动时会将更高版本多出的权限给清理的问题
     */
    void parser(String serviceName, ServiceInstance instance, Boolean cleanPermission);
}
