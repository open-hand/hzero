package org.hzero.boot.file.dto;

/**
 * description
 *
 * @author fanghan.liu 2020/06/05 10:08
 */
public class OnlyOfficeFileDTO {

    private Long tenantId;
    /**
     * onlyOffice文件唯一标识
     */
    private String key;

    /**
     * 文件url，当type类型为url时使用
     */
    private String url;

    /**
     * 文件key，当type类型为key时使用
     */
    private String fileKey;

    /**
     * 桶名，当type类型为url时使用
     */
    private String bucketName;

    /**
     * 存储配置，当type类型为url时使用
     */
    private String storageCode;

    /**
     * 文件存储来源类型，url或者key
     */
    private String type;

    public Long getTenantId() {
        return tenantId;
    }

    public OnlyOfficeFileDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getKey() {
        return key;
    }

    public OnlyOfficeFileDTO setKey(String key) {
        this.key = key;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public OnlyOfficeFileDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public OnlyOfficeFileDTO setFileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public OnlyOfficeFileDTO setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public OnlyOfficeFileDTO setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    public String getType() {
        return type;
    }

    public OnlyOfficeFileDTO setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "OnlyOfficeFileDTO{" +
                "tenantId=" + tenantId +
                ", key='" + key + '\'' +
                ", url='" + url + '\'' +
                ", fileKey='" + fileKey + '\'' +
                ", bucketName='" + bucketName + '\'' +
                ", storageCode='" + storageCode + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
