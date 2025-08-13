package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import org.hibernate.validator.constraints.Length;
import org.hzero.core.util.Regexs;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.*;

/**
 * <p>
 * 地区定义数据库实体映射
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 9:18
 */
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_region")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Region extends AuditDomain {
    public static final String FIELD_REGION_ID = "regionId";
    public static final String FIELD_COUNTRY_ID = "countryId";
    public static final String FIELD_PARENT_REGION_ID = "parentRegionId";

    public static final String FIELD_REGION_NAME = "regionName";
    public static final String FIELD_QUICK_INDEX = "quickIndex";
    public static final String FIELD_ENABLE_FLAG = "enabledFlag";


    @Id
    @GeneratedValue
    @Encrypt
    private Long regionId;
    @Encrypt
    private Long countryId;
    @NotBlank
    @Pattern(regexp = Regexs.CODE_UPPER)
    @Length(max = 30)
    private String regionCode;
    @NotBlank
    @MultiLanguageField
    @Length(max = 120)
    private String regionName;
    @Encrypt
    private Long parentRegionId;
    private String levelPath;
    private Integer levelNumber;
    @NotNull
    @Max(1)
    @Min(0)
    private Integer enabledFlag;
    @Length(max = 30)
    @MultiLanguageField
    private String quickIndex;
    @NotNull
    @MultiLanguageField
    private Long tenantId;

    /**
     * @return 地区ID
     */
    public Long getRegionId() {
        return regionId;
    }

    public Region setRegionId(Long regionId) {
        this.regionId = regionId;
        return this;
    }

    /**
     * @return 国家ID
     */
    public Long getCountryId() {
        return countryId;
    }

    public Region setCountryId(Long countryId) {
        this.countryId = countryId;
        return this;
    }

    /**
     * @return 地区编码
     */
    public String getRegionCode() {
        return regionCode;
    }

    public Region setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        return this;
    }

    /**
     * @return 地区名称
     */
    public String getRegionName() {
        return regionName;
    }

    public Region setRegionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    /**
     * @return 父地区ID
     */
    public Long getParentRegionId() {
        return parentRegionId;
    }

    public Region setParentRegionId(Long parentRegionId) {
        this.parentRegionId = parentRegionId;
        return this;
    }

    /**
     * @return 返回等级路径
     */
    @JsonIgnore
    public String getLevelPath() {
        return levelPath;
    }

    public Region setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    /**
     * @return 国家是否启用标记
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Region setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 快速检索
     */
    public String getQuickIndex() {
        return quickIndex;
    }

    public Region setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
        return this;
    }

    /**
     * @return 国家地区层级值
     */
    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
