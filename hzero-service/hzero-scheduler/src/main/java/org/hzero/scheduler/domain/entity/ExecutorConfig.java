package org.hzero.scheduler.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.scheduler.domain.repository.ExecutorConfigRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 执行器配置
 *
 * @author shuangfei.zhu@hand-china.com 2019-03-19 20:38:59
 */
@ApiModel("执行器配置")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_executor_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutorConfig extends AuditDomain {

    public static final String FIELD_CONFIG_ID = "configId";
    public static final String FIELD_EXECUTOR_ID = "executorId";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_MAX_CONCURRENT = "maxConcurrent";
    public static final String FIELD_WEIGHT = "weight";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     */
    public void validate(ExecutorConfigRepository repository) {
        Assert.isTrue(repository.selectCount(new ExecutorConfig().setAddress(address).setExecutorId(executorId)) == 0, BaseConstants.ErrorCode.DATA_EXISTS);
    }

    // 执行器最大并发量控制缓存

    /**
     * 生成redis存储key
     *
     * @param executorId 执行器Id
     * @return key
     */
    private static String getCacheKey(Long executorId) {
        return HZeroService.Scheduler.CODE + ":executor-config:" + executorId;
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param repository  仓库
     * @param address     执行器地址
     * @param jobId       任务Id
     */
    public static boolean addCache(RedisHelper redisHelper, ExecutorConfigRepository repository, Long executorId, String address, Long jobId) {
        List<String> data = getCache(redisHelper, executorId, address);
        ExecutorConfig config = repository.selectByUnique(executorId, address);
        if (config == null) {
            config = new ExecutorConfig();
        }
        Integer maxConcurrent = config.getMaxConcurrent();
        if (maxConcurrent == null || maxConcurrent > data.size()) {
            if (!data.contains(String.valueOf(jobId))) {
                // 向缓存添加
                data.add(String.valueOf(jobId));
                clearCache(redisHelper, executorId, address);
                redisHelper.hshPut(getCacheKey(executorId), address, redisHelper.toJson(data));
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper redis
     * @param jobId       任务Id
     */
    public static void deleteCache(RedisHelper redisHelper, Long executorId, Long jobId) {
        Map<String, String> map = redisHelper.hshGetAll(getCacheKey(executorId));
        Map<String, String> newMap = new HashMap<>(16);
        map.forEach((address, jobIds) -> {
            List<String> list = redisHelper.fromJsonList(jobIds, String.class);
            list.remove(String.valueOf(jobId));
            if (CollectionUtils.isNotEmpty(list)) {
                newMap.put(address, redisHelper.toJson(list));
            }
        });
        clearCache(redisHelper, executorId);
        if (newMap.size() > 0) {
            redisHelper.hshPutAll(getCacheKey(executorId), newMap);
        }
    }

    /**
     * 查询缓存
     *
     * @param redisHelper redis
     * @param executorId  执行器id
     * @param address     执行器地址
     */
    @SuppressWarnings("unchecked")
    public static List<String> getCache(RedisHelper redisHelper, Long executorId, String address) {
        String data = redisHelper.hshGet(getCacheKey(executorId), address);
        if (StringUtils.isNotBlank(data)) {
            return redisHelper.fromJson(data, List.class);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     * @param address     执行器地址
     */
    public static void clearCache(RedisHelper redisHelper, Long executorId, String address) {
        redisHelper.hshDelete(getCacheKey(executorId), address);
    }

    /**
     * 清除缓存
     *
     * @param redisHelper redis
     */
    public static void clearCache(RedisHelper redisHelper, Long executorId) {
        redisHelper.delKey(getCacheKey(executorId));
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long configId;
    @ApiModelProperty(value = "执行器ID，hsdr_executor.executor_id", required = true)
    @NotNull
    @Encrypt
    private Long executorId;
    @ApiModelProperty(value = "执行器地址", required = true)
    @NotBlank
    @Length(max = 30)
    private String address;
    @ApiModelProperty(value = "最大并发数")
    @Min(0)
    private Integer maxConcurrent;
    @ApiModelProperty(value = "执行器权重")
    @NotNull
    @Min(0)
    private Integer weight;
    @ApiModelProperty(value = "启用标识")
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getConfigId() {
        return configId;
    }

    public ExecutorConfig setConfigId(Long configId) {
        this.configId = configId;
        return this;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public ExecutorConfig setExecutorId(Long executorId) {
        this.executorId = executorId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public ExecutorConfig setAddress(String address) {
        this.address = address;
        return this;
    }

    public Integer getMaxConcurrent() {
        return maxConcurrent;
    }

    public ExecutorConfig setMaxConcurrent(Integer maxConcurrent) {
        this.maxConcurrent = maxConcurrent;
        return this;
    }

    public Integer getWeight() {
        return weight;
    }

    public ExecutorConfig setWeight(Integer weight) {
        this.weight = weight;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public ExecutorConfig setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ExecutorConfig setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }
}
