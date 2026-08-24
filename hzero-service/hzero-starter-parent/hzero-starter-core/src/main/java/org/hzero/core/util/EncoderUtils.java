package org.hzero.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.Charsets;

/**
 * Encoder
 *
 * @author bojiangzhou 2018/07/27
 */
public class EncoderUtils {
    
    private EncoderUtils() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    /**
     * 对文件名进行编码 过滤特殊字符
     * 
     * @param filename 文件名称
     * @return 编码后的文件名称
     */
    public static String encodeFilename(String filename) {
        String encodeFilename = null;
        try {
            encodeFilename = URLEncoder.encode(filename, Charsets.UTF_8.displayName())
                    .replaceAll("\\+", "%20")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%3B", ";")
                    .replaceAll("%40", "@")
                    .replaceAll("%23", "#")
                    .replaceAll("%26", "&")
                    .replaceAll("%2C", ",");
        } catch (UnsupportedEncodingException e) {
            return filename;
        }
        return encodeFilename;
    }

}
