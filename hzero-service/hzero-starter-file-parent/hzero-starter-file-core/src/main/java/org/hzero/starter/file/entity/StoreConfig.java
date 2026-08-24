package org.hzero.starter.file.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/18 17:22
 */
public class StoreConfig {

    @ApiModelProperty("存储类型")
    private Integer storageType;
    @ApiModelProperty("绑定域名")
    private String domain;
    @ApiModelProperty("EndPoint")
    private String endPoint;
    @ApiModelProperty("AccessKeyId")
    private String accessKeyId;
    @ApiModelProperty("AccessKeySecret")
    private String accessKeySecret;
    @ApiModelProperty("腾讯云AppId")
    private Integer appId;
    @ApiModelProperty("腾讯云COS所属地区")
    private String region;
    @ApiModelProperty("默认标识，0:不启用，1:启用")
    private Integer defaultFlag;
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("bucket权限控制")
    private String accessControl;
    @ApiModelProperty("bucket前缀")
    private String bucketPrefix;
    @ApiModelProperty("存储编码")
    private String storageCode;
    @ApiModelProperty("文件名前缀策略")
    private String prefixStrategy;
    @ApiModelProperty("自动创建bucket，0:不启用，1:启用")
    private Integer createBucketFlag;

    public Integer getStorageType() {
        return storageType;
    }

    public StoreConfig setStorageType(Integer storageType) {
        this.storageType = storageType;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public StoreConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public StoreConfig setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public StoreConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public StoreConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public Integer getAppId() {
        return appId;
    }

    public StoreConfig setAppId(Integer appId) {
        this.appId = appId;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public StoreConfig setRegion(String region) {
        this.region = region;
        return this;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public StoreConfig setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public StoreConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getAccessControl() {
        return accessControl;
    }

    public StoreConfig setAccessControl(String accessControl) {
        this.accessControl = accessControl;
        return this;
    }

    public String getBucketPrefix() {
        return bucketPrefix;
    }

    public StoreConfig setBucketPrefix(String bucketPrefix) {
        this.bucketPrefix = bucketPrefix;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public StoreConfig setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    public String getPrefixStrategy() {
        return prefixStrategy;
    }

    public StoreConfig setPrefixStrategy(String prefixStrategy) {
        this.prefixStrategy = prefixStrategy;
        return this;
    }

    public Integer getCreateBucketFlag() {
        return createBucketFlag;
    }

    public StoreConfig setCreateBucketFlag(Integer createBucketFlag) {
        this.createBucketFlag = createBucketFlag;
        return this;
    }
}
