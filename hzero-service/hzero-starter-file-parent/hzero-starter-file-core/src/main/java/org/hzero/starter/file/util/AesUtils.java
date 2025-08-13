package org.hzero.starter.file.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.hzero.core.base.BaseConstants;

import io.choerodon.core.exception.CommonException;

/**
 * AES 加密解密工具类
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/08 16:18
 */
public class AesUtils {

    private AesUtils() {
    }

    private static final String DEFAULT_PWD = "RGarqXE1wpAnW6V5hQs0Lg==";
    private static final String ALGORITHM = "AES";

    /**
     * 生成加密密钥
     */
    private static SecretKeySpec getKey(String password) {
        KeyGenerator kg;
        try {
            kg = KeyGenerator.getInstance(ALGORITHM);
            // AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(password.getBytes(StandardCharsets.UTF_8)));
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] data) {
        return encrypt(data, DEFAULT_PWD);
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] data, String password) {
        try {
            SecretKeySpec key = getKey(password);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] data) {
        return decrypt(data, DEFAULT_PWD);
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] data, String password) {
        try {
            SecretKeySpec key = getKey(password);
            byte[] raw = key.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }
}
