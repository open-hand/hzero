package org.hzero.swagger.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.core.util.Regexs;

/**
 * 应用服务
 */
@ApiModel("应用服务")
@VersionAudit
@ModifyAudit
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "hadm_service")
public class HService extends AuditDomain {

    @Id
    @GeneratedValue
    @ApiModelProperty("服务ID")
    private Long serviceId;
    @ApiModelProperty("服务编码")
    @Length(max = 30)
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String serviceCode;
    @ApiModelProperty("服务名称")
    @Length(max = 30)
    private String serviceName;
//    @ApiModelProperty("应用来源ID")
//    private Long appSourceId;
    @ApiModelProperty("服务图标")
    private String serviceLogo;
    @JsonIgnore
    private String serviceSourceConfig;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

//    public Long getAppSourceId() {
//        return appSourceId;
//    }
//
//    public void setAppSourceId(Long appSourceId) {
//        this.appSourceId = appSourceId;
//    }

    public String getServiceLogo() {
        return serviceLogo;
    }

    public void setServiceLogo(String serviceLogo) {
        this.serviceLogo = serviceLogo;
    }

    public String getServiceSourceConfig() {
        return serviceSourceConfig;
    }

    public void setServiceSourceConfig(String serviceSourceConfig) {
        this.serviceSourceConfig = serviceSourceConfig;
    }
}
