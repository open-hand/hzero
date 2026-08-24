package org.hzero.file.app.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.api.dto.FileSimpleDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/11 17:32
 */
public interface FileService {

    /**
     * 上传分片文件
     *
     * @param tenantId    租户
     * @param bucketName  桶名
     * @param directory   目录
     * @param fileName    文件名
     * @param storageCode 存储编码
     * @param filePath    大文件路径
     * @param fileSize    文件大小
     * @return 文件url
     */
    String uploadFragmentFile(Long tenantId, String bucketName, String directory, String fileName, String storageCode, String filePath, Long fileSize);

    /**
     * 上传Multipart文件
     *
     * @param tenantId       租户Id
     * @param bucketName     桶
     * @param attachmentUuid 附件ID
     * @param directory      文件夹
     * @param fileName       文件名
     * @param docType        是否锁定文件
     * @param storageCode    存储编码
     * @param multipartFile  文件
     * @return 文件路径
     */
    String uploadMultipart(Long tenantId, String bucketName, String attachmentUuid, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile);

    /**
     * 上传byte文件
     *
     * @param tenantId       租户
     * @param bucketName     桶
     * @param attachmentUuid 附件ID
     * @param directory      目录
     * @param fileName       文件名
     * @param fileType       文件类型
     * @param storageCode    存储编码
     * @param byteFile       文件
     * @return 文件路径
     */
    String uploadByte(Long tenantId, String bucketName, String attachmentUuid, String directory, String fileName, String fileType, String storageCode, byte[] byteFile);

    /**
     * 获取文件授权url
     *
     * @param request     request
     * @param tenantId    租户Id
     * @param bucketName  桶
     * @param storageCode 存储编码
     * @param url         文件url
     * @param expires     有效时长
     * @return 授权url
     */
    String getSignedUrl(HttpServletRequest request, Long tenantId, String bucketName, String storageCode, String url, Long expires);

    /**
     * 获取文件下载url
     *
     * @param request     request
     * @param tenantId    租户Id
     * @param bucketName  桶
     * @param storageCode 存储编码
     * @param url         文件url
     * @param expires     有效时长
     * @return 授权url
     */
    String getDownloadUrl(HttpServletRequest request, Long tenantId, String bucketName, String storageCode, String url, Long expires);

    /**
     * 获取文件授权url
     *
     * @param request     request
     * @param tenantId    租户Id
     * @param bucketName  桶
     * @param storageCode 存储编码
     * @param url         文件url
     * @return 授权url
     */
    default String getSignedUrl(HttpServletRequest request, Long tenantId, String bucketName, String storageCode, String url) {
        return getSignedUrl(request, tenantId, bucketName, storageCode, url, null);
    }

    /**
     * 更新Multipart文件
     *
     * @param tenantId      租户
     * @param bucketName    桶
     * @param url           文件url
     * @param storageCode   存储编码
     * @param multipartFile 文件
     * @return 文件url
     */
    String updateMultipart(Long tenantId, String bucketName, String url, String storageCode, MultipartFile multipartFile);

    /**
     * 更新Byte文件
     *
     * @param tenantId    租户
     * @param bucketName  桶
     * @param url         文件url
     * @param storageCode 存储编码
     * @param byteFile    文件
     * @return 文件url
     */
    String updateByte(Long tenantId, String bucketName, String url, String storageCode, byte[] byteFile);

    /**
     * 根据文件url删除文件
     *
     * @param tenantId    租户Id
     * @param bucketName  桶
     * @param storageCode 存储编码
     * @param urls        文件url
     */
    void deleteByUrls(Long tenantId, String bucketName, String storageCode, List<String> urls);

    /**
     * 根据单个附件ID删除文件
     *
     * @param tenantId       租户Id
     * @param bucketName     桶
     * @param storageCode    存储编码
     * @param urls           文件url
     * @param attachmentUuid 附件ID
     */
    void deleteByAttachment(Long tenantId, String bucketName, String storageCode, List<String> urls, String attachmentUuid);

    /**
     * 根据单个附件ID删除文件
     *
     * @param tenantId       租户Id
     * @param bucketName     桶
     * @param storageCode    存储编码
     * @param urls           文件url
     * @param attachmentUuid 附件ID
     */
    void deleteByAttachmentNotNull(Long tenantId, String bucketName, String storageCode, List<String> urls, String attachmentUuid);

    /**
     * 根据附件Id删除文件
     *
     * @param tenantId       租户Id
     * @param bucketName     桶
     * @param storageCode    存储编码
     * @param attachmentList 附件Id
     */
    void deleteByAttachments(Long tenantId, String bucketName, String storageCode, List<String> attachmentList);

    /**
     * 下载文件
     *
     * @param request     request
     * @param response    response
     * @param tenantId    租户ID
     * @param bucketName  桶名
     * @param storageCode 存储编码
     * @param url         文件url
     */
    void downloadFile(HttpServletRequest request, HttpServletResponse response, Long tenantId, String bucketName, String storageCode, String url);

    /**
     * 通过文件服务下载文件
     *
     * @param request     request
     * @param response    response
     * @param tenantId    租户ID
     * @param bucketName  桶名
     * @param storageCode 存储编码
     * @param url         文件url
     */
    void downloadFileInner(HttpServletRequest request, HttpServletResponse response, Long tenantId, String bucketName, String storageCode, String url);

    /**
     * 解密下载文件
     *
     * @param request     request
     * @param response    response
     * @param tenantId    租户ID
     * @param bucketName  桶名
     * @param url         文件url
     * @param storageCode 存储编码
     * @param password    密钥
     */
    void decryptDownloadFile(HttpServletRequest request, HttpServletResponse response, Long tenantId, String bucketName, String url, String storageCode, String password);

    /**
     * 文件复制
     *
     * @param tenantId              租户ID
     * @param url                   文件url
     * @param bucketName            原桶名
     * @param destinationBucketName 目标桶名
     * @param destinationFileName   目标文件名
     * @return 新的文件url
     */
    String copyFileByUrl(Long tenantId, String url, String bucketName, String destinationBucketName, String destinationFileName);

    /**
     * 附件复制
     *
     * @param fileParamsDTO 参数
     * @param tenantId      租户ID
     * @param storageCode   存储编码
     * @return 原附件Id, 新附件Id
     */
    Map<String, String> copyAttachment(Long tenantId, String storageCode, FileParamsDTO fileParamsDTO);

    /**
     * 上传Multipart文件返回fileKey
     *
     * @param tenantId      租户
     * @param bucketName    桶
     * @param directory     目录
     * @param fileName      文件名
     * @param docType       是否固定类类型
     * @param storageCode   存储编码
     * @param multipartFile 文件
     * @return 文件信息
     */
    FileSimpleDTO uploadMultipartKey(Long tenantId, String bucketName, String directory, String fileName, Integer docType, String storageCode, MultipartFile multipartFile);

    /**
     * 上传Byte文件返回fileKey
     *
     * @param tenantId    租户
     * @param bucketName  桶
     * @param directory   目录
     * @param fileName    文件名
     * @param fileType    文件类型
     * @param storageCode 存储编码
     * @param byteFile    文件
     * @return 文件信息
     */
    FileSimpleDTO uploadByteKey(Long tenantId, String bucketName, String directory, String fileName, String fileType, String storageCode, byte[] byteFile);

    /**
     * 根据fileKey获取授权Url
     *
     * @param request  request
     * @param tenantId 租户Id
     * @param fileKey  文件Key
     * @param expires  有效时间
     * @return 文件信息
     */
    FileSimpleDTO getSignedUrlByKey(HttpServletRequest request, Long tenantId, String fileKey, Long expires);

    /**
     * 根据fileKey获取授权Url
     *
     * @param request  request
     * @param tenantId 租户Id
     * @param fileKey  文件Key
     * @param expires  有效时间
     * @return 文件信息
     */
    FileSimpleDTO getDownloadUrlByKey(HttpServletRequest request, Long tenantId, String fileKey, Long expires);

    /**
     * 根据fileKey获取授权Url
     *
     * @param request  request
     * @param tenantId 租户Id
     * @param fileKey  文件Key
     * @return 文件信息
     */
    default FileSimpleDTO getSignedUrlByKey(HttpServletRequest request, Long tenantId, String fileKey) {
        return getSignedUrlByKey(request, tenantId, fileKey, null);
    }

    /**
     * 根据key删除文件
     *
     * @param tenantId 租户Id
     * @param fileKey  文件ley
     */
    void deleteFileByKey(Long tenantId, String fileKey);

    /**
     * 根据key更新文件
     *
     * @param tenantId 租户Id
     * @param fileKey  文件ley
     * @param file     文件
     * @return fileKey
     */
    String updateMultipartKey(Long tenantId, String fileKey, MultipartFile file);

    /**
     * 根据key更新文件
     *
     * @param tenantId 租户Id
     * @param fileKey  文件ley
     * @param byteFile 文件
     * @return fileKey
     */
    String updateByteKey(Long tenantId, String fileKey, byte[] byteFile);

    /**
     * 根据key下载文件
     *
     * @param request  request
     * @param response response
     * @param tenantId 租户Id
     * @param fileKey  文件ley
     */
    void downloadByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey);

    /**
     * 根据key复制文件
     *
     * @param tenantId              租户Id
     * @param fileKey               文件ley
     * @param destinationBucketName 目标桶名
     * @param destinationFileName   目标文件名
     * @return 新的文件key
     */
    String copyByKey(Long tenantId, String fileKey, String destinationBucketName, String destinationFileName);

    /**
     * 根据key下载文件并解密
     *
     * @param request  request
     * @param response response
     * @param tenantId 租户Id
     * @param fileKey  文件ley
     * @param password 密钥
     */
    void decryptDownloadByKey(HttpServletRequest request, HttpServletResponse response, Long tenantId, String fileKey, String password);
}