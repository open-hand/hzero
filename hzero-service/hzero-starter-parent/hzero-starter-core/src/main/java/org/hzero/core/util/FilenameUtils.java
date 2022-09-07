package org.hzero.core.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 文件名的浏览器兼容处理
 *
 * @author shuangfei.zhu@hand-china.com 2019/11/01 11:23
 */
public class FilenameUtils {

    private FilenameUtils() {
    }

    /**
     * 针对浏览器的文件名转译
     *
     * @param request  request
     * @param filename 原文件名
     * @return 处理后的文件名
     * @throws IOException IOException
     */
    public static String encodeFileName(HttpServletRequest request, String filename) throws IOException {
        // 替换文件名中可能存在的行结束符
        filename = filename.replace(" ", "");
        filename = filename.replace("\n", "");
        filename = filename.replace("\r", "");
        if (request == null) {
            return URLEncoder.encode(filename, BaseConstants.DEFAULT_CHARSET);
        }
        String encodeFilename;
        String userAgent = request.getHeader("User-Agent");
        if (StringUtils.isBlank(userAgent)) {
            encodeFilename = URLEncoder.encode(filename, BaseConstants.DEFAULT_CHARSET);
        } else {
            if (userAgent.contains("MSIE") || userAgent.contains("like Gecko")) {
                // IE使用URLEncoder
                encodeFilename = URLEncoder.encode(filename, BaseConstants.DEFAULT_CHARSET);
            } else {
                // FireFox使用
                encodeFilename = "=?UTF-8?B?" + Base64.getEncoder()
                        .encodeToString(filename.getBytes(StandardCharsets.UTF_8)) + "?=";
            }
        }
        return encodeFilename;
    }

    /**
     * 针对浏览器的文件名转译
     *
     * @param filename 原文件名
     * @return 处理后的文件名
     * @throws IOException IOException
     */
    public static String encodeFileName(String filename) throws IOException {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            // request为null, 默认处理
            return URLEncoder.encode(filename, BaseConstants.DEFAULT_CHARSET);
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return encodeFileName(request, filename);
    }

    /**
     * 从文件链接中解析文件名
     *
     * @param fileUrl 文件url或者文件key
     * @return 文件名
     */
    public static String getFileName(String fileUrl) {
        return FileUtils.getFileName(fileUrl);
    }
}
