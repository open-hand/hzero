package org.hzero.platform.app.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.boot.platform.encrypt.EncryptRepository;
import org.hzero.boot.platform.encrypt.vo.EncryptVO;
import org.hzero.platform.app.service.ToolPassService;
import org.hzero.platform.infra.properties.PlatformProperties;
import org.springframework.stereotype.Service;

/**
 * 工具 service 实现类
 *
 * @author xiaoyu.zhao@hand-china.com 2020/05/13 14:22
 */
@Service
public class ToolPassServiceImpl implements ToolPassService {

    public static final String FIELD_ENCRYPT_PASS = "encryptPass";
    private final PlatformProperties platformProperties;
    private final EncryptRepository encryptRepository;
    private final EncryptClient encryptClient;

    public ToolPassServiceImpl(PlatformProperties platformProperties, EncryptRepository encryptRepository,
            EncryptClient encryptClient) {
        this.platformProperties = platformProperties;
        this.encryptRepository = encryptRepository;
        this.encryptClient = encryptClient;
    }

    @Override
    public EncryptVO getPublicKey() {
        if (StringUtils.isBlank(encryptClient.getPublicKey())) {
            PlatformProperties.Encrypt encrypt = platformProperties.getEncrypt();
            encryptRepository.savePrivateKey(encrypt.getPrivateKey());
            encryptRepository.savePublicKey(encrypt.getPublicKey());
        }
        return new EncryptVO().setPublicKey(encryptClient.getPublicKey());
    }

    @Override
    public Map<String, String> encryptPass(String pass) {
        Map<String, String> map = new HashMap<>(1);
        map.put(FIELD_ENCRYPT_PASS, encryptClient.encrypt(pass));
        return map;
    }
}
