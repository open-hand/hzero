package org.hzero.boot.message.handler;

import org.hzero.boot.message.entity.Msg;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/28 20:36
 */
public interface ISocketHandler {

    /**
     * 执行前端socket消息
     *
     * @param msg 消息内容
     */
    default void processMessage(Msg msg) {
    }
}
