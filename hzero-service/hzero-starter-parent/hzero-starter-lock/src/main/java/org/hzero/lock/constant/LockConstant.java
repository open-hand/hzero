package org.hzero.lock.constant;

/**
 * 
 * 分布式锁常量类
 * 
 * @author xianzhi.chen@hand-china.com 2019年4月4日下午2:25:24
 */
public class LockConstant {

    /**
     * 默认客户端名字
     */
    public static final String LOCK_CLIENT_NAME = "Lock";
    /**
     * 默认SSL实现方式：JDK
     */
    public static final String JDK = "JDK";
    /**
     * 逗号
     */
    public static final String COMMA = ",";
    /**
     * 冒号
     */
    public static final String COLON = ":";
    /**
     * 分号
     */
    public static final String SEMICOLON = ";";
    /**
     * redis默认URL前缀
     */
    public static final String REDIS_URL_PREFIX = "redis://";
    public static final String REDIS_SSL_URL_PREFIX = "rediss://";
    /**
     * 锁的前缀
     */
    public static final String KEY_PREFIX = "lock:key:";

    /**
     * 
     * 负载均衡策略
     * 
     * @author xianzhi.chen@hand-china.com 2019年4月4日下午2:29:15
     */
    public static class LoadBalancer {
        /**
         * 随机调度算法
         */
        public static final String RANDOM_LOAD_BALANCER = "RandomLoadBalancer";
        /**
         * 轮询调度算法
         */
        public static final String ROUND_ROBIN_LOAD_BALANCER = "RoundRobinLoadBalancer";
        /**
         * 权重调度算法
         */
        public static final String WEIGHTED_ROUND_ROBIN_BALANCER = "WeightedRoundRobinBalancer";
    }

    /**
     * 读取操作/订阅操作的负载均衡模式常量
     * 
     * @author xianzhi.chen@hand-china.com 2019年4月4日下午2:29:09
     */
    public static class SubReadMode {
        /**
         * 从节点
         */
        public static final String SLAVE = "SLAVE";
        /**
         * 主节点
         */
        public static final String MASTER = "MASTER";
        /**
         * 主从节点
         */
        public static final String MASTER_SLAVE = "MASTER_SLAVE";
    }

}
