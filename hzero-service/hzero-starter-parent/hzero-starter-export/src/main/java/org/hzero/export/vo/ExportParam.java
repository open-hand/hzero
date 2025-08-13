package org.hzero.export.vo;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.hzero.export.constant.CodeRender;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.constant.ExportType;
import org.hzero.export.constant.FileType;

/**
 * 导出参数设置
 *
 * @author bojiangzhou 2018/07/26
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportParam {

    /**
     * 导出类型，默认为头行打平导出
     */
    private String fillerType = ExportConstants.FillerType.SINGLE;
    /**
     * 导出类型
     */
    private ExportType exportType;
    /**
     * 导出格式，默认值为EXCEL2007
     */
    private String fileType = FileType.EXCEL_2007.getName();
    /**
     * 指定导出的列ID
     */
    private Set<Long> ids;
    /**
     * 指定的导出列，包含字段名、字段描述、排序等
     */
    private Set<ExportColumn> columns;
    /**
     * 单excel最大sheet页数量
     */
    private Integer singleExcelMaxSheetNum;
    /**
     * 单sheet最大行数
     */
    private Integer singleSheetMaxRow;
    /**
     * 异步导出
     */
    private boolean async = false;
    /**
     * 导出自定义文件名
     */
    private String fileName;
    /**
     * 当前导出允许的最大数据量
     */
    private Long maxDataCount;
    /**
     * 当前被选中导出的字段，导出组件不会使用该字段，开发可能会用
     */
    private Set<String> selection = new HashSet<>(8);
    /**
     * 编码渲染方式
     */
    private CodeRender codeRenderMode;
    /**
     * 导出模板编码
     */
    private String exportTemplateCode;
    /**
     * 导出模板类型
     */
    private String exportTemplateType;
    /**
     * 甄云有需求将自定义参数和导出参数都放到body里
     */
    private String customData;

    public String getFillerType() {
        return fillerType;
    }

    public ExportParam setFillerType(String fillerType) {
        this.fillerType = fillerType;
        return this;
    }

    public ExportType getExportType() {
        return exportType;
    }

    public ExportParam setExportType(ExportType exportType) {
        this.exportType = exportType;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public ExportParam setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public Set<Long> getIds() {
        return ids;
    }

    public ExportParam setIds(Set<Long> ids) {
        this.ids = ids;
        return this;
    }

    public Set<ExportColumn> getColumns() {
        return columns;
    }

    public ExportParam setColumns(Set<ExportColumn> columns) {
        this.columns = columns;
        return this;
    }

    public Integer getSingleExcelMaxSheetNum() {
        return singleExcelMaxSheetNum;
    }

    public ExportParam setSingleExcelMaxSheetNum(Integer singleExcelMaxSheetNum) {
        this.singleExcelMaxSheetNum = singleExcelMaxSheetNum;
        return this;
    }

    public Integer getSingleSheetMaxRow() {
        return singleSheetMaxRow;
    }

    public ExportParam setSingleSheetMaxRow(Integer singleSheetMaxRow) {
        this.singleSheetMaxRow = singleSheetMaxRow;
        return this;
    }

    public boolean isAsync() {
        return async;
    }

    public ExportParam setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public ExportParam setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Long getMaxDataCount() {
        return maxDataCount;
    }

    public ExportParam setMaxDataCount(Long maxDataCount) {
        this.maxDataCount = maxDataCount;
        return this;
    }

    public Set<String> getSelection() {
        return selection;
    }

    public ExportParam setSelection(Set<String> selection) {
        this.selection = selection;
        return this;
    }

    public CodeRender getCodeRenderMode() {
        return codeRenderMode;
    }

    public ExportParam setCodeRenderMode(CodeRender codeRenderMode) {
        this.codeRenderMode = codeRenderMode;
        return this;
    }

    public String getExportTemplateCode() {
        return exportTemplateCode;
    }

    public ExportParam setExportTemplateCode(String exportTemplateCode) {
        this.exportTemplateCode = exportTemplateCode;
        return this;
    }

    public String getExportTemplateType() {
        return exportTemplateType;
    }

    public ExportParam setExportTemplateType(String exportTemplateType) {
        this.exportTemplateType = exportTemplateType;
        return this;
    }

    public String getCustomData() {
        return customData;
    }

    public ExportParam setCustomData(String customData) {
        this.customData = customData;
        return this;
    }
}
