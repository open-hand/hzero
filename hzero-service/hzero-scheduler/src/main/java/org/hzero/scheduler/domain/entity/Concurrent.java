package org.hzero.scheduler.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.scheduler.domain.repository.ConcurrentRepository;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 并发程序
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-11 10:20:47
 */
@ApiModel("并发程序")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_concurrent")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class Concurrent extends AuditDomain {

    public static final String FIELD_CONCURRENT_ID = "concurrentId";
    public static final String FIELD_EXECUTABLE_ID = "executableId";
    public static final String FIELD_CONC_CODE = "concCode";
    public static final String FIELD_CONC_NAME = "concName";
    public static final String FIELD_CONC_DESCRIPTION = "concDescription";
    public static final String FIELD_ALARM_EMAIL = "alarmEmail";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验并发程序编码重复
     *
     * @param concurrentRepository 仓库
     */
    public void validateConcCodeRepeat(ConcurrentRepository concurrentRepository) {
        Concurrent concurrentCheck = new Concurrent();
        concurrentCheck.setConcCode(concCode);
        concurrentCheck.setTenantId(tenantId);
        Assert.isTrue(concurrentRepository.selectCount(concurrentCheck) == BaseConstants.Digital.ZERO, HsdrErrorCode.CODE_REPEAT);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long concurrentId;
    @ApiModelProperty(value = "可执行ID")
    @NotNull
    @Encrypt
    private Long executableId;
    @ApiModelProperty(value = "并发程序代码")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String concCode;
    @ApiModelProperty(value = "并发程序名称")
    @NotBlank(groups = {Validate.class})
    @Length(max = 120)
    @MultiLanguageField
    private String concName;
    @ApiModelProperty(value = "并发程序描述")
    @Length(max = 240)
    private String concDescription;
    @ApiModelProperty(value = "报警邮件")
    @Length(max = 240)
    @Email
    private String alarmEmail;
    @ApiModelProperty(value = "启用标识")
    @NotNull
    @Range(max = 1)
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @NotNull
    @MultiLanguageField
    private Long tenantId;

    public interface Validate {
    }
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty(value = "可执行代码")
    @Transient
    private String executableCode;
    @ApiModelProperty(value = "可执行名称")
    @Transient
    private String executableName;
    @ApiModelProperty(value = "详情集合")
    @Valid
    @Transient
    private List<ConcurrentParam> paramList;
    @ApiModelProperty(value = "租户名称")
    @Transient
    private String tenantName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getConcurrentId() {
        return concurrentId;
    }

    public Concurrent setConcurrentId(Long concurrentId) {
        this.concurrentId = concurrentId;
        return this;
    }

    public Long getExecutableId() {
        return executableId;
    }

    public Concurrent setExecutableId(Long executableId) {
        this.executableId = executableId;
        return this;
    }

    public String getConcCode() {
        return concCode;
    }

    public Concurrent setConcCode(String concCode) {
        this.concCode = concCode;
        return this;
    }

    public String getConcName() {
        return concName;
    }

    public Concurrent setConcName(String concName) {
        this.concName = concName;
        return this;
    }

    public String getConcDescription() {
        return concDescription;
    }

    public Concurrent setConcDescription(String concDescription) {
        this.concDescription = concDescription;
        return this;
    }

    public String getAlarmEmail() {
        return alarmEmail;
    }

    public Concurrent setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Concurrent setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Concurrent setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getExecutableCode() {
        return executableCode;
    }

    public Concurrent setExecutableCode(String executableCode) {
        this.executableCode = executableCode;
        return this;
    }

    public String getExecutableName() {
        return executableName;
    }

    public Concurrent setExecutableName(String executableName) {
        this.executableName = executableName;
        return this;
    }

    public List<ConcurrentParam> getParamList() {
        return paramList;
    }

    public Concurrent setParamList(List<ConcurrentParam> paramList) {
        this.paramList = paramList;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Concurrent setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }
}
