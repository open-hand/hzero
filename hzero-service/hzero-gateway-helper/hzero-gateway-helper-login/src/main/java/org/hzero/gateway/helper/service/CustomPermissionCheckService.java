package org.hzero.gateway.helper.service;

import org.hzero.gateway.helper.entity.CheckState;
import org.hzero.gateway.helper.entity.RequestContext;

/**
 * 项目权限校验
 *
 * @author bojiangzhou 2020/05/07
 */
public interface CustomPermissionCheckService {

    /**
     * 自定义权限检查
     * <p>
     * 检查权限后参考如下设置：
     *
     * <pre>
     * context.response.setStatus(CheckState.newState(value, code, name));
     * context.response.setMessage("Have access to this 'custom-level' interface, permission: " + context.getPermission());
     * </pre>
     *
     * <pre>
     * CheckState.value >= 200 : 鉴权成功
     * CheckState.value >= 400: 鉴权不通过
     * CheckState.value >= 500: 服务端报错
     * </pre>
     *
     * @see CheckState
     * @param context 请求上线文信息
     */
    void checkPermission(RequestContext context);

}
