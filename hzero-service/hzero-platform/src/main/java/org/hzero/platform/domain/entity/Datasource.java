package org.hzero.platform.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.ds.vo.DatasourceVO;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.Regexs;
import org.hzero.mybatis.annotation.DataSecurity;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.repository.DatasourceRepository;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据源配置
 *
 * @author like.zhang@hand-china.com 2018-09-13 14:10:13
 */
@ApiModel("数据源配置")
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_datasource")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class Datasource extends AuditDomain {
    public static final String FIELD_DATASOURCE_ID = "datasourceId";
    public static final String FIELD_DATASOURCE_CODE = "datasourceCode";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_DATASOURCE_URL = "datasourceUrl";
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD_ENCRYPTED = "passwordEncrypted";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_DB_TYPE = "dbType";
    public static final String FIELD_DB_POOL_TYPE = "dbPoolType";
    public static final String FIELD_QUERYER_CLASS = "queryerClass";
    public static final String FIELD_POOL_CLASS = "poolClass";
    public static final String FIELD_OPTIONS = "options";
    public static final String FIELD_REMARK = "remark";
    public static final String FIELD_DRIVER_CLASS = "driverClass";
    public static final String FIELD_DS_PURPOSE_CODE = "dsPurposeCode";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_EXT_CONFIG = "extConfig";
    public static final String FIELD_DRIVER_ID = "driverId";
    public static final String FIELD_DRIVER_TYPE = "driverType";
    public static final String FIELD_DATASOURCE_CLASS = "datasourceClass";

    public static final String DS_PURPOSE_LOV_CODE = "HPFM.DATASOURCE_PURPOSE";



    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 验证数据源配置编码唯一性
     *
     * @param datasourceRepository DatasourceServiceRepository
     */
    public void validateDatasourceCode(DatasourceRepository datasourceRepository) {
        // 租户和编码判断是否唯一
        int count = datasourceRepository.selectCountByCondition(Condition.builder(Datasource.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(Datasource.FIELD_DATASOURCE_CODE, datasourceCode)
                        .andEqualTo(Datasource.FIELD_TENANT_ID, tenantId)
                ).build());
        if (count != BaseConstants.Digital.ZERO) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
    }

    /**
     * 刷新缓存
     *
     * @param redisHelper                 RedisHelper
     */
    public void refreshCache(RedisHelper redisHelper) {
        if (BaseConstants.Flag.YES.equals(enabledFlag)) {
            String[] dsPurposeCodes = dsPurposeCode.split(BaseConstants.Symbol.COMMA);
            for (String purposeCode : dsPurposeCodes) {
                Datasource.pushRedis(this, redisHelper, purposeCode);
            }
        } else {
            // 禁用状态删除缓存
            this.clearCache(redisHelper);
        }
    }

    /**
     * 根据数据源id清空缓存
     *
     * @param redisHelper                 RedisHelper
     */
    public void clearCache(RedisHelper redisHelper) {
        String[] split = StringUtils.split(dsPurposeCode, BaseConstants.Symbol.COMMA);
        for (String purposeCode : split) {
            redisHelper.delKey(Datasource.generateKey(purposeCode, tenantId, datasourceCode));
        }
    }

    /**
     * 写入缓存
     *
     * @param datasource  数据源
     * @param redisHelper RedisHelper
     * @param dsPurposeCode 数据源用途编码，FIX20200804 支持单数据源维护多用途，多用途使用','拆分，在这里传递该code便于处理缓存
     */
    public static void pushRedis(Datasource datasource, RedisHelper redisHelper, String dsPurposeCode) {
        DatasourceVO cacheEntity = new DatasourceVO();
        BeanUtils.copyProperties(datasource, cacheEntity);
        cacheEntity.setDsPurposeCode(dsPurposeCode);
        if (datasource.getDatasourceId() == null) {
            // id不存在，即数据源不存在，将“error”写入用作防击穿
            redisHelper.strSet(Datasource.generateKey(dsPurposeCode, datasource.getTenantId(), datasource.getDatasourceCode()), "");
        } else {
            redisHelper.objectSet(Datasource.generateKey(dsPurposeCode, datasource.getTenantId(), datasource.getDatasourceCode()), cacheEntity);
        }
    }

    /**
     * 根据服务名称生成缓存key
     *
     * @param dsPurposeCode 数据源用途
     * @return key
     */
    public static String generateKey(String dsPurposeCode, Long tenantId, String datasourceCode) {
        long organizationId = tenantId == null ? 0 : tenantId;
        return FndConstants.CacheKey.DATASOURCE_KEY + BaseConstants.Symbol.COLON + dsPurposeCode +
                BaseConstants.Symbol.COLON + organizationId + BaseConstants.Symbol.COLON + datasourceCode;
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty("表ID，主键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long datasourceId;
    @ApiModelProperty(value = "数据源编码")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String datasourceCode;
    @ApiModelProperty(value = "数据源名称")
    @Length(max = 600)
    @NotBlank
    @MultiLanguageField
    private String description;
    @ApiModelProperty(value = "数据源URL地址")
    @Length(max = 600)
    private String datasourceUrl;
    @ApiModelProperty(value = "用户")
    @Length(max = 100)
    private String username;
    @ApiModelProperty(value = "加密密码")
    @Length(max = 300)
    @DataSecurity
    private String passwordEncrypted;
    @ApiModelProperty(value = "是否启用，1启用、0禁用")
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "数据库类型")
    @NotBlank
    @Length(max = 30)
    @LovValue("HPFM.DATABASE_TYPE")
    private String dbType;
    @ApiModelProperty(value = "数据库驱动类")
    private String driverClass;
    @ApiModelProperty(value = "连接池类型，独立值集：HPFM.DB_POOL_TYPE")
    @LovValue("HPFM.DB_POOL_TYPE")
    @Length(max = 30)
    private String dbPoolType;
    @ApiModelProperty(value = "获取报表引擎查询器类名")
    @Length(max = 240)
    private String queryerClass;
    @ApiModelProperty(value = "报表引擎查询器使用的数据源连接池类名")
    @Length(max = 240)
    private String poolClass;
    @ApiModelProperty(value = "数据源配置选项(JSON格式）")
    private String options;
    @ApiModelProperty(value = "备注")
    @Length(max = 240)
    private String remark;
    @ApiModelProperty(value = "数据源用途，值集：HPFM.DATASOURCE_PURPOSE")
    @Length(max = 120)
    @NotBlank
    private String dsPurposeCode;
    @ApiModelProperty(value = "租户Id")
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "扩展字段")
    private String extConfig;
    @ApiModelProperty(value = "关联驱动Id")
    @Encrypt
    private Long driverId;
    @NotBlank
    @Length(max = 30)
    @ApiModelProperty(value = "数据源分类")
    @LovValue(value = "HPFM.DATASOURCE_CLASS", meaningField = "dsClassMeaning")
    private String datasourceClass;
    @Length(max = 30)
    @ApiModelProperty(value = "驱动类型，快码：HPFM.DATASOURCE_DRIVER_TYPE")
    @LovValue(value = "HPFM.DATASOURCE_DRIVER_TYPE", meaningField = "driverTypeMeaning")
    private String driverType;
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String dsPurposeCodeMeaning;
    @Transient
    private String tenantName;
    @Transient
    private String driverName;
    @Transient
    private String dsClassMeaning;
    @Transient
    private String driverTypeMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public Datasource setDriverId(Long driverId) {
        this.driverId = driverId;
        return this;
    }

    public String getExtConfig() {
        return extConfig;
    }

    public void setExtConfig(String extConfig) {
        this.extConfig = extConfig;
    }

    /**
     * @return 表ID，主键
     */
    public Long getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(Long datasourceId) {
        this.datasourceId = datasourceId;
    }

    /**
     * @return 数据源编码
     */
    public String getDatasourceCode() {
        return datasourceCode;
    }

    public void setDatasourceCode(String datasourceCode) {
        this.datasourceCode = datasourceCode;
    }

    /**
     * @return 说明
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 数据源URL地址
     */
    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    /**
     * @return 用户
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return 加密密码
     */
    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    /**
     * @return 是否启用，1启用、0禁用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDbPoolType() {
        return dbPoolType;
    }

    public void setDbPoolType(String dbPoolType) {
        this.dbPoolType = dbPoolType;
    }

    public String getQueryerClass() {
        return queryerClass;
    }

    public void setQueryerClass(String queryerClass) {
        this.queryerClass = queryerClass;
    }

    public String getPoolClass() {
        return poolClass;
    }

    public void setPoolClass(String poolClass) {
        this.poolClass = poolClass;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDsPurposeCode() {
        return dsPurposeCode;
    }

    public void setDsPurposeCode(String dsPurposeCode) {
        this.dsPurposeCode = dsPurposeCode;
    }

    public String getDsPurposeCodeMeaning() {
        return dsPurposeCodeMeaning;
    }

    public void setDsPurposeCodeMeaning(String dsPurposeCodeMeaning) {
        this.dsPurposeCodeMeaning = dsPurposeCodeMeaning;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getDatasourceClass() {
        return datasourceClass;
    }

    public void setDatasourceClass(String datasourceClass) {
        this.datasourceClass = datasourceClass;
    }

    public String getDriverType() {
        return driverType;
    }

    public Datasource setDriverType(String driverType) {
        this.driverType = driverType;
        return this;
    }

    public String getDsClassMeaning() {
        return dsClassMeaning;
    }

    public void setDsClassMeaning(String dsClassMeaning) {
        this.dsClassMeaning = dsClassMeaning;
    }

    public String getDriverTypeMeaning() {
        return driverTypeMeaning;
    }

    public void setDriverTypeMeaning(String driverTypeMeaning) {
        this.driverTypeMeaning = driverTypeMeaning;
    }
}
