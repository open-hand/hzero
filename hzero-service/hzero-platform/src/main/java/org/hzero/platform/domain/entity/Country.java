package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Date;

/**
 * <p>
 * 国家定义数据库实体映射
 * </p>
 *
 * @author qingsheng.chen 2018/6/21 星期四 14:11
 */
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "hpfm_country")
public class Country extends AuditDomain {
    public static final String COUNTRY_ID = "countryId";
    public static final String FIELD_COUNTRY_NAME = "countryName";
    public static final String FIELD_QUICK_INDEX = "quickIndex";
    public static final String FIELD_ABBREVIATION = "abbreviation";
    public static final String FIELD_ENABLE_FLAG = "enabledFlag";



    @Id
    @GeneratedValue
    @NotNull(groups = PrimaryKeyValid.class)
    @Encrypt
    private Long countryId;
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String countryCode;
    @NotBlank
    @Length(max = 120)
    @MultiLanguageField
    private String countryName;
    @NotNull
    @Max(1)
    @Min(0)
    private Integer enabledFlag;
    @Length(max = 30)
    @MultiLanguageField
    private String quickIndex;
    @Length(max = 30)
    @MultiLanguageField
    private String abbreviation;
    @NotNull
    @MultiLanguageField
    private Long tenantId;

    /**
     * @return 返回国家ID
     */
    public Long getCountryId() {
        return countryId;
    }

    public Country setCountryId(Long countryId) {
        this.countryId = countryId;
        return this;
    }

    /**
     * @return 返回国家编码
     */
    public String getCountryCode() {
        return countryCode;
    }

    public Country setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    /**
     * @return 返回国家名称
     */
    public String getCountryName() {
        return countryName;
    }

    public Country setCountryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    /**
     * @return 返回国家是否启用
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Country setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    @Override
    @JsonIgnore
    public Date getCreationDate() {
        return super.getCreationDate();
    }

    @Override
    @JsonIgnore
    public Long getCreatedBy() {
        return super.getCreatedBy();
    }

    @Override
    @JsonIgnore
    public Date getLastUpdateDate() {
        return super.getLastUpdateDate();
    }

    @Override
    @JsonIgnore
    public Long getLastUpdatedBy() {
        return super.getLastUpdatedBy();
    }

    /**
     * @return 快速检索
     */
    public String getQuickIndex() {
        return quickIndex;
    }

    public Country setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
        return this;
    }

    /**
     * @return 简称
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    public Country setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Country setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * 主键验证分组
     */
    public interface PrimaryKeyValid {
    }
}
