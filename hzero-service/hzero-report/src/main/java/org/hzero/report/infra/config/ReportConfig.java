package org.hzero.report.infra.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 报表配置
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/10 9:09
 */
@Component
@ConfigurationProperties(prefix = "hzero.report")
public class ReportConfig {

    /**
     * 最大限制行数
     */
    private int maxRows = 100000;

    /**
     * 字体文件路径
     */
    private List<String> fontPaths;

    /**
     * resource字体文件路径
     */
    private List<String> srcFontPaths;

    /**
     * 邮件中的报表文件有效时长，默认1天
     */
    private Long emailFileExpires = 86400L;

    /**
     * 二维码生成地址
     */
    private String qrCodeUrl;

    /**
     * 报表请求参数
     */
    private Request request = new Request();

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public List<String> getFontPaths() {
        return fontPaths;
    }

    public void setFontPaths(List<String> fontPaths) {
        this.fontPaths = fontPaths;
    }

    public List<String> getSrcFontPaths() {
        return srcFontPaths;
    }

    public ReportConfig setSrcFontPaths(List<String> srcFontPaths) {
        this.srcFontPaths = srcFontPaths;
        return this;
    }

    public Long getEmailFileExpires() {
        return emailFileExpires;
    }

    public ReportConfig setEmailFileExpires(Long emailFileExpires) {
        this.emailFileExpires = emailFileExpires;
        return this;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public ReportConfig setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public ReportConfig setRequest(Request request) {
        this.request = request;
        return this;
    }

    public static class Request {

        /**
         * 分页大小
         */
        private Integer pageSize = 1000;
        /**
         * SXSS写入内存大小
         */
        private Integer cacheSize = 100;
        /**
         * 间隔时间
         */
        private Integer interval = 30;
        /**
         * 最大等待时间
         */
        private Integer maxWaitTime = 86400;

        public Integer getPageSize() {
            return pageSize;
        }

        public Request setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Integer getCacheSize() {
            return cacheSize;
        }

        public Request setCacheSize(Integer cacheSize) {
            this.cacheSize = cacheSize;
            return this;
        }

        public Integer getInterval() {
            return interval;
        }

        public Request setInterval(Integer interval) {
            this.interval = interval;
            return this;
        }

        public Integer getMaxWaitTime() {
            return maxWaitTime;
        }

        public Request setMaxWaitTime(Integer maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
            return this;
        }
    }
}
