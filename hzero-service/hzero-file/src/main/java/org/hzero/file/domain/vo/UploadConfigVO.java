package org.hzero.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 文件上传配置VO
 *
 * @author shuangfei.zhu@hand-china.com 2018/10/23 13:58
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadConfigVO {

    private Long uploadConfigId;
    private Long tenantId;
    private String bucketName;
    private String directory;
    private String contentType;
    private String storageUnit;
    private Long storageSize;
    private String fileFormat;
    private Integer multipleFileFlag;

    public Long getUploadConfigId() {
        return uploadConfigId;
    }

    public void setUploadConfigId(Long uploadConfigId) {
        this.uploadConfigId = uploadConfigId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getDirectory() {
        return directory;
    }

    public UploadConfigVO setDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public Long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Integer getMultipleFileFlag() {
        return multipleFileFlag;
    }

    public void setMultipleFileFlag(Integer multipleFileFlag) {
        this.multipleFileFlag = multipleFileFlag;
    }

    @Override
    public String toString() {
        return "UploadConfigVO{" +
                "uploadConfigId=" + uploadConfigId +
                ", tenantId=" + tenantId +
                ", bucketName='" + bucketName + '\'' +
                ", directory='" + directory + '\'' +
                ", contentType='" + contentType + '\'' +
                ", storageUnit='" + storageUnit + '\'' +
                ", storageSize=" + storageSize +
                ", fileFormat='" + fileFormat + '\'' +
                ", multipleFileFlag=" + multipleFileFlag +
                '}';
    }
}
