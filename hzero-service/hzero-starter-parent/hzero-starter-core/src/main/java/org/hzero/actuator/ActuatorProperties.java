package org.hzero.actuator;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import org.hzero.actuator.contributor.ExecutorInfoContributor;

/**
 * 核心通用配置
 *
 * @author bojiangzhou 2019/08/23
 */
@RefreshScope
@ConfigurationProperties(prefix = ActuatorProperties.PREFIX)
public class ActuatorProperties {

    public static final String PREFIX = "hzero.actuator";

    /**
     * 是否暴露线程池状态，设置为 true 将通过 {@link ExecutorInfoContributor} 展示线程池状态
     */
    private boolean showExecutorInfo = false;

    public boolean isShowExecutorInfo() {
        return showExecutorInfo;
    }

    public void setShowExecutorInfo(boolean showExecutorInfo) {
        this.showExecutorInfo = showExecutorInfo;
    }
}
