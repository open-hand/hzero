package org.hzero.scheduler.domain.entity;

import java.math.BigDecimal;
import java.text.ParseException;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.scheduler.domain.repository.ConcurrentParamRepository;
import org.hzero.scheduler.infra.constant.HsdrConstant;
import org.hzero.scheduler.infra.constant.HsdrErrorCode;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 并发程序参数
 *
 * @author shuangfei.zhu@hand-china.com 2018-09-11 10:20:47
 */
@ApiModel("并发程序参数")
@VersionAudit
@ModifyAudit
@Table(name = "hsdr_conc_param")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcurrentParam extends AuditDomain {

    public static final String FIELD_CONC_PARAM_ID = "concParamId";
    public static final String FIELD_CONCURRENT_ID = "concurrentId";
    public static final String FIELD_ORDER_SEQ = "orderSeq";
    public static final String FIELD_PARAM_CODE = "paramCode";
    public static final String FIELD_PARAM_NAME = "paramName";
    public static final String FIELD_PARAM_FORMAT_CODE = "paramFormatCode";
    public static final String FIELD_PARAM_EDIT_TYPE_CODE = "paramEditTypeCode";
    public static final String FIELD_NOTNULL_FLAG = "notnullFlag";
    public static final String FIELD_BUSINESS_MODEL = "businessModel";
    public static final String FIELD_VALUE_FILED_FROM = "valueFiledFrom";
    public static final String FIELD_VALUE_FILED_TO = "valueFiledTo";
    public static final String FIELD_SHOW_FLAG = "showFlag";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_DEFAULT_VALUE = "defaultValue";


    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(BaseConstants.Pattern.DATETIME);

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 校验并发程序参数编码重复
     *
     * @param concurrentParamRepository 仓库
     */
    public void validateParamCodeRepeat(ConcurrentParamRepository concurrentParamRepository) {
        ConcurrentParam concurrentParamCheck = new ConcurrentParam().setConcurrentId(concurrentId).setParamCode(paramCode).setTenantId(tenantId);
        Assert.isTrue(concurrentParamRepository.selectCount(concurrentParamCheck) == BaseConstants.Digital.ZERO, HsdrErrorCode.CODE_REPEAT);
    }

    public void validateParam() {
        switch (paramFormatCode) {
            case HsdrConstant.ParamFormat.NUMBER:
                if (StringUtils.isNotBlank(defaultValue) && notNumber(defaultValue)) {
                    throw new CommonException(HsdrErrorCode.WRONG_FORMAT);
                }
                if (StringUtils.isNotBlank(valueFiledFrom) && notNumber(valueFiledFrom)) {
                    throw new CommonException(HsdrErrorCode.WRONG_FORMAT);
                }
                if (StringUtils.isNotBlank(valueFiledTo) && notNumber(valueFiledTo)) {
                    throw new CommonException(HsdrErrorCode.WRONG_FORMAT);
                }
                break;
            case HsdrConstant.ParamFormat.DATE:
                if (StringUtils.isNotBlank(defaultValue) && notDate(defaultValue)) {
                    throw new CommonException(HsdrErrorCode.WRONG_FORMAT);
                }
                if (StringUtils.isNotBlank(valueFiledFrom) && notDate(valueFiledFrom)) {
                    throw new CommonException(HsdrErrorCode.WRONG_FORMAT);
                }
                if (StringUtils.isNotBlank(valueFiledTo) && notDate(valueFiledTo)) {
                    throw new CommonException(HsdrErrorCode.WRONG_FORMAT);
                }
                break;
            case HsdrConstant.ParamFormat.TEXT:
            default:
                break;
        }
    }

    public static boolean notNumber(String str) {
        try {
            new BigDecimal(str);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public static boolean notDate(String str) {
        try {
            DATE_FORMAT.parse(str);
        } catch (ParseException e) {
            return true;
        }
        return false;
    }


    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long concParamId;
    @ApiModelProperty(value = "并发程序ID，hsdr_concurrent.concurrent_id,不可更改")
    @Encrypt
    private Long concurrentId;
    @ApiModelProperty(value = "排序号")
    @NotNull(groups = {ConcurrentParam.Validate.class})
    private Long orderSeq;
    @ApiModelProperty(value = "参数名称")
    @NotBlank
    @Size(max = 30)
    @Pattern(regexp = Regexs.CODE)
    private String paramCode;
    @ApiModelProperty(value = "参数描述")
    @NotBlank(groups = {ConcurrentParam.Validate.class})
    @Size(max = 120)
    private String paramName;
    @ApiModelProperty(value = "参数格式，HSDR.PARAM_FORMAT")
    @NotBlank(groups = {ConcurrentParam.Validate.class})
    @LovValue(lovCode = "HSDR.PARAM_FORMAT")
    @Size(max = 30)
    private String paramFormatCode;
    @ApiModelProperty(value = "编辑类型，HSDR.PARAM_EDIT_TYPE")
    @NotBlank(groups = {ConcurrentParam.Validate.class})
    @LovValue(lovCode = "HSDR.PARAM_EDIT_TYPE")
    @Size(max = 30)
    private String paramEditTypeCode;
    @ApiModelProperty(value = "是否必须")
    @NotNull(groups = {ConcurrentParam.Validate.class})
    private Integer notnullFlag;
    @ApiModelProperty(value = "业务模型")
    @Size(max = 1200)
    private String businessModel;
    @ApiModelProperty(value = "字段值从")
    @Size(max = 30)
    private String valueFiledFrom;
    @ApiModelProperty(value = "字段值从")
    @Size(max = 30)
    private String valueFiledTo;
    @ApiModelProperty(value = "是否展示")
    @NotNull(groups = {ConcurrentParam.Validate.class})
    private Integer showFlag;
    @ApiModelProperty(value = "启用标识")
    private Integer enabledFlag;
    @ApiModelProperty(value = "租户ID,hpfm_tenant.tenant_id,不可更改")
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "默认值")
    @Size(max = 240)
    private String defaultValue;

    public interface Validate {
    }

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @ApiModelProperty(value = "参数格式Meaning")
    @Transient
    private String paramFormatMeaning;
    @ApiModelProperty(value = "编辑类型Meaning")
    @Transient
    private String paramEditTypeMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getConcParamId() {
        return concParamId;
    }

    public ConcurrentParam setConcParamId(Long concParamId) {
        this.concParamId = concParamId;
        return this;
    }

    public Long getConcurrentId() {
        return concurrentId;
    }

    public ConcurrentParam setConcurrentId(Long concurrentId) {
        this.concurrentId = concurrentId;
        return this;
    }

    public Long getOrderSeq() {
        return orderSeq;
    }

    public ConcurrentParam setOrderSeq(Long orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public String getParamCode() {
        return paramCode;
    }

    public ConcurrentParam setParamCode(String paramCode) {
        this.paramCode = paramCode;
        return this;
    }

    public String getParamName() {
        return paramName;
    }

    public ConcurrentParam setParamName(String paramName) {
        this.paramName = paramName;
        return this;
    }

    public String getParamFormatCode() {
        return paramFormatCode;
    }

    public ConcurrentParam setParamFormatCode(String paramFormatCode) {
        this.paramFormatCode = paramFormatCode;
        return this;
    }

    public String getParamEditTypeCode() {
        return paramEditTypeCode;
    }

    public ConcurrentParam setParamEditTypeCode(String paramEditTypeCode) {
        this.paramEditTypeCode = paramEditTypeCode;
        return this;
    }

    public Integer getNotnullFlag() {
        return notnullFlag;
    }

    public ConcurrentParam setNotnullFlag(Integer notnullFlag) {
        this.notnullFlag = notnullFlag;
        return this;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public ConcurrentParam setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
        return this;
    }

    public String getValueFiledFrom() {
        return valueFiledFrom;
    }

    public ConcurrentParam setValueFiledFrom(String valueFiledFrom) {
        this.valueFiledFrom = valueFiledFrom;
        return this;
    }

    public String getValueFiledTo() {
        return valueFiledTo;
    }

    public ConcurrentParam setValueFiledTo(String valueFiledTo) {
        this.valueFiledTo = valueFiledTo;
        return this;
    }

    public Integer getShowFlag() {
        return showFlag;
    }

    public ConcurrentParam setShowFlag(Integer showFlag) {
        this.showFlag = showFlag;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public ConcurrentParam setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public ConcurrentParam setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public ConcurrentParam setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public String getParamFormatMeaning() {
        return paramFormatMeaning;
    }

    public ConcurrentParam setParamFormatMeaning(String paramFormatMeaning) {
        this.paramFormatMeaning = paramFormatMeaning;
        return this;
    }

    public String getParamEditTypeMeaning() {
        return paramEditTypeMeaning;
    }

    public ConcurrentParam setParamEditTypeMeaning(String paramEditTypeMeaning) {
        this.paramEditTypeMeaning = paramEditTypeMeaning;
        return this;
    }
}
