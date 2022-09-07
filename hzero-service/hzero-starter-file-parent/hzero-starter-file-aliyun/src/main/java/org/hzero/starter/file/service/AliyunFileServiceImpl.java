package org.hzero.starter.file.service;


import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FilenameUtils;
import org.hzero.starter.file.constant.FileConstant;
import org.hzero.starter.file.constant.FileMessageConstant;
import org.hzero.starter.file.entity.FileInfo;
import org.hzero.starter.file.entity.StoreConfig;
import org.hzero.starter.file.util.AesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;

import io.choerodon.core.exception.CommonException;

/**
 * 阿里云OSS存储
 *
 * @author xianzhi.chen@hand-china.com 2018年6月25日下午5:06:29
 */
public class AliyunFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunFileServiceImpl.class);

    private OSSClient client;

    private CannedAccessControlList cannedAcl;

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        try {
            switch (config.getAccessControl()) {
                case FileConstant.AliyunAccessControl.DEFAULT:
                    this.cannedAcl = CannedAccessControlList.Default;
                    break;
                case FileConstant.AliyunAccessControl.PRIVATE:
                    this.cannedAcl = CannedAccessControlList.Private;
                    break;
                case FileConstant.AliyunAccessControl.PUBLIC_READ:
                    this.cannedAcl = CannedAccessControlList.PublicRead;
                    break;
                case FileConstant.AliyunAccessControl.PUBLIC_READ_WRITE:
                    this.cannedAcl = CannedAccessControlList.PublicReadWrite;
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return this;
    }

    private OSSClient getClient() {
        if (client == null) {
            client = new OSSClient(config.getEndPoint(), config.getAccessKeyId(), config.getAccessKeySecret());
        }
        return client;
    }

    @Override
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }

    @Override
    public String upload(FileInfo file, String filePath) {
        // 获取实际桶名
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        UploadFileRequest request = new UploadFileRequest(realBucketName, fileKey);
        ObjectMetadata metaData = new ObjectMetadata();
        // 指定上传的内容类型
        metaData.setContentType(file.getFileType());
        // 指定上传的本地文件
        request.setUploadFile(filePath);
        // 设置分段大小
        request.setPartSize(fileConfig.getDefaultSharedSize());
        // 开启断点续传模式
        request.setEnableCheckpoint(true);
        // 文件的元数据
        request.setObjectMetadata(metaData);
        try {
            getClient().uploadFile(request);
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (Throwable throwable) {
            // 删除文件
            try {
                getClient().deleteObject(realBucketName, fileKey);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, throwable);
        }
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        try {
            checkAndCreateBucket(realBucketName);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getFileType());
            getClient().putObject(realBucketName, fileKey, inputStream, metadata);
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception e) {
            // 删除文件
            try {
                getClient().deleteObject(realBucketName, fileKey);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    private void checkAndCreateBucket(String realBucketName) {
        boolean isExist = getClient().doesBucketExist(realBucketName);
        if (!isExist) {
            if (Objects.equals(config.getCreateBucketFlag(), BaseConstants.Flag.NO)) {
                throw new CommonException(FileMessageConstant.BUCKET_NOT_EXISTS);
            }
            getClient().createBucket(realBucketName);
            getClient().setBucketAcl(realBucketName, cannedAcl);
        }
    }

    @Override
    public String copyFile(FileInfo file, String oldFileKey, String oldBucketName) {
        String realBucketName = getRealBucketName(file.getBucketName());
        try {
            checkAndCreateBucket(realBucketName);
            getClient().copyObject(getRealBucketName(oldBucketName), oldFileKey, realBucketName, file.getFileKey());
            return getObjectPrefixUrl(realBucketName) + file.getFileKey();
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String url, String fileKey) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        // 删除附件文档
        try {
            getClient().deleteObject(realBucketName, fileKey);
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DELETE_FILE, e);
        }
    }

    @Override
    public String getSignedUrl(HttpServletRequest servletRequest, String bucketName, String url, String fileKey, String fileName, boolean download, Long expires) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(realBucketName, fileKey);
            // 路径有效期
            Long expiresTime = expires == null ? fileConfig.getDefaultExpires() : expires;
            request.setExpiration(new Date(System.currentTimeMillis() + (expiresTime * 1000)));
            request.setMethod(HttpMethod.GET);
            if (download) {
                ResponseHeaderOverrides responseHeader = new ResponseHeaderOverrides();
                responseHeader.setContentDisposition("attachment;filename=" + FilenameUtils.encodeFileName(servletRequest, fileName));
                responseHeader.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                responseHeader.setExpires(String.valueOf(System.currentTimeMillis() + 1000));
                responseHeader.setContentType(FileConstant.DEFAULT_MULTI_TYPE);
                request.setResponseHeaders(responseHeader);
            }
            URL fileUrl = getClient().generatePresignedUrl(request);
            return fileUrl.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return getObjectPrefixUrl(realBucketName) + fileKey;
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            OSSObject object = getClient().getObject(realBucketName, fileKey);
            byte[] data = IOUtils.toByteArray(object.getObjectContent());
            buildResponse(response, data, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            OSSObject object = getClient().getObject(realBucketName, fileKey);
            byte[] data = IOUtils.toByteArray(object.getObjectContent());
            if (StringUtils.isBlank(password)) {
                data = AesUtils.decrypt(data);
            } else {
                data = AesUtils.decrypt(data, password);
            }
            buildResponse(response, data, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public String getObjectPrefixUrl(String bucketName) {
        return String.format("https://%s.%s/", bucketName, config.getDomain());
    }
}
