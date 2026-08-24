package org.hzero.mybatis.service;

/**
 * @author 废柴 2020/9/2 10:21
 */
public interface DataSecurityKeyService {

    /**
     * 存储密钥
     *
     * @param securityKey 密钥
     * @param shared      是否服务间共享
     */
    void storeSecurityKey(String securityKey, boolean shared);

    /**
     * 获取密钥
     *
     * @return 密钥
     */
    String readSecurityKey();
}
