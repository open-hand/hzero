package org.hzero.swagger.domain.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 服务路由配置
 *
 * @author xiaoyu.zhao@hand-china.com 2018-12-05 11:15:21
 */
@ApiModel("服务路由配置")
@VersionAudit
@ModifyAudit
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "hadm_service_route")
public class ServiceRoute extends AuditDomain {

    public static final ServiceRoute EMPTY = new ServiceRoute();

    @ApiModelProperty("表ID，主键")
    @Id
    @GeneratedValue
    private Long serviceRouteId;
    private Long serviceId;
    @ApiModelProperty(value = "服务编码")
    @NotEmpty
    private String serviceCode;
    @ApiModelProperty(value = "服务id，zuulRoute的标识，对应zuulRoute的id字段")
    @NotEmpty
    private String name;
    @ApiModelProperty(value = "服务路径")
    @NotEmpty
    private String path;
    @ApiModelProperty(value = "物理路径")
    private String url;
    @ApiModelProperty(value = "是否去前缀")
    private Integer stripPrefix;
    @ApiModelProperty(value = "是否自定义敏感头")
    private Integer customSensitiveHeaders;
    @ApiModelProperty(value = "敏感头部列表")
    private String sensitiveHeaders;
    @ApiModelProperty(value = "配置经过的gateway helper服务名")
    private String helperService;
    @Transient
    private String serviceName;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键
     */
    public Long getServiceRouteId() {
        return serviceRouteId;
    }

    public void setServiceRouteId(Long serviceRouteId) {
        this.serviceRouteId = serviceRouteId;
    }

    /**
     * @return 服务ID
     */
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return 服务id，zuulRoute的标识，对应zuulRoute的id字段
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return 服务路径
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return 物理路径
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return 是否去前缀
     */
    public Integer getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(Integer stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public Integer getCustomSensitiveHeaders() {
        return customSensitiveHeaders;
    }

    public void setCustomSensitiveHeaders(Integer customSensitiveHeaders) {
        this.customSensitiveHeaders = customSensitiveHeaders;
    }

    /**
     * @return 敏感头部列表
     */
    public String getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(String sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    /**
     * @return 配置经过的gateway heler服务名
     */
    public String getHelperService() {
        return helperService;
    }

    public void setHelperService(String helperService) {
        this.helperService = helperService;
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

}
