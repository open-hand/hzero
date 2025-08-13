package org.hzero.mybatis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 数据放篡改配置属性
 * </p>
 *
 * @author qingsheng.chen 2018/9/17 星期一 20:10
 */
@Configuration
@ConfigurationProperties(prefix = "hzero.mybatis-mapper")
public class SecurityTokenProperty {
    private String securityKey = "UFvAgyz2bmPMeBzo3nPG/A==";

    public String getSecurityKey() {
        return securityKey;
    }

    public SecurityTokenProperty setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
        return this;
    }
}
