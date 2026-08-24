package org.hzero.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 文件服务工具类
 * </p>
 *
 * @author qingsheng.chen 2018/7/12 Thursday 21:19
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    /**
     * 从文件下载链接中解析文件名
     *
     * @param fileUrl 文件下载链接
     * @return 文件名
     */
    public static String getFileName(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return null;
        }
        try {
            // 第一个@之后的
            int index = fileUrl.indexOf("@");
            if (index > -1) {
                return fileUrl.substring(index + 1);
            } else {
                String[] s = fileUrl.split("/");
                if (s.length > 1) {
                    return s[s.length - 1];
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("Get filename failed.", e);
            return null;
        }
    }
}
