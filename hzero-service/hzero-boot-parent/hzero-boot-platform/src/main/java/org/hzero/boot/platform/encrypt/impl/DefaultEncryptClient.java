package org.hzero.boot.platform.encrypt.impl;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.boot.platform.encrypt.EncryptRepository;
import org.hzero.core.util.EncryptionUtils;

/**
 * 加密工具客户端
 *
 * @author bojiangzhou 2019/12/05
 */
public class DefaultEncryptClient implements EncryptClient {

    private final EncryptRepository encryptRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultEncryptClient.class);

    public DefaultEncryptClient(EncryptRepository encryptRepository) {
        this.encryptRepository = encryptRepository;
    }

    @Nonnull
    @Override
    public String getPublicKey() {
        return encryptRepository.getPublicKey();
    }

    @Nonnull
    @Override
    public String getPrivateKey() {
        return encryptRepository.getPrivateKey();
    }

    @Override
    public String encrypt(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        try {
            return EncryptionUtils.RSA.encrypt(content, EncryptionUtils.RSA.getPublicKey(getPublicKey()));
        } catch (Exception e) {
            LOGGER.warn("encrypt content error, will return content directly! ex={}, content={}", e.getMessage(), content);
            return content;
        }
    }

    @Override
    public String decrypt(@NotEmpty String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        try {
            return EncryptionUtils.RSA.decrypt(content, EncryptionUtils.RSA.getPrivateKey(getPrivateKey()));
        } catch (Exception e) {
            LOGGER.warn("decrypt content error, will return content directly! ex={}, content={}", e.getMessage(), content);
            return content;
        }
    }
}
