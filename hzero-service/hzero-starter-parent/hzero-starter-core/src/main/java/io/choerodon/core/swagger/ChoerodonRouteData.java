package io.choerodon.core.swagger;

/**
 * @author wuguokai
 */
public class ChoerodonRouteData {

    private Long id;

    private String name;

    private String path;

    private String serviceId;

    private String url;

    private Boolean stripPrefix = true;

    /**
     * 去除前缀段数，stripPrefix=true 时默认为 1
     */
    private int stripSegment = 1;

    private Boolean retryable;

    private String sensitiveHeaders;

    private Boolean customSensitiveHeaders = true;

    private String helperService;

    private Boolean builtIn;

    /**
     * 必须指定，表示属于该服务的包路径
     */
    private String packages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(Boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
        // 兼容处理
        if (stripPrefix) {
            this.stripSegment = 1;
        } else {
            this.stripSegment = 0;
        }
    }

    public int getStripSegment() {
        return stripSegment;
    }

    public void setStripSegment(int stripSegment) {
        this.stripSegment = stripSegment;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public String getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(String sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public Boolean getCustomSensitiveHeaders() {
        return customSensitiveHeaders;
    }

    public void setCustomSensitiveHeaders(Boolean customSensitiveHeaders) {
        this.customSensitiveHeaders = customSensitiveHeaders;
    }

    public String getHelperService() {
        return helperService;
    }

    public void setHelperService(String helperService) {
        this.helperService = helperService;
    }

    public Boolean getBuiltIn() {
        return builtIn;
    }

    public void setBuiltIn(Boolean builtIn) {
        this.builtIn = builtIn;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    @Override
    public String toString() {
        return "ChoerodonRouteData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", url='" + url + '\'' +
                ", stripPrefix=" + stripPrefix +
                ", stripSegment=" + stripSegment +
                ", retryable=" + retryable +
                ", sensitiveHeaders='" + sensitiveHeaders + '\'' +
                ", customSensitiveHeaders=" + customSensitiveHeaders +
                ", helperService='" + helperService + '\'' +
                ", builtIn=" + builtIn +
                ", packages='" + packages + '\'' +
                '}';
    }
}
