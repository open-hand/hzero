package org.hzero.file.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 文件汇总查询接收参数
 *
 * @author zhiying.dong@hand-china.com 2018/12/19 18:41
 */
public class FileParamsDTO {

    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("分组")
    private String bucketName;
    @ApiModelProperty("上传目录")
    private String directory;
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("批号")
    private String attachmentUUID;
    @ApiModelProperty("文件名")
    private String fileName;
    @ApiModelProperty("文件格式")
    private String fileFormat;
    @ApiModelProperty("文件最小")
    private Long fileMinSize;
    @ApiModelProperty("文件最小单位")
    private String fileMinUnit;
    @ApiModelProperty("文件最大")
    private Long fileMaxSize;
    @ApiModelProperty("文件最大单位")
    private String fileMaxUnit;
    @ApiModelProperty("上传人")
    private String realName;
    @ApiModelProperty("上传时间从")
    private Date fromCreateDate;
    @ApiModelProperty("上传时间至")
    private Date toCreateDate;
    @ApiModelProperty("来源类型")
    private String sourceType;
    @ApiModelProperty("服务器编码")
    private String serverCode;

    private List<String> uuidList;

    public Long getTenantId() {
        return tenantId;
    }

    public FileParamsDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public FileParamsDTO setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public FileParamsDTO setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public FileParamsDTO setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getAttachmentUUID() {
        return attachmentUUID;
    }

    public FileParamsDTO setAttachmentUUID(String attachmentUUID) {
        this.attachmentUUID = attachmentUUID;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileParamsDTO setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public FileParamsDTO setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
        return this;
    }

    public Long getFileMinSize() {
        return fileMinSize;
    }

    public FileParamsDTO setFileMinSize(Long fileMinSize) {
        this.fileMinSize = fileMinSize;
        return this;
    }

    public String getFileMinUnit() {
        return fileMinUnit;
    }

    public FileParamsDTO setFileMinUnit(String fileMinUnit) {
        this.fileMinUnit = fileMinUnit;
        return this;
    }

    public Long getFileMaxSize() {
        return fileMaxSize;
    }

    public FileParamsDTO setFileMaxSize(Long fileMaxSize) {
        this.fileMaxSize = fileMaxSize;
        return this;
    }

    public String getFileMaxUnit() {
        return fileMaxUnit;
    }

    public FileParamsDTO setFileMaxUnit(String fileMaxUnit) {
        this.fileMaxUnit = fileMaxUnit;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public FileParamsDTO setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public Date getFromCreateDate() {
        return fromCreateDate;
    }

    public FileParamsDTO setFromCreateDate(Date fromCreateDate) {
        this.fromCreateDate = fromCreateDate;
        return this;
    }

    public Date getToCreateDate() {
        return toCreateDate;
    }

    public FileParamsDTO setToCreateDate(Date toCreateDate) {
        this.toCreateDate = toCreateDate;
        return this;
    }

    public String getSourceType() {
        return sourceType;
    }

    public FileParamsDTO setSourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public FileParamsDTO setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public List<String> getUuidList() {
        return uuidList;
    }

    public FileParamsDTO setUuidList(List<String> uuidList) {
        this.uuidList = uuidList;
        return this;
    }

    @Override
    public String toString() {
        return "FileParamsDTO{" +
                "tenantId=" + tenantId +
                ", bucketName='" + bucketName + '\'' +
                ", directory='" + directory + '\'' +
                ", fileType='" + fileType + '\'' +
                ", attachmentUUID='" + attachmentUUID + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileFormat='" + fileFormat + '\'' +
                ", fileMinSize=" + fileMinSize +
                ", fileMinUnit='" + fileMinUnit + '\'' +
                ", fileMaxSize=" + fileMaxSize +
                ", fileMaxUnit='" + fileMaxUnit + '\'' +
                ", realName='" + realName + '\'' +
                ", fromCreateDate=" + fromCreateDate +
                ", toCreateDate=" + toCreateDate +
                ", sourceType='" + sourceType + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", uuidList=" + uuidList +
                '}';
    }
}
