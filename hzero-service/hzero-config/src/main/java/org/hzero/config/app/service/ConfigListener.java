package org.hzero.config.app.service;

/**
 * @author XCXCXCXCX
 * @date 2020/4/30 3:12 下午
 */
public interface ConfigListener {

    boolean interest(String key);

    /**
     * 监听到配置变化
     * @param key
     * @param value
     */
    void onChange(String key, String value);

}
