package org.hzero.oauth.infra.encrypt.impl;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.hzero.core.util.EncryptionUtils;
import org.hzero.oauth.infra.encrypt.EncryptClient;
import org.hzero.oauth.infra.encrypt.EncryptRepository;

/**
 * 加密工具客户端
 *
 * @author bojiangzhou 2019/12/05
 */
@Component
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

    @Nonnull
    @Override
    public String encrypt(@NotEmpty String content) {
        try {
            return EncryptionUtils.RSA.encrypt(content, EncryptionUtils.RSA.getPublicKey(getPublicKey()));
        } catch (Exception e) {
            LOGGER.warn("encrypt content error, will return content directly! ex={}, content={}", e.getMessage(), content);
            return content;
        }
    }

    @Nonnull
    @Override
    public String decrypt(@NotEmpty String content) {
        try {
            return EncryptionUtils.RSA.decrypt(content, EncryptionUtils.RSA.getPrivateKey(getPrivateKey()));
        } catch (Exception e) {
            LOGGER.warn("decrypt content error, will return content directly! ex={}, content={}", e.getMessage(), content);
            return content;
        }
    }
}
