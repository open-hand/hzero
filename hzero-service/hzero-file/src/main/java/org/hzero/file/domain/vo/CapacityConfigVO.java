package org.hzero.file.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 文件容量配置VO
 *
 * @author shuangfei.zhu@hand-china.com 2018/10/23 13:57
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapacityConfigVO {

    private Long capacityConfigId;
    private Long tenantId;
    private Long totalCapacity;
    private String totalCapacityUnit;
    private String storageUnit;
    private Long storageSize;

    public Long getCapacityConfigId() {
        return capacityConfigId;
    }

    public void setCapacityConfigId(Long capacityConfigId) {
        this.capacityConfigId = capacityConfigId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Long totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public String getTotalCapacityUnit() {
        return totalCapacityUnit;
    }

    public void setTotalCapacityUnit(String totalCapacityUnit) {
        this.totalCapacityUnit = totalCapacityUnit;
    }

    public String getStorageUnit() {
        return storageUnit;
    }

    public void setStorageUnit(String storageUnit) {
        this.storageUnit = storageUnit;
    }

    public Long getStorageSize() {
        return storageSize;
    }

    public void setStorageSize(Long storageSize) {
        this.storageSize = storageSize;
    }

    @Override
    public String toString() {
        return "CapacityConfigVO{" +
                "capacityConfigId=" + capacityConfigId +
                ", tenantId=" + tenantId +
                ", totalCapacity=" + totalCapacity +
                ", totalCapacityUnit='" + totalCapacityUnit + '\'' +
                ", storageUnit='" + storageUnit + '\'' +
                ", storageSize=" + storageSize +
                '}';
    }
}
