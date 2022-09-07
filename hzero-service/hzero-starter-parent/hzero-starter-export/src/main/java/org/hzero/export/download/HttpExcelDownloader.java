package org.hzero.export.download;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.core.util.FilenameUtils;
import org.hzero.export.ExcelDownloader;
import org.hzero.export.IExcelExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * @author XCXCXCXCX
 * @date 2020/7/21 2:47 下午
 */
public class HttpExcelDownloader implements ExcelDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpExcelDownloader.class);

    private static final String CONTENT_TYPE_TXT_FILE = "text/plain";
    private static final String CONTENT_TYPE_ZIP_FILE = "application/zip";
    private static final String CONTENT_TYPE_EXCEL_FILE = "application/vnd.ms-excel";


    private final String fileName;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final IExcelExporter excelExporter;

    public HttpExcelDownloader(String fileName, HttpServletRequest request, HttpServletResponse response, IExcelExporter excelExporter) {
        this.fileName = fileName;
        this.request = request;
        this.response = response;
        this.excelExporter = excelExporter;
    }

    @Override
    public void download() {
        try {
            setExcelHeader();
            excelExporter.export(response.getOutputStream());
        } catch (IOException e) {
            try {
                excelExporter.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void setExcelHeader() {
        String contentType = getContentType();
        String filename = getDownloadFileName();
        int index = filename.lastIndexOf(".");
        try {
            filename = FilenameUtils.encodeFileName(request, filename.substring(0, index)) + filename.substring(index);
        } catch (IOException e) {
            LOGGER.error("encode file name failed.", e);
        }
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
    }

    @Override
    public String getFileName() {
        return fileName == null ? excelExporter.getTitle() : fileName;
    }

    private String getDownloadFileName() {
        String suffix = getFileSuffix();
        return IExcelExporter.TXT_SUFFIX.equals(suffix) ? "error" + suffix : getFileName() + suffix;
    }

    private String getFileSuffix() {
        return excelExporter.getOutputFileSuffix();
    }

    private String getContentType() {
        if (!StringUtils.isEmpty(excelExporter.getError())) {
            return CONTENT_TYPE_TXT_FILE;
        }
        return getFileSuffix().equals(IExcelExporter.ZIP_SUFFIX) ? CONTENT_TYPE_ZIP_FILE : CONTENT_TYPE_EXCEL_FILE;
    }

}
