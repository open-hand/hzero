package org.hzero.admin.api.dto.condition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务实例查询条件对象
 *
 * @author bo.he02@hand-china.com 2020/05/12 14:25
 */
@ApiModel(description = "服务实例查询条件对象")
public class InstanceQueryDTO {
    @ApiModelProperty(value = "服务")
    private String service;

    @ApiModelProperty(value = "实例ID")
    private String instanceId;

    @ApiModelProperty(value = "版本")
    private String version;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "其他参数")
    private String params;

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    /**
     * 转换成map
     *
     * @return 转换成功的map
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(8);
        map.put("service", this.service);
        map.put("instanceId", this.instanceId);
        map.put("version", this.version);
        map.put("status", this.status);
        map.put("params", this.params);

        return map;
    }

    @Override
    public String toString() {
        return "InstanceQueryDTO{" +
                "service='" + this.service + '\'' +
                ", instanceId='" + this.instanceId + '\'' +
                ", version='" + this.version + '\'' +
                ", status='" + this.status + '\'' +
                ", params='" + this.params + '\'' +
                '}';
    }
}
