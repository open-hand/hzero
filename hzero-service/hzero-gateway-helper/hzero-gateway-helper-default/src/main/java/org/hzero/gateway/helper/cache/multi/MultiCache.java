package org.hzero.gateway.helper.cache.multi;

import org.springframework.cache.Cache;
import org.springframework.lang.UsesJava8;

@UsesJava8
public abstract class MultiCache implements Cache {

    private final String name;

    public MultiCache(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final Object getNativeCache() {
        return this;
    }

}
