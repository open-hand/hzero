package org.hzero.starter.file.service;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
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
 * Aws S3与京东云OSS存储
 *
 * @author xianzhi.chen@hand-china.com 2019年1月17日上午9:59:20
 */
public class CephFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CephFileServiceImpl.class);

    private AmazonS3 s3;

    private CannedAccessControlList cannedAcl;

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        // 设置权限
        switch (config.getAccessControl()) {
            case FileConstant.AwsAccessControl.PRIVATE:
                this.cannedAcl = CannedAccessControlList.Private;
                break;
            case FileConstant.AwsAccessControl.PUBLIC_READ:
                this.cannedAcl = CannedAccessControlList.PublicRead;
                break;
            case FileConstant.AwsAccessControl.PUBLIC_READ_WRITE:
                this.cannedAcl = CannedAccessControlList.PublicReadWrite;
                break;
            default:
                break;
        }
        return this;
    }

    private AmazonS3 getClient() {
        if (s3 == null) {
            ClientConfiguration clientConfig = new ClientConfiguration();
            if (FileConstant.Protocol.HTTP.equals(getFileConfig().getProtocol())) {
                clientConfig.setProtocol(Protocol.HTTP);
            } else {
                clientConfig.setProtocol(Protocol.HTTPS);
            }
            // 设置endpoint与区域  region 参考  Regions.CN_NORTH_1
            AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(config.getEndPoint(), config.getRegion());
            AWSCredentials awsCredentials = new BasicAWSCredentials(config.getAccessKeyId(), config.getAccessKeySecret());
            AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
            s3 = AmazonS3Client.builder().withEndpointConfiguration(endpointConfig).withClientConfiguration(clientConfig)
                    .withCredentials(awsCredentialsProvider).disableChunkedEncoding()
                    .withPathStyleAccessEnabled(true).build();
        }
        return s3;
    }

    @Override
    public void shutdown() {
        if (s3 != null) {
            s3.shutdown();
        }
    }

    @Override
    public String upload(FileInfo file, String filePath) {
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        long contentLength = file.getFileSize();
        long partSize = fileConfig.getDefaultSharedSize();
        try {
            checkAndCreateBucket(realBucketName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getFileType());
            // 启动分段上传
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(realBucketName, fileKey, objectMetadata);
            InitiateMultipartUploadResult initResponse = getClient().initiateMultipartUpload(initRequest);
            // 创建Tag对象的列表。您为上载的每个对象部分检索Tag，然后，在每个单独的部分上载之后，将Tags列表传递到来完成上载的请求。
            List<PartETag> partTags = new ArrayList<>();
            // 上载文件部分
            long filePosition = 0;
            File realFile = new File(filePath);
            for (int i = 1; filePosition < contentLength; i++) {
                // 由于最后一部分可能少于分片大小，因此请根据需要调整部分大小.
                partSize = Math.min(partSize, (contentLength - filePosition));
                // 创建请求以上传零件.
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(realBucketName)
                        .withKey(fileKey)
                        .withUploadId(initResponse.getUploadId())
                        .withPartNumber(i)
                        .withFileOffset(filePosition)
                        .withFile(realFile)
                        .withPartSize(partSize);
                // 上载零件并将响应的Tag添加到我们的列表中.
                UploadPartResult uploadResult = getClient().uploadPart(uploadRequest);
                partTags.add(uploadResult.getPartETag());
                filePosition += partSize;
            }
            // 完成分片上传.
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(realBucketName, fileKey,
                    initResponse.getUploadId(), partTags);
            getClient().completeMultipartUpload(compRequest);
            return this.getObjectPrefixUrl(realBucketName) + fileKey;
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

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(IOUtils.toByteArray(inputStream))) {
            checkAndCreateBucket(realBucketName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getFileType());
            getClient().putObject(realBucketName, fileKey, byteArrayInputStream, objectMetadata);
            return this.getObjectPrefixUrl(realBucketName) + fileKey;
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
        boolean isExist = getClient().doesBucketExistV2(realBucketName);
        if (!isExist) {
            if (Objects.equals(config.getCreateBucketFlag(), BaseConstants.Flag.NO)) {
                throw new CommonException(FileMessageConstant.BUCKET_NOT_EXISTS);
            }
            CreateBucketRequest cbr = new CreateBucketRequest(realBucketName);
            cbr.setCannedAcl(cannedAcl);
            getClient().createBucket(cbr);
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
        String realBucketName = getRealBucketName(bucketName);
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
                responseHeader.setCacheControl("must-revalidate, post-check=0, pre-check=0");
                responseHeader.setContentDisposition("attachment;filename=" + FilenameUtils.encodeFileName(servletRequest, fileName));
                responseHeader.setExpires(String.valueOf(System.currentTimeMillis() + 1000));
                responseHeader.setContentType(FileConstant.DEFAULT_MULTI_TYPE);
                request.setResponseHeaders(responseHeader);
            }
            URL fileUrl = getClient().generatePresignedUrl(request);
            return fileUrl.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } finally {
            shutdown();
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey) {
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            S3Object object = getClient().getObject(realBucketName, fileKey);
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
        String realBucketName = getRealBucketName(bucketName);
        if (StringUtils.isBlank(fileKey)) {
            fileKey = getFileKey(realBucketName, url);
        }
        try {
            S3Object object = getClient().getObject(realBucketName, fileKey);
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
    public String getObjectPrefixUrl(String bucketName) {
        if (FileConstant.Protocol.HTTP.equals(getFileConfig().getProtocol())) {
            return String.format("http://%s.%s/", bucketName, config.getDomain());
        } else {
            return String.format("https://%s.%s/", bucketName, config.getDomain());
        }
    }
}
