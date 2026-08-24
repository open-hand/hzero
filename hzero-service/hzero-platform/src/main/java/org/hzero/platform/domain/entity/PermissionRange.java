package org.hzero.platform.domain.entity;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.platform.api.dto.PermissionRelDTO;
import org.hzero.platform.domain.repository.PermissionRangeExclRepository;
import org.hzero.platform.domain.repository.PermissionRangeRepository;
import org.hzero.platform.domain.repository.PermissionRelRepository;
import org.hzero.platform.domain.vo.PermissionRangeVO;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.StringUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 屏蔽范围
 *
 * @author yunxiang.zhou01@hand-china.com 2018-07-23 15:01:44
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_permission_range")
@ApiModel("数据屏蔽范围")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionRange extends AuditDomain {

    public static final String FIELD_RANGE_ID = "rangeId";
    public static final String FIELD_TABLE_NAME = "tableName";
    public static final String FIELD_SQL_ID = "sqlId";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_EDITABLE_FLAG = "editableFlag";
    public static final String FIELD_CUSTOM_RULE_FLAG = "customRuleFlag";


    /**
     * 将所有的屏蔽范围数据初始化到redis中
     *
     * @param redisHelper     redisHelper
     * @param rangeRepository rangeRepository
     * @param relRepository   relRepository
     */
    public static void initAllData(RedisHelper redisHelper,
                                   PermissionRangeRepository rangeRepository,
                                   PermissionRangeExclRepository rangeExclRepository,
                                   PermissionRelRepository relRepository) {
        List<PermissionRange> rangeList = rangeRepository.selectAll();
        if (CollectionUtils.isNotEmpty(rangeList)) {
            rangeList.forEach(range -> initCache(range.getRangeId(), redisHelper, rangeRepository, rangeExclRepository, relRepository));
        }
    }

    /**
     * 校验数据屏蔽数据合法性，不是当前租户的就报错
     *
     * @param rangeRepository 数据屏蔽范围资源层
     */
    public void judgeDataLegality(PermissionRangeRepository rangeRepository) {
        PermissionRange range = rangeRepository.selectByPrimaryKey(rangeId);
        if (tenantId != null && range != null && tenantId.equals(range.getTenantId())) {
            return;
        }
        throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
    }

    /**
     * 初始化某一个数据屏蔽范围的所有redis数据
     *
     * @param redisHelper               redisHelper
     * @param permissionRangeRepository permissionRangeRepository
     * @param permissionRelRepository   permissionRelRepository
     */
    public void initCache(RedisHelper redisHelper,
                          PermissionRangeRepository permissionRangeRepository,
                          PermissionRangeExclRepository rangeExclRepository,
                          PermissionRelRepository permissionRelRepository) {
        if (rangeId != null) {
            initCache(rangeId, redisHelper, permissionRangeRepository, rangeExclRepository, permissionRelRepository);
        }
    }

    /**
     * 初始化某一个数据屏蔽范围的所有redis数据
     *
     * @param rangeId                   范围id
     * @param redisHelper               redisHelper
     * @param permissionRangeRepository permissionRangeRepository
     * @param permissionRelRepository   permissionRelRepository
     */
    public static void initCache(Long rangeId, RedisHelper redisHelper,
                                 PermissionRangeRepository permissionRangeRepository,
                                 PermissionRangeExclRepository permissionRangeExclRepository,
                                 PermissionRelRepository permissionRelRepository) {
        PermissionRange permissionRange = permissionRangeRepository.selectByPrimaryKey(rangeId);
        if (permissionRange == null || BaseConstants.Flag.NO.equals(permissionRange.getEnabledFlag())) {
            return;
        }
        permissionRange.setRangeExclList(permissionRangeExclRepository.select(PermissionRangeExcl.FIELD_RANGE_ID, rangeId));
        String cacheKey = PermissionRange.generateCacheKey(permissionRange.getTableName());
        String mapKey = PermissionRange.generateMapKey(permissionRange.getTenantId(), permissionRange.getServiceName(),
                permissionRange.getSqlId());
        List<PermissionRelDTO> permissionRelDTOList = permissionRelRepository.selectPermissionRuleByRangeId(rangeId);
        if (CollectionUtils.isNotEmpty(permissionRelDTOList)) {
            // 获取屏蔽范围所有规则类型为sql的sql值
            List<String> sqlList = permissionRelDTOList.stream()
                    .filter(relDTO -> FndConstants.PermissionRuleTypeCode.SQL.equals(relDTO.getRuleTypeCode())
                            && BaseConstants.Flag.YES.equals(relDTO.getEnabledFlag()))
                    .map(PermissionRelDTO::getSqlValue).collect(Collectors.toList());
            // 获取屏蔽范围所有规则类型为prefix的sql值
            List<String> prefixList = permissionRelDTOList.stream().filter(
                    relDTO -> FndConstants.PermissionRuleTypeCode.PREFIX.equals(relDTO.getRuleTypeCode())
                            && BaseConstants.Flag.YES.equals(relDTO.getEnabledFlag()))
                    .map(PermissionRelDTO::getSqlValue).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(prefixList)) {
                redisHelper.hshPut(cacheKey, mapKey, redisHelper
                        .toJson(new PermissionRangeVO(permissionRange.getCustomRuleFlag(), sqlList, permissionRange.getRangeExclList())));
            } else {
                redisHelper.hshPut(cacheKey, mapKey,
                        redisHelper.toJson(new PermissionRangeVO(permissionRange.getCustomRuleFlag(), sqlList,
                                prefixList.get(0), permissionRange.getRangeExclList())));
            }
        } else {
            redisHelper.hshPut(cacheKey, mapKey, redisHelper.toJson(
                    new PermissionRangeVO(permissionRange.getCustomRuleFlag(), Collections.emptyList(), permissionRange.getRangeExclList())));
        }
    }

    /**
     * 往redis中新增数据
     *
     * @param redisHelper redisHelper
     * @param cacheKey    cacheKey
     * @param mapKey      mapKey
     * @param rangeVO     rangeVO
     */
    public static void createCache(RedisHelper redisHelper, String cacheKey, String mapKey, PermissionRangeVO rangeVO) {
        redisHelper.hshPut(cacheKey, mapKey, redisHelper.toJson(rangeVO));
    }

    /**
     * 删除redis缓存
     *
     * @param redisHelper     redisHelper
     * @param permissionRange permissionRange
     */
    public static void deleteCache(RedisHelper redisHelper, PermissionRange permissionRange) {
        String cacheKey = PermissionRange.generateCacheKey(permissionRange.getTableName());
        String mapKey = PermissionRange.generateMapKey(permissionRange.getTenantId(), permissionRange.getServiceName(),
                permissionRange.getSqlId());
        redisHelper.hshDelete(cacheKey, mapKey);
    }

    /**
     * 生成数据屏蔽cacheKey
     *
     * @param tableName 表名
     * @return key
     */
    public static String generateCacheKey(String tableName) {
        return FndConstants.CacheKey.PERMISSION_KEY + ":" + tableName.toLowerCase().trim();
    }

    /**
     * 生成数据屏蔽redis哈希map中的key
     *
     * @param tenantId    tenantId
     * @param serviceName 服务名
     * @param sqlId       sqlId
     * @return mapKey
     */
    public static String generateMapKey(Long tenantId, String serviceName, String sqlId) {
        if (serviceName != null && !"".equals(serviceName.trim())) {
            serviceName = "." + serviceName;
        }
        if (sqlId != null && !"".equals(sqlId.trim())) {
            sqlId = "." + sqlId;
        }
        return tenantId + (serviceName == null ? "" : serviceName) + (sqlId == null ? "" : sqlId);
    }

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @ApiModelProperty("范围ID")
    @Encrypt
    private Long rangeId;
    @NotBlank
    @ApiModelProperty("表名")
    @Length(max = 60)
    private String tableName;
    @NotNull
    @ApiModelProperty("是否启用")
    @Range(max = 1)
    private Integer enabledFlag;
    @NotNull
    @ApiModelProperty("租户ID")
    private Long tenantId;
    @ApiModelProperty("SQLID")
    @Length(max = 120)
    private String sqlId;
    @ApiModelProperty("描述")
    @Length(max = 240)
    private String description;
    @ApiModelProperty("服务名")
    @Length(max = 60)
    private String serviceName;
    @ApiModelProperty("自定义规则标识")
    @Range(max = 1)
    private Integer customRuleFlag;
    @ApiModelProperty("编辑标识")
    @Range(max = 1)
    private Integer editableFlag;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    /**
     * 查询条件  规则编码
     */
    @Transient
    private String ruleCode;
    @Transient
    private List<PermissionRangeExcl> rangeExclList;
    @Transient
    private List<PermissionRangeExcl> rangeServiceNameExclList;
    @Transient
    private List<PermissionRangeExcl> rangeTenantExclList;
    @Transient
    private List<PermissionRangeExcl> rangeSqlidExclList;

    public void groupExcel() {
        rangeServiceNameExclList = Optional.ofNullable(getRangeExclList())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(item -> StringUtils.hasText(item.getServiceName()))
                .collect(Collectors.toList());
        rangeTenantExclList = Optional.ofNullable(getRangeExclList())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(item -> Objects.nonNull(item.getTenantId()))
                .collect(Collectors.toList());
        rangeSqlidExclList = Optional.ofNullable(getRangeExclList())
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(item -> StringUtils.hasText(item.getSqlId()))
                .collect(Collectors.toList());
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getRangeId() {
        return rangeId;
    }

    public void setRangeId(Long rangeId) {
        this.rangeId = rangeId;
    }

    /**
     * @return 屏蔽表名
     */
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return 屏蔽sqlId
     */
    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    /**
     * @return 屏蔽描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public PermissionRange setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 租户id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public PermissionRange setDescription(String description) {
        this.description = description;
        return this;
    }

    public Integer getCustomRuleFlag() {
        return customRuleFlag;
    }

    public void setCustomRuleFlag(Integer customRuleFlag) {
        this.customRuleFlag = customRuleFlag;
    }

    public Integer getEditableFlag() {
        return editableFlag;
    }

    public PermissionRange setEditableFlag(Integer editableFlag) {
        this.editableFlag = editableFlag;
        return this;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public PermissionRange setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
        return this;
    }

    public List<PermissionRangeExcl> getRangeExclList() {
        return rangeExclList;
    }

    public PermissionRange setRangeExclList(List<PermissionRangeExcl> rangeExclList) {
        this.rangeExclList = rangeExclList;
        return this;
    }

    public List<PermissionRangeExcl> getRangeServiceNameExclList() {
        return rangeServiceNameExclList;
    }

    public PermissionRange setRangeServiceNameExclList(List<PermissionRangeExcl> rangeServiceNameExclList) {
        this.rangeServiceNameExclList = rangeServiceNameExclList;
        return this;
    }

    public List<PermissionRangeExcl> getRangeTenantExclList() {
        return rangeTenantExclList;
    }

    public PermissionRange setRangeTenantExclList(List<PermissionRangeExcl> rangeTenantExclList) {
        this.rangeTenantExclList = rangeTenantExclList;
        return this;
    }

    public List<PermissionRangeExcl> getRangeSqlidExclList() {
        return rangeSqlidExclList;
    }

    public PermissionRange setRangeSqlidExclList(List<PermissionRangeExcl> rangeSqlidExclList) {
        this.rangeSqlidExclList = rangeSqlidExclList;
        return this;
    }
}
