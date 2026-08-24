package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.domain.repository.ServiceConfigRepository;
import org.hzero.admin.domain.repository.ServiceRepository;
import org.hzero.admin.infra.constant.ConfigConstant;
import org.hzero.admin.infra.util.config.ConfigUtil;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务配置
 *
 * @author zhiying.dong@hand-china.com 2018-12-07 14:45:51
 */
@ApiModel("服务配置")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_service_config")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceConfig extends AuditDomain {

    public static final String ENCRYPT_KEY = "hadm_service_config";

    public static final String FIELD_SERVICE_CONFIG_ID = "serviceConfigId";
    public static final String FIELD_SERVICE_ID = "serviceId";
    public static final String FIELD_CONFIG_YAML = "configYaml";
    public static final String FIELD_CONFIG_VERSION = "configVersion";
    public static final String FIELD_CONFIG_VALUE = "configValue";
    public static final String FIELD_OBJECT_VERSION_NUMBER = "objectVersionNumber";
    public static final String FIELD_SERVICE_CODE = "serviceCode";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfig.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 校验、初始化
     */
    public void validateAndInit(ServiceRepository serviceRepository, ServiceConfigRepository configRepository) {
        // service
        // selectByPrimary() join muti-language table that cause [service not found]
        List<HService> services = serviceRepository.selectByIds(String.valueOf(this.serviceId));
        Assert.notEmpty(services, String.format("service with id=%s not found.", this.serviceId));
        this.serviceCode = services.get(0).getServiceCode();

        if (this.serviceConfigId == null) {
            // config
            if (configRepository.selectConfigCount(this.serviceCode, this.configVersion) > 0) {
                throw new CommonException("service.config.exists");
            }
        }
    }

    public Map<String, Object> jsonToMap() {
        if (StringUtils.isNotEmpty(this.configValue)) {
            try {
                return MAPPER.readValue(this.configValue, Map.class);
            } catch (IOException e) {
                LOGGER.warn("deserialize json error.");
            }
        }
        return new HashMap<>();
    }

    public void mapToJson(Map<String, Object> value) {
        try {
            this.configValue = MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            LOGGER.warn("deserialize json error.");
        }
    }

    public void yamlToJson() {
        try {
            Map<String, Object> value = ConfigUtil.convertTextToMap(ConfigConstant.CONFIG_TYPE_YAML, this.configYaml);
            this.configValue = MAPPER.writeValueAsString(removeGatewayRoute(value));
        } catch (IOException e) {
            LOGGER.warn("parse yaml to json error.");
            throw new CommonException("hadm.error.parse_yaml_failed");
        }
    }

    public void mapToYaml(Map<String,Object> map) {
        this.configYaml = ConfigUtil.convertMapToText(map, ConfigConstant.CONFIG_TYPE_YAML);
    }

    private Map<String, Object> removeGatewayRoute(final Map<String, Object> value) {
        Map<String, Object> newValue = new HashMap<>(value.size());
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            if (!entry.getKey().startsWith("spring.cloud.gateway.routes")) {
                newValue.put(entry.getKey(), entry.getValue());
            }
        }
        return newValue;
    }

    @Encrypt
    @Id
    @GeneratedValue
    private Long serviceConfigId;
    @Encrypt
    @ApiModelProperty(value = "服务ID")
    @NotNull
    private Long serviceId;
    @ApiModelProperty(value = "配置版本")
    @Size(max = 60)
    private String configVersion;
    @ApiModelProperty(value = "yaml配置")
    @NotNull
    private String configYaml;
    @ApiModelProperty(value = "json配置")
    @JsonIgnore
    private String configValue;
    @ApiModelProperty(value = "服务编码")
    private String serviceCode;

    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 表ID，主键
     */
    public Long getServiceConfigId() {
        return serviceConfigId;
    }

    public void setServiceConfigId(Long serviceConfigId) {
        this.serviceConfigId = serviceConfigId;
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
     * @return 服务编码
     */
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    /**
     * @return yaml配置
     */
    public String getConfigYaml() {
        return configYaml;
    }

    public void setConfigYaml(String configYaml) {
        this.configYaml = configYaml;
    }

    /**
     * @return json配置
     */
    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
