package org.hzero.register.event.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * MD5 非对称加密
 * @author XCXCXCXCX
 */
public class MD5Util {
    /**
     * MD5 加密
     *
     * @param content 加密内容
     * @return 加密结果
     */
    public static String encrypt(String content) {
        return DigestUtils.md5Hex(content);
    }

    /**
     * MD5 加密
     *
     * @param content 加密内容
     * @return 加密结果
     */
    public static String encrypt(byte[] content) {
        return DigestUtils.md5Hex(content);
    }

    /**
     * MD5 加密
     *
     * @param contentStream 加密内容
     * @return 加密结果
     */
    public static String encrypt(InputStream contentStream) {
        try {
            return DigestUtils.md5Hex(contentStream);
        } catch (IOException e) {
            throw new RuntimeException("MD5 encrypt failed!", e);
        }
    }
}
