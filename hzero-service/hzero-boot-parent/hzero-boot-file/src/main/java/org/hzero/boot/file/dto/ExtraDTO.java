package org.hzero.boot.file.dto;

/**
 * onlyOffice额外参数
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/27 9:03
 */
public class ExtraDTO {

    private String type;
    private Long tenantId;
    private String bucketName;
    private String storageCode;

    public String getType() {
        return type;
    }

    public ExtraDTO setType(String type) {
        this.type = type;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ExtraDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public ExtraDTO setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public ExtraDTO setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    @Override
    public String toString() {
        return "ExtraDTO{" +
                "type='" + type + '\'' +
                ", tenantId=" + tenantId +
                ", bucketName='" + bucketName + '\'' +
                ", storageCode='" + storageCode + '\'' +
                '}';
    }
}
