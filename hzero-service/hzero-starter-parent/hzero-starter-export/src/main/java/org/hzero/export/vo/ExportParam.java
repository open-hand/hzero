package org.hzero.export.vo;

import java.util.HashSet;
import java.util.Set;

import org.hzero.export.constant.ExportType;
import org.hzero.export.filler.SingleSheetFiller;

/**
 * 导出参数设置
 *
 * @author bojiangzhou 2018/07/26
 */
public class ExportParam {

    private String fillerType = SingleSheetFiller.FILLER_TYPE;

    private ExportType exportType;

    private Set<Long> ids;

    private Set<String> selection = new HashSet<>(8);

    private Integer singleExcelMaxSheetNum;

    private Integer singleSheetMaxRow;

    private boolean async = false;

    private String fileName;

    /**
     * @return 数据填充方式
     */
    public String getFillerType() {
        return fillerType;
    }

    public void setFillerType(String fillerType) {
        this.fillerType = fillerType;
    }

    /**
     * @return 导出类型
     */
    public ExportType getExportType() {
        return exportType;
    }

    public void setExportType(ExportType exportType) {
        this.exportType = exportType;
    }

    /**
     * @return 导出的列ID，只有当父级ID勾选的时候才会导出子集
     */
    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }


    /**
     * @return 返回子列表的字段名称，可根据该列表判断是否查询子集。该列表不为null
     */
    public Set<String> getSelection() {
        return selection;
    }

    public void setSelection(Set<String> selection) {
        this.selection = selection;
    }

    public Integer getSingleExcelMaxSheetNum() {
        return singleExcelMaxSheetNum;
    }

    public void setSingleExcelMaxSheetNum(Integer singleExcelMaxSheetNum) {
        this.singleExcelMaxSheetNum = singleExcelMaxSheetNum;
    }

    public Integer getSingleSheetMaxRow() {
        return singleSheetMaxRow;
    }

    public void setSingleSheetMaxRow(Integer singleSheetMaxRow) {
        this.singleSheetMaxRow = singleSheetMaxRow;
    }

    public boolean isAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
