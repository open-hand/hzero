package org.hzero.scheduler.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.scheduler.domain.repository.ExecutableRepository;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Objects;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 并发可执行
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-10 10:30:58
 */
@ApiModel("并发可执行")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_conc_executable")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class Executable extends AuditDomain {

    public static final String FIELD_EXECUTABLE_ID = "executableId";
    public static final String FIELD_EXECUTABLE_CODE = "executableCode";
    public static final String FIELD_EXECUTABLE_NAME = "executableName";
    public static final String FIELD_EXECUTABLE_DESC = "executableDesc";
    public static final String FIELD_EXECUTOR_ID = "executorId";
    public static final String FIELD_EXECUTOR_STRATEGY = "executorStrategy";
    public static final String FIELD_FAIL_STRATEGY = "failStrategy";
    public static final String FIELD_STRATEGY_PARAM = "strategyParam";
    public static final String FIELD_EXE_TYPE_CODE = "exeTypeCode";
    public static final String FIELD_JOB_HANDLER = "jobHandler";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验并发程序校验
     *
     * @param executableRepository 仓库
     */
    public void validate(ExecutableRepository executableRepository) {
        if (Objects.equals(exeTypeCode, HsdrConstant.JobType.SIMPLE)) {
            Assert.notNull(jobHandler, HsdrErrorCode.PARAMETER_ERROR);
        }

        Executable executableCheck = new Executable();
        executableCheck.setExecutableCode(executableCode);
        executableCheck.setTenantId(tenantId);
        Assert.isTrue(executableRepository.selectCount(executableCheck) == BaseConstants.Digital.ZERO, HsdrErrorCode.CODE_REPEAT);
    }
    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long executableId;
    @ApiModelProperty(value = "可执行代码,不可更改")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String executableCode;
    @ApiModelProperty(value = "可执行名称")
    @NotBlank(groups = {Executable.Validate.class})
    @Length(max = 120)
    @MultiLanguageField
    private String executableName;
    @ApiModelProperty(value = "可执行描述")
    @Length(max = 240)
    private String executableDesc;
    @ApiModelProperty(value = "执行器ID,hsdr_executor.executor_id")
    @NotNull(groups = {Executable.Validate.class})
    @Encrypt
    private Long executorId;
    @ApiModelProperty(value = "执行器策略，HSDR.EXECUTOR_STRATEGY")
    @NotBlank(groups = {Executable.Validate.class})
    @LovValue(lovCode = HsdrConstant.ExecutorStrategy.CODE)
    @Length(max = 30)
    private String executorStrategy;
    @ApiModelProperty(value = "失败处理策略，HSDR.FAIL_STRATEGY")
    @NotBlank(groups = {Executable.Validate.class})
    @LovValue(lovCode = HsdrConstant.FailStrategy.CODE)
    @Length(max = 30)
    private String failStrategy;
    @ApiModelProperty(value = "策略参数")
    @Length(max = 240)
    private String strategyParam;
    @ApiModelProperty(value = "可执行类型，HSDR.GLUE_TYPE")
    @NotBlank(groups = {Executable.Validate.class})
    @LovValue(lovCode = HsdrConstant.JobType.CODE)
    @Length(max = 30)
    private String exeTypeCode;
    @ApiModelProperty(value = "jobHandler")
    @Length(max = 30)
    private String jobHandler;
    @ApiModelProperty(value = "启用标识")
    @NotNull(groups = {Executable.Validate.class})
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id,不可更改")
    @NotNull
    @MultiLanguageField
    private Long tenantId;

    public interface Validate {
    }
    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    /**
     * 执行器名称
     */
    @Transient
    private String executorName;
    /**
     * 可执行类型Meaning
     */
    @Transient
    private String exeTypeMeaning;
    /**
     * 租户名称
     */
    @Transient
    private String tenantName;
    @Transient
    private String executorStrategyMeaning;
    @Transient
    private String failStrategyMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getExecutableId() {
        return executableId;
    }

    public Executable setExecutableId(Long executableId) {
        this.executableId = executableId;
        return this;
    }

    public String getExecutableCode() {
        return executableCode;
    }

    public Executable setExecutableCode(String executableCode) {
        this.executableCode = executableCode;
        return this;
    }

    public String getExecutableName() {
        return executableName;
    }

    public Executable setExecutableName(String executableName) {
        this.executableName = executableName;
        return this;
    }

    public String getExecutableDesc() {
        return executableDesc;
    }

    public Executable setExecutableDesc(String executableDesc) {
        this.executableDesc = executableDesc;
        return this;
    }

    public Long getExecutorId() {
        return executorId;
    }

    public Executable setExecutorId(Long executorId) {
        this.executorId = executorId;
        return this;
    }

    public String getExecutorStrategy() {
        return executorStrategy;
    }

    public Executable setExecutorStrategy(String executorStrategy) {
        this.executorStrategy = executorStrategy;
        return this;
    }

    public String getFailStrategy() {
        return failStrategy;
    }

    public Executable setFailStrategy(String failStrategy) {
        this.failStrategy = failStrategy;
        return this;
    }

    public String getStrategyParam() {
        return strategyParam;
    }

    public Executable setStrategyParam(String strategyParam) {
        this.strategyParam = strategyParam;
        return this;
    }

    public String getExeTypeCode() {
        return exeTypeCode;
    }

    public Executable setExeTypeCode(String exeTypeCode) {
        this.exeTypeCode = exeTypeCode;
        return this;
    }

    public String getJobHandler() {
        return jobHandler;
    }

    public Executable setJobHandler(String jobHandler) {
        this.jobHandler = jobHandler;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public Executable setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Executable setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getExecutorName() {
        return executorName;
    }

    public Executable setExecutorName(String executorName) {
        this.executorName = executorName;
        return this;
    }

    public String getExeTypeMeaning() {
        return exeTypeMeaning;
    }

    public Executable setExeTypeMeaning(String exeTypeMeaning) {
        this.exeTypeMeaning = exeTypeMeaning;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Executable setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getExecutorStrategyMeaning() {
        return executorStrategyMeaning;
    }

    public Executable setExecutorStrategyMeaning(String executorStrategyMeaning) {
        this.executorStrategyMeaning = executorStrategyMeaning;
        return this;
    }

    public String getFailStrategyMeaning() {
        return failStrategyMeaning;
    }

    public Executable setFailStrategyMeaning(String failStrategyMeaning) {
        this.failStrategyMeaning = failStrategyMeaning;
        return this;
    }
}
