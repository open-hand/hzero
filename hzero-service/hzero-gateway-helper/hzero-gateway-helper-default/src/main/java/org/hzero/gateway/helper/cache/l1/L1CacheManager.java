package org.hzero.gateway.helper.cache.l1;

import org.springframework.cache.CacheManager;


public interface L1CacheManager extends CacheManager {

    L1Cache getL1Cache(String name, String spec);

}
