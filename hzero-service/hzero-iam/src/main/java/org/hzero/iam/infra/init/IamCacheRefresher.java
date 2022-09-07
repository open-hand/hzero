package org.hzero.iam.infra.init;

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
public class IamCacheRefresher implements CacheRefresher {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final IamDataInit iamDataInit;

    public IamCacheRefresher(IamDataInit iamDataInit) {
        this.iamDataInit = iamDataInit;
    }

    @Override
    public void refreshCache() {
        logger.info("Refresh service cache by CacheRefresher");
        iamDataInit.initCacheData(true);
    }
}
