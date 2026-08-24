package org.hzero.platform.infra.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hzero.data-hierarchy.display")
public class DataHierarchyProperties {
    private int maxSelectCount = 3;

    public int getMaxSelectCount() {
        return maxSelectCount;
    }

    public void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }
}
