package org.hzero.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.hzero.core.exception.ContentNotFoundSaltException;
import org.hzero.core.exception.EncryptionException;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 加解密工具类
 * </p>
 *
 * @author qingsheng.chen 2018/9/13 星期四 11:56
 */
@SuppressWarnings("all")
public class EncryptionUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private EncryptionUtils() {
    }

    /**
     * MD5 非对称加密
     */
    public static class MD5 {
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
                throw new EncryptionException("MD5 encrypt failed!", e);
            }
        }
    }

    /**
     * AES 对称加密
     */
    public static class AES {
        private static final String ALGORITHM = "AES";

        /**
         * 生成秘钥
         */
        public static String generaterKey() {
            KeyGenerator keygen = null;
            try {
                keygen = KeyGenerator.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new EncryptionException("AES generater Key failed!", e);
            }
            // 16 字节 == 128 bit
            keygen.init(128, new SecureRandom());
            SecretKey secretKey = keygen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }

        /**
         * 生成秘钥
         */
        public static String generaterKey(int keySize) {
            KeyGenerator keygen = null;
            try {
                keygen = KeyGenerator.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new EncryptionException("AES generater Key failed!", e);
            }
            keygen.init(keySize, new SecureRandom());
            SecretKey secretKey = keygen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }

        /**
         * 生成密钥
         */
        private static SecretKeySpec getSecretKeySpec(String secretKeyStr) {
            return new SecretKeySpec(Base64.getDecoder().decode(secretKeyStr), ALGORITHM);
        }

        /**
         * 加密
         */
        public static String encrypt(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);
            try {
                // 创建密码器
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                // 初始化
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(DEFAULT_CHARSET)));
            } catch (Exception e) {
                throw new EncryptionException("AES encrypt failed!", e);
            }
        }

        public static String encryptWithUrlEncoder(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);
            try {
                // 创建密码器
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                // 初始化
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return Base64.getUrlEncoder().encodeToString(cipher.doFinal(content.getBytes(DEFAULT_CHARSET)));
            } catch (Exception e) {
                throw new EncryptionException("AES encrypt failed!", e);
            }
        }

        /**
         * 解密
         */
        public static String decrypt(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, key);
                return new String(cipher.doFinal(Base64.getDecoder().decode(content)), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new EncryptionException("AES decrypt failed!", e);
            }
        }

        /**
         * 解密
         */
        public static String decryptWithUrlDecoder(String content, String secretKey) {
            Key key = getSecretKeySpec(secretKey);
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, key);
                return new String(cipher.doFinal(Base64.getUrlDecoder().decode(content.getBytes(DEFAULT_CHARSET))), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new EncryptionException("AES decrypt failed!", e);
            }
        }
    }

    @FunctionalInterface
    interface WithSalt {
        /**
         * 加盐
         *
         * @param content 加密内容
         * @param salt    盐
         * @return 加盐密文
         */
        String withSalt(String content, String salt);
    }

    private static final WithSalt DEFAULT_WITH_SALT = new WithSalt() {
        @Override
        public String withSalt(String content, String salt) {
            return content + salt;
        }
    };

    @FunctionalInterface
    interface WithoutSalt {
        /**
         * 加盐
         *
         * @param content 加密内容
         * @param salt    盐
         * @return 加盐密文
         */
        String withoutSalt(String content, String salt);
    }

    private static final WithoutSalt DEFAULT_WITHOUT_SALT = new WithoutSalt() {
        @Override
        public String withoutSalt(String content, String salt) {
            if (!StringUtils.hasText(content)) {
                return content;
            }
            if (content.endsWith(salt)) {
                return content.substring(0, salt.length());
            }
            throw new ContentNotFoundSaltException(content, salt);
        }
    };

    /**
     * RSA 对称加密
     */
    public static class RSA {
        private static final String ALGORITHM = "RSA";
        private static final String ALGORITHMS_SHA1 = "SHA1WithRSA";

        /**
         * 生成秘钥对
         * throw {@link NoSuchAlgorithmException} 找不到算法异常
         *
         * @return first : 私钥/second : 公钥
         */
        public static Pair<String, String> generateKeyPair() {
            KeyPairGenerator keygen = null;
            try {
                keygen = KeyPairGenerator.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new EncryptionException("RSA generate Key Pair failed!", e);
            }
            keygen.initialize(512, new SecureRandom());
            // 生成密钥对
            KeyPair keyPair = keygen.generateKeyPair();
            return Pair.of(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                    Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        }

        /**
         * 生成秘钥对
         *
         * @param keySize 密钥大小
         *                throw {@link NoSuchAlgorithmException} 找不到算法异常
         * @return first : 私钥/second : 公钥
         */
        public static Pair<String, String> generateKeyPair(int keySize) {
            KeyPairGenerator keygen = null;
            try {
                keygen = KeyPairGenerator.getInstance(ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new EncryptionException("RSA generate Key Pair failed!", e);
            }
            keygen.initialize(keySize, new SecureRandom());
            // 生成密钥对
            KeyPair keyPair = keygen.generateKeyPair();
            return Pair.of(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()),
                    Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        }

        /**
         * 获取公钥
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link NoSuchAlgorithmException} 找不到算法异常
         *
         * @param publicKey 公钥
         * @return 公钥
         */
        public static RSAPublicKey getPublicKey(String publicKey) {
            try {
                return (RSAPublicKey) KeyFactory.getInstance(ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));
            } catch (Exception e) {
                throw new EncryptionException("RSA get Public Key failed!", e);
            }
        }

        /**
         * 获取私钥
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link NoSuchAlgorithmException} 找不到算法异常
         *
         * @param privateKey 私钥
         * @return 私钥
         */
        public static RSAPrivateKey getPrivateKey(String privateKey) {
            try {
                return (RSAPrivateKey) KeyFactory.getInstance(ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
            } catch (Exception e) {
                throw new EncryptionException("RSA get Private Key failed!", e);
            }
        }

        /**
         * 私钥签名内容
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link NoSuchAlgorithmException} 无效的 Key
         * throw {@link UnsupportedEncodingException} 不支持的编码
         * throw {@link SignatureException} 签名异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         *
         * @param content    内容
         * @param privateKey 私钥
         * @return 私钥签名
         */
        public static String sign(String content, String privateKey) {
            try {
                Signature signature = Signature.getInstance(ALGORITHMS_SHA1);
                signature.initSign(getPrivateKey(privateKey));
                signature.update(content.getBytes(DEFAULT_CHARSET));
                return Base64.getEncoder().encodeToString(signature.sign());
            } catch (Exception e) {
                throw new EncryptionException("RSA sign failed!", e);
            }
        }


        /**
         * 公钥校验签名
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link NoSuchAlgorithmException} 无效的 Key
         * throw {@link UnsupportedEncodingException} 不支持的编码
         * throw {@link SignatureException} 签名异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         *
         * @param content   内容
         * @param sign      签名
         * @param publicKey 公钥
         * @return 是否匹配
         */
        public static boolean verify(String content, String sign, String publicKey) {
            try {
                Signature signature = Signature.getInstance(ALGORITHMS_SHA1);
                signature.initVerify(getPublicKey(publicKey));
                signature.update(content.getBytes(DEFAULT_CHARSET));
                return signature.verify(Base64.getDecoder().decode(sign));
            } catch (Exception e) {
                throw new EncryptionException("RSA verify failed!", e);
            }
        }

        /**
         * 使用公钥或者私钥加密
         * <p>
         * throw {@link InvalidKeyException} 无效的 Key
         * throw {@link UnsupportedEncodingException} 不支持的解码
         * throw {@link BadPaddingException} 错误间隔异常
         * throw {@link IllegalBlockSizeException} 无效块大小异常
         * throw {@link NoSuchPaddingException} 无效的监间距异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         *
         * @param content 内容
         * @param key     公钥或者私钥
         * @return 密文
         */
        public static String encrypt(String content, Key key) {
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(DEFAULT_CHARSET)));
            } catch (Exception e) {
                throw new EncryptionException("RSA encrypt failed!", e);
            }
        }

        /**
         * 使用公钥或者私钥解密
         * <p>
         * throw {@link InvalidKeyException} 无效的 Key
         * throw {@link BadPaddingException} 错误间隔异常
         * throw {@link IllegalBlockSizeException} 无效块大小异常
         * throw {@link NoSuchPaddingException} 无效的监间距异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         *
         * @param content 内容
         * @param key     公钥或者私钥
         * @return 明文
         */
        public static String decrypt(String content, Key key) {
            try {
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, key);
                return new String(cipher.doFinal(Base64.getDecoder().decode(content)), StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new EncryptionException("RSA decrypt failed!", e);
            }
        }

        /**
         * 使用公钥加盐加密
         *
         * @param content   明文
         * @param publicKey 公钥
         * @return 密文
         */
        public static String encrypt(String content, String publicKey) {
            return RSA.encrypt(content, null, publicKey, null);
        }

        /**
         * 使用公钥加盐加密
         *
         * @param content   明文
         * @param key       盐
         * @param publicKey 公钥
         * @return 密文
         */
        public static String encrypt(String content, String key, String publicKey) {
            return RSA.encrypt(content, key, publicKey, DEFAULT_WITH_SALT);
        }

        /**
         * 使用公钥加盐加密
         *
         * @param content   明文
         * @param key       盐
         * @param publicKey 公钥
         * @param withSalt  明文加盐
         * @return 密文
         */
        public static String encrypt(String content, String key, String publicKey, WithSalt withSalt) {
            return RSA.encrypt(withSalt != null ? withSalt.withSalt(content, key) : content, getPublicKey(publicKey));
        }

        /**
         * 使用私钥解密去盐
         *
         * @param content    密文
         * @param privateKey 私钥
         * @return 明文
         */
        public static String decrypt(String content, String privateKey) {
            return decrypt(content, null, privateKey, null);
        }

        /**
         * 使用私钥解密去盐
         *
         * @param content    密文
         * @param key        盐
         * @param privateKey 私钥
         * @return 明文
         */
        public static String decrypt(String content, String key, String privateKey) {
            return decrypt(content, key, privateKey, DEFAULT_WITHOUT_SALT);
        }

        /**
         * 使用私钥解密去盐
         *
         * @param content     密文
         * @param key         盐
         * @param privateKey  私钥
         * @param withoutSalt 解密内容去盐
         * @return 明文
         */
        public static String decrypt(String content, String key, String privateKey, WithoutSalt withoutSalt) {
            return withoutSalt != null
                    ? withoutSalt.withoutSalt(decrypt(content, getPrivateKey(privateKey)), key)
                    : decrypt(content, getPrivateKey(privateKey));
        }
    }

    /**
     * RSA2 对称加密
     */
    public static class RSA2 {
        private static final String ALGORITHMS_SHA256 = "SHA256WithRSA";

        /**
         * 生成秘钥对
         * <p>
         * throw {@link NoSuchAlgorithmException} 找不到算法异常
         *
         * @return first : 私钥/second : 公钥
         */
        public static Pair<String, String> generateKeyPair() {
            return RSA.generateKeyPair();
        }

        /**
         * 获取公钥
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link NoSuchAlgorithmException} 找不到算法异常
         *
         * @param publicKey 公钥
         * @return 公钥
         */
        private static RSAPublicKey getPublicKey(String publicKey) {
            return RSA.getPublicKey(publicKey);
        }

        /**
         * 获取私钥
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link NoSuchAlgorithmException} 找不到算法异常
         *
         * @param privateKey 私钥
         * @return 私钥
         */
        private static RSAPrivateKey getPrivateKey(String privateKey) {
            return RSA.getPrivateKey(privateKey);
        }

        /**
         * 私钥签名内容
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link InvalidKeyException} 无效的 Key
         * throw {@link UnsupportedEncodingException} 不支持的编码
         * throw {@link SignatureException} 签名异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         *
         * @param content    内容
         * @param privateKey 私钥
         * @return 私钥签名
         */
        public static String sign(String content, String privateKey) {
            try {
                Signature signature = Signature.getInstance(ALGORITHMS_SHA256);
                signature.initSign(getPrivateKey(privateKey));
                signature.update(content.getBytes(DEFAULT_CHARSET));
                return Base64.getEncoder().encodeToString(signature.sign());
            } catch (Exception e) {
                throw new EncryptionException("RSA2 sign failed!", e);
            }
        }


        /**
         * 公钥校验签名
         * <p>
         * throw {@link InvalidKeySpecException} 无效的 Key Spec
         * throw {@link InvalidKeyException} 无效的 Key
         * throw {@link UnsupportedEncodingException} 不支持的编码
         * throw {@link SignatureException} 签名异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         * throw {@link NoSuchPaddingException} 无效的监间距异常
         *
         * @param content   内容
         * @param sign      签名
         * @param publicKey 公钥
         * @return 是否匹配
         */
        public static boolean verify(String content, String sign, String publicKey) {
            try {
                Signature signature = Signature.getInstance(ALGORITHMS_SHA256);
                signature.initVerify(getPublicKey(publicKey));
                signature.update(content.getBytes(DEFAULT_CHARSET));
                return signature.verify(Base64.getDecoder().decode(sign));
            } catch (Exception e) {
                throw new EncryptionException("RSA2 verify failed!", e);
            }
        }

        /**
         * 使用公钥或者私钥加密
         * <p>
         * throw {@link IllegalBlockSizeException} 无效块大小异常
         * throw {@link InvalidKeyException} 无效的 Key
         * throw {@link UnsupportedEncodingException} 不支持的解码
         * throw {@link BadPaddingException} 错误间隔异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         * throw {@link NoSuchPaddingException} 无效的监间距异常
         *
         * @param content 内容
         * @param key     公钥或者私钥
         * @return 密文
         */
        public static String encrypt(String content, Key key) {
            return RSA.encrypt(content, key);
        }

        /**
         * 使用公钥或者私钥解密
         * <p>
         * throw {@link IllegalBlockSizeException} 无效块大小异常
         * throw {@link InvalidKeyException} 无效的 Key
         * throw {@link BadPaddingException} 错误间隔异常
         * throw {@link NoSuchAlgorithmException} 无效的算法
         * throw {@link NoSuchPaddingException} 无效的监间距异常
         *
         * @param content 内容
         * @param key     公钥或者私钥
         * @return 明文
         */
        public static String decrypt(String content, Key key) {
            return RSA.decrypt(content, key);
        }
    }

    /**
     * 异或加密
     *
     * @since 1.4.0.RELEASE
     */
    public static class XOR {
        private static final long MIN_SIZE = 0x1000000000000000L;
        private static final long MAX_SIZE = 0x7fffffffffffffffL;

        public static long generateKey() {
            SecureRandom random = new SecureRandom();
            double d = random.nextDouble();
            return ((long) (d * (MAX_SIZE - MIN_SIZE + 1))) + MIN_SIZE;
        }

        public static String encrypt(long key, long content) {
            String contentBinary = Long.toBinaryString(content);
            String keyBinary = Long.toBinaryString(key);
            long subKey = Long.parseLong(contentBinary.length() < keyBinary.length()
                    ? keyBinary.substring(0, contentBinary.length())
                    : keyBinary, 2);
            return String.valueOf(contentBinary.length() < 10 ? contentBinary.length() + 64 : contentBinary.length()) + (content ^ subKey);
        }

        public static long decrypt(long key, String encrypt) {
            int keySize = Integer.parseInt(encrypt.substring(0, 2)) % 64;
            String keyBinary = Long.toBinaryString(key);
            long subKey = Long.parseLong(keySize < keyBinary.length()
                    ? keyBinary.substring(0, keySize)
                    : keyBinary, 2);
            return Long.parseLong(encrypt.substring(2)) ^ subKey;
        }

    }
}
