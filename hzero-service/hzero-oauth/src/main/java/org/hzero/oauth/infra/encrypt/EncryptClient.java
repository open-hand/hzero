package org.hzero.oauth.infra.encrypt;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotEmpty;

/**
 * 加密解密客户端
 *
 * @author bojiangzhou 2019/12/05
 */
public interface EncryptClient {

    /**
     * 获取公钥
     * 
     * @return 公钥
     */
    @Nonnull
    String getPublicKey();

    /**
     * 获取私钥
     * 
     * @return 私钥
     */
    @Nonnull
    String getPrivateKey();

    /**
     * 使用公钥加密
     * 
     * @param content 待加密的内容
     * @return 加密后的内容
     */
    @Nonnull
    String encrypt(@NotEmpty String content);

    /**
     * 使用私钥解密
     * 
     * @param content 待解密的内容
     * @return 解密后的内容
     */
    @Nonnull
    String decrypt(@NotEmpty String content);

}
