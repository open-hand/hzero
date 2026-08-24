package org.hzero.starter.keyencrypt.core;

import java.util.Objects;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.base.BaseConstants;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-12-03.
 */
public class EncryptContext {
    public static final String HEADER_ENCRYPT = "H-Request-Encrypt";
    private static volatile EncryptType defaultEncryptType = null;
    private static final ThreadLocal<EncryptType> encrypt = new ThreadLocal<>();

    public static EncryptType encryptType() {
        return encrypt.get() == null ? (defaultEncryptType == null ? EncryptType.DO_NOTHING : defaultEncryptType) : encrypt.get();
    }

    public static void setEncryptType(String encryptType) {
        for (EncryptType value : EncryptType.values()) {
            if (value.name().equalsIgnoreCase(encryptType)) {
                encrypt.set(value);
                break;
            }
        }
    }

    public static boolean isAllowedEncrypt() {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        return userDetails == null
                || userDetails.getApiEncryptFlag() == null
                || BaseConstants.Flag.YES.equals(userDetails.getApiEncryptFlag());
    }

    public static void clear() {
        encrypt.remove();
    }

    public static boolean isEncrypt() {
        return Objects.equals(encrypt.get(), EncryptType.ENCRYPT) && isAllowedEncrypt();
    }

    public static EncryptType getDefaultEncryptType() {
        return defaultEncryptType;
    }

    public static void setDefaultEncryptType(EncryptType defaultEncryptType) {
        EncryptContext.defaultEncryptType = defaultEncryptType;
    }
}
