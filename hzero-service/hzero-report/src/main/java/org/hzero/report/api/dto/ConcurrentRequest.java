package org.hzero.report.api.dto;

import java.util.Date;

/**
 * 并发请求
 *
 * @author shuangfei.zhu@hand-china.com 2019/03/04 15:57
 */
public class ConcurrentRequest {

    public static final String FIELD_CONCURRENT_ID = "concurrentId";
    public static final String FIELD_CONC_CODE = "concCode";
    public static final String FIELD_CYCLE_FLAG = "cycleFlag";
    public static final String FIELD_INTERVAL_TYPE = "intervalType";
    public static final String FIELD_INTERVAL_NUMBER = "intervalNumber";
    public static final String FIELD_INTERVAL_HOUR = "intervalHour";
    public static final String FIELD_INTERVAL_MINUTE = "intervalMinute";
    public static final String FIELD_INTERVAL_SECOND = "intervalSecond";
    public static final String FIELD_REQUEST_PARAM = "requestParam";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";
    public static final String FIELD_TENANT_ID = "tenantId";

    private Long concurrentId;
    private String concCode;
    private Integer cycleFlag;
    private String intervalType;
    private Long intervalNumber;
    private Long intervalHour;
    private Long intervalMinute;
    private Long intervalSecond;
    private String requestParam;
    private Date startDate;
    private Date endDate;
    private Long tenantId;

    public Long getConcurrentId() {
        return concurrentId;
    }

    public void setConcurrentId(Long concurrentId) {
        this.concurrentId = concurrentId;
    }

    public String getConcCode() {
        return concCode;
    }

    public void setConcCode(String concCode) {
        this.concCode = concCode;
    }

    public Integer getCycleFlag() {
        return cycleFlag;
    }

    public void setCycleFlag(Integer cycleFlag) {
        this.cycleFlag = cycleFlag;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

    public Long getIntervalNumber() {
        return intervalNumber;
    }

    public void setIntervalNumber(Long intervalNumber) {
        this.intervalNumber = intervalNumber;
    }

    public Long getIntervalHour() {
        return intervalHour;
    }

    public void setIntervalHour(Long intervalHour) {
        this.intervalHour = intervalHour;
    }

    public Long getIntervalMinute() {
        return intervalMinute;
    }

    public void setIntervalMinute(Long intervalMinute) {
        this.intervalMinute = intervalMinute;
    }

    public Long getIntervalSecond() {
        return intervalSecond;
    }

    public void setIntervalSecond(Long intervalSecond) {
        this.intervalSecond = intervalSecond;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "ConcurrentRequest{" +
                "concurrentId=" + concurrentId +
                ", concCode='" + concCode + '\'' +
                ", cycleFlag=" + cycleFlag +
                ", intervalType='" + intervalType + '\'' +
                ", intervalNumber=" + intervalNumber +
                ", intervalHour=" + intervalHour +
                ", intervalMinute=" + intervalMinute +
                ", intervalSecond=" + intervalSecond +
                ", requestParam='" + requestParam + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", tenantId=" + tenantId +
                '}';
    }
}
