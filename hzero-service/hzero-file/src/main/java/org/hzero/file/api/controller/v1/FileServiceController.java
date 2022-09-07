package org.hzero.file.api.controller.v1;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.core.util.UUIDUtils;
import org.hzero.file.api.dto.FileDTO;
import org.hzero.file.api.dto.FileParamsDTO;
import org.hzero.file.api.dto.FileSimpleDTO;
import org.hzero.file.app.service.FileService;
import org.hzero.file.config.FileSwaggerApiConfig;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.util.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * 租户级文件服务接口类
 *
 * @author xianzhi.chen@hand-china.com 2018年6月22日下午4:36:37
 */
@Api(tags = FileSwaggerApiConfig.FILE_SERVICE)
@RestController("fileServiceController.v1")
@RequestMapping("/v1/{organizationId}/files")
public class FileServiceController extends BaseController {

    private final FileService fileService;
    private final FileRepository fileRepository;

    @Autowired
    public FileServiceController(FileService fileService,
                                 FileRepository fileRepository) {
        this.fileService = fileService;
        this.fileRepository = fileRepository;
    }


    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "获取上传附件UUID")
    @PostMapping("/uuid")
    public ResponseEntity<Map<String, String>> getAttachUUID(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId) {
        String uuid = UUIDUtils.generateTenantUUID(organizationId);
        Map<String, String> map = new HashMap<>(3);
        map.put(BaseConstants.FIELD_CONTENT, uuid);
        return Results.success(map);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "基于Multipart上传文件")
    @PostMapping("/multipart")
    public ResponseEntity<String> uploadFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件名") @RequestParam(value = "fileName", required = false) String fileName,
            @ApiParam(value = "默认类型 1:固定,0:不固定") @RequestParam(value = "docType", defaultValue = "0") Integer docType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestParam("file") MultipartFile multipartFile) {
        return Results.success(fileService.uploadMultipart(organizationId, bucketName, null, directory, fileName, docType, storageCode, multipartFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "基于Byte上传文件")
    @PostMapping("/byte")
    public ResponseEntity<String> uploadByteFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件名", required = true) @RequestParam("fileName") String fileName,
            @ApiParam(value = "文件类型") @RequestParam(value = "fileType", required = false) String fileType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestBody byte[] byteFile) {
        CodeUtils.checkFileName(fileName);
        return Results.success(fileService.uploadByte(organizationId, bucketName, null, directory, fileName, fileType, storageCode, byteFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "基于Multipart上传附件文件")
    @PostMapping("/attachment/multipart")
    public ResponseEntity<String> uploadAttachMultipartFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "附件UUID", required = true) @RequestParam("attachmentUUID") String attachmentUuid,
            @ApiParam(value = "文件名") @RequestParam(value = "fileName", required = false) String fileName,
            @ApiParam(value = "默认类型 1:固定,0:不固定") @RequestParam(value = "docType", defaultValue = "0") Integer docType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestParam("file") MultipartFile multipartFile) {
        return Results.success(fileService.uploadMultipart(organizationId, bucketName, attachmentUuid, directory, fileName, docType, storageCode, multipartFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "基于Byte上传附件文件")
    @PostMapping("/attachment/byte")
    public ResponseEntity<String> uploadAttachByteFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件名", required = true) @RequestParam("fileName") String fileName,
            @ApiParam(value = "附件UUID", required = true) @RequestParam("attachmentUUID") String attachmentUuid,
            @ApiParam(value = "文件类型") @RequestParam(value = "fileType", required = false) String fileType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestBody byte[] byteFile) {
        Assert.isTrue(StringUtils.isNotBlank(fileName), HfleMessageConstant.ERROR_FILE_NAME);
        CodeUtils.checkFileName(fileName);
        return Results.success(fileService.uploadByte(organizationId, bucketName, attachmentUuid, directory, fileName, fileType, storageCode, byteFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "获取文件授权url")
    @GetMapping("/signedUrl")
    public ResponseEntity<String> getSignedUrl(
            HttpServletRequest request,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "文件url", required = true) @RequestParam("url") String url,
            @ApiParam(value = "授权有效时长(单位秒)") @RequestParam(value = "expires", required = false) Long expires) {
        url = CodeUtils.decode(url);
        return Results.success(fileService.getDownloadUrl(request, organizationId, bucketName, storageCode, url, expires));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "重定向文件授权url")
    @GetMapping("/redirect-url")
    public void getRedirectUrl(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "文件url", required = true) @RequestParam("url") String url) throws IOException {
        url = CodeUtils.decode(url);
        response.sendRedirect(fileService.getSignedUrl(request, organizationId, bucketName, storageCode, url));
    }

    @ApiOperation(value = "获取文件列表")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{attachmentUUID}/file")
    public ResponseEntity<List<FileDTO>> getFileList(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "附件UUID", required = true) @PathVariable("attachmentUUID") String attachmentUuid,
            @ApiParam(value = "排序方向") @RequestParam(value = "sortDirection", required = false) String sortDirection) {
        List<FileDTO> list = fileRepository.selectFileByAttachmentUUID(organizationId, bucketName, attachmentUuid,sortDirection);
        return Results.success(list);
    }

    @ApiOperation(value = "获取指定附件ID的文件列表")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/attachments")
    @CustomPageRequest
    public ResponseEntity<Page<FileDTO>> getAttachmentFiles(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "附件UUID") @RequestParam List<String> attachmentUuids,
            @ApiIgnore @SortDefault(value = File.FIELD_FILE_ID, direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(fileRepository.selectFileByAttachmentUUID(pageRequest, organizationId, bucketName, attachmentUuids));
    }

    @ApiOperation(value = "校验附件ID下文件的数量")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{attachmentUUID}/count")
    public ResponseEntity<Integer> fileCount(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "附件UUID", required = true) @PathVariable("attachmentUUID") String attachmentUuid) {
        return Results.success(fileRepository.selectFileCountByAttachmentUUID(organizationId, bucketName, attachmentUuid));
    }

    @ApiOperation(value = "校验多个附件ID下是否有文件")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @PostMapping("/check-uuids")
    public ResponseEntity<Map<String, Boolean>> checkUUid(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "附件UUID", required = true) @RequestBody List<String> uuidList) {
        return Results.success(fileRepository.checkUuid(organizationId, bucketName, uuidList));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据URL批量获取文件信息")
    @PostMapping
    public ResponseEntity<List<FileDTO>> selectFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "文件地址", required = true) @RequestBody List<String> urls) {
        urls = CodeUtils.decode(urls);
        List<FileDTO> list = fileRepository.selectFileByUrls(bucketName, urls);
        return Results.success(list);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新Multipart文件")
    @PostMapping("/multipart-displacement")
    public ResponseEntity<String> updateMultipartFileByURL(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "文件地址", required = true) @RequestParam("url") String url,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestParam("file") MultipartFile multipartFile) {
        url = CodeUtils.decode(url);
        return Results.success(fileService.updateMultipart(organizationId, bucketName, url, storageCode, multipartFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新Byte文件")
    @PostMapping("/byte-displacement")
    public ResponseEntity<String> updateByteFileByURL(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "文件地址", required = true) @RequestParam("url") String url,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestBody byte[] byteFile) {
        url = CodeUtils.decode(url);
        return Results.success(fileService.updateByte(organizationId, bucketName, url, storageCode, byteFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "保存Multipart文件(有url更新文件，没有则新增)")
    @PostMapping("/multipart-save")
    public ResponseEntity<String> saveMultipartFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件地址") @RequestParam(value = "url", required = false) String url,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestParam("file") MultipartFile multipartFile) {
        if (StringUtils.isBlank(url)) {
            // 上传
            return Results.success(fileService.uploadMultipart(organizationId, bucketName, null, directory, null, 0, storageCode, multipartFile));
        } else {
            // 更新
            url = CodeUtils.decode(url);
            return Results.success(fileService.updateMultipart(organizationId, bucketName, url, storageCode, multipartFile));
        }
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "保存Byte文件(有url更新文件，没有则新增)")
    @PostMapping("/byte-save")
    public ResponseEntity<String> saveByteFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件名") @RequestParam("fileName") String fileName,
            @ApiParam(value = "文件类型") @RequestParam(value = "fileType", required = false) String fileType,
            @ApiParam(value = "文件地址") @RequestParam(value = "url", required = false) String url,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestBody byte[] byteFile) {
        if (StringUtils.isBlank(url)) {
            CodeUtils.checkFileName(fileName);
            return Results.success(fileService.uploadByte(organizationId, bucketName, null, directory, fileName, fileType, storageCode, byteFile));
        } else {
            url = CodeUtils.decode(url);
            return Results.success(fileService.updateByte(organizationId, bucketName, url, storageCode, byteFile));
        }
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除对应URL的文件")
    @PostMapping("/delete-by-url")
    public ResponseEntity<Void> deleteFileByUrl(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "文件地址", required = true) @RequestBody List<String> urls) {
        urls = CodeUtils.decode(urls);
        fileService.deleteByUrls(organizationId, bucketName, storageCode, urls);
        return Results.success();
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除AttachmentUUID下URL对应的文件")
    @PostMapping("/delete-by-uuidurl")
    public ResponseEntity<Void> deleteFileByAttachmentUUIDURL(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "attachmentUUID", required = true) @RequestParam("attachmentUUID") String attachmentUuid,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "文件地址", required = true) @RequestBody List<String> urls) {
        urls = CodeUtils.decode(urls);
        fileService.deleteByAttachment(organizationId, bucketName, storageCode, urls, attachmentUuid);
        return Results.success();
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除AttachmentUUID下URL对应的文件，不能删空")
    @PostMapping("/delete-by-uuidurl/not-null")
    public ResponseEntity<Void> deleteFileByAttachmentUUIDUrlNotNull(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "attachmentUUID", required = true) @RequestParam("attachmentUUID") String attachmentUuid,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "文件地址", required = true) @RequestBody List<String> urls) {
        urls = CodeUtils.decode(urls);
        fileService.deleteByAttachmentNotNull(organizationId, bucketName, storageCode, urls, attachmentUuid);
        return Results.success();
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除AttachmentUUID下所有文件")
    @PostMapping("/delete-by-uuid")
    public ResponseEntity<Void> deleteFileByAttachmentUUID(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "attachmentUUID", required = true) @RequestBody List<String> attachmentList) {
        fileService.deleteByAttachments(organizationId, bucketName, storageCode, attachmentList);
        return Results.success();
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "下载文件")
    @GetMapping("/download")
    public void downloadByUrl(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "url", required = true) @RequestParam("url") String url) {
        url = CodeUtils.decode(url);
        fileService.downloadFile(request, response, organizationId, bucketName, storageCode, url);
    }

    @Permission(permissionWithin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "客户端下载文件")
    @GetMapping("/download/inner")
    public void downloadByUrlInner(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "url", required = true) @RequestParam("url") String url) {
        url = CodeUtils.decode(url);
        fileService.downloadFileInner(request, response, organizationId, bucketName, storageCode, url);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "解密并下载文件")
    @GetMapping("/decrypt/download")
    public void decryptDownloadByUrl(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "url", required = true) @RequestParam("url") String url,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "密钥") @RequestParam(value = "password", required = false) String password) {
        url = CodeUtils.decode(url);
        fileService.decryptDownloadFile(request, response, organizationId, bucketName, url, storageCode, password);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据url复制文件")
    @PostMapping("/copy-by-url")
    public ResponseEntity<String> copyByUrl(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "url", required = true) @RequestParam String url,
            @ApiParam(value = "原桶名", required = true) @RequestParam String bucketName,
            @ApiParam(value = "目标桶名") @RequestParam(value = "destinationBucketName", required = false) String destinationBucketName,
            @ApiParam(value = "文件名") @RequestParam(value = "destinationFileName", required = false) String destinationFileName) {
        url = CodeUtils.decode(url);
        return Results.success(fileService.copyFileByUrl(organizationId, url, bucketName, destinationBucketName, destinationFileName));
    }

    @ApiOperation(value = "附件复制")
    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @PostMapping("/copy-file")
    public ResponseEntity<Map<String, String>> copyFile(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "bucketName,uuidList必传，其他为筛选条件") @RequestBody FileParamsDTO fileParamsDTO) {
        fileParamsDTO.setTenantId(organizationId);
        return Results.success(fileService.copyAttachment(organizationId, storageCode, fileParamsDTO));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "基于Multipart上传文件,返回key")
    @PostMapping("/secret-multipart")
    public ResponseEntity<FileSimpleDTO> uploadMultipartFileWithMD5(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件名") @RequestParam(value = "fileName", required = false) String fileName,
            @ApiParam(value = "默认类型 1:固定,0:不固定") @RequestParam(value = "docType", defaultValue = "0") Integer docType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestParam("file") MultipartFile multipartFile) {
        return Results.success(fileService.uploadMultipartKey(organizationId, bucketName, directory, fileName, docType, storageCode, multipartFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "基于Byte上传文件,返回key")
    @PostMapping("/secret-byte")
    public ResponseEntity<FileSimpleDTO> uploadByteFileWithMD5(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "桶名", required = true) @RequestParam("bucketName") String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "文件名", required = true) @RequestParam("fileName") String fileName,
            @ApiParam(value = "文件类型") @RequestParam(value = "fileType", required = false) String fileType,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "上传文件") @RequestBody byte[] byteFile) {
        return Results.success(fileService.uploadByteKey(organizationId, bucketName, directory, fileName, fileType, storageCode, byteFile));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key获取文件授权url")
    @GetMapping("/file-url")
    public ResponseEntity<FileSimpleDTO> getFileUrl(
            HttpServletRequest request,
            @PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey,
            @ApiParam(value = "授权有效时长(单位秒)") @RequestParam(value = "expires", required = false) Long expires) {
        fileKey = CodeUtils.decode(fileKey);
        return Results.success(fileService.getDownloadUrlByKey(request, organizationId, fileKey, expires));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key删除文件")
    @DeleteMapping("/delete-by-key")
    public ResponseEntity<Void> deleteFileByKey(
            @PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey) {
        fileKey = CodeUtils.decode(fileKey);
        fileService.deleteFileByKey(organizationId, fileKey);
        return Results.success();
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key更新文件，Multipart方式")
    @PostMapping("/secret-multipart-displacement")
    public ResponseEntity<String> updateMultipartFileByKey(
            @PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey,
            @RequestParam @ApiParam(value = "上传文件") MultipartFile file) {
        fileKey = CodeUtils.decode(fileKey);
        return Results.success(fileService.updateMultipartKey(organizationId, fileKey, file));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key更新文件，Byte方式")
    @PostMapping("/secret-byte-displacement")
    public ResponseEntity<String> updateByteFileByKey(
            @PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey,
            @RequestBody byte[] file) {
        fileKey = CodeUtils.decode(fileKey);
        return Results.success(fileService.updateByteKey(organizationId, fileKey, file));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key保存文件，Multipart方式(有key更新文件，没有则新增)")
    @PostMapping("/secret-multipart-save")
    public ResponseEntity<String> saveMultipartFileByKey(
            @PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey,
            @RequestParam @ApiParam(value = "上传文件") MultipartFile file) {
        if (StringUtils.isBlank(fileKey)) {
            FileSimpleDTO dto = fileService.uploadMultipartKey(organizationId, bucketName, directory, null, 0, storageCode, file);
            return Results.success(dto.getFileKey());
        } else {
            fileKey = CodeUtils.decode(fileKey);
            return Results.success(fileService.updateMultipartKey(organizationId, fileKey, file));
        }
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key更新文件，Byte方式(有key更新文件，没有则新增)")
    @PostMapping("/secret-byte-save")
    public ResponseEntity<String> saveByteFileByKey(
            @PathVariable @ApiParam(value = "租户ID", required = true) Long organizationId,
            @ApiParam(value = "桶名") @RequestParam(value = "bucketName", required = false) String bucketName,
            @ApiParam(value = "上传目录") @RequestParam(value = "directory", required = false) String directory,
            @ApiParam(value = "存储配置编码") @RequestParam(value = "storageCode", required = false) String storageCode,
            @ApiParam(value = "文件名称") @RequestParam(value = "fileName", required = false) String fileName,
            @ApiParam(value = "文件类型") @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey,
            @RequestBody byte[] file) {
        if (StringUtils.isBlank(fileKey)) {
            FileSimpleDTO dto = fileService.uploadByteKey(organizationId, bucketName, directory, fileName, fileType, storageCode, file);
            return Results.success(dto.getFileKey());
        } else {
            fileKey = CodeUtils.decode(fileKey);
            return Results.success(fileService.updateByteKey(organizationId, fileKey, file));
        }
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key下载文件")
    @GetMapping("/download-by-key")
    public void downloadByKey(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey) {
        fileKey = CodeUtils.decode(fileKey);
        fileService.downloadByKey(request, response, organizationId, fileKey);
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key复制文件")
    @GetMapping("/copy-by-key")
    public ResponseEntity<String> copyByKey(
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @ApiParam(value = "文件Key", required = true) @RequestParam String fileKey,
            @ApiParam(value = "目标桶名") @RequestParam(value = "destinationBucketName", required = false) String destinationBucketName,
            @ApiParam(value = "文件名") @RequestParam(value = "destinationFileName", required = false) String destinationFileName) {
        fileKey = CodeUtils.decode(fileKey);
        return Results.success(fileService.copyByKey(organizationId, fileKey, destinationBucketName, destinationFileName));
    }

    @Permission(permissionLogin = true, level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据key下载文件并解密")
    @GetMapping("/decrypt/download-by-key")
    public void decryptDownloadByKey(
            HttpServletRequest request, HttpServletResponse response,
            @ApiParam(value = "租户ID", required = true) @PathVariable Long organizationId,
            @RequestParam @ApiParam(value = "文件Key", required = true) String fileKey,
            @ApiParam(value = "密钥") @RequestParam(value = "password", required = false) String password) {
        fileKey = CodeUtils.decode(fileKey);
        fileService.decryptDownloadByKey(request, response, organizationId, fileKey, password);
    }
}