package org.hzero.export.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.core.base.BaseConstants;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.exporter.IFileExporter;

/**
 * 文件下载
 *
 * @author XCXCXCXCX 2020/7/21 2:47
 */
public class HttpFileDownloader implements FileDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFileDownloader.class);
    private static final String MD5_PATTERN = "&{md5}";

    private String filenameWithOutSuffix;
    private final HttpServletResponse response;
    private final IFileExporter excelExporter;

    public HttpFileDownloader(String filenameWithOutSuffix, HttpServletResponse response, IFileExporter excelExporter) {
        this.filenameWithOutSuffix = filenameWithOutSuffix;
        this.response = response;
        this.excelExporter = excelExporter;
    }

    @Override
    public void download() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            if (StringUtils.isNotBlank(filenameWithOutSuffix) && filenameWithOutSuffix.contains(MD5_PATTERN)) {
                String md5 = excelExporter.export(outputStream, true);
                filenameWithOutSuffix = filenameWithOutSuffix.replace(MD5_PATTERN, md5);
            } else {
                excelExporter.export(outputStream, false);
            }
            setExcelHeader();
            // 写入response
            IOUtils.write(outputStream.toByteArray(), response.getOutputStream());
        } catch (IOException e) {
            try {
                excelExporter.close();
            } catch (Exception ex) {
                LOGGER.error("error", ex);
            }
        }
    }

    public void setExcelHeader() {
        String filename = getDownloadFileName();
        int index = filename.lastIndexOf(BaseConstants.Symbol.POINT);
        try {
            filename = URLEncoder.encode(filename.substring(0, index), BaseConstants.DEFAULT_CHARSET) + filename.substring(index);
        } catch (IOException e) {
            LOGGER.error("encode file name failed.", e);
        }
        response.setHeader("Content-disposition", "attachment; filename=" + filename);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
    }

    @Override
    public String getFileName() {
        return filenameWithOutSuffix == null ? excelExporter.getTitle() : filenameWithOutSuffix;
    }

    private String getDownloadFileName() {
        String suffix = excelExporter.getOutputFileSuffix();
        return ExportConstants.TXT_SUFFIX.equals(suffix) ? "error" + suffix : getFileName() + suffix;
    }
}
