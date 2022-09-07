package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * @author bo.he02@hand-china.com 2020-05-09 11:00:41
 */
@ApiModel("")
@VersionAudit
@ModifyAudit
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Table(name = "hadm_swagger")
public class Swagger extends AuditDomain {

    public static final String FIELD_ID = "id";
    public static final String FIELD_SERVICE_NAME = "serviceName";
    public static final String FIELD_SERVICE_VERSION = "serviceVersion";
    public static final String FIELD_VALUE = "value";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("")
    @Id
    @GeneratedValue
    private Long id;
    @ApiModelProperty(value = "服务名,如hap-user-service", required = true)
    @NotBlank
    private String serviceName;
    @ApiModelProperty(value = "服务版本", required = true)
    @NotBlank
    private String serviceVersion;
    @ApiModelProperty(value = "接口文档json数据", required = true)
    @NotBlank
    private String value;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 服务名, 如hap-user-service
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return 服务版本
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    /**
     * @return 接口文档json数据
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
