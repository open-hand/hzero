package org.hzero.admin.domain.vo;

/**
 * 刷新配置参数
 *
 * @author bojiangzhou 2018/12/19
 */
public class ConfigParam {

    public static final String FIELD_SERVICE_CODE = "serviceCode";
    public static final String FIELD_SERVICE_VERSION_CODE = "serviceVersionCode";

    public static ConfigParam build(String serviceCode) {
        ConfigParam param = new ConfigParam();
        param
                .setServiceCode(serviceCode);
        return param;
    }

    public static ConfigParam build(String serviceCode, String serviceVersionCode) {
        ConfigParam param = new ConfigParam();
        param
                .setServiceCode(serviceCode)
                .setServiceVersionCode(serviceVersionCode);
        return param;
    }

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

}
