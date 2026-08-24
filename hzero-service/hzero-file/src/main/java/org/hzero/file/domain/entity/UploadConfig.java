package org.hzero.file.domain.entity;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.app.service.CapacityConfigService;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.domain.vo.UploadConfigVO;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * 文件上传配置
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
@ApiModel("文件上传配置")
@VersionAudit
@ModifyAudit
@Table(name = "hfle_upload_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadConfig extends AuditDomain {

    public static final String FIELD_UPLOAD_CONFIG_ID = "uploadConfigId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_BUCKET_NAME = "bucketName";
    public static final String FIELD_CONTENT_TYPE = "contentType";
    public static final String FIELD_STORAGE_UNIT = "storageUnit";
    public static final String FIELD_STORAGE_SIZE = "storageSize";
    public static final String FIELD_FILE_FORMAT = "fileFormat";
    public static final String MULTIPLE_FILE_FLAG = "multipleFileFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     *
     * @param repository 仓库
     */
    public void validateRepeat(UploadConfigRepository repository) {
        UploadConfig uploadConfig = new UploadConfig();
        uploadConfig.setTenantId(tenantId);
        uploadConfig.setBucketName(bucketName);
        uploadConfig.setDirectory(directory);
        Assert.isTrue(repository.selectCount(uploadConfig) == BaseConstants.Digital.ZERO, HfleMessageConstant.ERROR_DATA_EXISTS);
    }

    /**
     * 校验大小
     *
     * @param capacityConfigService 仓库
     */
    public void validateSize(CapacityConfigService capacityConfigService) {
        // 获取头最大配置
        CapacityConfig capacityConfig = capacityConfigService.selectByTenantId(tenantId);

        // 没有配置头默认大小
        Assert.notNull(capacityConfig, HfleMessageConstant.ERROR_TENANT_CONFIG_NOT_SET);
        // 有租户配置
        Long tenantSize = capacityConfig.getStorageSize();
        String tenantUnit = capacityConfig.getStorageUnit();
        if (Objects.equals(storageUnit, tenantUnit)) {
            if (storageSize > tenantSize) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, tenantSize + tenantUnit);
            }
        } else {
            if (HfleConstant.StorageUnit.MB.equals(storageUnit) && storageSize * HfleConstant.ENTERING > tenantSize) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, tenantSize + tenantUnit);
            } else if (HfleConstant.StorageUnit.KB.equals(storageUnit) && storageSize > tenantSize * HfleConstant.ENTERING) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, tenantSize + tenantUnit);
            }
        }
    }

    //缓存方法

    /**
     * 生成redis存储key
     *
     * @param tenantId   租户Id
     * @param bucketName 桶
     * @param directory  目录
     * @return key
     */
    private static String getCacheKey(Long tenantId, String bucketName, String directory) {
        return HZeroService.File.CODE + ":upload-configs:" + tenantId + "." + bucketName + "." + directory;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper    redis
     * @param tenantId       租户Id
     * @param bucketName     桶
     * @param directory      目录
     * @param uploadConfigVO 文件上传配置DTO
     */
    public static void refreshCache(RedisHelper redisHelper, Long tenantId, String bucketName, String directory, UploadConfigVO uploadConfigVO) {
        clearRedisCache(redisHelper, tenantId, bucketName, directory);
        redisHelper.strSet(getCacheKey(tenantId, bucketName, directory), redisHelper.toJson(uploadConfigVO));
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param bucketName  桶
     * @param directory   目录
     */
    public static UploadConfig getCache(RedisHelper redisHelper, Long tenantId, String bucketName, String directory) {
        return redisHelper.fromJson(redisHelper.strGet(getCacheKey(tenantId, bucketName, directory)), UploadConfig.class);
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     * @param bucketName  桶
     * @param directory   目录
     */
    public static void clearRedisCache(RedisHelper redisHelper, Long tenantId, String bucketName, String directory) {
        redisHelper.delKey(getCacheKey(tenantId, bucketName, directory));
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long uploadConfigId;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull(message = "租户Id不能为空")
    private Long tenantId;
    @ApiModelProperty(value = "文件目录", required = true)
    @NotBlank(message = "文件目录不能为空", groups = {UploadConfig.Validate.class})
    @Length(max = 60)
    @Pattern(regexp = HfleConstant.LOW_STRIKE)
    private String bucketName;
    @Length(max = 60)
    @ApiModelProperty("上传目录")
    private String directory;
    @ApiModelProperty(value = "文件分类，值集HFLE.CONTENT_TYPE", required = true)
    @Length(max = 240)
    private String contentType;
    @ApiModelProperty(value = "存储大小限制单位，值集HFLE.STORAGE_UNIT,KB/MB", required = true)
    @NotBlank(message = "存储大小限制单位不能为空", groups = {UploadConfig.Validate.class})
    @Length(max = 30)
    @LovValue(lovCode = "HFLE.STORAGE_UNIT")
    private String storageUnit;
    @ApiModelProperty(value = "存储大小", required = true)
    @NotNull(message = "存储大小不能为空", groups = {UploadConfig.Validate.class})
    @Range(min = 0)
    private Long storageSize;
    @ApiModelProperty(value = "文件格式，文件分类子值集HFLE.FILE_FORMAT")
    @Length(max = 240)
    private String fileFormat;

    private interface Validate {
    }

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String contentTypeMeaning;
    @Transient
    private String storageUnitMeaning;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getUploadConfigId() {
        return uploadConfigId;
    }

    public void setUploadConfigId(Long uploadConfigId) {
        this.uploadConfigId = uploadConfigId;
    }

    /**
     * @return 租户ID，hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public UploadConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 文件目录
     */
    public String getBucketName() {
        return bucketName;
    }

    public UploadConfig setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    /**
     * @return 上传目录
     */
    public String getDirectory() {
        return directory;
    }

    public UploadConfig setDirectory(String directory) {
        if (StringUtils.isBlank(directory)) {
            this.directory = StringUtils.EMPTY;
            return this;
        } else {
            String[] forbidden = HfleConstant.getForbiddenSymbols();
            for (String item : forbidden) {
                Assert.isTrue(!directory.contains(item), HfleMessageConstant.ERROR_DIRECTORY_FORBIDDEN);
            }
            String[] dirs = directory.split(HfleConstant.DIRECTORY_SEPARATOR);
            StringBuilder builder = new StringBuilder();
            for (String item : dirs) {
                if (StringUtils.isNotBlank(item)) {
                    builder.append(item).append(HfleConstant.DIRECTORY_SEPARATOR);
                }
            }
            this.directory = String.valueOf(builder);
            return this;
        }
    }

    /**
     * @return 文件分类，值集HFLE.CONTENT_TYPE
     */
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return 存储大小限制单位，值集HFLE.STORAGE_UNIT,KB/MB
     */
    public String getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    /**
     * @return 存储大小
     */
    public Long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
    }

    /**
     * @return 文件格式，文件分类子值集HFLE.FILE_FORMAT
     */
    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getContentTypeMeaning() {
        return contentTypeMeaning;
    }

    public void setContentTypeMeaning(String contentTypeMeaning) {
        this.contentTypeMeaning = contentTypeMeaning;
    }

    public String getStorageUnitMeaning() {
        return storageUnitMeaning;
    }

    public void setStorageUnitMeaning(String storageUnitMeaning) {
        this.storageUnitMeaning = storageUnitMeaning;
    }
}
