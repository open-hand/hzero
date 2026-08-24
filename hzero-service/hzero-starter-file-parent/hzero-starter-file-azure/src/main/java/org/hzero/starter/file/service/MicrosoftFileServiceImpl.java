package org.hzero.starter.file.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.EnumSet;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.util.Assert;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;

import io.choerodon.core.exception.CommonException;

/**
 * 微软云Blob 存储
 *
 * @author liufanghan 2019/11/11 11:11
 */
public class MicrosoftFileServiceImpl extends AbstractFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MicrosoftFileServiceImpl.class);

    private CloudBlobClient client;
    private CloudBlobContainer container;
    private BlobContainerPermissions containerPermissions;

    @Override
    public AbstractFileService init(StoreConfig config) {
        this.config = config;
        try {
            containerPermissions = new BlobContainerPermissions();
            switch (config.getAccessControl()) {
                case FileConstant.MicrosoftControl.OFF:
                    containerPermissions.setPublicAccess(BlobContainerPublicAccessType.OFF);
                    break;
                case FileConstant.MicrosoftControl.CONTAINER:
                    containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);
                    break;
                case FileConstant.MicrosoftControl.BLOB:
                    containerPermissions.setPublicAccess(BlobContainerPublicAccessType.BLOB);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
        return this;
    }

    @Override
    public void shutdown() {
    }

    private CloudBlobClient getClient() {
        if (client == null) {
            String setting = "DefaultEndpointsProtocol=https;AccountName=" + config.getAccessKeyId() + ";AccountKey="
                    + config.getAccessKeySecret() + ";EndpointSuffix=" + config.getEndPoint();
            CloudStorageAccount storageAccount;
            try {
                storageAccount = CloudStorageAccount.parse(setting);
                client = storageAccount.createCloudBlobClient();
            } catch (Exception e) {
                throw new CommonException(e);
            }
        }
        return client;
    }

    @Override
    public String upload(FileInfo file, String filePath) {
        BlobRequestOptions options = new BlobRequestOptions();
        // 设置分片阈值
        options.setSingleBlobPutThresholdInBytes(fileConfig.getShardingThreshold());
        getClient().setDefaultRequestOptions(options);
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        CloudBlockBlob blockBlob = null;
        try {
            container = getClient().getContainerReference(realBucketName);
            boolean isExist = container.exists();
            if (!isExist) {
                if (Objects.equals(config.getCreateBucketFlag(), BaseConstants.Flag.NO)) {
                    throw new CommonException(FileMessageConstant.BUCKET_NOT_EXISTS);
                }
                container.createIfNotExists();
                container.uploadPermissions(containerPermissions);
            }
            blockBlob = container.getBlockBlobReference(fileKey);
            blockBlob.getProperties().setContentType(file.getFileType());
            // 设置分片大小
            blockBlob.setStreamWriteSizeInBytes(fileConfig.getDefaultSharedSize());
            FileInputStream inputStream = new FileInputStream(new File(filePath));
            blockBlob.upload(inputStream, BaseConstants.Digital.NEGATIVE_ONE);
            inputStream.close();
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception e) {
            // 删除块和快照
            try {
                Assert.notNull(blockBlob, FileMessageConstant.ERROR_FILE_UPDATE);
                blockBlob.delete(DeleteSnapshotsOption.INCLUDE_SNAPSHOTS, null, null, null);
            } catch (StorageException ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
        return getObjectPrefixUrl(realBucketName) + fileKey;
    }

    @Override
    public String upload(FileInfo file, InputStream inputStream) {
        String realBucketName = getRealBucketName(file.getBucketName());
        String fileKey = file.getFileKey();
        CloudBlockBlob blockBlob = null;
        try {
            container = getClient().getContainerReference(realBucketName);
            boolean isExist = container.exists();
            if (!isExist) {
                if (Objects.equals(config.getCreateBucketFlag(), BaseConstants.Flag.NO)) {
                    throw new CommonException(FileMessageConstant.BUCKET_NOT_EXISTS);
                }
                container.createIfNotExists();
                container.uploadPermissions(containerPermissions);
            }
            blockBlob = container.getBlockBlobReference(fileKey);
            blockBlob.getProperties().setContentType(file.getFileType());
            blockBlob.upload(inputStream, BaseConstants.Digital.NEGATIVE_ONE);
        } catch (CommonException ce) {
            throw ce;
        } catch (Exception e) {
            // 删除块和快照
            try {
                Assert.notNull(blockBlob, FileMessageConstant.ERROR_FILE_UPDATE);
                blockBlob.delete(DeleteSnapshotsOption.INCLUDE_SNAPSHOTS, null, null, null);
            } catch (StorageException ex) {
                LOGGER.error(ex.getMessage());
            }
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
        return getObjectPrefixUrl(realBucketName) + fileKey;
    }

    @Override
    public String copyFile(FileInfo file, String oldFileKey, String oldBucketName) {
        String newRealBucketName = getRealBucketName(file.getBucketName());
        String newFileKey = file.getFileKey();
        try {
            container = getClient().getContainerReference(newRealBucketName);
            CloudBlobContainer oldContainer = getClient().getContainerReference(getRealBucketName(oldBucketName));
            if (!container.exists()) {
                if (Objects.equals(config.getCreateBucketFlag(), BaseConstants.Flag.NO)) {
                    throw new CommonException(FileMessageConstant.BUCKET_NOT_EXISTS);
                }
                container.createIfNotExists();
            }
            container.uploadPermissions(containerPermissions);
            CloudBlockBlob oldBlockBlob = oldContainer.getBlockBlobReference(oldFileKey);
            CloudBlockBlob newBlockBlob = container.getBlockBlobReference(newFileKey);
            newBlockBlob.startCopy(oldBlockBlob);
            waitForCopyToComplete(newBlockBlob);
            return getObjectPrefixUrl(newRealBucketName) + newFileKey;
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public void deleteFile(String bucketName, String url, String fileKey) {
        try {
            CloudBlockBlob blockBlob = getClient().getContainerReference(getRealBucketName(bucketName)).getBlockBlobReference(fileKey);
            blockBlob.delete(DeleteSnapshotsOption.INCLUDE_SNAPSHOTS, null, null, null);
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
        // 中文文件名做一次encode处理
        int index = fileKey.lastIndexOf(FileConstant.DIRECTORY_SEPARATOR) + 1;
        String encodeFileKey = fileKey;
        try {
            encodeFileKey = fileKey.substring(0, index) + URLEncoder.encode(fileKey.substring(index), BaseConstants.DEFAULT_CHARSET);
            CloudBlockBlob blockBlob = getClient().getContainerReference(realBucketName).getBlockBlobReference(fileKey);
            // 创建授权url
            SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
            // 有url有效时间
            policy.setSharedAccessStartTime(new Date());
            Long expiresTime = expires == null ? fileConfig.getDefaultExpires() : expires;
            policy.setSharedAccessExpiryTime(new Date(System.currentTimeMillis() + expiresTime * 1000));
            // 设置读权限
            EnumSet<SharedAccessBlobPermissions> permissionSet = EnumSet.of(SharedAccessBlobPermissions.READ);
            policy.setPermissions(permissionSet);
            if (download) {
                // 设置contentType
                BlobProperties properties = blockBlob.getProperties();
                properties.setContentType(FileConstant.DEFAULT_MULTI_TYPE);
                properties.setContentDisposition("attachment");
                properties.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            }
            return getObjectPrefixUrl(realBucketName) + encodeFileKey + BaseConstants.Symbol.QUESTION + blockBlob.generateSharedAccessSignature(policy, null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return getObjectPrefixUrl(realBucketName) + encodeFileKey;
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CloudBlockBlob blockBlob = getClient().getContainerReference(getRealBucketName(bucketName)).getBlockBlobReference(fileKey);
            blockBlob.download(outputStream);
            buildResponse(response, outputStream.toByteArray(), FilenameUtils.encodeFileName(request, FilenameUtils.getFileName(StringUtils.isBlank(url) ? fileKey : url)));
        } catch (Exception e) {
            throw new CommonException(FileMessageConstant.ERROR_DOWNLOAD_FILE, e);
        }
    }

    @Override
    public void decryptDownload(HttpServletRequest request, HttpServletResponse response, String bucketName, String url, String fileKey, String password) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CloudBlockBlob blockBlob = getClient().getContainerReference(getRealBucketName(bucketName)).getBlockBlobReference(fileKey);
            blockBlob.download(outputStream);
            byte[] data = outputStream.toByteArray();
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
        String prefixUrl = "https://%s.blob." + config.getEndPoint() + "/%s/";
        return String.format(prefixUrl, config.getAccessKeyId(), bucketName);
    }

    /**
     * 等待文件复制完成
     *
     * @param blob 新的blob
     * @throws InterruptedException 异常
     * @throws StorageException     异常
     */
    private static void waitForCopyToComplete(CloudBlob blob) throws InterruptedException, StorageException {
        CopyStatus copyStatus = CopyStatus.PENDING;
        while (copyStatus == CopyStatus.PENDING) {
            Thread.sleep(1000);
            blob.downloadAttributes();
            copyStatus = blob.getCopyState().getStatus();
        }
    }
}
