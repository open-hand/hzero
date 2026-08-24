package org.hzero.config.domain.vo;

import org.springframework.util.StringUtils;

/**
 * 刷新配置参数
 *
 * @author bojiangzhou 2018/12/19
 */
public class ConfigParam {

    public static ConfigParam build(String serviceCode, String serviceVersion) {
        ConfigParam param = new ConfigParam();
        param.setServiceCode(serviceCode);
        if (!StringUtils.isEmpty(serviceVersion)) {
            param.setServiceVersion(serviceVersion);
        }
        return param;
    }

    /**
     * 服务编码
     */
    private String serviceCode;
    /**
     * 服务版本
     */
    private String serviceVersion;

    public String getServiceCode() {
        return serviceCode;
    }

    public ConfigParam setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
        return this;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
}
