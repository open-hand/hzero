package org.hzero.admin.api.dto;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @version 1.2.0
 * @date 2019/12/23 3:55 下午
 */
public class TraceReport {

    private String traceGroupId;

    private List<Trace> traceList = new ArrayList<>();

    private List<HttpTrace> httpTraceList = new ArrayList<>();
    private List<FeignTrace> feignTraceList = new ArrayList<>();
    private List<DataSourceTrace> dataSourceTraceList = new ArrayList<>();

    public TraceReport(String traceGroupId) {
        this.traceGroupId = traceGroupId;
    }

    public void addTrace(Trace trace){
        traceList.add(trace);
        if (trace instanceof HttpTrace) {
            addHttpTrace((HttpTrace) trace);
        }
        if (trace instanceof FeignTrace) {
            addFeignTrace((FeignTrace) trace);
        }
        if (trace instanceof DataSourceTrace) {
            addDataSourceTrace((DataSourceTrace) trace);
        }
    }

    private void addHttpTrace(HttpTrace trace){
        httpTraceList.add(trace);
    }

    private void addFeignTrace(FeignTrace trace){
        feignTraceList.add(trace);
    }

    private void addDataSourceTrace(DataSourceTrace trace){
        dataSourceTraceList.add(trace);
    }

    public String getTraceGroupId() {
        return traceGroupId;
    }

    public void setTraceGroupId(String traceGroupId) {
        this.traceGroupId = traceGroupId;
    }

    public List<HttpTrace> getHttpTraceList() {
        return httpTraceList;
    }

    public void setHttpTraceList(List<HttpTrace> httpTraceList) {
        this.httpTraceList = httpTraceList;
    }

    public List<FeignTrace> getFeignTraceList() {
        return feignTraceList;
    }

    public void setFeignTraceList(List<FeignTrace> feignTraceList) {
        this.feignTraceList = feignTraceList;
    }

    public List<DataSourceTrace> getDataSourceTraceList() {
        return dataSourceTraceList;
    }

    public void setDataSourceTraceList(List<DataSourceTrace> dataSourceTraceList) {
        this.dataSourceTraceList = dataSourceTraceList;
    }

    public List<Trace> getTraceList() {
        return traceList;
    }

    public void setTraceList(List<Trace> traceList) {
        this.traceList = traceList;
    }

    public static class HttpTrace extends Trace {
        private String requestUrl;
        private String method;
        private Integer requestSizeBytes;
        private Integer responseSizeBytes;
        public HttpTrace() {
            setTraceType("HTTP");
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
    }

    public static class FeignTrace extends HttpTrace {
        private String remoteHost;
        private String remotePort;

        public FeignTrace() {
            setTraceType("FEIGN");
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
    }

    public static class DataSourceTrace extends Trace {
        private String dbType;
        private String dbEndpoint;
        private String dbName;
        private String sql;

        public DataSourceTrace() {
            setTraceType("SQL");
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

    public static class Trace implements Traceable, Addable {

        private Long timestamp;
        private String logDate;
        private String cost;
        private String resultCode;
        private String traceGroupId;
        private String traceId;
        private String spanId;
        private String appName;
        private String threadName;
        /**
         * HTTP/SQL/FEIGN
         */
        private String traceType;

        private String businessAdditionInfo;
        private String systemAdditionInfo;

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
            this.logDate = DateFormatUtils.format(timestamp, "yyyy-MM-dd HH:mm:ss");
        }

        public String getLogDate() {
            return logDate;
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

        @Override
        public String getTraceGroupId() {
            return traceGroupId;
        }

        public void setTraceGroupId(String traceGroupId) {
            this.traceGroupId = traceGroupId;
        }

        @Override
        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        @Override
        public String getSpanId() {
            return spanId;
        }

        public void setSpanId(String spanId) {
            this.spanId = spanId;
        }

        @Override
        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        @Override
        public String getThreadName() {
            return threadName;
        }

        public void setThreadName(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public String getTraceType() {
            return traceType;
        }

        public void setTraceType(String traceType) {
            this.traceType = traceType;
        }

        @Override
        public String getBusinessAdditionInfo() {
            return businessAdditionInfo;
        }

        public void setBusinessAdditionInfo(String businessAdditionInfo) {
            this.businessAdditionInfo = businessAdditionInfo;
        }

        @Override
        public String getSystemAdditionInfo() {
            return systemAdditionInfo;
        }

        public void setSystemAdditionInfo(String systemAdditionInfo) {
            this.systemAdditionInfo = systemAdditionInfo;
        }
    }

    interface Traceable {
        String getTraceGroupId();
        String getTraceId();
        String getSpanId();
        String getAppName();
        String getThreadName();
        String getTraceType();
    }

    interface Addable {
        String getBusinessAdditionInfo();
        String getSystemAdditionInfo();
    }

}
