package org.hzero.gateway.helper.cache.multi;

import org.hzero.gateway.helper.cache.l1.L1Cache;
import org.hzero.gateway.helper.cache.l2.L2Cache;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * 拆分MultiCache为MultiAllCache、MultiL1Cache、MultiL1Cache.避免过多if判断
 * MultiAllCache：一级缓存和二级缓存都存在的情况
 */
public class MultiAllCache extends MultiCache {

    private final Cache l1Cache;
    private final Cache l2Cache;

    public MultiAllCache(String name, L1Cache l1Cache, L2Cache l2Cache) {
        super(name);
        this.l1Cache = l1Cache.getCache();
        this.l2Cache = l2Cache.getCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = l1Cache.get(key);
        if (wrapper != null) {
            return wrapper;
        }
        wrapper = l2Cache.get(key);
        if (wrapper != null) {
            l1Cache.putIfAbsent(key, wrapper.get());
            return wrapper;
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = l1Cache.get(key, type);
        if (value != null) {
            return value;
        }
        value = l2Cache.get(key, type);
        if (value != null) {
            l1Cache.putIfAbsent(key, value);
            return value;
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = l1Cache.get(key, valueLoader);
        if (value != null) {
            value = l2Cache.get(key, valueLoader);
            if (value != null) {
                l1Cache.putIfAbsent(key, value);
                return value;
            }
        }
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        l1Cache.putIfAbsent(key, value);
        return l2Cache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        l2Cache.evict(key);
        l1Cache.evict(key);
    }

    @Override
    public void clear() {
        l2Cache.clear();
        l1Cache.clear();
    }
}
