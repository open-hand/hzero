package org.hzero.platform.api.dto;

import org.hzero.core.base.BaseConstants;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class DataHierarchyDisplayStyleDTO {
    private List<DataHierarchyDTO> selectList;
    private List<DataHierarchyDTO> modalList;

    public List<DataHierarchyDTO> getSelectList() {
        return selectList;
    }

    public DataHierarchyDisplayStyleDTO setSelectList(List<DataHierarchyDTO> selectList) {
        this.selectList = selectList;
        return this;
    }

    public List<DataHierarchyDTO> getModalList() {
        return modalList;
    }

    public DataHierarchyDisplayStyleDTO setModalList(List<DataHierarchyDTO> modalList) {
        this.modalList = modalList;
        return this;
    }

    public int getHasSelect() {
        return CollectionUtils.isEmpty(this.selectList) ? BaseConstants.Flag.NO : BaseConstants.Flag.YES;
    }

    public int getHasModal() {
        return CollectionUtils.isEmpty(this.modalList) ? BaseConstants.Flag.NO : BaseConstants.Flag.YES;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataHierarchyDisplayStyleDTO that = (DataHierarchyDisplayStyleDTO) o;
        return Objects.equals(selectList, that.selectList) &&
                Objects.equals(modalList, that.modalList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selectList, modalList);
    }

    @Override
    public String toString() {
        return "DataHierarchyDisplayStyleDTO{" +
                "selectList=" + selectList +
                ", modalList=" + modalList +
                '}';
    }
}
