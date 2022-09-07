package org.hzero.starter.keyencrypt.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加密配置属性
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/12 17:07
 */
@ConfigurationProperties(prefix = EncryptProperties.PREFIX)
public class EncryptProperties {
    static final String PREFIX = "hzero.key-encrypt";

    private String secretKey = "A5/vRTuHzZshct28bZSAiw==";
    private String cipherFormat = "=%s=";


    public String getSecretKey() {
        return secretKey;
    }

    public EncryptProperties setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getCipherFormat() {
        return cipherFormat;
    }

    public EncryptProperties setCipherFormat(String cipherFormat) {
        this.cipherFormat = cipherFormat;
        return this;
    }
}
