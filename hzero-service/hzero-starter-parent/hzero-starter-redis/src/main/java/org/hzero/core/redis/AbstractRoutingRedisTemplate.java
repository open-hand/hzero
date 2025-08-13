package org.hzero.core.redis;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.script.ScriptExecutor;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * RedisTemplate 动态路由
 *
 * @author bojiangzhou 2018/08/28
 */
public abstract class AbstractRoutingRedisTemplate<K, V> extends RedisTemplate<K, V> implements InitializingBean {

    private Map<Object, RedisTemplate<K, V>> redisTemplates;

    private RedisTemplate<K, V> defaultRedisTemplate;

    @Override
    public <T> T execute(RedisCallback<T> action) {
        return this.determineTargetRedisTemplate().execute(action);
    }

    @Override
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection) {
        return this.determineTargetRedisTemplate().execute(action, exposeConnection);
    }

    @Override
    public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
        return this.determineTargetRedisTemplate().execute(action, exposeConnection, pipeline);
    }

    @Override
    public <T> T execute(SessionCallback<T> session) {
        return this.determineTargetRedisTemplate().execute(session);
    }

    @Override
    public List<Object> executePipelined(SessionCallback<?> session) {
        return this.determineTargetRedisTemplate().executePipelined(session);
    }

    @Override
    public List<Object> executePipelined(SessionCallback<?> session, RedisSerializer<?> resultSerializer) {
        return this.determineTargetRedisTemplate().executePipelined(session, resultSerializer);
    }

    @Override
    public List<Object> executePipelined(RedisCallback<?> action) {
        return this.determineTargetRedisTemplate().executePipelined(action);
    }

    @Override
    public List<Object> executePipelined(RedisCallback<?> action, RedisSerializer<?> resultSerializer) {
        return this.determineTargetRedisTemplate().executePipelined(action, resultSerializer);
    }

    @Override
    public <T> T execute(RedisScript<T> script, List<K> keys, Object... args) {
        return this.determineTargetRedisTemplate().execute(script, keys, args);
    }

    @Override
    public <T> T execute(RedisScript<T> script, RedisSerializer<?> argsSerializer, RedisSerializer<T> resultSerializer, List<K> keys, Object... args) {
        return this.determineTargetRedisTemplate().execute(script, argsSerializer, resultSerializer, keys, args);
    }

    @Override
    public <T extends Closeable> T executeWithStickyConnection(RedisCallback<T> callback) {
        return this.determineTargetRedisTemplate().executeWithStickyConnection(callback);
    }

    @Override
    public boolean isExposeConnection() {
        return this.determineTargetRedisTemplate().isExposeConnection();
    }

    @Override
    public void setExposeConnection(boolean exposeConnection) {
        this.determineTargetRedisTemplate().setExposeConnection(exposeConnection);
    }

    @Override
    public boolean isEnableDefaultSerializer() {
        return this.determineTargetRedisTemplate().isEnableDefaultSerializer();
    }

    @Override
    public void setEnableDefaultSerializer(boolean enableDefaultSerializer) {
        this.determineTargetRedisTemplate().setEnableDefaultSerializer(enableDefaultSerializer);
    }

    @Override
    public RedisSerializer<?> getDefaultSerializer() {
        return this.determineTargetRedisTemplate().getDefaultSerializer();
    }

    @Override
    public void setDefaultSerializer(RedisSerializer<?> serializer) {
        this.determineTargetRedisTemplate().setDefaultSerializer(serializer);
    }

    @Override
    public void setKeySerializer(RedisSerializer<?> serializer) {
        this.determineTargetRedisTemplate().setKeySerializer(serializer);
    }

    @Override
    public RedisSerializer<?> getKeySerializer() {
        return this.determineTargetRedisTemplate().getKeySerializer();
    }

    @Override
    public void setValueSerializer(RedisSerializer<?> serializer) {
        this.determineTargetRedisTemplate().setValueSerializer(serializer);
    }

    @Override
    public RedisSerializer<?> getValueSerializer() {
        return this.determineTargetRedisTemplate().getValueSerializer();
    }

    @Override
    public RedisSerializer<?> getHashKeySerializer() {
        return this.determineTargetRedisTemplate().getHashKeySerializer();
    }

    @Override
    public void setHashKeySerializer(RedisSerializer<?> hashKeySerializer) {
        this.determineTargetRedisTemplate().setHashKeySerializer(hashKeySerializer);
    }

    @Override
    public RedisSerializer<?> getHashValueSerializer() {
        return this.determineTargetRedisTemplate().getHashValueSerializer();
    }

    @Override
    public void setHashValueSerializer(RedisSerializer<?> hashValueSerializer) {
        this.determineTargetRedisTemplate().setHashValueSerializer(hashValueSerializer);
    }

    @Override
    public RedisSerializer<String> getStringSerializer() {
        return this.determineTargetRedisTemplate().getStringSerializer();
    }

    @Override
    public void setStringSerializer(RedisSerializer<String> stringSerializer) {
        this.determineTargetRedisTemplate().setStringSerializer(stringSerializer);
    }

    @Override
    public void setScriptExecutor(ScriptExecutor<K> scriptExecutor) {
        this.determineTargetRedisTemplate().setScriptExecutor(scriptExecutor);
    }

    @Override
    public List<Object> exec() {
        return this.determineTargetRedisTemplate().exec();
    }

    @Override
    public List<Object> exec(RedisSerializer<?> valueSerializer) {
        return this.determineTargetRedisTemplate().exec(valueSerializer);
    }

    @Override
    public Boolean delete(K key) {
        return this.determineTargetRedisTemplate().delete(key);
    }

    @Override
    public Long delete(Collection<K> keys) {
        return this.determineTargetRedisTemplate().delete(keys);
    }

    @Override
    public Boolean hasKey(K key) {
        return this.determineTargetRedisTemplate().hasKey(key);
    }

    @Override
    public Boolean expire(K key, long timeout, TimeUnit unit) {
        return this.determineTargetRedisTemplate().expire(key, timeout, unit);
    }

    @Override
    public Boolean expireAt(K key, Date date) {
        return this.determineTargetRedisTemplate().expireAt(key, date);
    }

    @Override
    public void convertAndSend(String channel, Object message) {
        this.determineTargetRedisTemplate().convertAndSend(channel, message);
    }

    @Override
    public Long getExpire(K key) {
        return this.determineTargetRedisTemplate().getExpire(key);
    }

    @Override
    public Long getExpire(K key, TimeUnit timeUnit) {
        return this.determineTargetRedisTemplate().getExpire(key, timeUnit);
    }

    @Override
    public Set<K> keys(K pattern) {
        return this.determineTargetRedisTemplate().keys(pattern);
    }

    @Override
    public Boolean persist(K key) {
        return this.determineTargetRedisTemplate().persist(key);
    }

    @Override
    public Boolean move(K key, int dbIndex) {
        return this.determineTargetRedisTemplate().move(key, dbIndex);
    }

    @Override
    public K randomKey() {
        return this.determineTargetRedisTemplate().randomKey();
    }

    @Override
    public void rename(K oldKey, K newKey) {
        this.determineTargetRedisTemplate().rename(oldKey, newKey);
    }

    @Override
    public Boolean renameIfAbsent(K oldKey, K newKey) {
        return this.determineTargetRedisTemplate().renameIfAbsent(oldKey, newKey);
    }

    @Override
    public DataType type(K key) {
        return this.determineTargetRedisTemplate().type(key);
    }

    @Override
    public byte[] dump(K key) {
        return this.determineTargetRedisTemplate().dump(key);
    }

    @Override
    public void restore(K key, byte[] value, long timeToLive, TimeUnit unit) {
        this.determineTargetRedisTemplate().restore(key, value, timeToLive, unit);
    }

    @Override
    public void multi() {
        this.determineTargetRedisTemplate().multi();
    }

    @Override
    public void discard() {
        this.determineTargetRedisTemplate().discard();
    }

    @Override
    public void watch(K key) {
        this.determineTargetRedisTemplate().watch(key);
    }

    @Override
    public void watch(Collection<K> keys) {
        this.determineTargetRedisTemplate().watch(keys);
    }

    @Override
    public void unwatch() {
        this.determineTargetRedisTemplate().unwatch();
    }

    @Override
    public List<V> sort(SortQuery<K> query) {
        return this.determineTargetRedisTemplate().sort(query);
    }

    @Override
    public <T> List<T> sort(SortQuery<K> query, RedisSerializer<T> resultSerializer) {
        return this.determineTargetRedisTemplate().sort(query, resultSerializer);
    }

    @Override
    public <T> List<T> sort(SortQuery<K> query, BulkMapper<T, V> bulkMapper) {
        return this.determineTargetRedisTemplate().sort(query, bulkMapper);
    }

    @Override
    public <T, S> List<T> sort(SortQuery<K> query, BulkMapper<T, S> bulkMapper, RedisSerializer<S> resultSerializer) {
        return this.determineTargetRedisTemplate().sort(query, bulkMapper, resultSerializer);
    }

    @Override
    public Long sort(SortQuery<K> query, K storeKey) {
        return this.determineTargetRedisTemplate().sort(query, storeKey);
    }

    @Override
    public BoundValueOperations<K, V> boundValueOps(K key) {
        return this.determineTargetRedisTemplate().boundValueOps(key);
    }

    @Override
    public ValueOperations<K, V> opsForValue() {
        return this.determineTargetRedisTemplate().opsForValue();
    }

    @Override
    public ListOperations<K, V> opsForList() {
        return this.determineTargetRedisTemplate().opsForList();
    }

    @Override
    public BoundListOperations<K, V> boundListOps(K key) {
        return this.determineTargetRedisTemplate().boundListOps(key);
    }

    @Override
    public BoundSetOperations<K, V> boundSetOps(K key) {
        return this.determineTargetRedisTemplate().boundSetOps(key);
    }

    @Override
    public SetOperations<K, V> opsForSet() {
        return this.determineTargetRedisTemplate().opsForSet();
    }

    @Override
    public BoundZSetOperations<K, V> boundZSetOps(K key) {
        return this.determineTargetRedisTemplate().boundZSetOps(key);
    }

    @Override
    public ZSetOperations<K, V> opsForZSet() {
        return this.determineTargetRedisTemplate().opsForZSet();
    }

    @Override
    public GeoOperations<K, V> opsForGeo() {
        return this.determineTargetRedisTemplate().opsForGeo();
    }

    @Override
    public BoundGeoOperations<K, V> boundGeoOps(K key) {
        return this.determineTargetRedisTemplate().boundGeoOps(key);
    }

    @Override
    public HyperLogLogOperations<K, V> opsForHyperLogLog() {
        return this.determineTargetRedisTemplate().opsForHyperLogLog();
    }

    @Override
    public <HK, HV> BoundHashOperations<K, HK, HV> boundHashOps(K key) {
        return this.determineTargetRedisTemplate().boundHashOps(key);
    }

    @Override
    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        return this.determineTargetRedisTemplate().opsForHash();
    }

    @Override
    public ClusterOperations<K, V> opsForCluster() {
        return this.determineTargetRedisTemplate().opsForCluster();
    }

    @Override
    public void killClient(String host, int port) {
        this.determineTargetRedisTemplate().killClient(host, port);
    }

    @Override
    public List<RedisClientInfo> getClientList() {
        return this.determineTargetRedisTemplate().getClientList();
    }

    @Override
    public void slaveOf(String host, int port) {
        this.determineTargetRedisTemplate().slaveOf(host, port);
    }

    @Override
    public void slaveOfNoOne() {
        this.determineTargetRedisTemplate().slaveOfNoOne();
    }

    @Override
    public void setEnableTransactionSupport(boolean enableTransactionSupport) {
        this.determineTargetRedisTemplate().setEnableTransactionSupport(enableTransactionSupport);
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.determineTargetRedisTemplate().setBeanClassLoader(classLoader);
    }

    @Override
    public RedisConnectionFactory getConnectionFactory() {
        return this.determineTargetRedisTemplate().getConnectionFactory();
    }

    @Override
    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.determineTargetRedisTemplate().setConnectionFactory(connectionFactory);
    }

    public void setRedisTemplates(Map<Object, RedisTemplate<K, V>> redisTemplates) {
        this.redisTemplates = redisTemplates;
    }

    public void setDefaultRedisTemplate(RedisTemplate<K, V> defaultRedisTemplate) {
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.redisTemplates == null) {
            throw new IllegalArgumentException("Property 'redisTemplates' is required");
        }
        if (this.defaultRedisTemplate == null) {
            throw new IllegalArgumentException("Property 'defaultRedisTemplate' is required");
        }
    }

    protected RedisTemplate<K, V> determineTargetRedisTemplate() {
        Object lookupKey = determineCurrentLookupKey();
        if (lookupKey == null) {
            return this.defaultRedisTemplate;
        }
        RedisTemplate<K, V> redisTemplate = this.redisTemplates.get(lookupKey);
        if (redisTemplate == null) {
            redisTemplate = createRedisTemplateOnMissing(lookupKey);
            this.redisTemplates.put(lookupKey, redisTemplate);
        }
        return redisTemplate;
    }

    /**
     * 获取当前 Redis db
     *
     * @return current redis db
     */
    protected abstract Object determineCurrentLookupKey();

    /**
     * 没有对应 db 的 RedisTemplate 时，则调用此方法创建 RedisTemplate
     *
     * @param lookupKey RedisDB
     * @return RedisTemplate
     */
    protected abstract RedisTemplate<K, V> createRedisTemplateOnMissing(Object lookupKey);

}
