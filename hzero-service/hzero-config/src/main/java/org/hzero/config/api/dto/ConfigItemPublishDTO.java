package org.hzero.config.api.dto;

/**
 * @author XCXCXCXCX
 * @date 2020/5/11 1:36 下午
 */
public class ConfigItemPublishDTO {

    private String serviceName;
    private String label;
    private String key;
    private String value;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ConfigItemPublishDTO{" +
                "serviceName='" + serviceName + '\'' +
                ", label='" + label + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
