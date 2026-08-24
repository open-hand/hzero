package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Regexs;
import org.hzero.message.domain.repository.ReceiverTypeRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 接收者类型
 *
 * @author runbai.chen@hand-china.com 2018-07-30 17:41:11
 */
@VersionAudit
@ModifyAudit
@ApiModel("接收者类型")
@Table(name = "hmsg_receiver_type")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiverType extends AuditDomain {

    public static final String FIELD_RECEIVER_TYPE_ID = "receiverTypeId";
    public static final String FIELD_TYPE_CODE = "typeCode";
    public static final String FIELD_TYPE_NAME = "typeName";
    public static final String FIELD_TYPE_MODE = "typeModeCode";
    public static final String FIELD_ROUTE_NAME = "routeName";
    public static final String FIELD_ROUTE_NAME_MEANING = "routeNameMeaning";
    public static final String FIELD_API_URL = "apiUrl";
    public static final String FIELD_ENABLED_FLAG = "enabledFlag";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @ApiModelProperty("接收者类型ID")
    @Encrypt
    private Long receiverTypeId;

    @NotBlank
    @Length(max = 30)
    @ApiModelProperty("类型代码")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String typeCode;

    @NotBlank
    @Length(max = 40)
    @ApiModelProperty("类型名称")
    private String typeName;
    @Length(max = 120)
    @ApiModelProperty("目标路由")
    private String routeName;
    @Length(max = 480)
    @ApiModelProperty("服务URL")
    private String apiUrl;
    @Range(min = 0, max = 1)
    @ApiModelProperty("启用标识")
    private Integer enabledFlag;
    @NotNull
    @ApiModelProperty("租户ID,hpfm_tenant.tenant_id")
    private Long tenantId;
    
    @LovValue(lovCode = "HMSG.RECEIVER.TYPE_MODE")
    private String typeModeCode;
    

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String typeMeaning;
    
    @Transient
    private String typeModeMeaning;

    @Transient
    private ReceiverDetail receiverDetail;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * 校验统一租户下编码不允许重复
     *
     * @param repository 仓库
     */
    public void validCodeRepeat(ReceiverTypeRepository repository) {
        Assert.isTrue(CollectionUtils.isEmpty(repository.select(new ReceiverType().setTenantId(tenantId).setTypeCode(typeCode))), BaseConstants.ErrorCode.ERROR_CODE_REPEAT);
    }

    /**
     * @return 表ID，主键，供其他表做外键
     */
    public Long getReceiverTypeId() {
        return receiverTypeId;
    }

    public void setReceiverTypeId(Long receiverTypeId) {
        this.receiverTypeId = receiverTypeId;
    }

    /**
     * @return 类型代码
     */
    public String getTypeCode() {
        return typeCode;
    }

    public ReceiverType setTypeCode(String typeCode) {
        this.typeCode = typeCode;
        return this;
    }

    /**
     * @return 类型名称
     */
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return 目标路由
     */
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * @return 服务URL
     */
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    /**
     * @return 启用标识
     */
    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public ReceiverType setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    /**
     * @return 租户ID, hpfm_tenant.tenant_id
     */
    public Long getTenantId() {
        return tenantId;
    }

    public ReceiverType setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

	public String getTypeMeaning() {
		return typeMeaning;
	}

	public void setTypeMeaning(String typeMeaning) {
		this.typeMeaning = typeMeaning;
	}

	public String getTypeModeCode() {
		return typeModeCode;
	}

	public void setTypeModeCode(String typeModeCode) {
		this.typeModeCode = typeModeCode;
	}

	public String getTypeModeMeaning() {
		return typeModeMeaning;
	}

	public void setTypeModeMeaning(String typeModeMeaning) {
		this.typeModeMeaning = typeModeMeaning;
	}

    public ReceiverDetail getReceiverDetail() {
        return receiverDetail;
    }

    public ReceiverType setReceiverDetail(ReceiverDetail receiverDetail) {
        this.receiverDetail = receiverDetail;
        return this;
    }
}
