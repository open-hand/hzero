package org.hzero.file.infra.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.http.MediaType;

/**
 * 获取文件contentType
 *
 * @author shuangfei.zhu@hand-china.com 2020/05/26 14:02
 */
public class ContentTypeUtils {

    private ContentTypeUtils() {
    }

    private static final Map<String, String> CONTENT_TYPE = new HashMap<>(16);

    static {
        CONTENT_TYPE.put("asp", "text/asp");
        CONTENT_TYPE.put("asx", "video/x-ms-asf");
        CONTENT_TYPE.put("avi", "video/avi");
        CONTENT_TYPE.put("awf", "application/vnd");
        CONTENT_TYPE.put("bmp", "application/x-bmp");
        CONTENT_TYPE.put("class", "java/*");
        CONTENT_TYPE.put("css", "text/css");
        CONTENT_TYPE.put("dbm", "application/x-dbm");
        CONTENT_TYPE.put("dbx", "application/x-dbx");
        CONTENT_TYPE.put("dcx", "application/x-dcx");
        CONTENT_TYPE.put("doc", "application/msword");
        CONTENT_TYPE.put("dot", "application/msword");
        CONTENT_TYPE.put("dtd", "text/xml");
        CONTENT_TYPE.put("dwf", "application/x-dwf");
        CONTENT_TYPE.put("dxf", "application/x-dxf");
        CONTENT_TYPE.put("epi", "application/x-epi");
        CONTENT_TYPE.put("eps", "application/x-ps");
        CONTENT_TYPE.put("exe", "application/x-msdownload");
        CONTENT_TYPE.put("fax", "image/fax");
        CONTENT_TYPE.put("fdf", "application/vnd");
        CONTENT_TYPE.put("gif", "image/gif");
        CONTENT_TYPE.put("htm", "text/html");
        CONTENT_TYPE.put("html", "text/html");
        CONTENT_TYPE.put("htt", "text/webviewhtml");
        CONTENT_TYPE.put("ico", "image/x-icon");
        CONTENT_TYPE.put("iff", "application/x-iff");
        CONTENT_TYPE.put("img", "application/x-img");
        CONTENT_TYPE.put("java", "java/*");
        CONTENT_TYPE.put("jpe", "image/jpeg");
        CONTENT_TYPE.put("jpeg", "image/jpeg");
        CONTENT_TYPE.put("jpg", "image/jpeg");
        CONTENT_TYPE.put("js", "application/x-javascript");
        CONTENT_TYPE.put("jsp", "text/html");
        CONTENT_TYPE.put("m1v", "video/x-mpeg");
        CONTENT_TYPE.put("m2v", "video/x-mpeg");
        CONTENT_TYPE.put("m3u", "audio/mpegurl");
        CONTENT_TYPE.put("m4e", "video/mpeg4");
        CONTENT_TYPE.put("mac", "application/x-mac");
        CONTENT_TYPE.put("mp1", "audio/mp1");
        CONTENT_TYPE.put("mp2", "audio/mp2");
        CONTENT_TYPE.put("mp2v", "video/mpeg");
        CONTENT_TYPE.put("mp3", "audio/mp3");
        CONTENT_TYPE.put("mp4", "video/mpeg4");
        CONTENT_TYPE.put("mpv", "video/mpg");
        CONTENT_TYPE.put("mpv2", "video/mpeg");
        CONTENT_TYPE.put("odc", "text/x-ms-odc");
        CONTENT_TYPE.put("out", "application/x-out");
        CONTENT_TYPE.put("pdf", "application/pdf");
        CONTENT_TYPE.put("pdx", "application/vnd");
        CONTENT_TYPE.put("png", "image/png");
        CONTENT_TYPE.put("ppa", "application/vnd");
        CONTENT_TYPE.put("pps", "application/vnd");
        CONTENT_TYPE.put("ppt", "application/x-ppt");
        CONTENT_TYPE.put("ps", "application/x-ps");
        CONTENT_TYPE.put("rmvb", "application/vnd");
        CONTENT_TYPE.put("rp", "image/vnd");
        CONTENT_TYPE.put("rtf", "application/x-rtf");
        CONTENT_TYPE.put("rv", "video/vnd");
        CONTENT_TYPE.put("svg", "text/xml");
        CONTENT_TYPE.put("tif", "image/tiff");
        CONTENT_TYPE.put("tiff", "image/tiff");
        CONTENT_TYPE.put("top", "drawing/x-top");
        CONTENT_TYPE.put("torrent", "application/x-bittorrent");
        CONTENT_TYPE.put("txt", "text/plain");
        CONTENT_TYPE.put("vml", "text/xml");
        CONTENT_TYPE.put("apk", "application/vnd");
    }

    public static String getContentType(String filename) {
        String fileType = filename.substring(filename.lastIndexOf(BaseConstants.Symbol.POINT) + 1);
        if (StringUtils.isBlank(fileType)) {
            return MediaType.TEXT_PLAIN_VALUE;
        }
        return CONTENT_TYPE.getOrDefault(fileType, MediaType.TEXT_PLAIN_VALUE);
    }
}