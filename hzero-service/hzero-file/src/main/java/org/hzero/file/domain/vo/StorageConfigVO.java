package org.hzero.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * StorageConfig
 *
 * @author zhiying.dong@hand-china.com 2018/10/17 16:19
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageConfigVO {
    private Long storageConfigId;

    private Integer storageType;
    private String domain;
    private String endPoint;
    private String accessKeyId;
    private String accessKeySecret;
    private Integer appId;
    private String region;
    private Integer defaultFlag;
    private Long tenantId;
    private String accessControl;
    private String bucketPrefix;
    private String storageCode;
    private String prefixStrategy;
    private Integer createBucketFlag;

    public Long getStorageConfigId() {
        return storageConfigId;
    }

    public StorageConfigVO setStorageConfigId(Long storageConfigId) {
        this.storageConfigId = storageConfigId;
        return this;
    }

    public Integer getStorageType() {
        return storageType;
    }

    public StorageConfigVO setStorageType(Integer storageType) {
        this.storageType = storageType;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public StorageConfigVO setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public StorageConfigVO setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public StorageConfigVO setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public StorageConfigVO setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public Integer getAppId() {
        return appId;
    }

    public StorageConfigVO setAppId(Integer appId) {
        this.appId = appId;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public StorageConfigVO setRegion(String region) {
        this.region = region;
        return this;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public StorageConfigVO setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public StorageConfigVO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getAccessControl() {
        return accessControl;
    }

    public StorageConfigVO setAccessControl(String accessControl) {
        this.accessControl = accessControl;
        return this;
    }

    public String getBucketPrefix() {
        return bucketPrefix;
    }

    public StorageConfigVO setBucketPrefix(String bucketPrefix) {
        this.bucketPrefix = bucketPrefix;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public StorageConfigVO setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    public String getPrefixStrategy() {
        return prefixStrategy;
    }

    public StorageConfigVO setPrefixStrategy(String prefixStrategy) {
        this.prefixStrategy = prefixStrategy;
        return this;
    }

    public Integer getCreateBucketFlag() {
        return createBucketFlag;
    }

    public StorageConfigVO setCreateBucketFlag(Integer createBucketFlag) {
        this.createBucketFlag = createBucketFlag;
        return this;
    }

    @Override
    public String toString() {
        return "StorageConfigVO{" +
                "storageConfigId=" + storageConfigId +
                ", storageType=" + storageType +
                ", domain='" + domain + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", appId=" + appId +
                ", region='" + region + '\'' +
                ", defaultFlag=" + defaultFlag +
                ", tenantId=" + tenantId +
                ", accessControl='" + accessControl + '\'' +
                ", bucketPrefix='" + bucketPrefix + '\'' +
                ", storageCode='" + storageCode + '\'' +
                ", prefixStrategy='" + prefixStrategy + '\'' +
                ", createBucketFlag=" + createBucketFlag +
                '}';
    }
}
