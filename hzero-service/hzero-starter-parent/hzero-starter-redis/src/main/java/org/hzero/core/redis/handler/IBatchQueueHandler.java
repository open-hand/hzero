package org.hzero.core.redis.handler;

import java.util.List;

/**
 * 每次处理队列中的全部消息
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/23 16:13
 */
public interface IBatchQueueHandler {

    /**
     * 每次消费的最大数量
     *
     * @return 最大数量
     */
    default int getSize() {
        return 500;
    }

    /**
     * 每次处理队列中的全部消息
     *
     * @param messages 消息内容
     */
    void process(List<String> messages);
}
