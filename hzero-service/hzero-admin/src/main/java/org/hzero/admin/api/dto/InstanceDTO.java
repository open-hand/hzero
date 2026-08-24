package org.hzero.admin.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author flyleft
 * @date 2018/4/20
 */
public class InstanceDTO {

    @ApiModelProperty(value = "实例ID")
    private String instanceId;

    @ApiModelProperty(value = "所属微服务")
    private String service;
    @ApiModelProperty(value = "实例版本")
    private String version;
    @ApiModelProperty(value = "实例状态")
    private String status;
    @ApiModelProperty(value = "端口号")
    private String pod;
    @ApiModelProperty(value = "实例注册时间")
    private Date registrationTime;

    @JsonIgnore
    private String params;

    public InstanceDTO(String instanceId, String service, String version, String status, String pod, Date registrationTime) {
        this.instanceId = instanceId;
        this.service = service;
        this.version = version;
        this.status = status;
        this.pod = pod;
        this.registrationTime = registrationTime;
    }

    public InstanceDTO() {
    }

    public InstanceDTO(String instanceId, String service, String version, String status, String params, String pod, Date registrationTime) {
        this.instanceId = instanceId;
        this.service = service;
        this.version = version;
        this.status = status;
        this.params = params;
        this.pod = pod;
        this.registrationTime = registrationTime;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getPod() {
        return pod;
    }

    public void setPod(String pod) {
        this.pod = pod;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }
}