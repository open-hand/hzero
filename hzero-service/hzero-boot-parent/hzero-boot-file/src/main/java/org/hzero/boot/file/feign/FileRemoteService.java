package org.hzero.boot.file.feign;

import java.util.Date;
import java.util.List;

import feign.Response;
import org.hzero.boot.file.dto.FileParamsDTO;
import org.hzero.boot.file.feign.fallback.FileRemoteServiceFallback;
import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务远程调用
 *
 * @author shuangfei.zhu@hand-china.com 2019/3/11
 */
@FeignClient(value = HZeroService.File.NAME, fallback = FileRemoteServiceFallback.class)
public interface FileRemoteService {

    /**
     * 获取文件UUID
     *
     * @param organizationId 租户ID
     * @return Map
     */
    @PostMapping("/v1/{organizationId}/files/uuid")
    ResponseEntity<String> getAttachUUID(@PathVariable("organizationId") Long organizationId);

    /**
     * 上传文件(Multipart)
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名称
     * @param directory      文件夹
     * @param fileName       文件名称
     * @param docType        特殊文件类型标识
     * @param storageCode    存储配置编码
     * @param multipartFile  文件
     * @return 文件url
     */
    @PostMapping(value = "/v1/{organizationId}/files/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@PathVariable("organizationId") Long organizationId,
                                      @RequestParam("bucketName") String bucketName,
                                      @RequestParam(value = "directory", required = false) String directory,
                                      @RequestParam(value = "fileName", required = false) String fileName,
                                      @RequestParam(value = "docType", defaultValue = "0") Integer docType,
                                      @RequestParam(value = "storageCode", required = false) String storageCode,
                                      @RequestPart("file") MultipartFile multipartFile);

    /**
     * 上传文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      目录
     * @param fileName       文件名
     * @param fileType       文件类型
     * @param storageCode    存储配置编码
     * @param byteFile       文件byte
     * @return URL地址
     */
    @PostMapping("/v1/{organizationId}/files/byte")
    ResponseEntity<String> uploadByteFile(@PathVariable("organizationId") Long organizationId,
                                          @RequestParam("bucketName") String bucketName,
                                          @RequestParam(value = "directory", required = false) String directory,
                                          @RequestParam("fileName") String fileName,
                                          @RequestParam(value = "fileType", required = false) String fileType,
                                          @RequestParam(value = "storageCode", required = false) String storageCode,
                                          @RequestBody byte[] byteFile);

    /**
     * 上传附件文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      目录
     * @param attachmentUUID 附件UUID
     * @param fileName       文件名
     * @param docType        特殊文件类型标识
     * @param storageCode    存储配置编码
     * @param multipartFile  文件信息
     * @return URL
     */
    @PostMapping(value = "/v1/{organizationId}/files/attachment/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadAttachMultipartFile(@PathVariable("organizationId") Long organizationId,
                                                     @RequestParam("bucketName") String bucketName,
                                                     @RequestParam(value = "directory", required = false) String directory,
                                                     @RequestParam("attachmentUUID") String attachmentUUID,
                                                     @RequestParam(value = "fileName", required = false) String fileName,
                                                     @RequestParam(value = "docType", defaultValue = "0") Integer docType,
                                                     @RequestParam(value = "storageCode", required = false) String storageCode,
                                                     @RequestPart("file") MultipartFile multipartFile);

    /**
     * 基于Byte上传附件文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      目录
     * @param fileName       文件名
     * @param attachmentUUID 附件UUID
     * @param fileType       文件类型
     * @param storageCode    存储配置编码
     * @param byteFile       文件byte
     * @return URL
     */
    @PostMapping("/v1/{organizationId}/files/attachment/byte")
    ResponseEntity<String> uploadAttachByteFile(@PathVariable("organizationId") Long organizationId,
                                                @RequestParam("bucketName") String bucketName,
                                                @RequestParam(value = "directory", required = false) String directory,
                                                @RequestParam("fileName") String fileName,
                                                @RequestParam("attachmentUUID") String attachmentUUID,
                                                @RequestParam(value = "fileType", required = false) String fileType,
                                                @RequestParam(value = "storageCode", required = false) String storageCode,
                                                @RequestBody byte[] byteFile);

    /**
     * "获取文件权限Token
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param storageCode    存储配置编码
     * @param url            文件URL
     * @param expires        授权时长
     * @return 带权限签名的URL
     */
    @GetMapping("/v1/{organizationId}/files/signedUrl")
    ResponseEntity<String> getSignedUrl(@PathVariable("organizationId") Long organizationId,
                                        @RequestParam("bucketName") String bucketName,
                                        @RequestParam(value = "storageCode", required = false) String storageCode,
                                        @RequestParam("url") String url,
                                        @RequestParam(value = "expires", required = false) Long expires);

    /**
     * 获取附件文件列表
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @return List<FileDTO>
     */
    @GetMapping("/v1/{organizationId}/files/{attachmentUUID}/file")
    ResponseEntity<String> getFileList(@PathVariable("organizationId") Long organizationId,
                                       @RequestParam(value = "bucketName", required = false) String bucketName,
                                       @PathVariable("attachmentUUID") String attachmentUUID);

    /**
     * 获取指定附件ID的文件列表
     *
     * @param organizationId  租户ID
     * @param bucketName      桶名
     * @param attachmentUuids 附件UUID
     * @param page            page
     * @param size            size
     * @return List<FileDTO>
     */
    @GetMapping("/v1/{organizationId}/files/attachments")
    ResponseEntity<String> getAttachmentFiles(@PathVariable("organizationId") Long organizationId,
                                              @RequestParam(value = "bucketName", required = false) String bucketName,
                                              @RequestParam("attachmentUuids") String attachmentUuids,
                                              @RequestParam("page") Integer page,
                                              @RequestParam("size") Integer size);

    /**
     * 校验附件UUID下文件的数量
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @return 数量
     */
    @GetMapping("/v1/{organizationId}/files/{attachmentUUID}/count")
    ResponseEntity<String> fileCount(@PathVariable("organizationId") Long organizationId,
                                     @RequestParam(value = "bucketName", required = false) String bucketName,
                                     @PathVariable("attachmentUUID") String attachmentUUID);

    /**
     * 校验多个附件ID下是否有文件
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名
     * @param uuidList       文件UUID
     * @return Map
     */
    @PostMapping("/v1/{organizationId}/files/check-uuids")
    ResponseEntity<String> checkUUid(@PathVariable("organizationId") Long organizationId,
                                     @RequestParam(value = "bucketName", required = false) String bucketName,
                                     @RequestBody List<String> uuidList);

    /**
     * 根据URL集合批量获取文件信息
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param urls           url集合
     * @return List<FileDTO>
     */
    @PostMapping("/v1/{organizationId}/files")
    ResponseEntity<String> selectFile(@PathVariable("organizationId") Long organizationId,
                                      @RequestParam("bucketName") String bucketName,
                                      @RequestBody List<String> urls);

    /**
     * 更新文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件url
     * @param storageCode    存储配置编码
     * @param multipartFile  文件
     * @return url
     */
    @PostMapping(value = "/v1/{organizationId}/files/multipart-displacement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> updateMultipartFileByURL(@PathVariable("organizationId") Long organizationId,
                                                    @RequestParam("bucketName") String bucketName,
                                                    @RequestParam("url") String url,
                                                    @RequestParam(value = "storageCode", required = false) String storageCode,
                                                    @RequestPart("file") MultipartFile multipartFile);

    /**
     * 更新文件(Byte)
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件url
     * @param storageCode    存储配置编码
     * @param byteFile       文件
     * @return URL
     */
    @PostMapping("/v1/{organizationId}/files/byte-displacement")
    ResponseEntity<String> updateByteFileByURL(@PathVariable("organizationId") Long organizationId,
                                               @RequestParam("bucketName") String bucketName,
                                               @RequestParam("url") String url,
                                               @RequestParam(value = "storageCode", required = false) String storageCode,
                                               @RequestBody byte[] byteFile);

    /**
     * 删除对应URL的文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param storageCode    存储配置编码
     * @param urls           url集合
     * @return ResponseEntity
     */
    @PostMapping("/v1/{organizationId}/files/delete-by-url")
    ResponseEntity<String> deleteFileByUrl(@PathVariable("organizationId") Long organizationId,
                                           @RequestParam("bucketName") String bucketName,
                                           @RequestParam(value = "storageCode", required = false) String storageCode,
                                           @RequestBody List<String> urls);

    /**
     * 删除AttachmentUUID下URL对应的文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @param storageCode    存储配置编码
     * @param urls           url集合
     * @return ResponseEntity
     */
    @PostMapping("/v1/{organizationId}/files/delete-by-uuidurl")
    ResponseEntity<String> deleteFileByAttachmentUUIDURL(@PathVariable("organizationId") Long organizationId,
                                                         @RequestParam(value = "bucketName", required = false) String bucketName,
                                                         @RequestParam("attachmentUUID") String attachmentUUID,
                                                         @RequestParam(value = "storageCode", required = false) String storageCode,
                                                         @RequestBody List<String> urls);

    /**
     * 删除AttachmentUUID下URL对应的文件，不能删空
     *
     * @param organizationId 租户Id
     * @param bucketName     桶名
     * @param attachmentUUID 附件UUID
     * @param storageCode    存储配置编码
     * @param urls           文件url
     * @return ResponseEntity
     */
    @PostMapping("/v1/{organizationId}/files/delete-by-uuidurl/not-null")
    ResponseEntity<String> deleteFileByAttachmentUUIDUrlNotNull(@PathVariable("organizationId") Long organizationId,
                                                                @RequestParam(value = "bucketName", required = false) String bucketName,
                                                                @RequestParam("attachmentUUID") String attachmentUUID,
                                                                @RequestParam(value = "storageCode", required = false) String storageCode,
                                                                @RequestBody List<String> urls);

    /**
     * 删除AttachmentUUID下所有文件
     *
     * @param organizationId  租户ID
     * @param bucketName      桶名
     * @param storageCode     存储配置编码
     * @param attachmentUUIDs 附件UUID集合
     * @return ResponseEntity
     */
    @PostMapping("/v1/{organizationId}/files/delete-by-uuid")
    ResponseEntity<String> deleteFileByAttachmentUUID(@PathVariable("organizationId") Long organizationId,
                                                      @RequestParam(value = "bucketName", required = false) String bucketName,
                                                      @RequestParam(value = "storageCode", required = false) String storageCode,
                                                      @RequestBody List<String> attachmentUUIDs);

    /**
     * 下载文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param storageCode    存储配置编码
     * @param url            文件url路径
     * @return response
     */
    @GetMapping(value = "/v1/{organizationId}/files/download", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response downloadByUrl(@PathVariable("organizationId") Long organizationId,
                           @RequestParam("bucketName") String bucketName,
                           @RequestParam(value = "storageCode", required = false) String storageCode,
                           @RequestParam("url") String url);

    /**
     * 内部下载文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param storageCode    存储配置编码
     * @param url            文件url路径
     * @return response
     */
    @GetMapping(value = "/v1/{organizationId}/files/download/inner", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response downloadByUrlInner(@PathVariable("organizationId") Long organizationId,
                                @RequestParam("bucketName") String bucketName,
                                @RequestParam(value = "storageCode", required = false) String storageCode,
                                @RequestParam("url") String url);

    /**
     * 解密并下载文件
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param url            文件url路径
     * @param storageCode    存储配置编码
     * @param password       密钥
     * @return response
     */
    @GetMapping(value = "/v1/{organizationId}/files/decrypt/download", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response decryptDownloadByUrl(@PathVariable("organizationId") Long organizationId,
                                  @RequestParam("bucketName") String bucketName,
                                  @RequestParam("url") String url,
                                  @RequestParam(value = "storageCode", required = false) String storageCode,
                                  @RequestParam(value = "password", required = false) String password);

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
    @PostMapping("/v1/{organizationId}/files/copy-by-url")
    ResponseEntity<String> copyByUrl(@PathVariable("organizationId") Long organizationId,
                                     @RequestParam("url") String url,
                                     @RequestParam("bucketName") String bucketName,
                                     @RequestParam(value = "destinationBucketName", required = false) String destinationBucketName,
                                     @RequestParam(value = "destinationFileName", required = false) String destinationFileName);

    /**
     * 复制文件
     *
     * @param organizationId 租户ID
     * @param fileParamsDTO  文件参数
     * @return Map
     */
    @PostMapping(value = "/v1/{organizationId}/files/copy-file", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<String> copyFile(@PathVariable("organizationId") Long organizationId,
                                    @RequestBody FileParamsDTO fileParamsDTO);

    /**
     * 基于Multipart上传文件,生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      目录
     * @param fileName       文件名
     * @param docType        特殊文件类型标识
     * @param storageCode    存储配置编码
     * @param multipartFile  文件
     * @return FileSimpleDTO
     */
    @PostMapping(value = "/v1/{organizationId}/files/secret-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadMultipartFileWithMD5(@PathVariable("organizationId") Long organizationId,
                                                      @RequestParam("bucketName") String bucketName,
                                                      @RequestParam(value = "directory", required = false) String directory,
                                                      @RequestParam(value = "fileName", required = false) String fileName,
                                                      @RequestParam(value = "docType", defaultValue = "0") Integer docType,
                                                      @RequestParam(value = "storageCode", required = false) String storageCode,
                                                      @RequestPart("file") MultipartFile multipartFile);

    /**
     * 基于Byte上传文件,生成MD5
     *
     * @param organizationId 租户ID
     * @param bucketName     桶名
     * @param directory      目录
     * @param fileName       文件名
     * @param fileType       文件类型
     * @param storageCode    存储配置编码
     * @param byteFile       文件
     * @return FileSimpleDTO
     */
    @PostMapping("/v1/{organizationId}/files/secret-byte")
    ResponseEntity<String> uploadByteFileWithMD5(@PathVariable("organizationId") Long organizationId,
                                                 @RequestParam("bucketName") String bucketName,
                                                 @RequestParam(value = "directory", required = false) String directory,
                                                 @RequestParam("fileName") String fileName,
                                                 @RequestParam(value = "fileType", required = false) String fileType,
                                                 @RequestParam(value = "storageCode", required = false) String storageCode,
                                                 @RequestBody byte[] byteFile);

    /**
     * 通过文件KEY获取文件授权url
     *
     * @param organizationId 租户ID
     * @param fileKey        文件kEY
     * @param expires        授权时长
     * @return FileSimpleDTO
     */
    @GetMapping("/v1/{organizationId}/files/file-url")
    ResponseEntity<String> getFileUrl(@PathVariable("organizationId") Long organizationId,
                                      @RequestParam("fileKey") String fileKey,
                                      @RequestParam(value = "expires", required = false) Long expires);

    /**
     * 根据文件key删除文件
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @return ResponseEntity
     */
    @DeleteMapping("/v1/{organizationId}/files/delete-by-key")
    ResponseEntity<String> deleteFileByKey(@PathVariable("organizationId") Long organizationId,
                                           @RequestParam("fileKey") String fileKey);

    /**
     * 根据key更新文件(Multipart)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param file           文件
     * @return ResponseEntity
     */
    @PostMapping(value = "/v1/{organizationId}/files/secret-multipart-displacement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> updateMultipartFileByKey(@PathVariable("organizationId") Long organizationId,
                                                    @RequestParam("fileKey") String fileKey,
                                                    @RequestPart("file") MultipartFile file);

    /**
     * 根据key更新文件(Byte)
     *
     * @param organizationId 租户ID
     * @param fileKey        文件KEY
     * @param file           文件
     * @return ResponseEntity
     */
    @PostMapping("/v1/{organizationId}/files/secret-byte-displacement")
    ResponseEntity<String> updateByteFileByKey(@PathVariable("organizationId") Long organizationId,
                                               @RequestParam("fileKey") String fileKey,
                                               @RequestBody byte[] file);

    /**
     * 根据文件key下载文件
     *
     * @param organizationId 租户Id
     * @param fileKey        文件KEY
     * @return response
     */
    @GetMapping(value = "/v1/{organizationId}/files/download-by-key", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response downloadByKey(@PathVariable("organizationId") Long organizationId,
                           @RequestParam("fileKey") String fileKey);

    /**
     * 根据文件key解密并下载文件
     *
     * @param organizationId 租户Id
     * @param fileKey        文件KEY
     * @param password       密钥
     * @return response
     */
    @GetMapping(value = "/v1/{organizationId}/files/decrypt/download-by-key", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response decryptDownloadByKey(@PathVariable("organizationId") Long organizationId,
                                  @RequestParam("fileKey") String fileKey,
                                  @RequestParam(value = "password", required = false) String password);

    /**
     * 根据文件key为pdf添加水印
     *
     * @param organizationId 租户ID
     * @param fileKey        文件key
     * @param watermarkCode  水印配置编码
     * @param context        水印内容(文本或图片key)
     * @return 水印后的pdf
     */
    @GetMapping(value = "/v1/{organizationId}/watermark/with-config/key", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response watermarkByKey(@PathVariable("organizationId") Long organizationId,
                            @RequestParam("fileKey") String fileKey,
                            @RequestParam("watermarkCode") String watermarkCode,
                            @RequestParam(value = "context", required = false) String context);

    /**
     * 根据文件key为pdf添加水印
     *
     * @param organizationId 租户ID
     * @param bucketName     文件桶
     * @param storageCode    存储配置编码
     * @param url            文件url
     * @param watermarkCode  水印配置编码
     * @param context        水印内容(文本或图片url)
     * @param contextBucket  水印图片的桶
     * @return 水印后的pdf
     */
    @GetMapping(value = "/v1/{organizationId}/watermark/with-config/url", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Response watermarkByUrl(@PathVariable("organizationId") Long organizationId,
                            @RequestParam("bucketName") String bucketName,
                            @RequestParam(value = "storageCode", required = false) String storageCode,
                            @RequestParam("url") String url,
                            @RequestParam("watermarkCode") String watermarkCode,
                            @RequestParam(value = "context", required = false) String context,
                            @RequestParam(value = "contextBucket", required = false) String contextBucket);

    /**
     * 查询文件列表
     *
     * @param tenantId       租户ID
     * @param bucketName     桶名
     * @param directory      目录
     * @param fileType       文件类型
     * @param attachmentUUID 附件ID
     * @param fileName       文件名
     * @param fileFormat     文件类型
     * @param fileMinSize    文件最小
     * @param fileMinUnit    最小单位
     * @param fileMaxSize    文件最大
     * @param fileMaxUnit    最大单位
     * @param fromCreateDate 创建时间从
     * @param toCreateDate   创建时间至
     * @param sourceType     来源类型
     * @param serverCode     服务编码
     * @param storageCode    存储配置编码
     * @param page           页码
     * @param size           分页大小
     * @return 文件列表
     */
    @GetMapping("/v1/files/summary")
    ResponseEntity<String> pageFileList(@RequestParam(value = "tenantId", required = false) Long tenantId,
                                        @RequestParam(value = "bucketName", required = false) String bucketName,
                                        @RequestParam(value = "directory", required = false) String directory,
                                        @RequestParam(value = "fileType", required = false) String fileType,
                                        @RequestParam(value = "attachmentUUID", required = false) String attachmentUUID,
                                        @RequestParam(value = "fileName", required = false) String fileName,
                                        @RequestParam(value = "fileFormat", required = false) String fileFormat,
                                        @RequestParam(value = "fileMinSize", required = false) Long fileMinSize,
                                        @RequestParam(value = "fileMinUnit", required = false) String fileMinUnit,
                                        @RequestParam(value = "fileMaxSize", required = false) Long fileMaxSize,
                                        @RequestParam(value = "fileMaxUnit", required = false) String fileMaxUnit,
                                        @RequestParam(value = "fromCreateDate", required = false) Date fromCreateDate,
                                        @RequestParam(value = "toCreateDate", required = false) Date toCreateDate,
                                        @RequestParam(value = "sourceType", required = false) String sourceType,
                                        @RequestParam(value = "serverCode", required = false) String serverCode,
                                        @RequestParam(value = "storageCode", required = false) String storageCode,
                                        @RequestParam(value = "page", required = false) Integer page,
                                        @RequestParam(value = "size", required = false) Integer size);
}
