package org.hzero.oauth.infra.encrypt;

import javax.annotation.Nonnull;

import io.choerodon.core.exception.CommonException;

/**
 * 加密资源库
 *
 * @author bojiangzhou 2019/12/05
 */
public interface EncryptRepository {

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
