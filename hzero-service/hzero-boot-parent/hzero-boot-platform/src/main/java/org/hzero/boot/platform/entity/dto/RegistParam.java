package org.hzero.boot.platform.entity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册参数类
 *
 * @author xingxing.wu@hand-china.com 2019/07/17 8:34
 */
public class RegistParam {
    private List<EntityTableDTO> entityTableInsertList = new ArrayList<>();
    private Boolean entityTableInsertFlag = false;
    private List<EntityTableDTO> entityTableUpdateList = new ArrayList<>();
    private Boolean entityTableUpdateFlag = false;
    private List<EntityTableDTO> entityTableDeleteList = new ArrayList<>();
    private Boolean entityTableDeleteFlag = false;


    private List<EntityColumnDTO> entityColumnInsertList = new ArrayList<>();
    private Boolean entityColumnInsertFlag = false;
    private List<EntityColumnDTO> entityColumnUpdateList = new ArrayList<>();
    private Boolean entityColumnUpdateFlag = false;
    private List<EntityColumnDTO> entityColumnDeleteList = new ArrayList<>();
    private Boolean entityColumnDeleteFlag = false;


    /**
     * 返回是否需要注册
     *
     * @return
     */
    public Boolean isNeedRegist() {
        return (entityTableInsertFlag || entityTableUpdateFlag || entityTableDeleteFlag || entityColumnInsertFlag ||
                entityColumnUpdateFlag || entityColumnDeleteFlag);
    }

    public void addEntityTableInsert(EntityTableDTO entityTableDTO) {
        this.entityTableInsertList.add(entityTableDTO);
    }

    public void addEntityTableUpdate(EntityTableDTO entityTableDTO) {
        this.entityTableUpdateList.add(entityTableDTO);
    }

    public void addEntityTableDelete(EntityTableDTO entityTableDTO) {
        this.entityTableDeleteList.add(entityTableDTO);
    }

    public void addEntityColumnInsert(EntityColumnDTO entityColumnDTO) {
        this.entityColumnInsertList.add(entityColumnDTO);
    }

    public void addEntityColumnUpdate(EntityColumnDTO entityColumnDTO) {
        this.entityColumnUpdateList.add(entityColumnDTO);
    }

    public void addEntityColumnDelete(EntityColumnDTO entityColumnDTO) {
        this.entityColumnDeleteList.add(entityColumnDTO);
    }


    public List<EntityTableDTO> getEntityTableInsertList() {
        return entityTableInsertList;
    }

    public void setEntityTableInsertList(List<EntityTableDTO> entityTableInsertList) {
        this.entityTableInsertList = entityTableInsertList;
    }

    public Boolean getEntityTableInsertFlag() {
        return entityTableInsertFlag;
    }

    public void setEntityTableInsertFlag(Boolean entityTableInsertFlag) {
        this.entityTableInsertFlag = entityTableInsertFlag;
    }

    public List<EntityTableDTO> getEntityTableUpdateList() {
        return entityTableUpdateList;
    }

    public void setEntityTableUpdateList(List<EntityTableDTO> entityTableUpdateList) {
        this.entityTableUpdateList = entityTableUpdateList;
    }

    public Boolean getEntityTableUpdateFlag() {
        return entityTableUpdateFlag;
    }

    public void setEntityTableUpdateFlag(Boolean entityTableUpdateFlag) {
        this.entityTableUpdateFlag = entityTableUpdateFlag;
    }

    public List<EntityTableDTO> getEntityTableDeleteList() {
        return entityTableDeleteList;
    }

    public void setEntityTableDeleteList(List<EntityTableDTO> entityTableDeleteList) {
        this.entityTableDeleteList = entityTableDeleteList;
    }

    public Boolean getEntityTableDeleteFlag() {
        return entityTableDeleteFlag;
    }

    public void setEntityTableDeleteFlag(Boolean entityTableDeleteFlag) {
        this.entityTableDeleteFlag = entityTableDeleteFlag;
    }

    public List<EntityColumnDTO> getEntityColumnInsertList() {
        return entityColumnInsertList;
    }

    public void setEntityColumnInsertList(List<EntityColumnDTO> entityColumnInsertList) {
        this.entityColumnInsertList = entityColumnInsertList;
    }

    public Boolean getEntityColumnInsertFlag() {
        return entityColumnInsertFlag;
    }

    public void setEntityColumnInsertFlag(Boolean entityColumnInsertFlag) {
        this.entityColumnInsertFlag = entityColumnInsertFlag;
    }

    public List<EntityColumnDTO> getEntityColumnUpdateList() {
        return entityColumnUpdateList;
    }

    public void setEntityColumnUpdateList(List<EntityColumnDTO> entityColumnUpdateList) {
        this.entityColumnUpdateList = entityColumnUpdateList;
    }

    public Boolean getEntityColumnUpdateFlag() {
        return entityColumnUpdateFlag;
    }

    public void setEntityColumnUpdateFlag(Boolean entityColumnUpdateFlag) {
        this.entityColumnUpdateFlag = entityColumnUpdateFlag;
    }

    public List<EntityColumnDTO> getEntityColumnDeleteList() {
        return entityColumnDeleteList;
    }

    public void setEntityColumnDeleteList(List<EntityColumnDTO> entityColumnDeleteList) {
        this.entityColumnDeleteList = entityColumnDeleteList;
    }

    public Boolean getEntityColumnDeleteFlag() {
        return entityColumnDeleteFlag;
    }

    public void setEntityColumnDeleteFlag(Boolean entityColumnDeleteFlag) {
        this.entityColumnDeleteFlag = entityColumnDeleteFlag;
    }
}
