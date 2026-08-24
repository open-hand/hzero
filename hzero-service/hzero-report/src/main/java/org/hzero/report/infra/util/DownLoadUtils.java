package org.hzero.report.infra.util;

import java.io.BufferedOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hzero.core.util.FilenameUtils;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author fanghan.liu 2019/12/13 16:25
 */
public class DownLoadUtils {

    private DownLoadUtils() {
    }

    /**
     * 组装下载文件
     */
    public static void downloadFile(byte[] data, String outputType, String name, HttpServletRequest request, HttpServletResponse response) {
        String contentType;
        String suffix;
        boolean inline = false;
        switch (outputType) {
            case HrptConstants.DocumentData.OUTPUT_FORMAT_XLS:
                contentType = "application/vnd.ms-excel;";
                suffix = ".xls";
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_XLSX:
                contentType = "application/vnd.ms-excel;";
                suffix = ".xlsx";
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_DOCX:
                contentType = "application/msword;";
                suffix = ".docx";
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_PPTX:
                contentType = "application/x-ppt;";
                suffix = ".pptx";
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_PDF:
                contentType = "application/pdf;";
                suffix = ".pdf";
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_HTML:
                contentType = "text/html;";
                suffix = ".html";
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_CSV:
                contentType = "application/octet-stream;";
                suffix = ".csv";
                break;
            case HrptConstants.DocumentData.ONLINE_PRINT:
                contentType = "application/pdf;";
                suffix = ".pdf";
                inline = true;
                break;
            default:
                return;
        }
        // 组装下载文件
        try (BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            if (inline) {
                response.setHeader("Content-Disposition", "inline;filename=" + FilenameUtils.encodeFileName(request, name + suffix));
            } else {
                response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.encodeFileName(request, name + suffix));
            }
            response.setContentType(contentType);
            response.addHeader("Content-Length", "" + data.length);
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
            response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
            IOUtils.write(data, out);
            out.flush();
        } catch (IOException ex) {
            throw new CommonException(HrptMessageConstants.ERROR_DOWNLOAD_FILE, ex);
        }
    }

}
