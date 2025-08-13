package org.hzero.platform.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.format.annotation.DateTimeFormat;

import io.choerodon.mybatis.domain.AuditDomain;

import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;

/**
 * 公司DTO
 *
 * @author gaokuo.dai@hand-china.com 2018年6月22日上午11:18:41
 */
@ApiModel("公司信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDTO extends AuditDomain {

    @ApiModelProperty("公司ID")
    @Encrypt
    private Long companyId;
    @NotEmpty
    @Size(max = 30)
    @ApiModelProperty("公司编码")
    private String companyNum;
    @ApiModelProperty("集团ID hpfm_group.group_id")
    @Encrypt
    private Long groupId;
    @NotNull
    @ApiModelProperty("租户ID hpfm_tenant.tenant_id")
    @Encrypt
    private Long tenantId;
    @ApiModelProperty("关联组织ID hpfm_unit.unit_id")
    @Encrypt
    private Long unitId;
    @NotEmpty
    @Size(max = 10)
    @ApiModelProperty("境内境外，1境内，0境外")
    private Integer domesticForeignRelation;
    @Size(max = 30)
    @ApiModelProperty("统一社会信用码")
    private String unifiedSocialCode;
    @Size(max = 30)
    @ApiModelProperty("组织机构代码")
    private String organizingInstitutionCode;
    @NotEmpty
    @Size(max = 150)
    @ApiModelProperty("公司名称")
    private String companyName;
    @Size(max = 50)
    @ApiModelProperty("公司简称")
    private String shortName;
    @Size(max = 30)
    @ApiModelProperty("公司类型")
    @LovValue(lovCode = "HPFM.COMPANY_TYPE")
    private String companyType;
    @ApiModelProperty("公司类型含义")
    private String companyTypeMeaning;
    @NotNull
    @ApiModelProperty("国家ID")
    @Encrypt
    private Long registeredCountryId;
    @ApiModelProperty("国家名称")
    private String registeredCountryName;
    @ApiModelProperty("地区ID")
    @Encrypt
    private Long registeredRegionId;
    @ApiModelProperty("地区名称")
    private String registeredRegionName;
    @NotEmpty
    @Size(max = 150)
    @ApiModelProperty("详细地址")
    private String addressDetail;
    @Size(max = 30)
    @ApiModelProperty("邓白氏编码")
    private String dunsCode;
    @Size(max = 30)
    @ApiModelProperty("纳税人类型，值集HPFM.TAXPAYER_TYPE")
    @LovValue(lovCode = "HPFM.TAXPAYER_TYPE")
    private String taxpayerType;
    @ApiModelProperty("纳税人类型含义")
    private String taxpayerTypeMeaning;
    @Size(max = 30)
    @ApiModelProperty("法人姓名")
    private String legalRepName;
    @NotNull
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @ApiModelProperty("成立日期")
    private LocalDate buildDate;
    @ApiModelProperty("注册资本")
    private BigDecimal registeredCapital;
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @ApiModelProperty("营业期限")
    private LocalDate licenceEndDate;
    @ApiModelProperty("经营范围")
    private String businessScope;
    @NotNull
    @ApiModelProperty("长期标志")
    private Integer longTermFlag;
    @ApiModelProperty("营业执照附件路径")
    private String licenceUrl;
    @ApiModelProperty("营业执照附件名")
    private String licenceFileName;
    @ApiModelProperty("源数据key")
    private String sourceKey;
    @NotEmpty
    @ApiModelProperty("数据来源,值集：HPFM.DATA_SOURCE")
    private String sourceCode;
    @NotNull
    @ApiModelProperty("启用标识")
    private Integer enabledFlag;
    @ApiModelProperty("集团编码")
    private String groupNum;

    /**
     * @return 表ID
     */
    public Long getCompanyId() {
        return companyId;
    }
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
    /**
     * @return 公司编码，自动生成
     */
    public String getCompanyNum() {
        return companyNum;
    }
    public void setCompanyNum(String companyNum) {
        this.companyNum = companyNum;
    }
    /**
     * @return 集团id
     */
    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    /**
     * @return 租户id，从集团带过来
     */
    public Long getTenantId() {
        return tenantId;
    }
    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
    /**
     * @return 组织id
     */
    public Long getUnitId() {
        return unitId;
    }
    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
    /**
     * @return 境内境外，IN境内，OUT境外
     */
    public Integer getDomesticForeignRelation() {
        return domesticForeignRelation;
    }
    public void setDomesticForeignRelation(Integer domesticForeignRelation) {
        this.domesticForeignRelation = domesticForeignRelation;
    }
    /**
     * @return 统一社会信用码
     */
    public String getUnifiedSocialCode() {
        return unifiedSocialCode;
    }
    public void setUnifiedSocialCode(String unifiedSocialCode) {
        this.unifiedSocialCode = unifiedSocialCode;
    }
    /**
     * @return 公司名称
     */
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    /**
     * @return 公司简称
     */
    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    /**
     * @return 公司类型
     */
    public String getCompanyType() {
        return companyType;
    }
    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
    /**
     * @return 国家
     */
    public Long getRegisteredCountryId() {
        return registeredCountryId;
    }
    public void setRegisteredCountryId(Long registeredCountryId) {
        this.registeredCountryId = registeredCountryId;
    }
    /**
     * @return 地区id，树形值集
     */
    public Long getRegisteredRegionId() {
        return registeredRegionId;
    }
    public void setRegisteredRegionId(Long registeredRegionId) {
        this.registeredRegionId = registeredRegionId;
    }
    /**
     * @return 详细地址
     */
    public String getAddressDetail() {
        return addressDetail;
    }
    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
    /**
     * @return 邓白氏编码
     */
    public String getDunsCode() {
        return dunsCode;
    }
    public void setDunsCode(String dunsCode) {
        this.dunsCode = dunsCode;
    }
    /**
     * @return 纳税人类型，值集HPFM.TAXPAYER_TYPE
     */
    public String getTaxpayerType() {
        return taxpayerType;
    }
    public void setTaxpayerType(String taxpayerType) {
        this.taxpayerType = taxpayerType;
    }
    /**
     * @return 法人姓名
     */
    public String getLegalRepName() {
        return legalRepName;
    }
    public void setLegalRepName(String legalRepName) {
        this.legalRepName = legalRepName;
    }
    /**
     * @return 成立日期
     */
    public LocalDate getBuildDate() {
        return buildDate;
    }
    public void setBuildDate(LocalDate buildDate) {
        this.buildDate = buildDate;
    }
    /**
     * @return 注册资本
     */
    public BigDecimal getRegisteredCapital() {
        return registeredCapital;
    }
    public void setRegisteredCapital(BigDecimal registeredCapital) {
        this.registeredCapital = registeredCapital;
    }
    /**
     * @return 营业期限
     */
    public LocalDate getLicenceEndDate() {
        return licenceEndDate;
    }
    public void setLicenceEndDate(LocalDate licenceEndDate) {
        this.licenceEndDate = licenceEndDate;
    }
    /**
     * @return 经营范围
     */
    public String getBusinessScope() {
        return businessScope;
    }
    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }
    /**
     * @return 长期标志，1：长期，0：非长期
     */
    public Integer getLongTermFlag() {
        return longTermFlag;
    }
    public void setLongTermFlag(Integer longTermFlag) {
        this.longTermFlag = longTermFlag;
    }
    /**
     * @return 营业执照附件路径
     */
    public String getLicenceUrl() {
        return licenceUrl;
    }
    public void setLicenceUrl(String licenceUrl) {
        this.licenceUrl = licenceUrl;
    }
    /**
     * @return 查询字段: 集团编码
     */
    public String getGroupNum() {
        return groupNum;
    }
    public void setGroupNum(String groupNum) {
        this.groupNum = groupNum;
    }
    /**
     * @return 源数据key
     */
    public String getSourceKey() {
        return sourceKey;
    }
    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }
    /**
     * @return 来源,值集：HPFM.DATA_SOURCE
     */
    public String getSourceCode() {
        return sourceCode;
    }
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }
    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
    /**
     * @return 组织机构代码
     */
    public String getOrganizingInstitutionCode() {
        return organizingInstitutionCode;
    }
    /**
     * @return 公司类型含义
     */
    public String getCompanyTypeMeaning() {
        return companyTypeMeaning;
    }
    /**
     * @return 国家名称
     */
    public String getRegisteredCountryName() {
        return registeredCountryName;
    }
    /**
     * @return 地区名称
     */
    public String getRegisteredRegionName() {
        return registeredRegionName;
    }
    /**
     * @return 纳税人类型含义
     */
    public String getTaxpayerTypeMeaning() {
        return taxpayerTypeMeaning;
    }
    /**
     * @return 营业执照附件名称
     */
    public String getLicenceFileName() {
        return licenceFileName;
    }
    public void setOrganizingInstitutionCode(String organizingInstitutionCode) {
        this.organizingInstitutionCode = organizingInstitutionCode;
    }
    public void setCompanyTypeMeaning(String companyTypeMeaning) {
        this.companyTypeMeaning = companyTypeMeaning;
    }
    public void setRegisteredCountryName(String registeredCountryName) {
        this.registeredCountryName = registeredCountryName;
    }
    public void setRegisteredRegionName(String registeredRegionName) {
        this.registeredRegionName = registeredRegionName;
    }
    public void setTaxpayerTypeMeaning(String taxpayerTypeMeaning) {
        this.taxpayerTypeMeaning = taxpayerTypeMeaning;
    }
    public void setLicenceFileName(String licenceFileName) {
        this.licenceFileName = licenceFileName;
    }


}
