package org.hzero.platform.infra.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.hzero.actuator.cache.CacheRefresher;

/**
 * 缓存刷新器
 *
 * @author bojiangzhou
 */
@Component
public class HpfmCacheRefresher implements CacheRefresher {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HpfmDataInit hpfmDataInit;

    public HpfmCacheRefresher(HpfmDataInit hpfmDataInit) {
        this.hpfmDataInit = hpfmDataInit;
    }

    @Override
    public void refreshCache() {
        logger.info("Refresh service cache by CacheRefresher");
        hpfmDataInit.initCacheData(true);
    }
}
