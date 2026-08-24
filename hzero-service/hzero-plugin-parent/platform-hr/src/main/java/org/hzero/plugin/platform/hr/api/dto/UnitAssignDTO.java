package org.hzero.plugin.platform.hr.api.dto;

import java.util.List;

import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.plugin.platform.hr.domain.entity.Unit;
import org.hzero.starter.keyencrypt.core.Encrypt;

public class UnitAssignDTO {

    @Encrypt
    private Long positionId;
    private String positionName;
    @Encrypt
    private Long unitId;
    private String UnitName;
    private String levelPath;
    private Integer primaryPositionFlag;
    private List<Unit> units;
    private List<Position> positionList;

    private String unitCode;
    private String positionCode;

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return UnitName;
    }

    public void setUnitName(String unitName) {
        UnitName = unitName;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public Integer getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public void setPrimaryPositionFlag(Integer primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public void setLevelPath(String levelPath) {
        this.levelPath = levelPath;
    }

    public List<Position> getPositionList() {
        return positionList;
    }

    public UnitAssignDTO setPositionList(List<Position> positionList) {
        this.positionList = positionList;
        return this;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public UnitAssignDTO setUnitCode(String unitCode) {
        this.unitCode = unitCode;
        return this;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public UnitAssignDTO setPositionCode(String positionCode) {
        this.positionCode = positionCode;
        return this;
    }
}
