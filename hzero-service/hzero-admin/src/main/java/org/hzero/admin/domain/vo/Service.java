package org.hzero.admin.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 10:04 上午
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Service {

    private String serviceName;

    private String version;

    private String healthUrl;

    private Map<String, String> metadata;

    private Boolean initialized;

    public Service() {
        this.initialized = Boolean.FALSE;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceNameAndVersion(String serviceNameAndVersion, String split) {
        if (StringUtils.isEmpty(serviceNameAndVersion)) {
            throw new IllegalArgumentException("serviceNameAndVersion is null!");
        }
        String[] strings = serviceNameAndVersion.split(split);
        if (strings.length > 1) {
            setServiceName(strings[0]);
            setVersion(strings[1]);
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHealthUrl() {
        return healthUrl;
    }

    public void setHealthUrl(String healthUrl) {
        this.healthUrl = healthUrl;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Boolean getInitialized() {
        return initialized;
    }

    public void setInitialized(Boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Service service = (Service) o;
        return serviceName.equals(service.serviceName) &&
                version.equals(service.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceName, version);
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceName='" + serviceName + '\'' +
                ", version='" + version + '\'' +
                ", metadata=" + metadata +
                ", initialized=" + initialized +
                '}';
    }
}
