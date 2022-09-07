package org.hzero.core.algorithm.structure;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;

/**
 * key值有有效期的Map集合<p></p>
 * 当一个key被set了超过一段时间(ttl x timeUnit),则再次get的时候则会返回null值<p></p>
 * 注意,计时的最小时间单位为毫秒{@link TimeUnit#MILLISECONDS}
 *
 * @param <K> key类型
 * @param <V> value类型
 * @author gaokuo.dai@hand-china.com 2018-12-13 11:26:29
 */
public class TTLMap<K, V> implements Map<K, V> {

    /**
     * 内部Map集合
     */
    protected Map<K, Pair<V, Long>> innerMap;
    /**
     * key超时时间单位
     */
    protected TimeUnit timeUnit;
    /**
     * key超时时间单位数
     */
    protected long ttl;
    /**
     * ️以毫秒计key超时时间
     */
    private long ttlInMillisecond;

    /**
     * 默认内部Map集合类型--HashMap
     */
    @SuppressWarnings("rawtypes")
    private static final Class<? extends Map> DEFAULT_INNER_MAP_CLASS = HashMap.class;
    /**
     * 默认超时时间单位--分钟
     */
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    /**
     * 默认超时时间单位数--10
     */
    private static final long DEFAULT_TTL = 10;

    public TTLMap() {
        this.initWithDefaultValue();
    }

    /**
     * 内部使用的特殊构造方法
     *
     * @param isBuildeMode 是否为builder模式.是则不初始化任何属性;否则用默认值进行初始化
     */
    private TTLMap(boolean isBuildeMode) {
        if (!isBuildeMode) {
            initWithDefaultValue();
        }
    }

    @Override
    public int size() {
        this.clearTimeoutKeys();
        return this.innerMap.size();
    }

    @Override
    public boolean isEmpty() {
        this.clearTimeoutKeys();
        return this.innerMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (this.clearTimeoutKey(key)) {
            return false;
        }
        return this.innerMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        this.clearTimeoutKeys();
        if (this.innerMap.isEmpty()) {
            return false;
        }
        return this.innerMap.values().stream().map(Pair::getFirst).anyMatch(v -> Objects.equals(v, value));
    }

    @Override
    public V get(Object key) {
        return this.getTrueValue(key);
    }

    @Override
    public V put(K key, V value) {
        this.innerMap.put(key, new Pair<>(value, System.currentTimeMillis() + this.ttlInMillisecond));
        return value;
    }

    /**
     * put a key value pair with ttl in default time unit
     *
     * @param key   key
     * @param value value
     * @param ttl   ttl
     * @return value
     */
    public V put(K key, V value, long ttl) {
        this.innerMap.put(key, new Pair<>(value, System.currentTimeMillis() + this.timeUnit.toMillis(ttl)));
        return value;
    }

    /**
     * put a key value pair with ttl and time unit
     *
     * @param key      key
     * @param value    value
     * @param ttl      ttl
     * @param timeUnit time unit
     * @return value
     */
    public V put(K key, V value, long ttl, TimeUnit timeUnit) {
        if (timeUnit == null) {
            timeUnit = this.timeUnit;
        }
        this.innerMap.put(key, new Pair<>(value, System.currentTimeMillis() + timeUnit.toMillis(ttl)));
        return value;
    }

    @Override
    public V remove(Object key) {
        if (!this.containsKey(key)) {
            return null;
        }
        V value = this.getTrueValue(key);
        this.innerMap.remove(key);
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m == null || m.isEmpty()) {
            return;
        }
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public void clear() {
        this.innerMap.clear();
    }

    @Override
    public Set<K> keySet() {
        this.clearTimeoutKeys();
        if (this.innerMap.isEmpty()) {
            return Collections.emptySet();
        }
        return this.innerMap.keySet();
    }

    @Override
    public Collection<V> values() {
        this.clearTimeoutKeys();
        if (this.innerMap.isEmpty()) {
            return Collections.emptyList();
        }
        return this.innerMap.values().stream().map(Pair::getFirst).collect(Collectors.toList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        this.clearTimeoutKeys();
        if (this.innerMap.isEmpty()) {
            return Collections.emptySet();
        }
        return this.innerMap.entrySet().stream().map(entry ->
                new InnerEntry<>(entry.getKey(), Optional.ofNullable(entry.getValue()).map(Pair::getFirst).orElse(null))
        ).collect(Collectors.toSet());
    }

    /**
     * 清理所有已过期的key
     *
     * @return 被清理的key的数量
     */
    protected int clearTimeoutKeys() {
        if (this.innerMap.isEmpty()) {
            return 0;
        }
        final long nowTimestamp = System.currentTimeMillis();
        Set<K> keySet = this.innerMap.keySet().stream().filter(key -> this.isTimeOut(key, nowTimestamp)).collect(Collectors.toSet());
        keySet.forEach(this.innerMap::remove);
        return keySet.size();
    }

    /**
     * 清理已过期的key
     *
     * @param key key
     * @return 该key是否被清理
     */
    protected boolean clearTimeoutKey(Object key) {
        if (this.innerMap.isEmpty()) {
            return false;
        }
        if (this.isTimeOut(key, System.currentTimeMillis())) {
            this.innerMap.remove(key);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断一个key是否已过期
     *
     * @param key          key
     * @param nowTimestamp 当前时间戳(毫秒计)
     * @return 是否已过期
     */
    protected boolean isTimeOut(Object key, long nowTimestamp) {
        if (this.innerMap.isEmpty()) {
            return true;
        }
        Pair<V, Long> pair = this.innerMap.get(key);
        if (pair == null) {
            return true;
        }
        return (pair.getSecond() < nowTimestamp);
    }

    /**
     * 计算key对应的真实value
     *
     * @param key key
     * @return key对应的真实value
     */
    protected V getTrueValue(Object key) {
        if (this.innerMap.isEmpty()) {
            return null;
        }
        return clearTimeoutKey(key) ? null : Optional.ofNullable(this.innerMap.get(key)).map(Pair::getFirst).orElse(null);

    }

    /**
     * 用默认值初始化对象
     */
    private void initWithDefaultValue() {
        this.innerMap = new HashMap<>();
        this.timeUnit = TTLMap.DEFAULT_TIME_UNIT;
        this.ttl = TTLMap.DEFAULT_TTL;
        this.ttlInMillisecond = this.timeUnit.toMillis(ttl);
    }

    /**
     * 搬运自{@link HashMap.Node}
     *
     * @param <K>
     * @param <V>
     * @author gaokuo.dai@hand-china.com 2018-12-13 11:23:47
     */
    protected static class InnerEntry<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        InnerEntry<K, V> next;

        InnerEntry(int hash, K key, V value, InnerEntry<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        private InnerEntry(K key, V value) {
            this.hash = Objects.hash(key, value);
            this.key = key;
            this.value = value;
            this.next = null;
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final String toString() {
            return key + "=" + value;
        }

        public final boolean hasNext() {
            return next != null;
        }

        public final InnerEntry<K, V> next() {
            return next;
        }

        @Override
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                return Objects.equals(key, e.getKey()) &&
                        Objects.equals(value, e.getValue());
            }
            return false;
        }
    }

    public static class Builder<K, V> {
        /**
         * 内部Map集合类型
         */
        @SuppressWarnings("rawtypes")
        private Class<? extends Map> innerMapClass;
        /**
         * key超时时间单位
         */
        protected TimeUnit timeUnit;
        /**
         * key超时时间单位数
         */
        protected long ttl;

        private final Logger logger = LoggerFactory.getLogger(Builder.class);

        public Builder() {
            this.innerMapClass = TTLMap.DEFAULT_INNER_MAP_CLASS;
            this.timeUnit = TTLMap.DEFAULT_TIME_UNIT;
            this.ttl = TTLMap.DEFAULT_TTL;
        }

        /**
         * 内部Map集合类型<p></p>
         * 默认HashMap
         *
         * @param innerMapClass
         * @return
         */
        public Builder<K, V> innerMapClass(@SuppressWarnings("rawtypes") Class<? extends Map> innerMapClass) {
            Assert.notNull(innerMapClass, BaseConstants.ErrorCode.NOT_NULL);
            this.innerMapClass = innerMapClass;
            return this;
        }

        /**
         * key超时时间单位<p></p>
         * 默认分钟
         *
         * @param timeUnit
         * @return
         */
        public Builder<K, V> timeUnit(TimeUnit timeUnit) {
            Assert.notNull(timeUnit, BaseConstants.ErrorCode.NOT_NULL);
            this.timeUnit = timeUnit;
            return this;
        }

        /**
         * key超时时间单位数<p></p>
         * 默认10
         *
         * @param ttl
         * @return
         */
        public Builder<K, V> ttl(long ttl) {
            Assert.isTrue(ttl >= 0, BaseConstants.ErrorCode.DATA_INVALID);
            this.ttl = ttl;
            return this;
        }

        /**
         * 构建
         *
         * @return TTLMap
         */
        @SuppressWarnings("unchecked")
        public TTLMap<K, V> build() {
            TTLMap<K, V> ttlMap = new TTLMap<>(true);
            try {
                ttlMap.innerMap = this.innerMapClass.newInstance();
                ttlMap.timeUnit = this.timeUnit;
                ttlMap.ttl = this.ttl;
                ttlMap.ttlInMillisecond = ttlMap.timeUnit.toMillis(ttlMap.ttl);
            } catch (InstantiationException | IllegalAccessException e) {
                this.logger.error("can not build TTLMap with class {}, fallback to default builder", this.innerMapClass.getName());
                this.logger.error(e.getMessage(), e);
                ttlMap = new TTLMap<>();
            }
            return ttlMap;
        }
    }

}
