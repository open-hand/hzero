package org.hzero.swagger.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 从消息队列拿到的服务启动下线信息对应的实体
 *
 * @author zhipeng.zuo
 * @date 2018/1/23
 */
public class RegisterInstancePayload {

    private String status;

    private String appName;

    private String version;

    private String instanceAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date createTime;

    private String apiData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInstanceAddress() {
        return instanceAddress;
    }

    public void setInstanceAddress(String instanceAddress) {
        this.instanceAddress = instanceAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getApiData() {
        return apiData;
    }

    public void setApiData(String apiData) {
        this.apiData = apiData;
    }

    @Override
    public String toString() {
        return "RegisterInstancePayload{"
                + "status='" + status + '\''
                + ", appName='" + appName + '\''
                + ", version='" + version + '\''
                + ", instanceAddress='" + instanceAddress + '\''
                + ", createTime=" + createTime
                + '}';
    }


}
