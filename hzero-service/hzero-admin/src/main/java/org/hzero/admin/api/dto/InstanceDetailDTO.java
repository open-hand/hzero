package org.hzero.admin.api.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.Map;

/**
 * @author superlee
 */
public class InstanceDetailDTO {

    @ApiModelProperty(value = "实例ID")
    private String instanceId;
    @ApiModelProperty(value = "实例HOST")
    private String hostName;
    @ApiModelProperty(value = "实例IP")
    private String ipAddr;
    @ApiModelProperty(value = "实例应用名")
    private String app;
    @ApiModelProperty(value = "实例端口")
    private String port;
    @ApiModelProperty(value = "实例版本")
    private String version;
    @ApiModelProperty(value = "实例注册时间")
    private Date registrationTime;
    @ApiModelProperty(value = "实例元数据列表")
    private Map<String, String> metadata;
    @ApiModelProperty(value = "配置文件")
    private YamlDTO configInfoYml;
    @ApiModelProperty(value = "环境配置")
    private YamlDTO envInfoYml;

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public YamlDTO getConfigInfoYml() {
        return configInfoYml;
    }

    public void setConfigInfoYml(YamlDTO configInfoYml) {
        this.configInfoYml = configInfoYml;
    }

    public YamlDTO getEnvInfoYml() {
        return envInfoYml;
    }

    public void setEnvInfoYml(YamlDTO envInfoYml) {
        this.envInfoYml = envInfoYml;
    }
}