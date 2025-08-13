package org.hzero.config.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.IOException;
import java.util.HashMap;
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
    public static final String FIELD_SERVICE_CONFIG_ID = "serviceConfigId";
    public static final String FIELD_SERVICE_ID = "serviceId";
    public static final String FIELD_CONFIG_YAML = "configYaml";
    public static final String FIELD_CONFIG_VERSION = "configVersion";
    public static final String FIELD_CONFIG_VALUE = "configValue";
    public static final String FIELD_OBJECT_VERSION_NUMBER = "objectVersionNumber";
    public static final String FIELD_SERVICE_CODE = "serviceCode";

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfig.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    @Id
    @GeneratedValue
    private Long serviceConfigId;
    @ApiModelProperty(value = "配置版本")
    private String configVersion;
    @ApiModelProperty(value = "yaml配置")
    private String configYaml;
    @ApiModelProperty(value = "json配置")
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
