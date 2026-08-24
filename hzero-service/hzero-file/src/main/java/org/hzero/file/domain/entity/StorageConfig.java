package org.hzero.file.domain.entity;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.domain.vo.StorageConfigVO;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.starter.file.constant.FileConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author xianzhi.chen@hand-china.com
 */
@ApiModel("存储配置实体")
@VersionAudit
@ModifyAudit
@Table(name = "hfle_storage_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StorageConfig extends AuditDomain {

    public static final String FIELD_STORAGE_CONFIG_ID = "storageConfigId";
    public static final String FIELD_STORAGE_TYPE = "storageType";
    public static final String FIELD_DOMAIN = "domain";
    public static final String FIELD_END_POINT = "endPoint";
    public static final String FIELD_ACCESS_KEY_ID = "accessKeyId";
    public static final String FIELD_ACCESS_KEY_SECRET = "accessKeySecret";
    public static final String FIELD_APP_ID = "appId";
    public static final String FIELD_REGION = "region";
    public static final String FIELD_DEFAULT_FLAG = "defaultFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ACCESS_CONTROL = "accessControl";
    public static final String FIELD_BUCKET_PREFIX = "bucketPrefix";
    public static final String FIELD_STORAGE_CODE = "storageCode";
    public static final String FIELD_PREFIX_STRATEGY = "prefixStrategy";
    public static final String FIELD_CREATE_BUCKET_FLAG = "createBucketFlag";

    /**
     * 验证QCloud的特殊配置
     *
     * @return boolean
     */
    public boolean validateQCloud() {
        if (appId == null || StringUtils.isBlank(region)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 初始化本地存储数据
     */
    public void initData() {
        if (accessKeyId == null) {
            accessKeyId = StringUtils.EMPTY;
        }
        if (accessKeySecret == null) {
            accessKeySecret = StringUtils.EMPTY;
        }
        if (!endPoint.endsWith(HfleConstant.DIRECTORY_SEPARATOR)) {
            endPoint = endPoint + HfleConstant.DIRECTORY_SEPARATOR;
        }
        // 权限设为公共读写（目前没有权限控制）
        accessControl = FileConstant.LocalAccessController.PUBLIC_READ_WRITE;
    }

    @ApiModelProperty("存储配置ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long storageConfigId;

    /**
     * 类型 1:阿里 2:华为 3:Minio 4:腾讯 5:七牛 6:本地 7:京东云 8:AWS 9:百度云
     */
    @ApiModelProperty("存储类型")
    @NotNull(message = "存储类型不能为空")
    private Integer storageType;
    @ApiModelProperty("绑定域名")
    private String domain;
    @ApiModelProperty("EndPoint")
    @NotBlank(message = "EndPoint不能为空")
    private String endPoint;
    @ApiModelProperty("AccessKeyId")
    @NotNull(message = "AccessKeyId不能为空")
    private String accessKeyId;
    @ApiModelProperty("AccessKeySecret")
    @DataSecurity
    private String accessKeySecret;
    @ApiModelProperty("腾讯云AppId")
    private Integer appId;
    @ApiModelProperty("腾讯云COS所属地区")
    private String region;
    @ApiModelProperty("默认标识，0:不启用，1:启用")
    @Range(min = 0, max = 1)
    private Integer defaultFlag;
    @ApiModelProperty("租户ID")
    @NotNull(message = "租户ID不能为空")
    private Long tenantId;

    @ApiModelProperty("bucket权限控制")
    @NotBlank
    @LovValue(lovCode = "HFLE.CAPACITY.ACCESS_CONTROL")
    private String accessControl;

    @ApiModelProperty("bucket前缀")
    private String bucketPrefix;

    @ApiModelProperty("存储编码")
    private String storageCode;

    @ApiModelProperty("文件名前缀策略")
    @LovValue(lovCode = "HFLE.PREFIX_STRATEGY")
    private String prefixStrategy;

    @ApiModelProperty("自动创建bucket，0:不启用，1:启用")
    @Range(min = 0, max = 1)
    private Integer createBucketFlag;

    @Transient
    private String prefixStrategyMeaning;

    @Transient
    private String endPointMeaning;

    public Long getStorageConfigId() {
        return storageConfigId;
    }

    public StorageConfig setStorageConfigId(Long storageConfigId) {
        this.storageConfigId = storageConfigId;
        return this;
    }

    public Integer getStorageType() {
        return storageType;
    }

    public StorageConfig setStorageType(Integer storageType) {
        this.storageType = storageType;
        return this;
    }

    public String getDomain() {
        return domain;
    }

    public StorageConfig setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public StorageConfig setEndPoint(String endPoint) {
        this.endPoint = endPoint;
        return this;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public StorageConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public StorageConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public Integer getAppId() {
        return appId;
    }

    public StorageConfig setAppId(Integer appId) {
        this.appId = appId;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public StorageConfig setRegion(String region) {
        this.region = region;
        return this;
    }

    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public StorageConfig setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public StorageConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getAccessControl() {
        return accessControl;
    }

    public StorageConfig setAccessControl(String accessControl) {
        this.accessControl = accessControl;
        return this;
    }

    public String getBucketPrefix() {
        return bucketPrefix;
    }

    public StorageConfig setBucketPrefix(String bucketPrefix) {
        this.bucketPrefix = bucketPrefix;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public StorageConfig setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    public String getPrefixStrategy() {
        return prefixStrategy;
    }

    public StorageConfig setPrefixStrategy(String prefixStrategy) {
        this.prefixStrategy = prefixStrategy;
        return this;
    }

    public Integer getCreateBucketFlag() {
        return createBucketFlag;
    }

    public StorageConfig setCreateBucketFlag(Integer createBucketFlag) {
        this.createBucketFlag = createBucketFlag;
        return this;
    }

    public String getPrefixStrategyMeaning() {
        return prefixStrategyMeaning;
    }

    public StorageConfig setPrefixStrategyMeaning(String prefixStrategyMeaning) {
        this.prefixStrategyMeaning = prefixStrategyMeaning;
        return this;
    }

    public String getEndPointMeaning() {
        return endPointMeaning;
    }

    public StorageConfig setEndPointMeaning(String endPointMeaning) {
        this.endPointMeaning = endPointMeaning;
        return this;
    }

    //缓存方法

    /**
     * 生成redis存储key
     *
     * @param tenantId Long
     * @return String key
     */
    private String getDefaultConfigCacheKey(Long tenantId) {
        return HZeroService.File.CODE + ":storage-configs:" + tenantId;
    }


    /**
     * 刷新缓存
     *
     * @param redisHelper   RedisHelper
     * @param storageConfig StorageConfig
     */
    public void refreshDefaultConfigCache(RedisHelper redisHelper, StorageConfig storageConfig) {
        if (storageConfig == null) {
            clearDefaultConfigCache(redisHelper);
            return;
        }
        if (Objects.equals(BaseConstants.Digital.ONE, storageConfig.getDefaultFlag())) {
            clearDefaultConfigCache(redisHelper);
            StorageConfigVO storageConfigVO = new StorageConfigVO();
            BeanUtils.copyProperties(storageConfig, storageConfigVO);
            redisHelper.strSet(getDefaultConfigCacheKey(tenantId), redisHelper.toJson(storageConfigVO));
        }
    }

    /**
     * 查询缓存
     *
     * @param redisHelper RedisHelper
     */
    public StorageConfigVO getDefaultConfigCache(RedisHelper redisHelper) {
        String config = redisHelper.strGet(getDefaultConfigCacheKey(tenantId));
        if (StringUtils.isBlank(config)) {
            return null;
        } else {
            return redisHelper.fromJson(config, StorageConfigVO.class);
        }
    }

    /**
     * 清除缓存
     *
     * @param redisHelper RedisHelper
     */
    private void clearDefaultConfigCache(RedisHelper redisHelper) {
        redisHelper.delKey(getDefaultConfigCacheKey(tenantId));
    }
}
