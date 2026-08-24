package org.hzero.boot.file.feign.fallback;

import java.util.Date;
import java.util.List;

import feign.Response;
import org.hzero.boot.file.dto.FileParamsDTO;
import org.hzero.boot.file.feign.FileRemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;

/**
 * 远程文件服务Fallback类
 *
 * @author xianzhi.chen@hand-china.com 2019年1月31日上午11:48:49
 */
@Component
public class FileRemoteServiceFallback implements FileRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(FileRemoteServiceFallback.class);

    @Override
    public ResponseEntity<String> getAttachUUID(Long organizationId) {
        logger.error("Get attachment uuid failed,organizationId = {}.", organizationId);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> uploadFile(Long organizationId, String bucketName, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        logger.error("Upload file failed,organizationId = {}, bucketName = {}, fileName = {}.", organizationId, bucketName, fileName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> uploadByteFile(Long organizationId, String bucketName, String directory, String fileName, String fileType, String storageCode, byte[] byteFile) {
        logger.error("Upload file failed,organizationId = {}, bucketName = {}, fileName = {}.", organizationId, bucketName, fileName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> uploadAttachMultipartFile(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        logger.error("Upload attachment file failed, organizationId = {}, bucketName = {}, fileName = {}.", organizationId, bucketName, fileName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> uploadAttachByteFile(Long organizationId, String bucketName, String directory, String fileName, String attachmentUUID, String fileType, String storageCode, byte[] byteFile) {
        logger.error("Upload attachment file failed, organizationId = {}, bucketName = {}, fileName = {}.", organizationId, bucketName, fileName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> getSignedUrl(Long organizationId, String bucketName, String storageCode, String url, Long expires) {
        logger.error("Get sign url failed, organizationId = {}, bucketName = {}, url = {}.", organizationId, bucketName, url);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> getFileList(Long organizationId, String bucketName, String attachmentUUID) {
        logger.error("Get file list failed,organizationId = {}, bucketName = {}, attachmentUUID = {}.", organizationId, bucketName, attachmentUUID);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> getAttachmentFiles(Long organizationId, String bucketName, String attachmentUuids, Integer page, Integer size) {
        logger.error("Get file list failed,organizationId = {}, bucketName = {}, attachmentUUIDs = {}.", organizationId, bucketName, attachmentUuids);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> fileCount(Long organizationId, String bucketName, String attachmentUUID) {
        logger.error("Get file count failed,organizationId = {}, bucketName = {}, attachmentUUID = {}.", organizationId, bucketName, attachmentUUID);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> checkUUid(Long organizationId, String bucketName, List<String> uuidList) {
        logger.error("Check uuid failed, organizationId = {}, bucketName = {}", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> selectFile(Long organizationId, String bucketName, List<String> urls) {
        logger.error("Get file info failed, organizationId = {}, bucketName = {}", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> updateMultipartFileByURL(Long organizationId, String bucketName, String url, String storageCode, MultipartFile multipartFile) {
        logger.error("Update file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> updateByteFileByURL(Long organizationId, String bucketName, String url, String storageCode, byte[] byteFile) {
        logger.error("Update file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> deleteFileByUrl(Long organizationId, String bucketName, String storageCode, List<String> urls) {
        logger.error("Delete file failed,organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> deleteFileByAttachmentUUIDURL(Long organizationId, String bucketName, String attachmentUUID, String storageCode, List<String> urls) {
        logger.error("Delete file failed,organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> deleteFileByAttachmentUUIDUrlNotNull(Long organizationId, String bucketName, String attachmentUUID, String storageCode, List<String> urls) {
        logger.error("Delete file failed,organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> deleteFileByAttachmentUUID(Long organizationId, String bucketName, String storageCode, List<String> attachmentUUIDs) {
        logger.error("Delete file failed,organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> copyFile(Long organizationId, FileParamsDTO fileParamsDTO) {
        logger.error("Copy file failed, organizationId = {}, sourceBucketName = {}, listUuid = {}.", organizationId, fileParamsDTO.getBucketName(), fileParamsDTO.getAttachmentUUID());
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> uploadMultipartFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        logger.error("Upload file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> uploadByteFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, String fileType, String storageCode, byte[] byteFile) {
        logger.error("Download file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> getFileUrl(Long organizationId, String fileKey, Long expires) {
        logger.error("Get file url failed,organizationId = {}, fileKey = {}.", organizationId, fileKey);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> deleteFileByKey(Long organizationId, String fileKey) {
        logger.error("Delete file failed, organizationId = {}, fileKey = {}.", organizationId, fileKey);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> updateMultipartFileByKey(Long organizationId, String fileKey, MultipartFile file) {
        logger.error("Update file failed, organizationId = {}, fileKey = {}.", organizationId, fileKey);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public ResponseEntity<String> updateByteFileByKey(Long organizationId, String fileKey, byte[] file) {
        logger.error("Update file failed, organizationId = {}, fileKey = {}.", organizationId, fileKey);
        throw new CommonException("File service is not available, please check");
    }

    @Override
    public Response downloadByUrl(Long organizationId, String bucketName, String storageCode, String url) {
        logger.error("Download file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        return null;
    }

    @Override
    public Response downloadByUrlInner(Long organizationId, String bucketName, String storageCode, String url) {
        logger.error("Download file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        return null;
    }

    @Override
    public Response decryptDownloadByUrl(Long organizationId, String bucketName, String url, String storageCode, String password) {
        logger.error("Download file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        return null;
    }

    @Override
    public ResponseEntity<String> copyByUrl(Long organizationId, String url, String bucketName, String destinationBucketName, String destinationFileName) {
        logger.error("Copy file failed, organizationId = {}, bucketName = {}.", organizationId, bucketName);
        return null;
    }

    @Override
    public Response downloadByKey(Long organizationId, String fileKey) {
        logger.error("Download file failed, organizationId = {}, fileKey = {}.", organizationId, fileKey);
        return null;
    }

    @Override
    public Response decryptDownloadByKey(Long organizationId, String fileKey, String password) {
        logger.error("Download file failed, organizationId = {}, fileKey = {}.", organizationId, fileKey);
        return null;
    }

    @Override
    public Response watermarkByKey(Long organizationId, String fileKey, String watermarkCode, String context) {
        logger.error("Watermark file failed, organizationId = {}, fileKey = {}.", organizationId, fileKey);
        return null;
    }

    @Override
    public Response watermarkByUrl(Long organizationId, String bucketName, String storageCode, String url, String watermarkCode, String context, String contextBucket) {
        logger.error("Watermark file failed, organizationId = {}, fileUrl = {}.", organizationId, url);
        return null;
    }

    @Override
    public ResponseEntity<String> pageFileList(Long tenantId, String bucketName, String directory, String fileType, String attachmentUUID, String fileName, String fileFormat, Long fileMinSize, String fileMinUnit, Long fileMaxSize, String fileMaxUnit, Date fromCreateDate, Date toCreateDate, String sourceType, String serverCode, String storageCode, Integer page, Integer size) {
        logger.error("Get file list failed, organizationId = {}", tenantId);
        return null;
    }
}
