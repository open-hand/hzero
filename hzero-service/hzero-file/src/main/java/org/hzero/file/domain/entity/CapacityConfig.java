package org.hzero.file.domain.entity;

import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.file.domain.repository.CapacityConfigRepository;
import org.hzero.file.domain.repository.UploadConfigRepository;
import org.hzero.file.domain.vo.CapacityConfigVO;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件容量配置
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-26 15:05:19
 */
@ApiModel("文件容量配置")
@VersionAudit
@ModifyAudit
@Table(name = "hfle_capacity_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapacityConfig extends AuditDomain {

    public static final String FIELD_CAPACITY_CONFIG_ID = "capacityConfigId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_TOTAL_CAPACITY = "totalCapacity";
    public static final String FIELD_TOTAL_CAPACITY_UNIT = "totalCapacityUnit";
    public static final String FIELD_USED_CAPACITY = "usedCapacity";
    public static final String FIELD_STORAGE_UNIT = "storageUnit";
    public static final String FIELD_STORAGE_SIZE = "storageSize";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     *
     * @param repository 仓库
     */
    public void validateRepeat(CapacityConfigRepository repository) {
        CapacityConfig capacityConfig = new CapacityConfig();
        capacityConfig.setTenantId(tenantId);
        Assert.isTrue(repository.selectCount(capacityConfig) == BaseConstants.Digital.ZERO, HfleMessageConstant.ERROR_DATA_EXISTS);
    }

    /**
     * 校验头数据
     *
     * @param maxConfig   最大文件大小
     * @param maxCapacity 最大存储容量
     */
    public void validateSize(String maxConfig, String maxCapacity) {
        Long config;
        String num = maxConfig.substring(0, maxConfig.length() - 2);
        if (maxConfig.endsWith(HfleConstant.StorageUnit.MB)) {
            // yml文件中配置的最大值单位转为KB
            config = Long.parseLong(num) * HfleConstant.ENTERING;
        } else if (maxConfig.endsWith(HfleConstant.StorageUnit.KB)) {
            config = Long.parseLong(num);
        } else {
            throw new CommonException(HfleMessageConstant.ERROR_YML_ERROR);
        }
        if (HfleConstant.StorageUnit.MB.equals(storageUnit) && storageSize * HfleConstant.ENTERING > config) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, maxConfig);
        } else if (HfleConstant.StorageUnit.KB.equals(storageUnit) && storageSize > config) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE, maxConfig);
        }
        // 校验存储容量
        if (StringUtils.isBlank(maxCapacity)) {
            return;
        }
        // 平台配置的最大值单位转为KB
        String maxStore = maxCapacity.substring(0, maxCapacity.length() - 2);
        Long capacity = maxCapacity.endsWith(HfleConstant.StorageUnit.MB) ? Long.parseLong(maxStore) * HfleConstant.ENTERING : Long.parseLong(maxStore);
        if (HfleConstant.StorageUnit.MB.equals(totalCapacityUnit) && totalCapacity * HfleConstant.ENTERING > capacity) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_CAPACITY_SIZE, maxCapacity);
        } else if (HfleConstant.StorageUnit.KB.equals(storageUnit) && storageSize > capacity) {
            throw new CommonException(HfleMessageConstant.ERROR_FILE_CAPACITY_SIZE, maxCapacity);
        }
    }

    /**
     * 校验头的单文件限制是否大于行
     */
    public void validateSize(UploadConfigRepository uploadConfigRepository) {
        List<UploadConfig> list = uploadConfigRepository.listUploadConfig(tenantId);
        for (UploadConfig uploadConfig : list) {
            Long size = uploadConfig.getStorageSize();
            String unit = uploadConfig.getStorageUnit();
            if (unit.equals(storageUnit) && storageSize < size) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE_SMALL, size + unit);
            } else if (HfleConstant.StorageUnit.MB.equals(unit) && HfleConstant.StorageUnit.KB.equals(storageUnit)
                    && storageSize < size * HfleConstant.ENTERING) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE_SMALL, size + unit);
            } else if (HfleConstant.StorageUnit.MB.equals(storageUnit) && HfleConstant.StorageUnit.KB.equals(unit)
                    && storageSize * HfleConstant.ENTERING < size) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_SIZE_SMALL, size + unit);
            }
        }
    }

    //缓存方法

    /**
     * 生成redis存储key
     *
     * @param tenantId 租户Id
     * @return key
     */
    private static String getCacheKey(Long tenantId) {
        return HZeroService.File.CODE + ":capacity-configs:" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper      redis
     * @param tenantId         租户Id
     * @param capacityConfigVO 文件容量配置DTO
     */
    public static void refreshCache(RedisHelper redisHelper, Long tenantId, CapacityConfigVO capacityConfigVO) {
        clearRedisCache(redisHelper, tenantId);
        redisHelper.strSet(getCacheKey(tenantId), redisHelper.toJson(capacityConfigVO));
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     */
    public static CapacityConfig getCache(RedisHelper redisHelper, Long tenantId) {
        return redisHelper.fromJson(redisHelper.strGet(getCacheKey(tenantId)), CapacityConfig.class);
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param tenantId    租户Id
     */
    private static void clearRedisCache(RedisHelper redisHelper, Long tenantId) {
        redisHelper.delKey(getCacheKey(tenantId));
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long capacityConfigId;
    @ApiModelProperty(value = "租户ID，hpfm_tenant.tenant_id", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "总容量限制", required = true)
    @NotNull
    @Range(min = 1)
    private Long totalCapacity;
    @ApiModelProperty(value = "存储大小限制单位，值集HFLE.STORAGE_UNIT,KB/MB", required = true)
    @NotBlank
    @LovValue(lovCode = "HFLE.STORAGE_UNIT")
    @Length(max = 30)
    private String totalCapacityUnit;
    @ApiModelProperty(value = "已使用容量")
    private Long usedCapacity;
    @ApiModelProperty(value = "存储大小限制单位，值集HFLE.STORAGE_UNIT,KB/MB", required = true)
    @NotBlank
    @LovValue(lovCode = "HFLE.STORAGE_UNIT")
    @Length(max = 30)
    private String storageUnit;
    @ApiModelProperty(value = "存储大小", required = true)
    @NotNull
    @Range(min = 0)
    private Long storageSize;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private Page<UploadConfig> listConfig;
    @Transient
    private Long redisUsedCapacity;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getCapacityConfigId() {
        return capacityConfigId;
    }

    public CapacityConfig setCapacityConfigId(Long capacityConfigId) {
        this.capacityConfigId = capacityConfigId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public CapacityConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getTotalCapacity() {
        return totalCapacity;
    }

    public CapacityConfig setTotalCapacity(Long totalCapacity) {
        this.totalCapacity = totalCapacity;
        return this;
    }

    public String getTotalCapacityUnit() {
        return totalCapacityUnit;
    }

    public CapacityConfig setTotalCapacityUnit(String totalCapacityUnit) {
        this.totalCapacityUnit = totalCapacityUnit;
        return this;
    }

    public Long getUsedCapacity() {
        return usedCapacity;
    }

    public CapacityConfig setUsedCapacity(Long usedCapacity) {
        this.usedCapacity = usedCapacity;
        return this;
    }

    public String getStorageUnit() {
        return storageUnit;
    }

    public CapacityConfig setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
        return this;
    }

    public Long getStorageSize() {
        return storageSize;
    }

    public CapacityConfig setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
        return this;
    }

    public Page<UploadConfig> getListConfig() {
        return listConfig;
    }

    public CapacityConfig setListConfig(Page<UploadConfig> listConfig) {
        this.listConfig = listConfig;
        return this;
    }

    public Long getRedisUsedCapacity() {
        return redisUsedCapacity;
    }

    public CapacityConfig setRedisUsedCapacity(Long redisUsedCapacity) {
        this.redisUsedCapacity = redisUsedCapacity;
        return this;
    }
}
