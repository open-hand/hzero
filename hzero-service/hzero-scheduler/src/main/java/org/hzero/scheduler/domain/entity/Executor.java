package org.hzero.scheduler.domain.entity;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.scheduler.domain.repository.ExecutorRepository;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author shuangfei.zhu@hand-china.com 2019-01-09 14:45:13
 */
@ApiModel("执行器")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_executor")
@JsonInclude(JsonInclude.Include.NON_NULL)
@MultiLanguage
public class Executor extends AuditDomain {

    public static final String FIELD_EXECUTOR_ID = "executorId";
    public static final String FIELD_EXECUTOR_CODE = "executorCode";
    public static final String FIELD_EXECUTOR_NAME = "executorName";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_EXECUTOR_TYPE = "executorType";
    public static final String FIELD_ADDRESS_LIST = "addressList";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_SERVER_NAME = "serverName";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 唯一性校验
     */
    public void validateRepeat(ExecutorRepository repository) {
        if (Objects.equals(executorType, BaseConstants.Flag.YES)) {
            Assert.isTrue(StringUtils.isNotBlank(addressList), HsdrErrorCode.ADDRESS_LIST_NULL);
        }
        Assert.isTrue(repository.selectCount(new Executor().setExecutorCode(executorCode)) == BaseConstants.Digital.ZERO, HsdrErrorCode.CODE_REPEAT);
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long executorId;
    @ApiModelProperty(value = "执行器编码")
    @NotBlank
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String executorCode;
    @ApiModelProperty(value = "执行器名称")
    @NotBlank
    @Length(max = 120)
    @MultiLanguageField
    private String executorName;
    @ApiModelProperty(value = "排序")
    @NotNull
    private Integer orderSeq;
    @ApiModelProperty(value = "执行器地址类型：0=自动注册、1=手动录入")
    @NotNull
    @Range(max = 1)
    private Integer executorType;
    @ApiModelProperty(value = "执行器地址列表，多地址逗号分隔")
    @Length(max = 240)
    private String addressList;
    @ApiModelProperty(value = "执行器状态")
    @Length(max = 30)
    @LovValue(lovCode = HsdrConstant.ExecutorStatus.CODE)
    private String status;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id")
    @MultiLanguageField
    private Long tenantId;
    @ApiModelProperty(value = "服务名称")
    @Length(max = 120)
    private String serverName;


    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String statusMeaning;
    @Transient
    private String tenantName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public Long getExecutorId() {
        return executorId;
    }

    public Executor setExecutorId(Long executorId) {
        this.executorId = executorId;
        return this;
    }

    public String getExecutorCode() {
        return executorCode;
    }

    public Executor setExecutorCode(String executorCode) {
        this.executorCode = executorCode;
        return this;
    }

    public String getExecutorName() {
        return executorName;
    }

    public Executor setExecutorName(String executorName) {
        this.executorName = executorName;
        return this;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public Executor setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public Integer getExecutorType() {
        return executorType;
    }

    public Executor setExecutorType(Integer executorType) {
        this.executorType = executorType;
        return this;
    }

    public String getAddressList() {
        return addressList;
    }

    public Executor setAddressList(String addressList) {
        this.addressList = addressList;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Executor setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public Executor setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Executor setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public Executor setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getServerName() {
        return serverName;
    }

    public Executor setServerName(String serverName) {
        this.serverName = serverName;
        return this;
    }
}
