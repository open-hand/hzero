package org.hzero.plugin.platform.hr.domain.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.*;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.PinyinUtils;
import org.hzero.core.util.Regexs;
import org.hzero.plugin.platform.hr.api.dto.EmployeeAssignDTO;
import org.hzero.plugin.platform.hr.infra.constant.PlatformHrConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 员工表
 *
 * @author liang.jin@hand-china.com 2018-06-20 15:20:43
 */
@VersionAudit
@ModifyAudit
@Table(name = "hpfm_employee")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employee extends AuditDomain {

    public static final String FIELD_EMPLOYEE_ID = "employeeId";
    public static final String FIELD_EMPLOYEE_NUM = "employeeNum";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_NAME_EN = "nameEn";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_MOBILE = "mobile";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_CID = "cid";
    public static final String FIELD_ENABLE_FLAG = "enabledFlag";
    public static final String FIELD_QUICT_INDEX = "quickIndex";
    public static final String FIELD_PHONETICIZE = "phoneticize";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_ENTRY_DATE = "entryDate";
    public static final String ENCRYPT = "hpfm_employee";


    /**
     * 生成拼音和快速索引
     */
    public void generateQuickIndexAndPinyin() {
        if (this.quickIndex == null) {
            // 快速索引为空则生成快速索引
            this.quickIndex = PinyinUtils.getPinyinCapital(this.name);
            if (quickIndex.length() > PlatformHrConstants.QUICK_INDEX_LENGTH) {
                // 快速索引过长，截取前240个字符，并排除不完整的拼音内容
                this.quickIndex =
                        StringUtils.join(StringUtils.substringBeforeLast(
                                StringUtils.substring(quickIndex, BaseConstants.Digital.ZERO,
                                        PlatformHrConstants.QUICK_INDEX_LENGTH),
                                BaseConstants.Symbol.VERTICAL_BAR), BaseConstants.Symbol.VERTICAL_BAR);
            }
        }
        if (this.phoneticize == null) {
            // 拼音为空则生成拼音
            this.phoneticize = PinyinUtils.getPinyin(this.name);
            if (phoneticize.length() > PlatformHrConstants.PINYIN_LENGTH) {
                // 拼音长度过长，截取前240个字符，排除不完整的拼音内容
                this.phoneticize =
                        StringUtils.join(StringUtils.substringBeforeLast(
                                StringUtils.substring(phoneticize, BaseConstants.Digital.ZERO,
                                        PlatformHrConstants.PINYIN_LENGTH),
                                BaseConstants.Symbol.VERTICAL_BAR), BaseConstants.Symbol.VERTICAL_BAR);
            }
        }
    }

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @NotNull(groups = PrimaryKeyValid.class)
    @Encrypt
    private Long employeeId;
    @NotBlank
    @Size(max = 30)
    @Pattern(regexp = Regexs.CODE)
    private String employeeNum;
    @NotBlank
    @Size(max = 60)
    private String name;
    private String nameEn;
    @NotNull
    private Long tenantId;
    @NotNull
    @Size(max = 60)
    @Email
    private String email;
    @NotNull
    @Size(max = 60)
    private String mobile;
    @NotNull
    @Max(1)
    private Integer gender;
    private String cid;
    @NotNull
    private Integer enabledFlag;
    @Size(max = PlatformHrConstants.QUICK_INDEX_LENGTH)
    private String quickIndex;
    @Size(max = PlatformHrConstants.PINYIN_LENGTH)
    private String phoneticize;
    @LovValue(lovCode = "HPFM.EMPLOYEE_STATUS", meaningField = "statusMeaning")
    private String status;
    private LocalDate entryDate;
    @Transient
    private String statusMeaning;
    @Transient
    private String companyName;

    @Transient
    private List<EmployeeAssignDTO> employeeAssignList;

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * @return 员工ID
     */
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * @return 员工编码
     */
    public String getEmployeeNum() {
        return employeeNum;
    }

    public Employee setEmployeeNum(String employeeNum) {
        this.employeeNum = employeeNum;
        return this;
    }

    /**
     * @return 员工姓名
     */
    public String getName() {
        return name;
    }

    public Employee setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return 员工英文名
     */
    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public Employee setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    /**
     * @return 电子邮件
     */
    public String getEmail() {
        return email;
    }

    public Employee setEmail(String email) {
        this.email = email;
        return this;
    }

    /**
     * @return 移动电话
     */
    public String getMobile() {
        return mobile;
    }

    public Employee setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    /**
     * @return 性别, 0: 男 1: 女
     */
    public Integer getGender() {
        return gender;
    }

    public Employee setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    /**
     * @return 身份编码
     */
    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    /**
     * @return 启用状态
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
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
     * 主键验证分组
     */
    public interface PrimaryKeyValid {
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public void setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
    }

    public String getPhoneticize() {
        return phoneticize;
    }

    public void setPhoneticize(String phoneticize) {
        this.phoneticize = phoneticize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public void setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
    }

    public List<EmployeeAssignDTO> getEmployeeAssignList() {
        return employeeAssignList;
    }

    public Employee setEmployeeAssignList(List<EmployeeAssignDTO> employeeAssignList) {
        this.employeeAssignList = employeeAssignList;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", employeeNum='" + employeeNum + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", tenantId=" + tenantId +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", gender=" + gender +
                ", cid='" + cid + '\'' +
                ", enabledFlag=" + enabledFlag +
                ", quickIndex='" + quickIndex + '\'' +
                ", phoneticize='" + phoneticize + '\'' +
                ", status='" + status + '\'' +
                ", entryDate=" + entryDate +
                ", statusMeaning='" + statusMeaning + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}

