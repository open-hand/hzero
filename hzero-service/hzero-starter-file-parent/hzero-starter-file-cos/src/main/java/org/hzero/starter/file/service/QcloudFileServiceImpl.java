package org.hzero.starter.file.service;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.endpoint.SuffixEndpointBuilder;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
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

import io.choerodon.core.exception.CommonException;

/**
 * 用于配置腾讯云存储COS相关信息
 *
 * @author xianzhi.chen@hand-china.com 2018年6月25日下午5:36:48
 */
public class QcloudFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QcloudFileServiceImpl.class);

    private COSClient client;
    private CannedAccessControlList cannedAcl;

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        try {
            switch (config.getAccessControl()) {
                case FileConstant.QcloudAccessControl.DEFAULT:
                    this.cannedAcl = CannedAccessControlList.Default;
                    break;
                case FileConstant.QcloudAccessControl.PRIVATE:
                    this.cannedAcl = CannedAccessControlList.Private;
                    break;
                case FileConstant.QcloudAccessControl.PUBLIC_READ:
                    this.cannedAcl = CannedAccessControlList.PublicRead;
                    break;
                case FileConstant.QcloudAccessControl.PUBLIC_READ_WRITE:
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

    private COSClient getClient() {
        if (client == null) {
            COSCredentials credentials = new BasicCOSCredentials(config.getAccessKeyId(), config.getAccessKeySecret());
            // 初始化客户端配置
            ClientConfig clientConfig = new ClientConfig();
            clientConfig.setRegion(new Region(config.getRegion()));
            clientConfig.setEndpointBuilder(new SuffixEndpointBuilder(config.getEndPoint()));
            client = new COSClient(credentials, clientConfig);
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
        //获取实际桶名
        String realBucketName = getRealBucketName(file.getBucketName());
        //根据文件url获取文件实际的ObjectKey
        String fileKey = file.getFileKey();
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(realBucketName, fileKey);
        InitiateMultipartUploadResult initResponse = getClient().initiateMultipartUpload(initRequest);
        //标识指定分片上传的 uploadId
        String uploadId = initResponse.getUploadId();
        // 计算文件总大小
        long fileSize = file.getFileSize();
        //分片大小
        long partSize = fileConfig.getDefaultSharedSize().longValue();
        //计算分块数
        long partCount = fileSize / partSize + (fileSize % partSize != 0 ? 1 : 0);
        File partFile = new File(filePath);
        // 新建一个List保存每个分块上传后的ETag和PartNumber
        List<PartETag> partETags = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(partFile)) {
            // 获取文件流
            byte[] partByte = new byte[fileConfig.getDefaultSharedSize()];
            InputStream partStream;
            for (int i = 0; i < partCount; i++) {
                // 跳到每个分块的开头
                long skipBytes = partSize * i;
                fis.skip(skipBytes);
                //文件读取
                fis.read(partByte);
                partStream = new ByteArrayInputStream(partByte);
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(realBucketName)
                        .withUploadId(uploadId)
                        .withKey(fileKey)
                        .withPartNumber(i + 1)
                        .withInputStream(partStream)
                        .withPartSize(partSize);
                UploadPartResult uploadPartResult = getClient().uploadPart(uploadRequest);
                // 获取 part 的 Etag
                String eTag = uploadPartResult.getETag();
                // partETags 记录所有已上传的 part 的 Etag 信息
                partETags.add(new PartETag(i, eTag));
            }
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (Exception e) {
            // 删除文件
            try {
                getClient().deleteObject(realBucketName, fileKey);
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        } finally {
            shutdown();
        }
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String realBucketName = getRealBucketName(file.getBucketName()) + "-" + config.getAppId();
        String fileKey = file.getFileKey();
        try {
            checkAndCreateBucket(realBucketName);
            // 上传到腾讯云
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getFileSize());
            objectMetadata.setContentType(file.getFileType());
            PutObjectRequest request = new PutObjectRequest(realBucketName, fileKey, inputStream, objectMetadata);
            getClient().putObject(request);
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
        } finally {
            shutdown();
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
        } finally {
            shutdown();
        }
    }

    @Override
    public void deleteFile(String bucketName, String url, String fileKey) {
        String realBucketName = getRealBucketName(bucketName) + "-" + config.getAppId();
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        // 删除附件文档
        try {
            getClient().deleteObject(realBucketName, fileKey);
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DELETE_FILE, e);
        } finally {
            shutdown();
        }
    }

    @Override
    public String getSignedUrl(HttpServletRequest servletRequest, String bucketName, String url, String fileKey, String fileName, boolean download, Long expires) {
        String realBucketName = getRealBucketName(bucketName) + "-" + config.getAppId();
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(realBucketName, fileKey, HttpMethodName.GET);
            // 设置URL过期时间
            Long expiresTime = expires == null ? fileConfig.getDefaultExpires() : expires;
            request.setExpiration(new Date(System.currentTimeMillis() + expiresTime * 1000));
            if (download) {
                ResponseHeaderOverrides responseHeader = new ResponseHeaderOverrides();
                responseHeader.setExpires(String.valueOf(System.currentTimeMillis() + 1000));
                responseHeader.setContentDisposition("attachment;filename=" + FilenameUtils.encodeFileName(servletRequest, fileName));
                responseHeader.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                responseHeader.setContentType(FileConstant.DEFAULT_MULTI_TYPE);
                request.setResponseHeaders(responseHeader);
            }
            URL sgUrl = getClient().generatePresignedUrl(request);
            return sgUrl.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } finally {
            shutdown();
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey) {
        String realBucketName = getRealBucketName(bucketName) + "-" + config.getAppId();
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            COSObject object = getClient().getObject(realBucketName, fileKey);
            byte[] data = IOUtils.toByteArray(object.getObjectContent());
            buildResponse(response, data, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        } finally {
            shutdown();
        }
    }

    @Override
    public void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password) {
        String realBucketName = getRealBucketName(bucketName) + "-" + config.getAppId();
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            COSObject object = getClient().getObject(realBucketName, fileKey);
            byte[] data = IOUtils.toByteArray(object.getObjectContent());
            if (StringUtils.isBlank(password)) {
                data = AesUtils.decrypt(data);
            } else {
                data = AesUtils.decrypt(data, password);
            }
            buildResponse(response, data, FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        } finally {
            shutdown();
        }
    }

    @Override
    public String getObjectPrefixUrl(String realBucketName) {
        if (StringUtils.isNotBlank(config.getDomain())) {
            return getProxy(realBucketName);
        } else {
            return String.format("http://%s.%s/", realBucketName, config.getEndPoint());
        }
    }

    private String getProxy(String realBucketName) {
        String proxy = config.getDomain();
        proxy = proxy.replace(FileConstant.DOMAIN_BUCKET_NAME, realBucketName);
        if (!proxy.startsWith("http")) {
            // 未指定协议，默认使用https
            proxy = "http://" + proxy;
        }
        if (!proxy.endsWith("/")) {
            proxy = proxy + "/";
        }
        return proxy;
    }
}
