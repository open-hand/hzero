package org.hzero.starter.keyencrypt.util;

import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Objects;

public class EncryptUtils {
    public static boolean ignoreValue(Encrypt encrypt, String value) {
        return encrypt != null && contain(encrypt.ignoreValue(), value);
    }

    private static boolean contain(String[] array, String value) {
        for (String s : array) {
            if (Objects.equals(s, value)) {
                return true;
            }
        }
        return false;
    }
}
