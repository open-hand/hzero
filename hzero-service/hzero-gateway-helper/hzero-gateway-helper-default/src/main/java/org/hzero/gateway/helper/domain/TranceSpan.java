package org.hzero.gateway.helper.domain;

import java.time.LocalDate;

/**
 * @author superlee
 */
public class TranceSpan {
    /**
     * 服务调用KEY模板: gateway:span:{{today}}
     *
     * @see TranceSpan#today
     */
    private static final String SERVICE_INVOKE_KEY_TEMPLATE = "gateway:span:%s";
    /**
     * 服务调用VALUE模板: {{service}}
     *
     * @see TranceSpan#service
     */
    private static final String SERVICE_INVOKE_VALUE_TEMPLATE = "%s";
    /**
     * API调用KEY模板: gateway:span:{{today}}:{{service}}
     *
     * @see TranceSpan#today
     * @see TranceSpan#service
     */
    private static final String API_INVOKE_KEY_TEMPLATE = SERVICE_INVOKE_KEY_TEMPLATE + ":%s";
    /**
     * API调用VALUE模板: {{url}}:{{method}}
     *
     * @see TranceSpan#url
     * @see TranceSpan#method
     */
    private static final String API_INVOKE_VALUE_TEMPLATE = "%s:%s";

    private String url;

    private String service;

    private String method;

    private LocalDate today;

    public TranceSpan() {
    }

    public TranceSpan(String url, String service, String method, LocalDate today) {
        this.url = url;
        this.service = service;
        this.method = method;
        this.today = today;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDate getToday() {
        return today;
    }

    public void setToday(LocalDate today) {
        this.today = today;
    }

    /**
     * 获取服务访问的key(缓存key)
     *
     * @return 服务访问的key(缓存key)
     */
    public String getServiceInvokeKey() {
        return String.format(SERVICE_INVOKE_KEY_TEMPLATE, String.valueOf(this.today));
    }

    /**
     * 获取服务访问的value
     *
     * @return 服务访问的value
     */
    public String getServiceInvokeValue() {
        return String.format(SERVICE_INVOKE_VALUE_TEMPLATE, this.service);
    }

    /**
     * 获取API访问的key(缓存key)
     *
     * @return API访问的key(缓存key)
     */
    public String getApiInvokeKey() {
        return String.format(API_INVOKE_KEY_TEMPLATE, String.valueOf(this.today), this.service);
    }

    /**
     * 获取API访问的value
     *
     * @return API访问的value
     */
    public String getApiInvokeValue() {
        return String.format(API_INVOKE_VALUE_TEMPLATE, this.url, this.method);
    }

    @Override
    public String toString() {
        return "TranceSpan{" +
                "url='" + url + '\'' +
                ", service='" + service + '\'' +
                ", method='" + method + '\'' +
                ", today=" + today +
                '}';
    }
}
