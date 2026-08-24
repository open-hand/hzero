package org.hzero.boot.message.service;

import org.hzero.boot.message.entity.Message;

/**
 * <p>
 * 获取消息内容
 * </p>
 *
 * @author qingsheng.chen 2018/8/7 星期二 21:05
 */
@FunctionalInterface
public interface GetMessage {
    /**
     * 获取消息内容
     *
     * @return 消息内容
     */
    Message getMessage();
}
