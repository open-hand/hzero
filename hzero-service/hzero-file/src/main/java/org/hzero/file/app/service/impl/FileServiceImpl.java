package org.hzero.file.app.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FileUtils;
import org.hzero.core.util.FilenameUtils;
import org.hzero.core.util.ValidUtils;
import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.api.dto.FileSimpleDTO;
import org.hzero.file.app.service.FileService;
import org.hzero.file.app.service.UploadConfigService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.service.factory.StoreFactory;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.domain.service.impl.LocalFileServiceImpl;
import org.hzero.file.infra.constant.FileServiceType;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.util.CodeUtils;
import org.hzero.file.infra.util.ContentTypeUtils;
import org.hzero.file.infra.util.FileHeaderUtils;
import org.hzero.starter.file.entity.StoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;

/**
 * 文件服务实现类
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/11 17:33
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final StoreFactory factory;
    private final FileRepository fileRepository;
    private final UploadConfigService uploadConfigService;
    private final Validator validator;

    @Autowired
    public FileServiceImpl(StoreFactory factory,
                           FileRepository fileRepository,
                           UploadConfigService uploadConfigService,
                           Validator validator) {
        this.factory = factory;
        this.fileRepository = fileRepository;
        this.uploadConfigService = uploadConfigService;
        this.validator = validator;
    }


    @Override
    public String uploadFragmentFile(Long tenantId, String bucketName, String directory, String fileName, String storageCode, String filePath, Long fileSize) {
        Path path = Paths.get(filePath);
        try {
            String contentType = Files.probeContentType(path);
            // 组合文件对象
            File file = new File()
                    .setBucketName(bucketName)
                    .setDirectory(directory)
                    .setFileName(fileName)
                    .setFileType(StringUtils.isBlank(contentType) ? ContentTypeUtils.getContentType(fileName) : contentType)
                    .setFileSize(fileSize)
                    .setTenantId(tenantId)
                    .setStorageCode(storageCode)
                    .setAttachmentUuid(HfleConstant.DEFAULT_ATTACHMENT_UUID);
            // 验证数据
            ValidUtils.valid(validator, file);
            StoreService storeService = factory.build(tenantId, storageCode);
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            return storeService.uploadFile(file, filePath).getFileUrl();
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadMultipart(Long tenantId, String bucketName, String attachmentUuid, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        if (StringUtils.isBlank(fileName)) {
            fileName = multipartFile.getOriginalFilename();
        }
        CodeUtils.checkFileName(fileName);
        InputStream is = null;
        try {
            is = multipartFile.getInputStream();
            // 组合文件对象
            File file = new File()
                    .setBucketName(bucketName)
                    .setDirectory(directory)
                    .setFileName(fileName)
                    .setFileType(BaseConstants.Flag.YES.equals(docType) ? HfleConstant.DEFAULT_MULTI_TYPE : multipartFile.getContentType())
                    .setFileSize(multipartFile.getSize())
                    .setTenantId(tenantId)
                    .setStorageCode(storageCode)
                    .setAttachmentUuid(StringUtils.isBlank(attachmentUuid) ? HfleConstant.DEFAULT_ATTACHMENT_UUID : attachmentUuid);
            // 获取文件头
            String fileCode = FileHeaderUtils.getFileHeader(multipartFile);
            // 校验文件大小
            uploadConfigService.validateFileSize(file, fileCode);
            // 验证数据
            ValidUtils.valid(validator, file);
            StoreService storeService = factory.build(tenantId, storageCode);
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            return storeService.uploadFile(file, is).getFileUrl();
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadByte(Long tenantId, String bucketName, String attachmentUuid, String directory, String fileName, String fileType, String storageCode, byte[] byteFile) {
        Assert.isTrue(StringUtils.isNotBlank(fileName), HfleMessageConstant.ERROR_FILE_NAME);
        CodeUtils.checkFileName(fileName);
        // 组合文件对象
        File file = new File()
                .setBucketName(bucketName)
                .setDirectory(directory)
                .setFileSize((long) byteFile.length)
                .setFileType(StringUtils.isNotBlank(fileType) ? fileType : ContentTypeUtils.getContentType(fileName))
                .setFileName(fileName)
                .setTenantId(tenantId)
                .setStorageCode(storageCode)
                .setAttachmentUuid(StringUtils.isBlank(attachmentUuid) ? HfleConstant.DEFAULT_ATTACHMENT_UUID : attachmentUuid);
        // 校验文件大小
        uploadConfigService.validateByteFileSize(file);
        // 验证数据
        ValidUtils.valid(validator, file);
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        try (InputStream inputStream = new ByteArrayInputStream(byteFile)) {
            return storeService.uploadFile(file, inputStream).getFileUrl();
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public String getSignedUrl(HttpServletRequest request, Long tenantId, String bucketName, String storageCode, String url, Long expires) {
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        String fileName = FilenameUtils.getFileName(url);
        return storeService.getAbstractFileService().getSignedUrl(request, bucketName, url, null, fileName, false, expires);
    }

    @Override
    public String getDownloadUrl(HttpServletRequest request, Long tenantId, String bucketName, String storageCode, String url, Long expires) {
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        String fileName = FilenameUtils.getFileName(url);
        return storeService.getAbstractFileService().getSignedUrl(request, bucketName, url, null, fileName, true, expires);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateMultipart(Long tenantId, String bucketName, String url, String storageCode, MultipartFile multipartFile) {
        InputStream is = null;
        try {
            is = multipartFile.getInputStream();
            // 组合文件对象
            File file = new File()
                    .setBucketName(bucketName)
                    .setFileUrl(url)
                    .setFileSize(multipartFile.getSize())
                    .setTenantId(tenantId);
            // 验证数据
            ValidUtils.valid(validator, file);
            StoreService storeService = factory.build(tenantId, storageCode);
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            return storeService.updateFileByUrl(file, is);
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateByte(Long tenantId, String bucketName, String url, String storageCode, byte[] byteFile) {
        // 组合文件对象
        File file = new File()
                .setFileUrl(url)
                .setBucketName(bucketName)
                .setFileSize((long) byteFile.length)
                .setTenantId(tenantId);
        // 验证数据
        ValidUtils.valid(validator, file);
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        try (InputStream inputStream = new ByteArrayInputStream(byteFile)) {
            return storeService.updateFileByUrl(file, inputStream);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUrls(Long tenantId, String bucketName, String storageCode, List<String> urls) {
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        storeService.deleteFile(urls, tenantId, bucketName, HfleConstant.DEFAULT_ATTACHMENT_UUID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAttachment(Long tenantId, String bucketName, String storageCode, List<String> urls, String attachmentUuid) {
        Assert.isTrue(!HfleConstant.DEFAULT_ATTACHMENT_UUID.equals(attachmentUuid), BaseConstants.ErrorCode.DATA_EXISTS);
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        storeService.deleteFile(urls, tenantId, bucketName, attachmentUuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAttachmentNotNull(Long tenantId, String bucketName, String storageCode, List<String> urls, String attachmentUuid) {
        Assert.isTrue(!HfleConstant.DEFAULT_ATTACHMENT_UUID.equals(attachmentUuid), BaseConstants.ErrorCode.DATA_EXISTS);
        // 查询附件UUID下文件个数并进行锁定
        Integer cnt = fileRepository.selectFileCountByAttachmentUuidAndLock(tenantId, bucketName, attachmentUuid);
        if (urls.size() > cnt) {
            throw new CommonException(HfleMessageConstant.ERROR_ATTACHMENT_FILE_NOT_NULL);
        }
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        storeService.deleteFile(urls, tenantId, bucketName, attachmentUuid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAttachments(Long tenantId, String bucketName, String storageCode, List<String> attachmentList) {
        for (String attachmentUuid : attachmentList) {
            List<String> urls = fileRepository.selectFileUrlByAttachmentUUID(tenantId, bucketName, attachmentUuid);
            deleteByAttachment(tenantId, bucketName, storageCode, urls, attachmentUuid);
        }
    }

    @Override
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, Long tenantId, String bucketName, String storageCode, String url) {
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        String fileName = FilenameUtils.getFileName(url);
        if (storeService.getAbstractFileService() instanceof LocalFileServiceImpl) {
            ((LocalFileServiceImpl) storeService.getAbstractFileService()).downloadByUrl(request, response, bucketName, url, null, fileName);
        } else {
            try {
                StoreConfig storeConfig = storeService.getConfig();
                // 若minio配置了代理路径，需要执行本地下载逻辑
                if (FileServiceType.MINIO.getValue() == storeConfig.getStorageType()) {
                    if (StringUtils.isNotBlank(storeConfig.getDomain())) {
                        // 文件服务本地下载
                        storeService.getAbstractFileService().download(request, response, bucketName, url, null);
                        return;
                    }
                }
                // 其他类型或上述三种类型未指定代理路径，使用重定向下载
                response.sendRedirect(storeService.getAbstractFileService().getSignedUrl(request, bucketName, url, null, fileName, true, null));
            } catch (IOException e) {
                throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
            }
        }
    }

    @Override
    public void downloadFileInner(HttpServletRequest request, HttpServletResponse response, Long tenantId, String bucketName, String storageCode, String url) {
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        String fileName = FilenameUtils.getFileName(url);
        if (storeService.getAbstractFileService() instanceof LocalFileServiceImpl) {
            ((LocalFileServiceImpl) storeService.getAbstractFileService()).downloadByUrl(request, response, bucketName, url, null, fileName);
        } else {
            // 文件服务本地下载
            storeService.getAbstractFileService().download(request, response, bucketName, url, null);
        }
    }

    @Override
    public void decryptDownloadFile(HttpServletRequest request, HttpServletResponse response, Long tenantId, String bucketName, String url, String storageCode, String password) {
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        storeService.getAbstractFileService().decryptDownload(request, response, bucketName, url, null, password);
    }

    @Override
    public String copyFileByUrl(Long tenantId, String url, String bucketName, String destinationBucketName, String destinationFileName) {
        if (StringUtils.isBlank(destinationBucketName)) {
            destinationBucketName = bucketName;
        }
        if (StringUtils.isBlank(destinationFileName)) {
            destinationFileName = FileUtils.getFileName(url);
        }
        File file = fileRepository.selectOne(new File().setTenantId(tenantId).setFileUrl(url));
        Assert.notNull(file, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        StoreService storeService = factory.build(tenantId, file.getStorageCode());
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        File newFile = storeService.copy(file, HfleConstant.DEFAULT_ATTACHMENT_UUID, destinationBucketName, destinationFileName);
        return newFile.getFileUrl();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> copyAttachment(Long tenantId, String storageCode, FileParamsDTO fileParamsDTO) {
        Assert.isTrue(CollectionUtils.isNotEmpty(fileParamsDTO.getUuidList()), HfleMessageConstant.FILE_UUID);
        StoreService storeService = factory.build(tenantId, storageCode);
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        return storeService.copyAttachment(fileParamsDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileSimpleDTO uploadMultipartKey(Long tenantId, String bucketName, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile) {
        if (StringUtils.isBlank(fileName)) {
            fileName = multipartFile.getOriginalFilename();
        }
        CodeUtils.checkFileName(fileName);
        File file = new File();
        InputStream is = null;
        InputStream inputStream = null;
        try {
            is = multipartFile.getInputStream();
            inputStream = multipartFile.getInputStream();
            // 组合文件对象
            file.setMd5(DigestUtils.md5DigestAsHex(inputStream))
                    .setBucketName(bucketName)
                    .setDirectory(directory)
                    .setFileName(fileName)
                    .setFileType(BaseConstants.Flag.YES.equals(docType) ? HfleConstant.DEFAULT_MULTI_TYPE : multipartFile.getContentType())
                    .setFileSize(multipartFile.getSize())
                    .setTenantId(tenantId)
                    .setStorageCode(storageCode)
                    .setAttachmentUuid(HfleConstant.DEFAULT_ATTACHMENT_UUID);
            // 获取文件头
            String fileCode = FileHeaderUtils.getFileHeader(multipartFile);
            // 校验文件大小
            uploadConfigService.validateFileSize(file, fileCode);
            // 验证数据
            ValidUtils.valid(validator, file);
            StoreService storeService = factory.build(tenantId, storageCode);
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            file = storeService.uploadFile(file, is);
            return new FileSimpleDTO().setFileKey(file.getFileKey()).setMd5(file.getMd5());
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileSimpleDTO uploadByteKey(Long tenantId, String bucketName, String directory, String fileName, String fileType, String storageCode, byte[] byteFile) {
        Assert.isTrue(StringUtils.isNotBlank(fileName), HfleMessageConstant.ERROR_FILE_NAME);
        CodeUtils.checkFileName(fileName);
        // 组合文件对象
        File file = new File();
        try (InputStream inputStream = new ByteArrayInputStream(byteFile);
             InputStream is = new ByteArrayInputStream(byteFile)) {
            file.setMd5(DigestUtils.md5DigestAsHex(inputStream))
                    .setBucketName(bucketName)
                    .setDirectory(directory)
                    .setFileName(fileName)
                    .setFileSize((long) byteFile.length)
                    .setFileType(StringUtils.isNotBlank(fileType) ? fileType : ContentTypeUtils.getContentType(fileName))
                    .setTenantId(tenantId)
                    .setStorageCode(storageCode)
                    .setAttachmentUuid(HfleConstant.DEFAULT_ATTACHMENT_UUID);
            // 校验文件大小
            uploadConfigService.validateByteFileSize(file);
            // 验证数据
            ValidUtils.valid(validator, file);
            StoreService storeService = factory.build(tenantId, storageCode);
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            file = storeService.uploadFile(file, is);
            return new FileSimpleDTO().setFileKey(file.getFileKey()).setMd5(file.getMd5());
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public FileSimpleDTO getSignedUrlByKey(HttpServletRequest request, Long tenantId, String fileKey, Long expires) {
        File file = fileRepository.getFile(fileKey);
        StoreService storeService = factory.build(tenantId, file.getStorageCode());
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        String tokenUrl = storeService.getAbstractFileService().getSignedUrl(request, file.getBucketName(), file.getFileUrl(), fileKey, file.getFileName(), false, expires);
        return new FileSimpleDTO().setFileTokenUrl(tokenUrl).setMd5(file.getMd5());
    }

    @Override
    public FileSimpleDTO getDownloadUrlByKey(HttpServletRequest request, Long tenantId, String fileKey, Long expires) {
        File file = fileRepository.getFile(fileKey);
        StoreService storeService = factory.build(tenantId, file.getStorageCode());
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        String tokenUrl = storeService.getAbstractFileService().getSignedUrl(request, file.getBucketName(), file.getFileUrl(), fileKey, file.getFileName(), true, expires);
        return new FileSimpleDTO().setFileTokenUrl(tokenUrl).setMd5(file.getMd5());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileByKey(Long tenantId, String fileKey) {
        File file = fileRepository.getFile(fileKey);
        StoreService storeService = factory.build(tenantId, file.getStorageCode());
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        storeService.deleteFileByKey(file, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateMultipartKey(Long tenantId, String fileKey, MultipartFile file) {
        File newFile = new File();
        InputStream is = null;
        try {
            is = file.getInputStream();
            newFile.setMd5(DigestUtils.md5DigestAsHex(file.getInputStream()))
                    .setFileSize(file.getSize())
                    .setFileKey(fileKey);
            File oldFile = fileRepository.getFile(fileKey);
            StoreService storeService = factory.build(tenantId, oldFile.getStorageCode());
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            return storeService.updateFileByKey(oldFile, newFile, is);
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateByteKey(Long tenantId, String fileKey, byte[] byteFile) {
        File newFile = new File();
        try (InputStream inputStream = new ByteArrayInputStream(byteFile);
             InputStream is = new ByteArrayInputStream(byteFile)) {
            // 组合文件对象
            newFile.setMd5(DigestUtils.md5DigestAsHex(inputStream))
                    .setFileKey(fileKey)
                    .setFileSize((long) byteFile.length);
            File oldFile = fileRepository.getFile(fileKey);
            StoreService storeService = factory.build(tenantId, oldFile.getStorageCode());
            Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
            return storeService.updateFileByKey(oldFile, newFile, is);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
        }
    }

    @Override
    public void downloadByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey) {
        File file = fileRepository.getFile(fileKey);
        Assert.notNull(file, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        StoreService storeService = factory.build(tenantId, file.getStorageCode());
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        storeService.getAbstractFileService().download(request, response, file.getBucketName(), file.getFileUrl(), fileKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copyByKey(Long tenantId, String fileKey, String destinationBucketName, String destinationFileName) {
        File file = fileRepository.selectOne(new File().setTenantId(tenantId).setFileKey(fileKey));
        Assert.notNull(file, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        if (StringUtils.isBlank(destinationBucketName)) {
            destinationBucketName = file.getBucketName();
        }
        if (StringUtils.isBlank(destinationFileName)) {
            destinationFileName = file.getFileName();
        }
        StoreService storeService = factory.build(tenantId, file.getStorageCode());
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        File newFile = storeService.copy(file, HfleConstant.DEFAULT_ATTACHMENT_UUID, destinationBucketName, destinationFileName);
        return newFile.getFileKey();
    }

    @Override
    public void decryptDownloadByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey, String password) {
        File file = fileRepository.getFile(fileKey);
        StoreService storeService = factory.build(tenantId, file.getStorageCode());
        Assert.notNull(storeService, HfleMessageConstant.ERROR_FILE_STORE_CONFIG);
        storeService.getAbstractFileService().decryptDownload(request, response, file.getBucketName(), file.getFileUrl(), fileKey, password);
    }
}
