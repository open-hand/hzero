package org.hzero.file.infra.config;

import java.util.List;

import org.hzero.file.infra.constant.HfleConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 最大文件存储容量
 *
 * @author shuangfei.zhu@hand-china.com 2018/09/26 17:33
 */
@ConfigurationProperties(prefix = "hzero.file")
@Component
public class FileConfig {

    /**
     * 文件预览策略
     */
    private String previewType = HfleConstant.PreviewType.ON;

    private String kkFileViewUrl;

    private String gatewayPath = "";

    /**
     * Aspose 字体补充  文件夹路径或文件路径
     */
    private List<String> asposeFonts;
    /**
     * 存储配置二级缓存
     */
    private StoreCache storeCache = new StoreCache();

    public String getPreviewType() {
        return previewType;
    }

    public FileConfig setPreviewType(String previewType) {
        this.previewType = previewType;
        return this;
    }

    public String getKkFileViewUrl() {
        return kkFileViewUrl;
    }

    public FileConfig setKkFileViewUrl(String kkFileViewUrl) {
        this.kkFileViewUrl = kkFileViewUrl;
        return this;
    }

    public List<String> getAsposeFonts() {
        return asposeFonts;
    }

    public FileConfig setAsposeFonts(List<String> asposeFonts) {
        this.asposeFonts = asposeFonts;
        return this;
    }

    public String getGatewayPath() {
        return gatewayPath;
    }

    public FileConfig setGatewayPath(String gatewayPath) {
        this.gatewayPath = gatewayPath;
        return this;
    }

    public StoreCache getStoreCache() {
        return storeCache;
    }

    public FileConfig setStoreCache(StoreCache storeCache) {
        this.storeCache = storeCache;
        return this;
    }

    public static class StoreCache {

        /**
         * 存储配置二级缓存有效时长
         */
        private Integer time = 3600;
        /**
         * 启用存储配置二级缓存
         */
        private boolean enable = false;

        public Integer getTime() {
            return time;
        }

        public StoreCache setTime(Integer time) {
            this.time = time;
            return this;
        }

        public boolean isEnable() {
            return enable;
        }

        public StoreCache setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }
    }
}
