package io.choerodon.mybatis.helper.snowflake;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.hzero.core.redis.RedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.MybatisConfigurationProperties;

/**
 * @author qingsheng.chen@hand-china.com
 */
@SuppressWarnings("DuplicatedCode")
public final class RedisSnowflakeMetaProvider implements SnowflakeMetaProvider {
    private static final Logger logger = LoggerFactory.getLogger(RedisSnowflakeMetaProvider.class);
    private static final String CACHE_LOCK_KEY = "mybatis:snowflake:lock";
    private static final String CACHE_LOCK_VALUE = "%s:%s";
    private static final String CACHE_DATA_CENTER_MASK = "mybatis:snowflake:data-center";
    private static final String CACHE_DATA_CENTER_ID_MASK = "mybatis:snowflake:data-center:id:%s";
    private static final String CACHE_DATA_CENTER_ID_SEQUENCE_MASK = "mybatis:snowflake:data-center:sequence:id";
    private static final String CACHE_WORKER_MASK = "mybatis:snowflake:worker:%s";
    private static final String CACHE_WORKER_ID_MASK = "mybatis:snowflake:worker:%s:id:%s";
    private static final String CACHE_WORKER_ID_SEQUENCE_MASK = "mybatis:snowflake:worker:%s:sequence:id";
    private static final long CACHE_LOCK_EXPIRE_SECOND = 60;
    private static final long CACHE_LOCK_WAIT_MILLISECOND = 1000;

    private final String dataCenterName;
    private final String workerName;
    private final String cacheLockValue;
    private final String cacheDataCenterKey;
    private final String cacheDataCenterIdKey;
    private final String cacheDataCenterIdSequenceKey;
    private final String cacheWorkerKey;
    private final String cacheWorkerIdKey;
    private final String cacheWorkerIdSequenceKey;
    private final int redisDb;
    private final int expire;
    private final int refreshInterval;
    private final TimeUnit milliSeconds = TimeUnit.MILLISECONDS;
    private Long dataCenterId;
    private Long workerId;
    private final RedisHelper redisHelper;
    private final ScheduledExecutorService executorService;
    private final DataCenterProvider dataCenterProvider;
    private final WorkerProvider workerProvider;
    private final long maxDataCenter;
    private final long maxWorker;

    public RedisSnowflakeMetaProvider(String dataCenterName,
                                      MybatisConfigurationProperties.SnowflakeProperties properties,
                                      RedisHelper redisHelper) {
        this(dataCenterName, UUID.randomUUID().toString().replace("-", ""), properties, BIT_DATA_CENTER, BIT_WORKER, redisHelper);
    }

    public RedisSnowflakeMetaProvider(String dataCenterName,
                                      MybatisConfigurationProperties.SnowflakeProperties properties,
                                      Integer bitDataCenterId,
                                      Integer bitWorkerId,
                                      RedisHelper redisHelper) {
        this(dataCenterName, UUID.randomUUID().toString().replace("-", ""), properties, bitDataCenterId, bitWorkerId, redisHelper);
    }

    public RedisSnowflakeMetaProvider(String dataCenterName,
                                      String workerName,
                                      MybatisConfigurationProperties.SnowflakeProperties properties,
                                      Integer bitDataCenterId,
                                      Integer bitWorkerId,
                                      RedisHelper redisHelper) {
        this.dataCenterName = dataCenterName;
        this.workerName = workerName;
        this.redisHelper = redisHelper;
        this.redisDb = properties.getMetaProviderRedisDb();
        this.expire = properties.getMetaProviderRedisExpire();
        this.refreshInterval = properties.getMetaProviderRedisRefreshInterval() < this.expire ? properties.getMetaProviderRedisRefreshInterval() : (int) (this.expire * .9);
        this.dataCenterId = properties.getDataCenterId();
        this.workerId = properties.getWorkerId();
        this.cacheLockValue = String.format(CACHE_LOCK_VALUE, this.dataCenterName, this.workerName);
        this.cacheDataCenterKey = CACHE_DATA_CENTER_MASK;
        this.cacheDataCenterIdKey = String.format(CACHE_DATA_CENTER_ID_MASK, this.dataCenterName);
        this.cacheDataCenterIdSequenceKey = CACHE_DATA_CENTER_ID_SEQUENCE_MASK;
        this.cacheWorkerKey = String.format(CACHE_WORKER_MASK, this.dataCenterName);
        this.cacheWorkerIdKey = String.format(CACHE_WORKER_ID_MASK, this.dataCenterName, this.workerName);
        this.cacheWorkerIdSequenceKey = String.format(CACHE_WORKER_ID_SEQUENCE_MASK, this.dataCenterName);
        this.dataCenterProvider = new DataCenterProvider(this);
        this.workerProvider = new WorkerProvider(this);
        this.maxDataCenter = bitDataCenterId != null ? ~(-1L << bitDataCenterId) : MAX_DATA_CENTER;
        this.maxWorker = bitWorkerId != null ? ~(-1L << bitWorkerId) : MAX_WORKER;

        Assert.isTrue(this.redisDb >= 0, "[Snowflake] Redis DB must be greater than or equal to 0.");
        Assert.isTrue(this.expire > 0, "[Snowflake] Redis cache timeout must be greater than or equal to 0.");
        Assert.isTrue(this.refreshInterval > 0, "[Snowflake] Redis cache refresh interval must be greater than or equal to 0.");
        Assert.isTrue(this.maxDataCenter > 0, "[Snowflake] Max data center must be greater than or equal to 0.");
        Assert.isTrue(this.maxWorker > 0, "[Snowflake] Max worker must be greater than or equal to 0.");

        // 核心线程数
        this.executorService = Executors.newScheduledThreadPool(1);
        start();
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown() && !executorService.isTerminated()) {
            executorService.shutdown();
        }
    }

    public ScheduledFuture<?> start() {
        return this.executorService.scheduleWithFixedDelay(() -> provide(dataCenterProvider, workerProvider), refreshInterval, refreshInterval, milliSeconds);
    }

    @Override
    public long dataCenterId(ApplicationContext context) {
        if (dataCenterId != null) {
            return dataCenterId;
        }
        provide(dataCenterProvider);
        return dataCenterId == null ? 0 : dataCenterId;
    }

    @Override
    public long workerId(ApplicationContext context) {
        if (workerId != null) {
            return workerId;
        }
        provide(workerProvider);
        return workerId == null ? 0 : workerId;
    }

    interface Provider {
        void provide();
    }

    class DataCenterProvider implements Provider {
        private final RedisSnowflakeMetaProvider redisSnowflakeMetaProvider;

        public DataCenterProvider(RedisSnowflakeMetaProvider redisSnowflakeMetaProvider) {
            this.redisSnowflakeMetaProvider = redisSnowflakeMetaProvider;
        }

        @Override
        public void provide() {
            provideDataCenterId();
            logger.info("[Snowflake] meta provider : provide data center id = {}", redisSnowflakeMetaProvider.dataCenterId);
        }
    }

    class WorkerProvider implements Provider {
        private final RedisSnowflakeMetaProvider redisSnowflakeMetaProvider;

        public WorkerProvider(RedisSnowflakeMetaProvider redisSnowflakeMetaProvider) {
            this.redisSnowflakeMetaProvider = redisSnowflakeMetaProvider;
        }

        @Override
        public void provide() {
            provideWorkerId();
            logger.info("[Snowflake] meta provider : provide worker id = {}", redisSnowflakeMetaProvider.workerId);
        }
    }

    private synchronized void provide(Provider... providers) {
        long start = System.currentTimeMillis();
        logger.info("[Snowflake] meta provider : start [{}] - [{}]", dataCenterName, workerName);
        redisHelper.setCurrentDatabase(redisDb);
        try {
            logger.info("[Snowflake] meta provider : lock...");
            lock();
            logger.info("[Snowflake] meta provider : locked.");
            for (Provider provider : providers) {
                provider.provide();
            }
        } finally {
            unlock();
            logger.info("[Snowflake] meta provider : release lock.");
            redisHelper.clearCurrentDatabase();
        }
        logger.info("[Snowflake] meta provider : end  [{}] [{}] - [{}]", System.currentTimeMillis() - start, dataCenterName, workerName);
    }

    private void unlock() {
        redisHelper.delKey(CACHE_LOCK_KEY);
    }

    private void lock() {
        ValueOperations<String, String> operations = redisHelper.getRedisTemplate().opsForValue();
        while (BooleanUtils.isNotTrue(operations.setIfAbsent(CACHE_LOCK_KEY, cacheLockValue))) {
            try {
                // 获取锁过期时间
                Long expire = redisHelper.getExpire(CACHE_LOCK_KEY);
                // 如果锁超时时间为 -1（服务启动中停止）， 则重置超时时间
                if (Objects.equals(expire, -1L)) {
                    logger.error("[Snowflake] Found deadlock, reset timeout.");
                    redisHelper.setExpire(CACHE_LOCK_KEY, CACHE_LOCK_EXPIRE_SECOND);
                    continue;
                }
                logger.info("[Snowflake] The lock is held by another service instance, waiting for the lock to be released, there are {} seconds left.", expire);
                Thread.sleep(CACHE_LOCK_WAIT_MILLISECOND);
            } catch (InterruptedException e) {
                logger.error("Error get lock.", e);
                // 重置线程中断标记
                Thread.currentThread().interrupt();
                throw new CommonException("[Snowflake] Thread was interrupted while acquiring lock.");
            }
        }
        redisHelper.setExpire(CACHE_LOCK_KEY, CACHE_LOCK_EXPIRE_SECOND);
    }

    private void provideDataCenterId() {
        // 获取已经注册所有的 data-center
        Set<String> dataCenters = redisHelper.setMembers(cacheDataCenterKey);
        if (dataCenters == null) {
            dataCenters = new HashSet<>(1);
        }
        // 注册当前 data-center
        dataCenters.add(dataCenterName);

        // 获取所有 data-center id
        Set<Long> registerDataCenterIds = new HashSet<>(dataCenters.size() - 1);
        Set<String> expireDataCenters = new HashSet<>();
        dataCenters.forEach(dataCenter -> {
            if (Objects.equals(dataCenter, dataCenterName)) {
                return;
            }
            String dataCenterKey = String.format(CACHE_DATA_CENTER_ID_MASK, dataCenter);
            String registerDataCenterId = redisHelper.strGet(dataCenterKey);
            if (StringUtils.hasText(registerDataCenterId)) {
                // 已经注册 data-center id
                registerDataCenterIds.add(Long.parseLong(registerDataCenterId));
            } else {
                // 注册的 data-center id 已经过期
                expireDataCenters.add(dataCenter);
            }
        });
        // 移除已过期 data-center
        dataCenters.removeAll(expireDataCenters);
        if (!expireDataCenters.isEmpty()) {
            redisHelper.setRemove(cacheDataCenterKey, expireDataCenters.toArray());
        }
        // 获取当前 data-center id
        String dataCenterId = redisHelper.strGet(cacheDataCenterIdKey);
        // 如果当前已经有 data-center id 续期
        if (StringUtils.hasText(dataCenterId)) {
            this.dataCenterId = Long.parseLong(dataCenterId);
        } else {
            // 如果没有，注册 data-center id
            if (this.dataCenterId == null) {
                for (int i = 0; i <= maxDataCenter; i++) {
                    long generateDataCenterId = redisHelper.strIncrement(cacheDataCenterIdSequenceKey, 1L) % maxDataCenter;
                    if (!registerDataCenterIds.contains(generateDataCenterId)) {
                        this.dataCenterId = generateDataCenterId;
                        break;
                    }
                }
            }
            if (this.dataCenterId == null) {
                logger.error("Error provide snowflake meta [{}] - [{}].", this.dataCenterName, this.workerName);
                throw new CommonException("Failed to get data center ID, request to ensure that the number of data centers is " + maxDataCenter + " or less, or manually specify the data center ID.");
            }
            // 缓存当前 data-center id
            redisHelper.strSet(cacheDataCenterIdKey, String.valueOf(this.dataCenterId));
        }
        // 缓存 data-center 列表
        redisHelper.setAdd(cacheDataCenterKey, dataCenters.toArray(new String[]{}));
        redisHelper.setExpire(cacheDataCenterKey, expire, milliSeconds);
        redisHelper.setExpire(cacheDataCenterIdKey, expire, milliSeconds);
        redisHelper.setExpire(cacheDataCenterIdSequenceKey, expire, milliSeconds);
    }

    private void provideWorkerId() {
        /// 获取已经注册的 data-center 下的所有 worker
        Set<String> workers = redisHelper.setMembers(cacheWorkerKey);
        if (workers == null) {
            workers = new HashSet<>(1);
        }
        // 注册当前 worker
        workers.add(workerName);

        // 获取所有 worker id
        Set<Long> registerWorkerIds = new HashSet<>(workers.size() - 1);
        Set<String> expireWorkers = new HashSet<>();
        workers.forEach(worker -> {
            if (Objects.equals(worker, workerName)) {
                return;
            }
            String workerKey = String.format(CACHE_WORKER_ID_MASK, dataCenterName, worker);
            String registerWorkerId = redisHelper.strGet(workerKey);
            if (StringUtils.hasText(registerWorkerId)) {
                // 已经注册 worker id
                registerWorkerIds.add(Long.parseLong(registerWorkerId));
            } else {
                // 注册的 worker id 已经过期
                expireWorkers.add(worker);
            }
        });
        // 移除已过期 worker
        workers.removeAll(expireWorkers);
        if (!expireWorkers.isEmpty()) {
            redisHelper.setRemove(cacheWorkerKey, expireWorkers.toArray());
        }
        // 获取当前 worker id
        String workerId = redisHelper.strGet(cacheWorkerIdKey);
        // 如果当前已经有 worker id 续期
        if (StringUtils.hasText(workerId)) {
            this.workerId = Long.parseLong(workerId);
        } else {
            // 如果没有，注册 worker id
            if (this.workerId == null) {
                for (int i = 0; i <= maxWorker; i++) {
                    long generateWorkerId = redisHelper.strIncrement(cacheWorkerIdSequenceKey, 1L) % maxWorker;
                    if (!registerWorkerIds.contains(generateWorkerId)) {
                        this.workerId = generateWorkerId;
                        break;
                    }
                }
            }
            if (this.workerId == null) {
                logger.error("Error provide snowflake meta [{}] - [{}].", this.dataCenterName, this.workerName);
                throw new CommonException("Failed to get worker ID, request to ensure that the number of worker is " + maxWorker + " or less, or manually specify the worker ID.");
            }
            // 缓存当前 Worker id
            redisHelper.strSet(cacheWorkerIdKey, String.valueOf(this.workerId));
        }
        // 缓存 Worker 列表
        redisHelper.setAdd(cacheWorkerKey, workers.toArray(new String[]{}));
        redisHelper.setExpire(cacheWorkerKey, expire, milliSeconds);
        redisHelper.setExpire(cacheWorkerIdKey, expire, milliSeconds);
        redisHelper.setExpire(cacheWorkerIdSequenceKey, expire, milliSeconds);
    }
}
