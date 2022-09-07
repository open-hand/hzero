package org.hzero.wechat.util;

import java.security.MessageDigest;

public class Sha1Utils {
    public static String encodeSHA1(String str) {
        if (str == null) {
            str = "";
        }

        try{
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes("UTF-8"));
            byte[] result = md.digest();

            StringBuilder sb = new StringBuilder();

            for (byte b : result) {
                int i = b & 0xff;
                if (i < 0xf) {
                    sb.append(0);
                }
                sb.append(Integer.toHexString(i));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}