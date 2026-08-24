package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 服务路由配置
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:53
 */
@ApiModel("服务路由配置")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_service_route")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceRoute extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_service_route";

    public static final String FIELD_SERVICE_ROUTE_ID = "serviceRouteId";
    public static final String FIELD_SERVICE_ID = "serviceId";
    public static final String FIELD_SERVICE_CODE = "serviceCode";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_PATH = "path";
    public static final String FIELD_URL = "url";
    public static final String FIELD_STRIP_PREFIX = "stripPrefix";
    public static final String FIELD_SENSITIVE_HEADERS = "sensitiveHeaders";
    public static final String FIELD_EXTEND_CONFIG_MAP = "extendConfigMap";

    public static final ServiceRoute EMPTY = new ServiceRoute();

    public ServiceRoute() {
    }

    public ServiceRoute(@NotNull String serviceCode, @NotBlank String name, @NotBlank String path) {
        this.serviceCode = serviceCode;
        this.name = name;
        this.path = path;
    }

    /**
     * 根据路由构建 Service
     */
    public HService buildService() {
        HService service = new HService();
        service.setServiceCode(this.serviceCode);
        service.setServiceName(this.serviceCode);
        return service;
    }

    public void checkExists(ServiceRouteRepository serviceRouteRepository) {

        if(serviceRouteRepository.selectCount(new ServiceRoute(null, null, this.path)) > 0) {
            throw new CommonException("hadm.warn.serviceRoute.pathExists", this.path);
        }

        if(serviceRouteRepository.selectCount(new ServiceRoute(null, this.name, null)) > 0) {
            throw new CommonException("hadm.warn.serviceRoute.nameExists", this.name);
        }

    }

    @Encrypt
    @ApiModelProperty("表ID，主键")
    @Id
    @GeneratedValue
    private Long serviceRouteId;
    @Encrypt
    @ApiModelProperty(value = "服务ID")
    private Long serviceId;
    @ApiModelProperty(value = "服务编码")
    @NotNull
    private String serviceCode;
    @ApiModelProperty(value = "服务id，gatewayRoute的标识，对应gatewayRoute的id字段")
    @NotBlank
    private String name;
    @ApiModelProperty(value = "服务路径")
    @NotBlank
    private String path;
    @ApiModelProperty(value = "物理路径")
    @Pattern(regexp = "([A-Za-z_]+)://(\\w+)(.\\w+)*(:[0-9_]+)?")
    private String url;
    @ApiModelProperty(value = "是否去前缀")
    private Integer stripPrefix;
    @ApiModelProperty(value = "敏感头部列表")
    private String sensitiveHeaders;

    @ApiModelProperty(value = "路由额外配置")
    private String extendConfigMap;

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
     * @return 服务编码
     */
    public String getServiceCode() {
        return serviceCode;
    }

    public ServiceRoute setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    /**
     * @return 服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return 服务ID
     */
    public Long getServiceId() {
        return serviceId;
    }

    public ServiceRoute setServiceId(Long serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    /**
     * @return 服务id，gatewayRoute的标识，对应gatewayRoute的id字段
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

    /**
     * @return 敏感头部列表
     */
    public String getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(String sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public String getExtendConfigMap() {
        return extendConfigMap;
    }

    public void setExtendConfigMap(String extendConfigMap) {
        this.extendConfigMap = extendConfigMap;
    }

    @Override
    public String toString() {
        return "ServiceRoute{" +
                "serviceRouteId=" + serviceRouteId +
                ", serviceId=" + serviceId +
                ", serviceCode='" + serviceCode + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", url='" + url + '\'' +
                ", stripPrefix=" + stripPrefix +
                ", sensitiveHeaders='" + sensitiveHeaders + '\'' +
                ", extendConfigMap='" + extendConfigMap + '\'' +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
