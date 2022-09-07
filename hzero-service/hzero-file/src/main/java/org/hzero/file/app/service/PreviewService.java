package org.hzero.file.app.service;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件预览应用服务接口
 *
 * @author xianzhi.chen@hand-china.com 2019年2月15日上午11:05:43
 */
public interface PreviewService {

    /**
     * 预览文件
     *
     * @param request  request
     * @param response response
     * @param tenantId 租户Id
     * @param fileKey  文件key
     */
    void previewFileByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey);

    /**
     * 预览文件
     *
     * @param request     request
     * @param response    response
     * @param tenantId    租户Id
     * @param storageCode 存储编码
     * @param bucketName  桶
     * @param url         文件url
     */
    void previewFileByUrl(HttpServletRequest request, HttpServletResponse response, Long tenantId, String storageCode, String bucketName, String url);

    /**
     * 根据key下载文件
     *
     * @param request  request
     * @param tenantId 租户Id
     * @param fileKey  文件key
     * @return 文件
     */
    InputStream downloadFileByKey(HttpServletRequest request, Long tenantId, String fileKey);

    /**
     * 根据url下载文件
     *
     * @param request     request
     * @param tenantId    租户Id
     * @param storageCode 存储编码
     * @param bucketName  桶名
     * @param url         文件url
     * @return 文件
     */
    InputStream downloadFileByUrl(HttpServletRequest request, Long tenantId, String storageCode, String bucketName, String url);

}
