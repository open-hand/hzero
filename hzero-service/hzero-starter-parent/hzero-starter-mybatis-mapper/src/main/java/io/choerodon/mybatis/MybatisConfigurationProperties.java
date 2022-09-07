package io.choerodon.mybatis;

import io.choerodon.mybatis.constant.SupportedKeyGenerator;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author qingsheng.chen@hand-china.com
 */
@ConfigurationProperties("mybatis.configuration")
public class MybatisConfigurationProperties {
    private String defaultLanguage = "zh_CN";
    private SupportedKeyGenerator keyGenerator;
    private SnowflakeProperties snowflake = new SnowflakeProperties();

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public MybatisConfigurationProperties setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return this;
    }

    public SupportedKeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public MybatisConfigurationProperties setKeyGenerator(SupportedKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
        return this;
    }

    public SnowflakeProperties getSnowflake() {
        return snowflake;
    }

    public MybatisConfigurationProperties setSnowflake(SnowflakeProperties snowflake) {
        this.snowflake = snowflake;
        return this;
    }

    public static class SnowflakeProperties {
        public enum MetaProvider {
            none,
            redis
        }
        /**
         * 时间戳位长度，默认 41 位，可是使用约 69 年，<strong>注意：如果修改了位长度，需要调整其他部分的位长度，最终需要 时间戳 + 数据中心ID + 工作集器ID + 序列 = 63</strong>
         */
        private Integer bitTimestamp;
        /**
         * 数据中心位长度，默认 5 位，可使用 32 个数据中心，<strong>注意：如果修改了位长度，需要调整其他部分的位长度，最终需要 时间戳 + 数据中心ID + 工作集器ID + 序列 = 63</strong>
         */
        private Integer bitDataCenterId;
        /**
         * 工作机器位长度，默认 5 位，可使用 32 个工作机器，<strong>注意：如果修改了位长度，需要调整其他部分的位长度，最终需要 时间戳 + 数据中心ID + 工作集器ID + 序列 = 63</strong>
         */
        private Integer bitWorkerId;
        /**
         * 序列位长度，默认 12 位，可使用 4096 个序列，<strong>注意：如果修改了位长度，需要调整其他部分的位长度，最终需要 时间戳 + 数据中心ID + 工作集器ID + 序列 = 63</strong>
         */
        private Integer bitSequence;
        /**
         * 元数据生成，默认使用redis（保证唯一），none不一定唯一，极高的并发下可能会导致雪花ID重复，当手动指定 dataCenterId 和 workerId 时，忽略此配置
         */
        private MetaProvider metaProvider = MetaProvider.redis;
        /**
         * 元数据生成，redis 模式的 db
         */
        private int metaProviderRedisDb = 1;
        /**
         * 元数据缓存超时时间，默认 10 分钟
         */
        private int metaProviderRedisExpire = 600000;
        /**
         * 元数据缓存刷新时间，默认 9 分钟，一定要小于缓存超时时间，如果大于等于缓存时间，默认重置为缓存时间的 90%
         */
        private int metaProviderRedisRefreshInterval = 540000;
        /**
         * 雪花ID开始时间，可以从起始时间开始使用 69 年，默认 1577808000000（2020/01/01 00:00:00）
         */
        private long startTimestamp = 1577808000000L;
        /**
         * 数据中心ID，分布式，建议每个服务一个编码
         */
        private Long dataCenterId;
        /**
         * 工作机器ID，集群，同一个服务的多个实例之间必须不相同
         */
        private Long workerId;

        public Integer getBitTimestamp() {
            return bitTimestamp;
        }

        public SnowflakeProperties setBitTimestamp(Integer bitTimestamp) {
            this.bitTimestamp = bitTimestamp;
            return this;
        }

        public Integer getBitDataCenterId() {
            return bitDataCenterId;
        }

        public SnowflakeProperties setBitDataCenterId(Integer bitDataCenterId) {
            this.bitDataCenterId = bitDataCenterId;
            return this;
        }

        public Integer getBitWorkerId() {
            return bitWorkerId;
        }

        public SnowflakeProperties setBitWorkerId(Integer bitWorkerId) {
            this.bitWorkerId = bitWorkerId;
            return this;
        }

        public Integer getBitSequence() {
            return bitSequence;
        }

        public SnowflakeProperties setBitSequence(Integer bitSequence) {
            this.bitSequence = bitSequence;
            return this;
        }

        public MetaProvider getMetaProvider() {
            return metaProvider;
        }

        public SnowflakeProperties setMetaProvider(MetaProvider metaProvider) {
            this.metaProvider = metaProvider;
            return this;
        }

        public int getMetaProviderRedisDb() {
            return metaProviderRedisDb;
        }

        public SnowflakeProperties setMetaProviderRedisDb(int metaProviderRedisDb) {
            this.metaProviderRedisDb = metaProviderRedisDb;
            return this;
        }

        public int getMetaProviderRedisExpire() {
            return metaProviderRedisExpire;
        }

        public SnowflakeProperties setMetaProviderRedisExpire(int metaProviderRedisExpire) {
            this.metaProviderRedisExpire = metaProviderRedisExpire;
            return this;
        }

        public int getMetaProviderRedisRefreshInterval() {
            return metaProviderRedisRefreshInterval;
        }

        public SnowflakeProperties setMetaProviderRedisRefreshInterval(int metaProviderRedisRefreshInterval) {
            this.metaProviderRedisRefreshInterval = metaProviderRedisRefreshInterval;
            return this;
        }

        public long getStartTimestamp() {
            return startTimestamp;
        }

        public SnowflakeProperties setStartTimestamp(long startTimestamp) {
            this.startTimestamp = startTimestamp;
            return this;
        }

        public Long getDataCenterId() {
            return dataCenterId;
        }

        public SnowflakeProperties setDataCenterId(Long dataCenterId) {
            this.dataCenterId = dataCenterId;
            return this;
        }

        public Long getWorkerId() {
            return workerId;
        }

        public SnowflakeProperties setWorkerId(Long workerId) {
            this.workerId = workerId;
            return this;
        }
    }
}
