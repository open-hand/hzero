package org.hzero.gateway.helper.cache.l2;

import org.springframework.cache.Cache;

public abstract class L2Cache {

    private final Cache cache;

    public L2Cache(Cache cache) {
        this.cache = cache;
    }

    public Cache getCache() {
        return cache;
    }

}
