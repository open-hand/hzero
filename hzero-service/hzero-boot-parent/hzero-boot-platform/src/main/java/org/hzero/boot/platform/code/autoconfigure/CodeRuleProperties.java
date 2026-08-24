package org.hzero.boot.platform.code.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hzero.code-rule")
public class CodeRuleProperties {
    private long encryptKey = 6618784731978442149L;

    public long getEncryptKey() {
        return encryptKey;
    }

    public CodeRuleProperties setEncryptKey(long encryptKey) {
        this.encryptKey = encryptKey;
        return this;
    }
}
