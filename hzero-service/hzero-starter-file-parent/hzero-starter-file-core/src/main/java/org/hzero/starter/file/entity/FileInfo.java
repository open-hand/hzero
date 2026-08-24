package org.hzero.starter.file.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/18 17:07
 */
public class FileInfo {

    @ApiModelProperty("附件集UUID")
    private String attachmentUuid;
    @ApiModelProperty("上传目录")
    private String directory;
    @ApiModelProperty("文件地址")
    private String fileUrl;
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("文件名称")
    private String fileName;
    @ApiModelProperty("文件大小")
    private Long fileSize;
    @ApiModelProperty("文件目录")
    private String bucketName;
    @ApiModelProperty("对象KEY")
    private String fileKey;
    @ApiModelProperty("租户Id")
    private Long tenantId;
    @ApiModelProperty("文件MD5")
    private String md5;
    @ApiModelProperty("存储编码")
    private String storageCode;
    @ApiModelProperty("来源类型")
    private String sourceType;
    @ApiModelProperty("服务器编码")
    private String serverCode;

    public String getAttachmentUuid() {
        return attachmentUuid;
    }

    public FileInfo setAttachmentUuid(String attachmentUuid) {
        this.attachmentUuid = attachmentUuid;
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public FileInfo setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public FileInfo setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public FileInfo setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileInfo setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public FileInfo setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public FileInfo setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public FileInfo setFileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public FileInfo setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public FileInfo setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public FileInfo setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    public String getSourceType() {
        return sourceType;
    }

    public FileInfo setSourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public FileInfo setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }
}
