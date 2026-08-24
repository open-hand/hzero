package org.hzero.core.modular;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * 服务合并属性
 * @author XCXCXCXCX
 * @date 2019/8/15
 */
@ConfigurationProperties(prefix = ModularProperties.PREFIX)
public class ModularProperties {

    public static final String PREFIX = "hzero.modular";

    private static final Set<String> DEFAULT_SKIP_CLASSES = new HashSet<>();

    static {
        DEFAULT_SKIP_CLASSES.add("io.choerodon.swagger.controller.CustomController");
        DEFAULT_SKIP_CLASSES.add("io.choerodon.core.config.RefreshConfigEndpoint");
        DEFAULT_SKIP_CLASSES.add("org.hzero.boot.scheduler.api.controller.v1.JobExecuteController");
    }

    /**
     * 默认关闭服务合并
     */
    private boolean enable = false;

    private Set<String> skipClasses = new HashSet<>();

    private Set<String> skipPrefixes = new HashSet<>();

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Set<String> getSkipClasses() {
        HashSet merge = new HashSet(skipClasses);
        merge.addAll(DEFAULT_SKIP_CLASSES);
        return merge;
    }

    public void setSkipClasses(Set<String> skipClasses) {
        this.skipClasses = skipClasses;
    }

    public Set<String> getSkipPrefixes() {
        return skipPrefixes;
    }

    public void setSkipPrefixes(Set<String> skipPrefixes) {
        this.skipPrefixes = skipPrefixes;
    }
}
