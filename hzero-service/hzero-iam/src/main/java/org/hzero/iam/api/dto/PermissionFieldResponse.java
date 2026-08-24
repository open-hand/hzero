package org.hzero.iam.api.dto;

import org.hzero.iam.domain.entity.Permission;

/**
 * @author 废柴 2020/12/1 10:35
 */
public class PermissionFieldResponse extends Permission {
    private Integer allocatedCount;

    public Integer getAllocatedCount() {
        return allocatedCount;
    }

    public PermissionFieldResponse setAllocatedCount(Integer allocatedCount) {
        this.allocatedCount = allocatedCount;
        return this;
    }
}
