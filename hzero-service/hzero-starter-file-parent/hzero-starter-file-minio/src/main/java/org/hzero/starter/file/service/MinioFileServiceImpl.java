package org.hzero.starter.file.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.minio.*;
import io.minio.http.Method;
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
 * Minio存储
 *
 * @author xianzhi.chen@hand-china.com 2018年6月25日下午5:06:29
 */
public class MinioFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MinioFileServiceImpl.class);

    private MinioClient client;
    private String policyConfig;

    private static final String BUCKET_PARAM = "${bucketName}";

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        switch (config.getAccessControl()) {
            case FileConstant.MinioAccessControl.NONE:
                this.policyConfig = "";
                break;
            case FileConstant.MinioAccessControl.READ_ONLY:
                this.policyConfig = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
                break;
            case FileConstant.MinioAccessControl.WRITE_ONLY:
                this.policyConfig = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
                break;
            case FileConstant.MinioAccessControl.READ_WRITE:
                this.policyConfig = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\"],\"Resource\":[\"arn:aws:s3:::" + BUCKET_PARAM + "/*\"]}]}";
                break;
            default:
                break;
        }

        return this;
    }

    @Override
    public void shutdown() {
    }

    private MinioClient getClient() {
        if (client == null) {
            try {
                client = MinioClient.builder().endpoint(config.getEndPoint()).credentials(config.getAccessKeyId(), config.getAccessKeySecret()).build();
                // https 忽略证书检查
                if (fileConfig.isIgnoreCertCheck()) {
                    client.ignoreCertCheck();
                }
            } catch (Exception e) {
                throw new CommonException(e);
            }
        }
        return client;
    }

    @Override
    public String upload(FileInfo file, String filePath) {
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        try {
            checkAndCreateBucket(realBucketName);
            getClient().uploadObject(UploadObjectArgs.builder().bucket(realBucketName).object(fileKey).filename(filePath).contentType(file.getFileType()).build());
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception e) {
            // 删除文件
            try {
                getClient().removeObject(RemoveObjectArgs.builder().bucket(realBucketName).object(fileKey).build());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        try {
            checkAndCreateBucket(realBucketName);
            getClient().putObject(PutObjectArgs.builder().bucket(realBucketName).object(fileKey).stream(inputStream, inputStream.available(), -1).contentType(file.getFileType()).build());
            return getObjectPrefixUrl(realBucketName) + fileKey;
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception e) {
            // 删除文件
            try {
                getClient().removeObject(RemoveObjectArgs.builder().bucket(realBucketName).object(fileKey).build());
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    private void checkAndCreateBucket(String realBucketName) throws Exception {
        boolean isExist = getClient().bucketExists(BucketExistsArgs.builder().bucket(realBucketName).build());
        if (!isExist) {
            if (Objects.equals(config.getCreateBucketFlag(), BaseConstants.Flag.NO)) {
                throw new CommonException(FileMessageConstant.BUCKET_NOT_EXISTS);
            }
            getClient().makeBucket(MakeBucketArgs.builder().bucket(realBucketName).build());
            if (StringUtils.isNotBlank(policyConfig)) {
                getClient().setBucketPolicy(SetBucketPolicyArgs.builder().bucket(realBucketName).config(policyConfig.replace(BUCKET_PARAM, realBucketName)).build());
            }
        }
    }

    @Override
    public String copyFile(FileInfo file, String oldFileKey, String oldBucketName) {
        String realBucketName = getRealBucketName(file.getBucketName());
        try {
            checkAndCreateBucket(realBucketName);
            CopySource source = CopySource.builder().bucket(getRealBucketName(oldBucketName)).object(oldFileKey).build();
            getClient().copyObject(CopyObjectArgs.builder().bucket(realBucketName).object(file.getFileKey()).source(source).build());
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
            getClient().removeObject(RemoveObjectArgs.builder().bucket(realBucketName).object(fileKey).build());
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
        String signedUrl;
        // 路径有效期
        Long expiresTime = expires == null ? fileConfig.getDefaultExpires() : expires;
        try {
            if (download) {
                Map<String, String> reqParams = new HashMap<>(16);
                reqParams.put("response-content-type", FileConstant.DEFAULT_MULTI_TYPE);
                reqParams.put("response-content-disposition", "attachment;filename=" + FilenameUtils.encodeFileName(servletRequest, fileName));
                reqParams.put("response-cache-control", "must-revalidate, post-check=0, pre-check=0");
                reqParams.put("response-expires", String.valueOf(System.currentTimeMillis() + 1000));
                signedUrl = getClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(realBucketName).object(fileKey).expiry(expiresTime.intValue()).extraQueryParams(reqParams).build());
            } else {
                signedUrl = getClient().getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(realBucketName).object(fileKey).expiry(expiresTime.intValue()).build());
            }
            // 是否转换代理地址
            if (StringUtils.isBlank(config.getDomain())) {
                return signedUrl;
            } else {
                return signedUrl.replace(config.getEndPoint(), config.getDomain());
            }
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
            InputStream is = getClient().getObject(GetObjectArgs.builder().bucket(realBucketName).object(fileKey).build());
            byte[] data = IOUtils.toByteArray(is);
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
            InputStream is = getClient().getObject(GetObjectArgs.builder().bucket(realBucketName).object(fileKey).build());
            byte[] data = IOUtils.toByteArray(is);
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
    public String getObjectPrefixUrl(String realBucketName) {
        if (StringUtils.isNotBlank(config.getDomain())) {
            return String.format("%s/%s/", config.getDomain(), realBucketName);
        } else {
            return String.format("%s/%s/", config.getEndPoint(), realBucketName);
        }
    }
}
