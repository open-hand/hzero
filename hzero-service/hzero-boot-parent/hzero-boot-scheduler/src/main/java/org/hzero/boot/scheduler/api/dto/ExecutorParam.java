package org.hzero.boot.scheduler.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 执行器参数DTO
 * <p>
 * 执行器需要暴露的参数在这里添加
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/14 15:08
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExecutorParam {

    /**
     * 地址
     */
    private String address;
    /**
     * 执行器编码
     */
    private String executorCode;

    public String getAddress() {
        return address;
    }

    public ExecutorParam setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getExecutorCode() {
        return executorCode;
    }

    public ExecutorParam setExecutorCode(String executorCode) {
        this.executorCode = executorCode;
        return this;
    }

    @Override
    public String toString() {
        return "ExecutorParam{" +
                "address='" + address + '\'' +
                ", executorCode='" + executorCode + '\'' +
                '}';
    }
}
