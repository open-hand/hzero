package org.hzero.plugin.platform.hr.api.dto;

import org.hzero.plugin.platform.hr.domain.entity.Unit;

public class SyncUnitDTO extends Unit {
    private String parentUnitCode;

    public String getParentUnitCode() {
        return this.parentUnitCode;
    }

    public SyncUnitDTO setParentUnitCode(String parentUnitCode) {
        this.parentUnitCode = parentUnitCode;
        return this;
    }
}
