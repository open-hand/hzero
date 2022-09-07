package org.hzero.starter.call.util;

import java.security.MessageDigest;

/**
 * @author zhangwenling1
 * @date 2017/4/24 15:42
 */
public class Md5Utils {

    public static String md5(String str) {
        String re = null;
        byte encrypt[];
        try {
            byte[] tem = str.getBytes("utf-8");
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(tem);
            encrypt = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte t : encrypt) {
                String s = Integer.toHexString(t & 0xFF);
                if (s.length() == 1) {
                    s = "0" + s;
                }
                sb.append(s);
            }
            re = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return re;
    }
}
