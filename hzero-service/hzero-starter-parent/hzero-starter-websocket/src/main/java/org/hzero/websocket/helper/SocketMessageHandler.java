package org.hzero.websocket.helper;

import java.util.Map;

import org.hzero.websocket.vo.MsgVO;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * 前端webSocket消息处理
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/27 16:25
 */
public abstract class SocketMessageHandler {


    private ThreadLocal<CustomUserDetails> customUserDetails = new ThreadLocal<>();

    /**
     * 处理接收到的消息
     *
     * @param msg 消息内容
     */
    public void processMessage(MsgVO msg) {
    }

    /**
     * 处理接收到的二进制数据
     *
     * @param data data
     */
    public void processByte(Map<String, Object> args, byte[] data) {
    }

    public boolean needPrincipal() {
        return false;
    }

    public CustomUserDetails getCustomUserDetails() {
        return customUserDetails.get();
    }

    public void setCustomUserDetails(CustomUserDetails customUserDetails) {
        this.customUserDetails.set(customUserDetails);
    }

    public void clearCustomUserDetails() {
        this.customUserDetails.remove();
    }
}
