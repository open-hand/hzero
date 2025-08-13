package org.hzero.file.app.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.DocumentException;

/**
 * 水印
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/14 14:31
 */
public interface WatermarkService {

    /**
     * pdf添加水印
     *
     * @param request       request
     * @param response      response
     * @param tenantId      租户Id
     * @param fileKey       文件key
     * @param watermarkCode 水印配置编码
     * @param context       水印内容(文本或图片key)
     */
    void watermarkWithConfigByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey, String watermarkCode, String context);

    /**
     * pdf添加水印
     *
     * @param request       request
     * @param response      response
     * @param tenantId      租户Id
     * @param storageCode   存储编码
     * @param bucketName    桶
     * @param url           文件url
     * @param watermarkCode 水印配置编码
     * @param context       水印内容(文本或图片url)
     * @param contextBucket 水印图片的桶
     */
    void watermarkWithConfigByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode,
                                  String bucketName, String url, String watermarkCode, String context, String contextBucket);

    /**
     * 水印预览
     *
     * @param request       request
     * @param response      response
     * @param tenantId      租户Id
     * @param watermarkCode 水印配置编码
     * @throws DocumentException DocumentException
     */
    void preview(HttpServletRequest request, HttpServletResponse response, Long tenantId, String watermarkCode) throws DocumentException;
}
