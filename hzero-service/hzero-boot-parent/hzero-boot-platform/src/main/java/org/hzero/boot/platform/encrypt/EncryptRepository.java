package org.hzero.boot.platform.encrypt;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;

import io.choerodon.core.exception.CommonException;

/**
 * 加密资源库
 *
 * @author bojiangzhou 2019/12/05
 */
public interface EncryptRepository {

    /**
     * 保存公钥信息，默认存储到 redis
     * 
     * @param publicKey 公钥
     */
    void savePublicKey(@NotEmpty String publicKey);

    /**
     * 保存私钥信息，默认存储到 redis
     *
     * @param privateKey 公钥
     */
    void savePrivateKey(@NotEmpty String privateKey);

    /**
     * 获取加密的公钥
     * 
     * @return 公钥
     * @throws CommonException 如果缓存中没有公钥则抛出异常
     */
    @Nonnull
    String getPublicKey();

    /**
     * 获取加密的私钥
     * 
     * @return 私钥
     * @throws CommonException 如果缓存中没有私钥则抛出异常
     */
    @Nonnull
    String getPrivateKey();

}
