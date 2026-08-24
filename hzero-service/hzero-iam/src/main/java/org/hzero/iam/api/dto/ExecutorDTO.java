package org.hzero.iam.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 调度器DTO
 *
 * @author yuqing.zhang@hand-china.com 2020/05/15 9:55
 */
public class ExecutorDTO {
    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long executorId;
    @ApiModelProperty("执行器编码")
    private String executorCode;
    @ApiModelProperty("执行器名称")
    private String executorName;
    @ApiModelProperty("排序")
    private Integer orderSeq;
    @ApiModelProperty("执行器地址类型：0=自动注册、1=手动录入")
    private Integer executorType;
    @ApiModelProperty("执行器地址列表，多地址逗号分隔")
    private String addressList;
    @ApiModelProperty("执行器状态")
    private String status;
    @ApiModelProperty("租户ID,hpfm_tenant.tenant_id")
    private Long tenantId;
    @ApiModelProperty("服务名称")
    private String serverName;
    private String statusMeaning;
    private String tenantName;

    public Long getExecutorId() {
        return executorId;
    }

    public void setExecutorId(Long executorId) {
        this.executorId = executorId;
    }

    public String getExecutorCode() {
        return executorCode;
    }

    public void setExecutorCode(String executorCode) {
        this.executorCode = executorCode;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public Integer getExecutorType() {
        return executorType;
    }

    public void setExecutorType(Integer executorType) {
        this.executorType = executorType;
    }

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public void setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Override
    public String toString() {
        return "ExecutorDTO{" +
                "executorId=" + executorId +
                ", executorCode='" + executorCode + '\'' +
                ", executorName='" + executorName + '\'' +
                ", orderSeq=" + orderSeq +
                ", executorType=" + executorType +
                ", addressList='" + addressList + '\'' +
                ", status='" + status + '\'' +
                ", tenantId=" + tenantId +
                ", serverName='" + serverName + '\'' +
                ", statusMeaning='" + statusMeaning + '\'' +
                ", tenantName='" + tenantName + '\'' +
                '}';
    }
}
