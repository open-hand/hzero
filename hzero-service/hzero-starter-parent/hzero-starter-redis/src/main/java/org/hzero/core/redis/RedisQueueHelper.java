package org.hzero.core.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 基于redis的消息队列，生产者/消费者模式
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/23 13:54
 */
public class RedisQueueHelper {

    private static final String PREFIX = "hzero-queue:";

    private final RedisHelper redisHelper;
    private final HZeroRedisProperties properties;

    public RedisQueueHelper(RedisHelper redisHelper, HZeroRedisProperties redisProperties) {
        this.redisHelper = redisHelper;
        this.properties = redisProperties;
    }

    /**
     * 添加队列消息
     *
     * @param key     key
     * @param message 消息内容
     */
    public void push(String key, String message) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        redisHelper.lstRightPush(PREFIX + key, message);
        redisHelper.clearCurrentDatabase();
    }

    /**
     * 批量添加队列消息
     *
     * @param key      key
     * @param messages 消息内容
     */
    public void pushAll(String key, Collection<String> messages) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        redisHelper.lstRightPushAll(PREFIX + key, messages);
        redisHelper.clearCurrentDatabase();
    }

    /**
     * 指定方向，添加队列消息
     *
     * @param key     key
     * @param message 消息内容
     * @param right   是否从右侧插入消息
     */
    public void push(String key, String message, boolean right) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        if (right) {
            redisHelper.lstRightPush(PREFIX + key, message);
        } else {
            redisHelper.lstLeftPush(PREFIX + key, message);
        }
        redisHelper.clearCurrentDatabase();
    }

    /**
     * 指定方向，批量添加队列消息
     *
     * @param key      key
     * @param messages 消息内容
     * @param right    是否从右侧插入消息
     */
    public void pushAll(String key, Collection<String> messages, boolean right) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        if (right) {
            redisHelper.lstRightPushAll(PREFIX + key, messages);
        } else {
            redisHelper.lstLeftPushAll(PREFIX + key, messages);
        }
        redisHelper.clearCurrentDatabase();
    }

    /**
     * 获取队列消息
     *
     * @param key key
     */
    public String pull(String key) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        String str = redisHelper.lstLeftPop(PREFIX + key);
        redisHelper.clearCurrentDatabase();
        return str;
    }

    /**
     * 批量获取队列消息
     *
     * @param key key
     */
    public List<String> pullAll(String key) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        List<String> result = new ArrayList<>();
        while (true) {
            String str = pull(key);
            if (str == null) {
                break;
            }
            result.add(str);
        }
        redisHelper.clearCurrentDatabase();
        return result;
    }

    /**
     * 批量获取队列消息, 指定最大量
     *
     * @param key key
     */
    public List<String> pullAll(String key, int maxSize) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        List<String> result = new ArrayList<>();
        while (true) {
            String str = pull(key);
            if (str == null || result.size() > maxSize) {
                break;
            }
            result.add(str);
        }
        redisHelper.clearCurrentDatabase();
        return result;
    }

    /**
     * 指定方向，添加队列消息
     *
     * @param key  key
     * @param left 是否从左侧插入消息
     */
    public String pull(String key, boolean left) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        String str;
        if (left) {
            str = redisHelper.lstLeftPop(PREFIX + key);
        } else {
            str = redisHelper.lstRightPop(PREFIX + key);
        }
        redisHelper.clearCurrentDatabase();
        return str;
    }

    /**
     * 指定方向，批量获取队列消息
     *
     * @param key key
     */
    public List<String> pullAll(String key, boolean left) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        List<String> result = new ArrayList<>();
        while (true) {
            String str = pull(key, left);
            if (str == null) {
                break;
            }
            result.add(str);
        }
        redisHelper.clearCurrentDatabase();
        return result;
    }

    /**
     * 指定方向，批量获取队列消息, 指定最大量
     *
     * @param key key
     */
    public List<String> pullAll(String key, boolean left, int max) {
        redisHelper.setCurrentDatabase(properties.getQueueDb());
        List<String> result = new ArrayList<>();
        while (true) {
            String str = pull(key, left);
            if (str == null || result.size() > max) {
                break;
            }
            result.add(str);
        }
        redisHelper.clearCurrentDatabase();
        return result;
    }
}
