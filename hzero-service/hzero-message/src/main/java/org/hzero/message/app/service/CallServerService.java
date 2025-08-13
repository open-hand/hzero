package org.hzero.message.app.service;

import org.hzero.message.domain.entity.CallServer;

/**
 * 语音消息服务应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2020-02-26 15:59:05
 */
public interface CallServerService {

    /**
     * 新建语音服务
     *
     * @param callServer 语音服务
     * @return 语音服务
     */
    CallServer createCallServer(CallServer callServer);

    /**
     * 更新语音服务
     *
     * @param callServer 语音服务
     * @return 语音服务
     */
    CallServer updateCallServer(CallServer callServer);

    /**
     * 获取语音服务
     *
     * @param tenantId   租户Id
     * @param serverCode 服务编码
     * @return 语音服务配置
     */
    CallServer getCallServer(Long tenantId, String serverCode);
}
