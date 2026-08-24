package org.hzero.mybatis.config;

import org.hzero.mybatis.helper.DataSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 数据放篡改配置属性
 * </p>
 *
 * @author qingsheng.chen 2018/9/17 星期一 20:10
 */
@Configuration
@ConfigurationProperties(prefix = "hzero.mybatis-mapper.data-security")
public class DataSecurityProperty {
    private static final Logger logger = LoggerFactory.getLogger(DataSecurityProperty.class);

    public enum IsolationLevel {
        ONCE,
        THREAD;

        public static IsolationLevel parser(String value) {
            if (StringUtils.hasText(value)) {
                for (IsolationLevel isolationLevel : values()) {
                    if (isolationLevel.name().equalsIgnoreCase(value)) {
                        return isolationLevel;
                    }
                }
                logger.error("Invalid data encryption and decryption isolation level : {}", value);
            }
            // default
            return IsolationLevel.ONCE;
        }
    }

    /**
     * 是否默认打开数据加解密，默认关闭，设置为 true 之后，不需要调用 {@link DataSecurityHelper#open()} 即可自动打开，<strong>但是相应的，如果需要禁用数据加解密，则需要显式调用 {@link DataSecurityHelper#close()} ()}，并且会影响性能</strong>
     */
    private boolean defaultOpen = false;
    /**
     * 隔离级别，表示设置的启用/禁用影响的范围，默认 once 表示一次查询或修改后失效(不包含 count 查询); thread 表示当前线程内
     */
    private IsolationLevel isolationLevel = IsolationLevel.ONCE;

    /**
     * 密钥
     */
    private String securityKey;

    /**
     * 是否作为密钥，默认关闭，开启后如果其他的服务没有配置密钥，会使用当前服务的密钥作为默认密钥
     */
    private boolean asDefaultKey = false;

    public boolean isDefaultOpen() {
        return defaultOpen;
    }

    public DataSecurityProperty setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
        return this;
    }

    public IsolationLevel getIsolationLevel() {
        return isolationLevel;
    }

    public DataSecurityProperty setIsolationLevel(IsolationLevel isolationLevel) {
        this.isolationLevel = isolationLevel;
        return this;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public DataSecurityProperty setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
        return this;
    }

    public boolean isAsDefaultKey() {
        return asDefaultKey;
    }

    public DataSecurityProperty setAsDefaultKey(boolean asDefaultKey) {
        this.asDefaultKey = asDefaultKey;
        return this;
    }
}
