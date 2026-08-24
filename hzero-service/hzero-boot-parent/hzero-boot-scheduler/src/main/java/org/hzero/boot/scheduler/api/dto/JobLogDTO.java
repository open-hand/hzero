package org.hzero.boot.scheduler.api.dto;

import java.util.Date;

import org.hzero.boot.scheduler.infra.constant.BootSchedulerConstant;

/**
 * 任务日志传输对象
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/23 16:17
 */
public class JobLogDTO {

    private Long logId;
    private Long jobId;
    private String clientResult;
    private String messageHeader;
    private String message;
    private Date endTime;
    private String logUrl;
    private String logMessage;
    private String outputFile;

    public Long getLogId() {
        return logId;
    }

    public JobLogDTO setLogId(Long logId) {
        this.logId = logId;
        return this;
    }

    public Long getJobId() {
        return jobId;
    }

    public JobLogDTO setJobId(Long jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getClientResult() {
        return clientResult;
    }

    public JobLogDTO setClientResult(String clientResult) {
        this.clientResult = clientResult;
        return this;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public JobLogDTO setMessageHeader(String messageHeader) {
        if (messageHeader == null){
            this.messageHeader = null;
            return this;
        }
        if (messageHeader.length() > BootSchedulerConstant.HEADER_MAX_LOG) {
            this.messageHeader = messageHeader.substring(0, BootSchedulerConstant.HEADER_MAX_LOG) + "...";
        } else {
            this.messageHeader = messageHeader;
        }
        return this;
    }

    public String getMessage() {
        return message;
    }

    public JobLogDTO setMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public JobLogDTO setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getLogUrl() {
        return logUrl;
    }

    public JobLogDTO setLogUrl(String logUrl) {
        this.logUrl = logUrl;
        return this;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public JobLogDTO setLogMessage(String logMessage) {
        if (logMessage == null){
            this.logMessage = null;
            return this;
        }
        if (logMessage.length() > BootSchedulerConstant.MAX_LOG) {
            this.logMessage = logMessage.substring(0, BootSchedulerConstant.MAX_LOG) + "...";
        } else {
            this.logMessage = logMessage;
        }
        return this;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public JobLogDTO setOutputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    @Override
    public String toString() {
        return "JobLogDTO{" +
                "logId=" + logId +
                ", jobId=" + jobId +
                ", clientResult='" + clientResult + '\'' +
                ", messageHeader='" + messageHeader + '\'' +
                ", message='" + message + '\'' +
                ", endTime=" + endTime +
                ", logUrl='" + logUrl + '\'' +
                ", logMessage='" + logMessage + '\'' +
                ", outputFile='" + outputFile + '\'' +
                '}';
    }
}
