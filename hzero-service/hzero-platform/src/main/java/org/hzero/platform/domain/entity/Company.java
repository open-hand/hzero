package org.hzero.platform.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.platform.app.service.CodeRuleService;
import org.hzero.platform.domain.repository.CompanyRepository;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.FndConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 公司信息
 *
 * @author gaokuo.dai@hand-china.com 2018-07-04 19:49:15
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_company")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("公司信息")
public class Company extends AuditDomain {

    public static final String FIELD_COMPANY_ID = "companyId";
    public static final String FIELD_COMPANY_NUM = "companyNum";
    public static final String FIELD_GROUP_ID = "groupId";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_UNIT_ID = "unitId";
    public static final String FIELD_DOMESTIC_FOREIGN_RELATION = "domesticForeignRelation";
    public static final String FIELD_UNIFIED_SOCIAL_CODE = "unifiedSocialCode";
    public static final String FIELD_ORGANIZING_INSTITUTION_CODE = "organizingInstitutionCode";
    public static final String FIELD_COMPANY_NAME = "companyName";
    public static final String FIELD_SHORT_NAME = "shortName";
    public static final String FIELD_COMPANY_TYPE = "companyType";
    public static final String FIELD_REGISTERED_COUNTRY_ID = "registeredCountryId";
    public static final String FIELD_REGISTERED_REGION_ID = "registeredRegionId";
    public static final String FIELD_ADDRESS_DETAIL = "addressDetail";
    public static final String FIELD_DUNS_CODE = "dunsCode";
    public static final String FIELD_TAXPAYER_TYPE = "taxpayerType";
    public static final String FIELD_LEGAL_REP_NAME = "legalRepName";
    public static final String FIELD_BUILD_DATE = "buildDate";
    public static final String FIELD_REGISTERED_CAPITAL = "registeredCapital";
    public static final String FIELD_LICENCE_END_DATE = "licenceEndDate";
    public static final String FIELD_BUSINESS_SCOPE = "business_scope";
    public static final String FIELD_LONG_TERM_FLAG = "longTermFlag";
    public static final String FIELD_LICENCE_URL = "licenceUrl";
    public static final String FIELD_SOURCE_KEY = "sourceKey";
    public static final String FIELD_SOURCE_CODE = "sourceCode";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";


    /**
     * 校验分组专用--插入
     */
    public static interface Insert{}
    /**
     * 校验分组专用--更新
     */
    public static interface Update{}
    
    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

	/**
	 * 补全必输参数
	 */
	public void complementLongTermFlag() {
		this.longTermFlag = this.licenceEndDate == null ? BaseConstants.Flag.YES : BaseConstants.Flag.NO;
	}
    
    /**
     * 根据给定数据更新
     * @param givenData
     * @param companyRepository
     * @return 更新后的公司信息
     */
    
    public Company updateBasedOnGivenData(Company givenData, CompanyRepository companyRepository) {
        this.unitId = givenData.unitId;
        this.domesticForeignRelation = givenData.domesticForeignRelation;
        this.unifiedSocialCode = givenData.unifiedSocialCode;
        this.organizingInstitutionCode = givenData.organizingInstitutionCode;
        this.companyName = givenData.companyName;
        this.shortName = givenData.shortName;
        this.companyType = givenData.companyType;
        this.registeredCountryId = givenData.registeredCountryId;
        this.registeredRegionId = givenData.registeredRegionId;
        this.addressDetail = givenData.addressDetail;
        this.dunsCode = givenData.dunsCode;
        this.taxpayerType = givenData.taxpayerType;
        this.legalRepName = givenData.legalRepName;
        this.buildDate = givenData.buildDate;
        this.registeredCapital = givenData.registeredCapital;
        this.licenceEndDate = givenData.licenceEndDate;
        this.businessScope = givenData.businessScope;
        this.longTermFlag = givenData.longTermFlag;
        this.licenceUrl = givenData.licenceUrl;
        this.sourceKey = givenData.sourceKey;
        this.sourceCode = givenData.sourceCode;
        this.enabledFlag = givenData.enabledFlag;
        companyRepository.updateByPrimaryKey(this);
        return this;
    }
    
    /**
     * 将本实体插入数据库
     * 
     * @param companyRepository
     * @param codeRuleService
     * @return 插入后的数据
     */
    public Company insertIntoDb(CompanyRepository companyRepository, CodeRuleService codeRuleService) {
        Assert.notNull(this.tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        if(StringUtils.isEmpty(this.companyNum)) {
            // 如果没有传入公司编码则自动生成
            this.companyNum = codeRuleService.generatePlatformLevelCode(Constants.RuleCodes.HPFM_COMPANY, null);
        }else {
            // 如果传入了公司编码则进行查重
            Assert.isTrue(companyRepository.selectRepeatCount(this) == 0, BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
        }
        if(StringUtils.isEmpty(this.sourceCode)) {
            this.sourceCode = FndConstants.DataSource.HZERO;
        }
        companyRepository.insertSelective(this);
        return this;
    }
    
    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
	@GeneratedValue
	@ApiModelProperty("公司ID")
	@Encrypt
	private Long companyId;
    @NotEmpty(groups = Update.class)
	@Size(max = 30)
    @ApiModelProperty("公司编码")
	private String companyNum;
    @NotNull
    @ApiModelProperty("集团ID hpfm_group.group_id")
	@Encrypt
	private Long groupId;
	@NotNull
	@ApiModelProperty("租户ID hpfm_tenant.tenant_id")
	private Long tenantId;
	@ApiModelProperty("关联组织ID hpfm_unit.unit_id")
	@Encrypt
	private Long unitId;
	@ApiModelProperty("境内境外，1境内，0境外")
	@Range(min = 0, max = 1)
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
	private String companyType;
	@NotNull
	@ApiModelProperty("国家ID")
	@Encrypt
	private Long registeredCountryId;
	@ApiModelProperty("地区ID")
	@Encrypt
	private Long registeredRegionId;
	@NotEmpty
	@Size(max = 150)
	@ApiModelProperty("详细地址")
	private String addressDetail;
	@Size(max = 30)
	@ApiModelProperty("邓白氏编码")
	private String dunsCode;
	@Size(max = 30)
	@ApiModelProperty("纳税人类型，值集HPFM.TAXPAYER_TYPE")
	private String taxpayerType;
	@Size(max = 120)
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
	@ApiModelProperty("源数据key")
	private String sourceKey;
	@ApiModelProperty("数据来源,值集：HPFM.DATA_SOURCE")
	private String sourceCode;
	@NotNull
	@ApiModelProperty("启用标识")
	private Integer enabledFlag;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
	
	@Transient
	@ApiModelProperty(hidden = true)
    private String groupNum;
	@Transient
	@ApiModelProperty(hidden = true)
	private Boolean newTenant;
	@Transient
	@ApiModelProperty(hidden = true)
	private Map<String, Object> params;
	@Transient
	@ApiModelProperty(hidden = true)
	private Integer successFlag;
	
    //
    // getter/setter
    // ------------------------------------------------------------------------------

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
     * @return 境内境外，1境内，0境外
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
	 * @return 组织机构代码，和统一社会信用码至少存在一个
	 */
    public String getOrganizingInstitutionCode() {
        return organizingInstitutionCode;
    }
    public void setOrganizingInstitutionCode(String organizingInstitutionCode) {
        this.organizingInstitutionCode = organizingInstitutionCode;
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
     * @return 查询字段:是否为新建租户
     */
    public Boolean isNewTenant() {
        return newTenant;
    }
    public void setNewTenant(Boolean newTenant) {
        this.newTenant = newTenant;
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
     * @return 其他参数
     */
    public Map<String, Object> getParams() {
        return params;
    }
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    /**
     * @return 是否处理成功
     */
    public Integer getSuccessFlag() {
        return successFlag;
    }
    public void setSuccessFlag(Integer successFlag) {
        this.successFlag = successFlag;
    }
    

}
