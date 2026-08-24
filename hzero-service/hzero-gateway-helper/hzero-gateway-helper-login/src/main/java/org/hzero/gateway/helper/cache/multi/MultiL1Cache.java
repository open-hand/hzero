package org.hzero.gateway.helper.cache.multi;

import org.hzero.gateway.helper.cache.l1.L1Cache;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class MultiL1Cache extends MultiCache {

    private final Cache l1Cache;

    public MultiL1Cache(String name, L1Cache l1Cache) {
        super(name);
        this.l1Cache = l1Cache.getCache();
    }

    @Override
    public Cache.ValueWrapper get(Object key) {
        return l1Cache.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return l1Cache.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return l1Cache.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        l1Cache.put(key, value);
    }

    @Override
    public Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        return l1Cache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {
        l1Cache.evict(key);
    }

    @Override
    public void clear() {
        l1Cache.clear();
    }
}
