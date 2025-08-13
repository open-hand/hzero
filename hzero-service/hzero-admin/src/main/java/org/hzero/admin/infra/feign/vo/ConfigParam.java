package org.hzero.admin.infra.feign.vo;

/**
 * 配置参数
 *
 * @author bojiangzhou 2018/12/26
 */
public class ConfigParam {

    /**
     * 服务编码
     */
    private String serviceCode;
    /**
     * 服务版本
     */
    private String serviceVersionCode;

    public String getServiceCode() {
        return serviceCode;
    }

    public ConfigParam setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    public String getServiceVersionCode() {
        return serviceVersionCode;
    }

    public ConfigParam setServiceVersionCode(String serviceVersionCode) {
        this.serviceVersionCode = serviceVersionCode;
        return this;
    }

    @Override
    public String toString() {
        return "ConfigParam{" +
                "serviceCode='" + serviceCode + '\'' +
                ", serviceVersionCode='" + serviceVersionCode + '\'' +
                '}';
    }
}
