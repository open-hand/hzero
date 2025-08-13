package org.hzero.starter.file.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
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

import com.baidubce.Protocol;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.http.HttpMethodName;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.*;

import io.choerodon.core.exception.CommonException;

/**
 * 百度云 BOS
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/11 19:32
 */
public class BaiduFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaiduFileServiceImpl.class);

    private BosClient client;

    private CannedAccessControlList cannedAcl;

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        try {
            switch (config.getAccessControl()) {
                case FileConstant.BaiduAccessControl.PRIVATE:
                    this.cannedAcl = CannedAccessControlList.Private;
                    break;
                case FileConstant.BaiduAccessControl.PUBLIC_READ:
                    this.cannedAcl = CannedAccessControlList.PublicRead;
                    break;
                case FileConstant.BaiduAccessControl.PUBLIC_READ_WRITE:
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

    private BosClient getClient() {
        if (client == null) {
            // 初始化一个BosClient
            BosClientConfiguration bosConfig = new BosClientConfiguration();
            bosConfig.setCredentials(new DefaultBceCredentials(config.getAccessKeyId(), config.getAccessKeySecret()));
            // 默认为http, 若 endpoint 指定了https://  这里设不设都会使用https
            bosConfig.setProtocol(Protocol.HTTPS);
            bosConfig.setEndpoint(config.getEndPoint());
            client = new BosClient(bosConfig);
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
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        long partSize = fileConfig.getDefaultSharedSize();
        File partFile = new File(filePath);
        try {
            checkAndCreateBucket(realBucketName);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getFileType());
            InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(realBucketName, fileKey);
            InitiateMultipartUploadResponse initResponse = getClient().initiateMultipartUpload(initRequest);
            // 计算分块数目
            int partCount = (int) (partFile.length() / partSize);
            if (partFile.length() % partSize != 0) {
                partCount++;
            }
            // 新建一个List保存每个分块上传后的ETag和PartNumber
            List<PartETag> partTags = new ArrayList<>();
            for (int i = 0; i < partCount; i++) {
                // 获取文件流
                FileInputStream fis = new FileInputStream(partFile);
                // 跳到每个分块的开头
                long skipBytes = partSize * i;
                fis.skip(skipBytes);
                // 计算每个分块的大小
                long size = Math.min(partSize, partFile.length() - skipBytes);
                // 创建UploadPartRequest，上传分块
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(realBucketName);
                uploadPartRequest.setKey(fileKey);
                uploadPartRequest.setUploadId(initResponse.getUploadId());
                uploadPartRequest.setInputStream(fis);
                uploadPartRequest.setPartSize(size);
                uploadPartRequest.setPartNumber(i + 1);
                UploadPartResponse uploadPartResponse = getClient().uploadPart(uploadPartRequest);
                // 将返回的PartETag保存到List中。
                partTags.add(uploadPartResponse.getPartETag());
                // 关闭文件
                fis.close();
            }
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(realBucketName, fileKey,
                    initResponse.getUploadId(), partTags);
            getClient().completeMultipartUpload(compRequest);
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
            // 设置桶权限
            getClient().setBucketAcl(realBucketName, cannedAcl);
            // 设置桶允许跨域
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            List<String> list = Collections.singletonList("*");
            // 只有 origin header允许使用通配符
            corsConfiguration.setAllowedHeaders(list);
            corsConfiguration.setAllowedOrigins(CollectionUtils.isEmpty(fileConfig.getOrigins()) ? list : fileConfig.getOrigins());
            List<AllowedMethods> allowedMethods = new ArrayList<>();
            allowedMethods.add(AllowedMethods.GET);
            allowedMethods.add(AllowedMethods.POST);
            allowedMethods.add(AllowedMethods.PUT);
            allowedMethods.add(AllowedMethods.DELETE);
            allowedMethods.add(AllowedMethods.HEAD);
            corsConfiguration.setAllowedMethods(allowedMethods);
            List<CorsConfiguration> corsConfigurationsList = new ArrayList<>();
            corsConfigurationsList.add(corsConfiguration);
            SetBucketCorsRequest request = new SetBucketCorsRequest();
            request.setCorsConfigurationsList(corsConfigurationsList);
            getClient().setBucketCors(request);
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
        // 删除文件
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
            request.setExpiration(expiresTime.intValue());
            request.setMethod(HttpMethodName.GET);
            if (download) {
                ResponseHeaderOverrides responseHeader = new ResponseHeaderOverrides();
                responseHeader.setContentDisposition("attachment;filename=" + FilenameUtils.encodeFileName(servletRequest, fileName));
                responseHeader.setExpires(String.valueOf(System.currentTimeMillis() + 1000));
                responseHeader.setCacheControl("must-revalidate, post-check=0, pre-check=0");
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
            BosObject object = getClient().getObject(realBucketName, fileKey);
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
            BosObject object = getClient().getObject(realBucketName, fileKey);
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
        GetBucketAclResponse aclResponse = getClient().getBucketAcl(bucketName);
        boolean flag = false;
        List<Grant> grantList = aclResponse.getAccessControlList();
        for (Grant item : grantList) {
            if (item.getPermission().contains(Permission.FULL_CONTROL)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            return String.format("https://%s.%s/", bucketName, config.getDomain());
        }
        return String.format("https://%s.%s/%s/", bucketName, config.getDomain(), bucketName);
    }
}
