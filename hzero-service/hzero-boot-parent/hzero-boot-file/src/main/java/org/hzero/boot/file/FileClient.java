package org.hzero.boot.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import feign.Response;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.file.constant.BootFileConstant;
import org.hzero.boot.file.dto.FileDTO;
import org.hzero.boot.file.dto.FileParamsDTO;
import org.hzero.boot.file.dto.FileSimpleDTO;
import org.hzero.boot.file.feign.FileRemoteService;
import org.hzero.boot.file.util.AesUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FilenameUtils;
import org.hzero.core.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;

/**
 * 文件服务客户端类
 * <p>
 * （重载方法之间尽量不要互相调用,方便排查方法的问题）
 *
 * @author xianzhi.chen@hand-china.com 2019年1月31日下午1:05:07
 */
@SuppressWarnings("all")
public class FileClient {

    private static final Logger logger = LoggerFactory.getLogger(FileClient.class);

    private FileRemoteService fileRemoteService;

    @Autowired
    public FileClient(FileRemoteService fileRemoteService) {
        this.fileRemoteService = fileRemoteService;
    }

    /**
     * 获取文件授权URL
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param storageCode    存储配置编码
     * @param url            文件URL
     * @param expires        授权时长(单位秒)
     * @return 带授权签名的URL
     */
    public String getSignedUrl(Long organizationId, String bucketName, String storageCode, String url, Long expires) {
        return ResponseUtils.getResponse(fileRemoteService.getSignedUrl(organizationId, bucketName, storageCode, url, expires), String.class);
    }

    /**
     * 获取文件授权URL
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param storageCode    存储配置编码
     * @param url            文件URL
     * @return 带授权签名的URL
     */
    public String getSignedUrl(Long organizationId, String bucketName, String storageCode, String url) {
        return getSignedUrl(organizationId, bucketName, storageCode, url, null);
    }

    /**
     * 获取文件授权URL
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件URL
     * @return 带授权签名的URL
     */
    public String getSignedUrl(Long organizationId, String bucketName, String url) {
        return getSignedUrl(organizationId, bucketName, null, url, null);
    }

    /**
     * 根据文件key,获取授权URL
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param expires        授权时长(单位秒)
     * @return FileSimpleDTO
     */
    public FileSimpleDTO getSignedUrl(Long organizationId, String fileKey, Long expires) {
        return ResponseUtils.getResponse(fileRemoteService.getFileUrl(organizationId, fileKey, expires), FileSimpleDTO.class);
    }

    /**
     * 根据文件key,获取授权URL
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @return FileSimpleDTO
     */
    public FileSimpleDTO getSignedUrl(Long organizationId, String fileKey) {
        return getSignedUrl(organizationId, fileKey, (Long) null);
    }

    /**
     * 获取附件UUID
     *
     * @param organizationId 租户ID
     * @return UUID
     */
    public String getAttachmentUUID(Long organizationId) {
        Map<String, String> map = ResponseUtils.getResponse(fileRemoteService.getAttachUUID(organizationId), new TypeReference<Map<String, String>>() {
        });
        return map.get(BaseConstants.FIELD_CONTENT);
    }

    /**
     * 上传附件文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param attachmentUUID 附件UUID
     * @param storageCode    存储配置编码
     * @param byteFile       文件byte
     * @return URL
     */
    public String uploadAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, String fileType, String storageCode, byte[] byteFile) {
        return ResponseUtils.getResponse(fileRemoteService.uploadAttachByteFile(organizationId, bucketName, directory, fileName, attachmentUUID, fileType, storageCode, byteFile), String.class);
    }

    /**
     * 上传附件文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param attachmentUUID 附件UUID
     * @param byteFile       文件byte
     * @return URL
     */
    public String uploadAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, String fileType, byte[] byteFile) {
        return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, fileName, fileType, null, byteFile);
    }

    /**
     * 上传附件文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param attachmentUUID 附件UUID
     * @param byteFile       文件byte
     * @return URL
     */
    public String uploadAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, byte[] byteFile) {
        return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, fileName, null, null, byteFile);
    }

    /**
     * 加密并上传附件文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param attachmentUUID 附件UUID
     * @param byteFile       文件byte
     * @return URL
     */
    public String uploadEncryptAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, byte[] byteFile) {
        return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, fileName, null, null, AesUtils.encrypt(byteFile));
    }

    /**
     * 指定密钥加密并上传附件文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param attachmentUUID 附件UUID
     * @param byteFile       文件byte
     * @param password       密钥
     * @return URL
     */
    public String uploadEncryptAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, byte[] byteFile, String password) {
        return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, fileName, null, null, AesUtils.encrypt(byteFile, password));
    }

    /**
     * 上传附件文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param attachmentUUID 附件UUID
     * @param fileName       文件名
     * @param multipartFile  文件
     * @return URL
     */
    public String uploadAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, MultipartFile multipartFile) {
        return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, fileName, BaseConstants.Flag.NO, null, multipartFile);
    }

    /**
     * 上传附件文件(Multipart),指定特殊文件类型
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param attachmentUUID 附件UUID
     * @param fileName       文件名
     * @param docType        特殊文件类型标识 0:按照文件原有类型，1:指定特殊类型
     * @param multipartFile  文件信息
     * @return URL
     */
    public String uploadAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, int docType, String storageCode, MultipartFile multipartFile) {
        return ResponseUtils.getResponse(fileRemoteService.uploadAttachMultipartFile(organizationId, bucketName, directory, attachmentUUID, fileName, docType, storageCode, multipartFile), String.class);
    }

    /**
     * 上传附件文件(Multipart),指定特殊文件类型
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param attachmentUUID 附件UUID
     * @param fileName       文件名
     * @param docType        特殊文件类型标识 0:按照文件原有类型，1:指定特殊类型
     * @param multipartFile  文件信息
     * @return URL
     */
    public String uploadAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, String fileName, int docType, MultipartFile multipartFile) {
        return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, fileName, docType, null, multipartFile);
    }


    /**
     * 加密并上传附件文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param attachmentUUID 附件UUID
     * @param multipartFile  文件
     * @return URL
     */
    public String uploadEncryptAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, MultipartFile multipartFile) {
        try {
            byte[] data = multipartFile.getBytes();
            return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, multipartFile.getOriginalFilename(), null, null, AesUtils.encrypt(data));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 指定密钥加密并上传附件文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param attachmentUUID 附件UUID
     * @param multipartFile  文件
     * @param password       密钥
     * @return URL
     */
    public String uploadEncryptAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, MultipartFile multipartFile, String password) {
        try {
            byte[] data = multipartFile.getBytes();
            return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, multipartFile.getOriginalFilename(), null, null, AesUtils.encrypt(data, password));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 上传附件文件(Multipart), 不指定文件名
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param attachmentUUID 附件UUID
     * @param multipartFile  文件
     * @return URL
     */
    public String uploadAttachment(Long organizationId, String bucketName, String directory, String attachmentUUID, MultipartFile multipartFile) {
        return uploadAttachment(organizationId, bucketName, directory, attachmentUUID, multipartFile.getOriginalFilename(), BaseConstants.Flag.NO, null, multipartFile);
    }

    /**
     * 上传文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param fileType       文件contentType
     * @param storageCode    存储配置编码
     * @param byteFile       文件byte
     * @return URL地址
     */
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, String fileType, String storageCode, byte[] byteFile) {
        return ResponseUtils.getResponse(fileRemoteService.uploadByteFile(organizationId, bucketName, directory, fileName, fileType, storageCode, byteFile), String.class);
    }

    /**
     * 上传文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param fileType       文件contentType
     * @param byteFile       文件byte
     * @return URL地址
     */
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, String fileType, byte[] byteFile) {
        return uploadFile(organizationId, bucketName, directory, fileName, fileType, null, byteFile);
    }

    /**
     * 上传文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param byteFile       文件byte
     * @return URL地址
     */
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, byte[] byteFile) {
        return uploadFile(organizationId, bucketName, directory, fileName, null, null, byteFile);
    }

    /**
     * 加密上传文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param byteFile       文件byte
     * @return URL地址
     */
    public String uploadEncryptFile(Long organizationId, String bucketName, String directory, String fileName, byte[] byteFile) {
        return uploadFile(organizationId, bucketName, directory, fileName, null, null, AesUtils.encrypt(byteFile));
    }

    /**
     * 指定密钥加密上传文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param byteFile       文件byte
     * @return URL地址
     */
    public String uploadEncryptFile(Long organizationId, String bucketName, String directory, String fileName, byte[] byteFile, String password) {
        return uploadFile(organizationId, bucketName, directory, fileName, null, null, AesUtils.encrypt(byteFile, password));
    }

    /**
     * 上传文件(Multipart),指定特殊类型
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名称
     * @param directory      文件夹
     * @param fileName       文件名称
     * @param docType        特殊文件类型标识 0:按照文件原有类型，1:指定特殊类型
     * @param multipartFile  文件
     * @return 文件url
     */
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, int docType, String storageCode, MultipartFile multipartFile) {
        return ResponseUtils.getResponse(fileRemoteService.uploadFile(organizationId, bucketName, directory, fileName, docType, storageCode, multipartFile), String.class);
    }

    /**
     * 上传文件(Multipart),指定特殊类型
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名称
     * @param directory      文件夹
     * @param fileName       文件名称
     * @param docType        特殊文件类型标识 0:按照文件原有类型，1:指定特殊类型
     * @param multipartFile  文件
     * @return 文件url
     */
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, int docType, MultipartFile multipartFile) {
        return uploadFile(organizationId, bucketName, directory, fileName, docType, null, multipartFile);
    }

    /**
     * 上传文件(Multipart)
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名称
     * @param directory      文件夹
     * @param fileName       文件名称
     * @param multipartFile  文件
     * @return 文件url
     */
    public String uploadFile(Long organizationId, String bucketName, String directory, String fileName, MultipartFile multipartFile) {
        return uploadFile(organizationId, bucketName, directory, fileName, BaseConstants.Flag.NO, null, multipartFile);
    }

    /**
     * 加密上传文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @return URL地址
     */
    public String uploadEncryptFile(Long organizationId, String bucketName, String directory, MultipartFile multipartFile) {
        try {
            byte[] data = multipartFile.getBytes();
            return uploadFile(organizationId, bucketName, directory, multipartFile.getOriginalFilename(), null, null, AesUtils.encrypt(data));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 指定密钥加密上传文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param password       密钥
     * @return URL地址
     */
    public String uploadEncryptFile(Long organizationId, String bucketName, String directory, MultipartFile multipartFile, String password) {
        try {
            byte[] data = multipartFile.getBytes();
            return uploadFile(organizationId, bucketName, directory, multipartFile.getOriginalFilename(), null, null, AesUtils.encrypt(data, password));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 上传文件(Multipart),不指定文件名
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名称
     * @param directory      文件夹
     * @param multipartFile  文件
     * @return 文件url
     */
    public String uploadFile(Long organizationId, String bucketName, String directory, MultipartFile multipartFile) {
        return uploadFile(organizationId, bucketName, directory, multipartFile.getOriginalFilename(), BaseConstants.Flag.NO, null, multipartFile);
    }

    /**
     * 上传文件(Byte)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param storageCode    存储配置编码
     * @param byteFile       文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, String fileType, String storageCode, byte[] byteFile) {
        return ResponseUtils.getResponse(fileRemoteService.uploadByteFileWithMD5(organizationId, bucketName, directory, fileName, fileType, storageCode, byteFile), FileSimpleDTO.class);
    }

    /**
     * 上传文件(Byte)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param byteFile       文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, String fileType, byte[] byteFile) {
        return uploadFileWithMD5(organizationId, bucketName, directory, fileName, fileType, null, byteFile);
    }

    /**
     * 上传文件(Byte)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param byteFile       文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, byte[] byteFile) {
        return uploadFileWithMD5(organizationId, bucketName, directory, fileName, null, null, byteFile);
    }

    /**
     * 加密上传文件(Byte)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param byteFile       文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadEncryptFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, byte[] byteFile) {
        return uploadFileWithMD5(organizationId, bucketName, directory, fileName, null, null, AesUtils.encrypt(byteFile));
    }

    /**
     * 指定密钥加密上传文件(Byte)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param byteFile       文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadEncryptFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, byte[] byteFile, String password) {
        return uploadFileWithMD5(organizationId, bucketName, directory, fileName, null, null, AesUtils.encrypt(byteFile, password));
    }

    /**
     * 上传文件(MultipartFile)生成MD5,指定特殊类型
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param docType        特殊文件类型标识 0:按照文件原有类型，1:指定特殊类型
     * @param storageCode    存储配置编码
     * @param multipartFile  文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, int docType, String storageCode, MultipartFile multipartFile) {
        return ResponseUtils.getResponse(fileRemoteService.uploadMultipartFileWithMD5(organizationId, bucketName, directory, fileName, docType, storageCode, multipartFile), FileSimpleDTO.class);
    }

    /**
     * 上传文件(MultipartFile)生成MD5,指定特殊类型
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param docType        特殊文件类型标识 0:按照文件原有类型，1:指定特殊类型
     * @param multipartFile  文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, int docType, MultipartFile multipartFile) {
        return uploadFileWithMD5(organizationId, bucketName, directory, fileName, docType, null, multipartFile);
    }

    /**
     * 上传文件(MultipartFile)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param fileName       文件名
     * @param multipartFile  文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadFileWithMD5(Long organizationId, String bucketName, String directory, String fileName, MultipartFile multipartFile) {
        return uploadFileWithMD5(organizationId, bucketName, directory, fileName, BaseConstants.Flag.NO, null, multipartFile);
    }

    /**
     * 上传文件(MultipartFile)生成MD5，不指定文件名
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param multipartFile  文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadFileWithMD5(Long organizationId, String bucketName, String directory, MultipartFile multipartFile) {
        return uploadFileWithMD5(organizationId, bucketName, directory, null, BaseConstants.Flag.NO, null, multipartFile);
    }

    /**
     * 加密上传文件(MultipartFile)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param multipartFile  文件
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadEncryptFileWithMD5(Long organizationId, String bucketName, String directory, MultipartFile multipartFile) {
        try {
            byte[] data = multipartFile.getBytes();
            return uploadFileWithMD5(organizationId, bucketName, directory, multipartFile.getOriginalFilename(), null, null, AesUtils.encrypt(data));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 指定密钥加密上传文件(MultipartFile)生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      文件夹
     * @param multipartFile  文件
     * @param password       密钥
     * @return FileSimpleDTO
     */
    public FileSimpleDTO uploadEncryptFileWithMD5(Long organizationId, String bucketName, String directory, MultipartFile multipartFile, String password) {
        try {
            byte[] data = multipartFile.getBytes();
            return uploadFileWithMD5(organizationId, bucketName, directory, multipartFile.getOriginalFilename(), null, null, AesUtils.encrypt(data, password));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 文件下载，文件服务使用了重定向，但是feign的自动重定向要求两个请求的的Protocol要一致
     * 所以文件服务在重定向 阿里 华为 等云服务时不会自动重定向
     * <p>
     * <p>
     * 通过文件KEY下载文件
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @return Response
     */
    public Response downloadFileResponse(Long organizationId, String fileKey) {
        return fileRemoteService.downloadByKey(organizationId, fileKey);
    }

    /**
     * 通过文件KEY下载文件并解密
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param password       密钥（不指定传null）
     * @return Response
     */
    public Response downloadDecryptFileResponse(Long organizationId, String fileKey, String password) {
        return fileRemoteService.decryptDownloadByKey(organizationId, fileKey, password);
    }

    /**
     * 通过文件URL下载文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶
     * @param url            文件URL
     * @return Response
     */
    public Response downloadFileResponse(Long organizationId, String bucketName, String storageCode, String url) {
        return fileRemoteService.downloadByUrlInner(organizationId, bucketName, storageCode, url);
    }

    /**
     * 通过文件URL下载文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶
     * @param url            文件URL
     * @return Response
     */
    public Response downloadFileResponse(Long organizationId, String bucketName, String url) {
        return downloadFileResponse(organizationId, bucketName, null, url);
    }

    /**
     * 通过文件URL下载文件并解密
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            url
     * @param password       密钥（不指定传null）
     * @return Response
     */
    public Response downloadDecryptFileResponse(Long organizationId, String bucketName, String url, String storageCode, String password) {
        return fileRemoteService.decryptDownloadByUrl(organizationId, bucketName, url, storageCode, password);
    }

    /**
     * 通过文件URL下载文件并解密
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            url
     * @param password       密钥（不指定传null）
     * @return Response
     */
    public Response downloadDecryptFileResponse(Long organizationId, String bucketName, String url, String password) {
        return downloadDecryptFileResponse(organizationId, bucketName, url, null, password);
    }

    /**
     * 通过文件KEY下载文件
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @return InputStream
     */
    public InputStream downloadFile(Long organizationId, String fileKey) {
        Response response = downloadFileResponse(organizationId, fileKey);
        Assert.isTrue(response.status() / 100 == 2, BootFileConstant.ErrorCode.DOWNLOAD);
        Response.Body body = response.body();
        try {
            return body.asInputStream();
        } catch (Exception ex) {
            logger.error("Download file error", ex);
            return null;
        }
    }

    /**
     * 通过文件KEY下载文件并解密
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param password       密钥（不指定传null）
     * @return Response
     */
    public InputStream downloadDecryptFile(Long organizationId, String fileKey, String password) {
        Response response = downloadDecryptFileResponse(organizationId, fileKey, password);
        Response.Body body = response.body();
        InputStream inputStream = null;
        try {
            inputStream = body.asInputStream();
        } catch (Exception ex) {
            logger.error("Download file error", ex);
        }
        return inputStream;
    }

    /**
     * 通过文件URL下载文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件URL
     * @return InputStream
     */
    public InputStream downloadFile(Long organizationId, String bucketName, String url) {
        Response response = downloadFileResponse(organizationId, bucketName, null, url);
        Assert.isTrue(response.status() / 100 == 2, BootFileConstant.ErrorCode.DOWNLOAD);
        Response.Body body = response.body();
        InputStream inputStream = null;
        try {
            inputStream = body.asInputStream();
        } catch (Exception ex) {
            logger.error("Download file error", ex);
        }
        return inputStream;
    }

    /**
     * 通过文件URL下载文件并解密
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            url
     * @param password       密钥（不指定传null）
     * @return Response
     */
    public InputStream downloadDecryptFile(Long organizationId, String bucketName, String url, String password) {
        Response response = downloadDecryptFileResponse(organizationId, bucketName, url, null, password);
        Response.Body body = response.body();
        InputStream inputStream = null;
        try {
            inputStream = body.asInputStream();
        } catch (Exception ex) {
            logger.error("Download file error", ex);
        }
        return inputStream;
    }

    /**
     * 根据文件KEY更新文件(Byte)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param byteFile       文件
     * @return 文件url
     */
    public String updateFile(Long organizationId, String fileKey, byte[] byteFile) {
        return ResponseUtils.getResponse(fileRemoteService.updateByteFileByKey(organizationId, fileKey, byteFile), String.class);
    }

    /**
     * 根据文件KEY更新文件并加密(Byte)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param byteFile       文件
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String fileKey, byte[] byteFile) {
        return updateFile(organizationId, fileKey, AesUtils.encrypt(byteFile));
    }

    /**
     * 根据文件KEY更新文件并指定密钥加密(Byte)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param byteFile       文件
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String fileKey, byte[] byteFile, String password) {
        return updateFile(organizationId, fileKey, AesUtils.encrypt(byteFile, password));
    }

    /**
     * 根据文件KEY更新文件(MultipartFile)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param multipartFile  文件
     * @return 文件url
     */
    public String updateFile(Long organizationId, String fileKey, MultipartFile multipartFile) {
        return ResponseUtils.getResponse(fileRemoteService.updateMultipartFileByKey(organizationId, fileKey, multipartFile), String.class);
    }

    /**
     * 根据文件KEY更新文件并加密(MultipartFile)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param multipartFile  文件
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String fileKey, MultipartFile multipartFile) {
        try {
            byte[] data = multipartFile.getBytes();
            return updateFile(organizationId, fileKey, AesUtils.encrypt(data));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 根据文件KEY更新文件并加密(MultipartFile)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param multipartFile  文件
     * @param password       密钥
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String fileKey, MultipartFile multipartFile, String password) {
        try {
            byte[] data = multipartFile.getBytes();
            return updateFile(organizationId, fileKey, AesUtils.encrypt(data, password));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 根据文件URL更新文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件URL
     * @param storageCode    存储配置编码
     * @param byteFile       文件
     * @return 文件url
     */
    public String updateFile(Long organizationId, String bucketName, String url, String storageCode, byte[] byteFile) {
        return ResponseUtils.getResponse(fileRemoteService.updateByteFileByURL(organizationId, bucketName, url, storageCode, byteFile), String.class);
    }

    /**
     * 根据文件URL更新文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件URL
     * @param byteFile       文件
     * @return 文件url
     */
    public String updateFile(Long organizationId, String bucketName, String url, byte[] byteFile) {
        return updateFile(organizationId, bucketName, url, null, byteFile);
    }

    /**
     * 根据文件URL更新文件并加密(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶
     * @param url            url
     * @param byteFile       文件
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String bucketName, String url, byte[] byteFile) {
        return updateFile(organizationId, bucketName, url, null, AesUtils.encrypt(byteFile));
    }

    /**
     * 根据文件URL更新文件并指定密钥加密(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶
     * @param url            url
     * @param byteFile       文件
     * @param password       密钥
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String bucketName, String url, byte[] byteFile, String password) {
        return updateFile(organizationId, bucketName, url, null, AesUtils.encrypt(byteFile, password));
    }

    /**
     * 根据文件URL更新文件(MultipartFile)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件URL
     * @param storageCode    存储配置编码
     * @param multipartFile  文件
     * @return 文件url
     */
    public String updateFile(Long organizationId, String bucketName, String url, String storageCode, MultipartFile multipartFile) {
        return ResponseUtils.getResponse(fileRemoteService.updateMultipartFileByURL(organizationId, bucketName, url, storageCode, multipartFile), String.class);
    }

    /**
     * 根据文件URL更新文件(MultipartFile)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件URL
     * @param multipartFile  文件
     * @return 文件url
     */
    public String updateFile(Long organizationId, String bucketName, String url, MultipartFile multipartFile) {
        return updateFile(organizationId, bucketName, url, null, multipartFile);
    }

    /**
     * 根据文件URL更新文件并加密(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶
     * @param url            url
     * @param multipartFile  文件
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String bucketName, String url, MultipartFile multipartFile) {
        try {
            byte[] data = multipartFile.getBytes();
            return updateFile(organizationId, bucketName, url, null, AesUtils.encrypt(data));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 根据文件URL更新文件并指定密钥加密(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶
     * @param url            url
     * @param multipartFile  文件
     * @param password       密钥
     * @return 文件url
     */
    public String updateEncryptFile(Long organizationId, String bucketName, String url, MultipartFile multipartFile, String password) {
        try {
            byte[] data = multipartFile.getBytes();
            return updateFile(organizationId, bucketName, url, null, AesUtils.encrypt(data, password));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 删除AttachmentUUID下所有文件
     *
     * @param organizationId  租户ID
     * @param bucketName      桶名
     * @param storageCode     存储配置编码
     * @param attachmentUUIDs 附件UUID集合
     */
    public void deleteFile(Long organizationId, String bucketName, List<String> attachmentUUIDs, String storageCode) {
        ResponseUtils.getResponse(fileRemoteService.deleteFileByAttachmentUUID(organizationId, bucketName, storageCode, attachmentUUIDs), String.class);
    }

    /**
     * 删除AttachmentUUID下所有文件
     *
     * @param organizationId  租户ID
     * @param bucketName      桶名
     * @param attachmentUUIDs 附件UUID集合
     */
    public void deleteFile(Long organizationId, String bucketName, List<String> attachmentUUIDs) {
        deleteFile(organizationId, bucketName, attachmentUUIDs, null);
    }

    /**
     * 删除AttachmentUUID下URL对应的文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @param urls           文件URL集合
     */
    public void deleteFile(Long organizationId, String bucketName, String attachmentUUID, List<String> urls, String storageCode) {
        ResponseUtils.getResponse(fileRemoteService.deleteFileByAttachmentUUIDURL(organizationId, bucketName, attachmentUUID, storageCode, urls), String.class);
    }

    /**
     * 删除AttachmentUUID下URL对应的文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @param urls           文件URL集合
     */
    public void deleteFile(Long organizationId, String bucketName, String attachmentUUID, List<String> urls) {
        deleteFile(organizationId, bucketName, attachmentUUID, urls, null);
    }

    /**
     * 删除AttachmentUUID下URL对应的文件，不能删空
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @param urls           文件url
     */
    public void deleteFileNotNull(Long organizationId, String bucketName, String attachmentUUID, String storageCode, List<String> urls) {
        ResponseUtils.getResponse(fileRemoteService.deleteFileByAttachmentUUIDUrlNotNull(organizationId, bucketName, attachmentUUID, storageCode, urls), String.class);
    }

    /**
     * 删除AttachmentUUID下URL对应的文件，不能删空
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @param urls           文件url
     */
    public void deleteFileNotNull(Long organizationId, String bucketName, String attachmentUUID, List<String> urls) {
        deleteFileNotNull(organizationId, bucketName, attachmentUUID, null, urls);
    }

    /**
     * 根据文件KEY删除文件
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     */
    public void deleteFileByKey(Long organizationId, String fileKey) {
        ResponseUtils.getResponse(fileRemoteService.deleteFileByKey(organizationId, fileKey), String.class);
    }

    /**
     * 根据文件URL删除文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param storageCode    存储配置编码
     * @param urls           文件URL集合
     */
    public void deleteFileByUrl(Long organizationId, String bucketName, String storageCode, List<String> urls) {
        ResponseUtils.getResponse(fileRemoteService.deleteFileByUrl(organizationId, bucketName, storageCode, urls), String.class);
    }

    /**
     * 根据文件URL删除文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param urls           文件URL集合
     */
    public void deleteFileByUrl(Long organizationId, String bucketName, List<String> urls) {
        deleteFileByUrl(organizationId, bucketName, null, urls);
    }

    /**
     * 获取附件文件列表
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @return List<FileDTO> 文件列表
     */
    public List<FileDTO> getAttachmentFiles(Long organizationId, String bucketName, String attachmentUUID) {
        return ResponseUtils.getResponse(fileRemoteService.getFileList(organizationId, bucketName, attachmentUUID), new TypeReference<List<FileDTO>>() {
        });
    }

    /**
     * 获取附件文件列表
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @return List<FileDTO> 文件列表
     */
    public List<FileDTO> getAttachmentFiles(Long organizationId, String bucketName, List<String> attachmentUUIDs) {
        return getAttachmentFiles(organizationId, bucketName, attachmentUUIDs, 0, 0).getContent();
    }

    /**
     * 分页获取附件文件列表
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @return List<FileDTO> 文件列表
     */
    public Page<FileDTO> getAttachmentFiles(Long organizationId, String bucketName, List<String> attachmentUUIDs, int page, int size) {
        return ResponseUtils.getResponse(fileRemoteService.getAttachmentFiles(organizationId, bucketName, StringUtils.join(attachmentUUIDs, BaseConstants.Symbol.COMMA), page, size), new TypeReference<Page<FileDTO>>() {
        });
    }

    /**
     * 根据URL集合批量获取文件信息
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param urls           文件URL集合
     * @return List<FileDTO> 文件集合
     */
    public List<FileDTO> getFiles(Long organizationId, String bucketName, List<String> urls) {
        return ResponseUtils.getResponse(fileRemoteService.selectFile(organizationId, bucketName, urls), new TypeReference<List<FileDTO>>() {
        });
    }

    /**
     * 获取附件文件个数
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @return Integer 条数
     */
    public Integer getAttachmentFileCount(Long organizationId, String bucketName, String attachmentUUID) {
        return ResponseUtils.getResponse(fileRemoteService.fileCount(organizationId, bucketName, attachmentUUID), Integer.class);
    }

    /**
     * 校验多个附件ID下是否有文件
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名
     * @param uuidList       文件UUID
     * @return Map
     */
    public Map<String, Boolean> checkUUid(Long organizationId, String bucketName, List<String> uuidList) {
        return ResponseUtils.getResponse(fileRemoteService.checkUUid(organizationId, bucketName, uuidList), new TypeReference<Map<String, Boolean>>() {
        });
    }

    /**
     * 根据url复制文件
     *
     * @param organizationId        租户ID
     * @param url                   文件url
     * @param bucketName            原桶名
     * @param destinationBucketName 目标桶名（不变不传）
     * @param destinationFileName   目标文件名（不变不传）
     * @return 复制的文件url
     */
    public String copyFileByUrl(Long organizationId, String url, String bucketName, String destinationBucketName, String destinationFileName) {
        return ResponseUtils.getResponse(fileRemoteService.copyByUrl(organizationId, url, bucketName, destinationBucketName, destinationFileName), String.class);
    }

    /**
     * 复制文件
     *
     * @param organizationId   租户ID
     * @param fileParamsDTO    文件参数
     * @param listUuid         UUID集合
     * @param sourceBucketName 来源桶名
     * @return Map
     */
    public Map<String, String> copyFile(Long organizationId, FileParamsDTO fileParamsDTO, List<String> listUuid, String sourceBucketName) {
        if (fileParamsDTO == null) {
            fileParamsDTO = new FileParamsDTO();
        }
        fileParamsDTO.setUuidList(listUuid).setBucketName(sourceBucketName);
        return ResponseUtils.getResponse(fileRemoteService.copyFile(organizationId, fileParamsDTO), new TypeReference<Map<String, String>>() {
        });
    }

    /**
     * 获取文件名
     *
     * @param string 文件url或者文件key
     * @return 文件名
     */
    public String getFileName(String string) {
        return FilenameUtils.getFileName(string);
    }

    /**
     * 根据文件key为pdf添加水印(使用默认水印)
     *
     * @param tenantId      租户ID
     * @param fileKey       文件key
     * @param watermarkCode 水印配置
     * @return 水印后的pdf
     */
    public Response watermarkByKey(Long tenantId, String fileKey, String watermarkCode) {
        return fileRemoteService.watermarkByKey(tenantId, fileKey, watermarkCode, null);
    }

    /**
     * 根据文件key为pdf添加水印
     *
     * @param tenantId      租户ID
     * @param fileKey       文件key
     * @param watermarkCode 水印配置
     * @param context       自定义水印内容(文字或图片key)
     * @return 水印后的pdf
     */
    public Response watermarkByKey(Long tenantId, String fileKey, String watermarkCode, String context) {
        return fileRemoteService.watermarkByKey(tenantId, fileKey, watermarkCode, context);
    }

    /**
     * 根据文件url为pdf添加水印(使用默认水印)
     *
     * @param tenantId      租户ID
     * @param bucketName    桶名
     * @param storageCode   存储配置编码
     * @param url           文件url
     * @param watermarkCode 水印配置
     * @return 水印后的pdf
     */
    public Response watermarkByUrl(Long tenantId, String bucketName, String storageCode, String url, String watermarkCode) {
        return fileRemoteService.watermarkByUrl(tenantId, bucketName, storageCode, url, watermarkCode, null, null);
    }

    /**
     * 根据文件url为pdf添加水印
     *
     * @param tenantId      租户ID
     * @param bucketName    桶名
     * @param storageCode   存储配置编码
     * @param url           文件url
     * @param watermarkCode 水印配置
     * @param context       自定义水印内容(文件或图片url)
     * @param contextBucket 水印图片的bucket
     * @return 水印后的pdf
     */
    public Response watermarkByUrl(Long tenantId, String bucketName, String storageCode, String url, String watermarkCode, String context, String contextBucket) {
        return fileRemoteService.watermarkByUrl(tenantId, bucketName, storageCode, url, watermarkCode, context, contextBucket);
    }

    /**
     * 获取文件列表
     *
     * @param fileParams 参数
     * @param page       页码
     * @param size       分页大小
     * @return
     */
    public Page<FileDTO> pageFiles(FileParamsDTO fileParams, Integer page, Integer size) {
        ResponseEntity<String> result = fileRemoteService.pageFileList(fileParams.getTenantId(), fileParams.getBucketName(),
                fileParams.getDirectory(), fileParams.getFileType(), fileParams.getAttachmentUUID(), fileParams.getFileName(),
                fileParams.getFileFormat(), fileParams.getFileMinSize(), fileParams.getFileMinUnit(),
                fileParams.getFileMaxSize(), fileParams.getFileMaxUnit(), fileParams.getFromCreateDate(), fileParams.getToCreateDate(),
                fileParams.getSourceType(), fileParams.getServerCode(), fileParams.getStorageCode(), page, size);
        return ResponseUtils.getResponse(result, new TypeReference<Page<FileDTO>>() {
        });
    }
}