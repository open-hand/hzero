package org.hzero.starter.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.starter.file.configuration.FileStoreConfig;
import org.hzero.starter.file.entity.FileInfo;
import org.hzero.starter.file.entity.StoreConfig;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 文件服务抽象类 description
 *
 * @author xianzhi.chen@hand-china.com 2018年6月22日下午4:54:40
 */
public abstract class AbstractFileService {

    protected StoreConfig config;

    protected FileStoreConfig fileConfig = ApplicationContextHelper.getContext().getBean(FileStoreConfig.class);

    public FileStoreConfig getFileConfig() {
        return this.fileConfig;
    }

    /**
     * 初始化文件服务配置
     *
     * @param config 存储配置
     * @return AbstractFileService
     */
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        return this;
    }

    /**
     * 关闭客户端
     */
    public void shutdown() {
    }

    /**
     * 上传本地文件
     *
     * @param file     文件信息
     * @param filePath 文件地址
     * @return 返回http地址
     */
    public abstract String upload(FileInfo file, String filePath);

    /**
     * 文件上传
     *
     * @param file        文件信息
     * @param inputStream 字节流
     * @return 返回http地址
     */
    public abstract String upload(FileInfo file, InputStream inputStream);

    /**
     * 文件复制
     *
     * @param file          原文件信息
     * @param oldFileKey    原文件的fileKey
     * @param oldBucketName 原文件的桶
     * @return 新文件的地址
     */
    public abstract String copyFile(FileInfo file, String oldFileKey, String oldBucketName);

    /**
     * 删除文件
     *
     * @param bucketName 桶
     * @param url        url
     * @param fileKey    文件key
     */
    public abstract void deleteFile(String bucketName, String url, String fileKey);

    /**
     * 获取文件授权url
     *
     * @param servletRequest request
     * @param bucketName     桶名
     * @param url            url
     * @param fileName       文件名
     * @param fileKey        文件key
     * @param download       是否下载(是否将contentType设置为stream)
     * @param expires        有效时长
     * @return 下载地址
     */
    public abstract String getSignedUrl(HttpServletRequest servletRequest, String bucketName, String url, String fileKey, String fileName, boolean download, Long expires);

    /**
     * 下载文件
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param bucketName 桶
     * @param url        url
     * @param fileKey    文件key
     */
    public abstract void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey);

    /**
     * 解密并下载文件
     *
     * @param request    HttpServletRequest
     * @param response   HttpServletResponse
     * @param bucketName 桶
     * @param url        url
     * @param fileKey    文件key
     * @param password   密钥
     */
    public abstract void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password);

    /**
     * 获取对象前缀URL
     *
     * @param realBucketName 真实桶
     * @return 文件URL前缀
     */
    public abstract String getObjectPrefixUrl(String realBucketName);

    /**
     * 构建文件下载的response
     */
    protected void buildResponse(HttpServletResponse response, byte[] data, String fileName) throws IOException {
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setContentType("multipart/form-data");
        response.addHeader("Content-Length", "" + data.length);
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));
        IOUtils.write(data, response.getOutputStream());
    }

    /**
     * 获取实际桶名称
     *
     * @param bucketName 桶名
     * @return 实际桶名
     */
    protected String getRealBucketName(String bucketName) {
        return StringUtils.isNotBlank(config.getBucketPrefix())
                ? String.format("%s-%s", config.getBucketPrefix(), bucketName)
                : bucketName;
    }

    /**
     * 根据文件url获取文件实际的ObjectKey
     *
     * @param url url
     * @return 文件Key
     */
    protected String getFileKey(String bucketName, String url) {
        String prefixUrl = getObjectPrefixUrl(bucketName);
        try {
            // 若url使用路径传参，则已经经过了decode操作
            return URLDecoder.decode(url, BaseConstants.DEFAULT_CHARSET).substring(prefixUrl.length());
        } catch (Exception e) {
            return url.substring(prefixUrl.length());
        }
    }
}
