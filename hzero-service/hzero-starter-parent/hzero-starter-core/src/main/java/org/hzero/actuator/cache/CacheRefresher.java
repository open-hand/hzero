package org.hzero.actuator.cache;

/**
 * 缓存刷新器
 *
 * @author bojiangzhou
 */
public interface CacheRefresher {

    /**
     * 刷新服务缓存
     */
    void refreshCache();
}
