package org.hzero.core.redis.handler;

/**
 * 每次处理队列中的第一条消息
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/23 16:13
 */
public interface IQueueHandler {

    /**
     * 每次处理队列中的第一条消息
     *
     * @param message 消息内容
     */
    void process(String message);
}
