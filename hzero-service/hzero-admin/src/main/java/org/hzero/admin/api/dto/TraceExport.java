package org.hzero.admin.api.dto;

import org.hzero.export.annotation.ExcelColumn;
import org.hzero.export.annotation.ExcelSheet;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2020/2/28 1:28 下午
 */
@ExcelSheet(zh = "日志追溯分析报告", en = "Trace log analysis report")
public class TraceExport {

//    @ExcelColumn(zh = "时间戳", en = "Timestamp")
    private Long timestamp;
    @ExcelColumn(zh = "记录时间", en = "Log Date")
    private String logDate;
    @ExcelColumn(zh = "耗时", en = "Cost")
    private String cost;
    @ExcelColumn(zh = "响应码", en = "Result Code")
    private String resultCode;
    @ExcelColumn(zh = "调用ID", en = "Trace ID")
    private String traceId;
    @ExcelColumn(zh = "子调用ID", en = "Span ID")
    private String spanId;
    @ExcelColumn(zh = "应用名", en = "App Name")
    private String appName;
    @ExcelColumn(zh = "线程名", en = "Thread Name")
    private String threadName;
    @ExcelColumn(zh = "调用类型", en = "Trace Type")
    private String traceType;
    @ExcelColumn(zh = "额外业务信息", en = "Business Addition Info")
    private String businessAdditionInfo;
    @ExcelColumn(zh = "额外系统信息", en = "System Addition Info")
    private String systemAdditionInfo;

    /**
     * http
     */
    @ExcelColumn(zh = "请求url", en = "Request Url")
    private String requestUrl;
    @ExcelColumn(zh = "请求类型", en = "Method")
    private String method;
    @ExcelColumn(zh = "请求字节数", en = "Request Size Bytes")
    private Integer requestSizeBytes;
    @ExcelColumn(zh = "响应字节数", en = "Response Size Bytes")
    private Integer responseSizeBytes;

    /**
     * feign
     */
    @ExcelColumn(zh = "远程主机", en = "Remote Host")
    private String remoteHost;
    @ExcelColumn(zh = "远程端口", en = "Remote Port")
    private String remotePort;

    /**
     * datasource
     */
    @ExcelColumn(zh = "数据库类型", en = "Database Type")
    private String dbType;
    @ExcelColumn(zh = "数据库地址", en = "Database Endpoint")
    private String dbEndpoint;
    @ExcelColumn(zh = "数据库名", en = "Database Name")
    private String dbName;
    @ExcelColumn(zh = "sql", en = "Sql")
    private String sql;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getTraceType() {
        return traceType;
    }

    public void setTraceType(String traceType) {
        this.traceType = traceType;
    }

    public String getBusinessAdditionInfo() {
        return businessAdditionInfo;
    }

    public void setBusinessAdditionInfo(String businessAdditionInfo) {
        this.businessAdditionInfo = businessAdditionInfo;
    }

    public String getSystemAdditionInfo() {
        return systemAdditionInfo;
    }

    public void setSystemAdditionInfo(String systemAdditionInfo) {
        this.systemAdditionInfo = systemAdditionInfo;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getRequestSizeBytes() {
        return requestSizeBytes;
    }

    public void setRequestSizeBytes(Integer requestSizeBytes) {
        this.requestSizeBytes = requestSizeBytes;
    }

    public Integer getResponseSizeBytes() {
        return responseSizeBytes;
    }

    public void setResponseSizeBytes(Integer responseSizeBytes) {
        this.responseSizeBytes = responseSizeBytes;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public String getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(String remotePort) {
        this.remotePort = remotePort;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbEndpoint() {
        return dbEndpoint;
    }

    public void setDbEndpoint(String dbEndpoint) {
        this.dbEndpoint = dbEndpoint;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
