package org.hzero.platform.app.service;

import java.util.Map;

import org.hzero.boot.platform.encrypt.vo.EncryptVO;

/**
 * 工具 service
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/13 14:22
 */
public interface ToolPassService {
    /**
     * 获取前端加密公钥
     *
     * @return EncryptVO
     */
    EncryptVO getPublicKey();

    /**
     * 解密密码
     *
     * @param pass 需要解密的密码
     * @return 解密结果
     */
    Map<String, String> encryptPass(String pass);
}
