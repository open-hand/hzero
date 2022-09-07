package org.hzero.export.filler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hzero.export.vo.ExportColumn;
import org.springframework.util.Assert;

/**
 * 多Sheet导出
 *
 * @author bojiangzhou 2018/07/27
 */
public class MultiSheetFiller extends ExcelFiller {

    public static final String FILLER_TYPE = "multi-sheet";

    public MultiSheetFiller() {
    }

    public MultiSheetFiller(ExportColumn root) {
        super(root);
    }

    @Override
    public String getFillerType() {
        return FILLER_TYPE;
    }

    @Override
    public void createSheetAndTitle0(SXSSFWorkbook workbook) {
        createSheetAndTitle0(workbook, null, null, getRootExportColumn());
    }

    @Override
    protected void fillData0(SXSSFSheet sheet, List<?> data) {
        fillData0(sheet, getRootExportColumn(), null, null, data);
    }

    private void fillData0(SXSSFSheet sheet, ExportColumn exportClass, Object parentData, List<ExportColumn> parentColumns, List<?> data) {
        List<ExportColumn> checkedChildren = getCheckedChildren(exportClass);
        // 父级 columns
        List<ExportColumn> innerParentColumns = exportClass.getChildren().stream()
                .filter(c -> c.getExcelColumn().showInChildren() && !c.hasChildren())
                .collect(Collectors.toList());
        // 处理每行数据
        for (int i = 0, len = data.size(); i < len; i++) {
            Assert.isTrue(sheet.getLastRowNum() < singleSheetMaxRow, "export.too-many-data");
            SXSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
            // 行数据
            fillRow(sheet.getWorkbook(), row, exportClass.getExcelSheet().colOffset(), parentData, parentColumns, data.get(i), exportClass.getChildren());
            // 子列表
            for (ExportColumn child : checkedChildren) {
                List<Object> childData = getChildData(data.get(i), child);
                SXSSFSheet childSheet = getFillSheet(sheet.getSheetName() + "." + child.getTitle());
                fillData0(childSheet, child, data.get(i), innerParentColumns, childData);
            }
        }
    }

    /**
     * sheet 标题最长31位 多余的会被截掉，标题不能重复
     */
    private void createSheetAndTitle0(SXSSFWorkbook workbook, String parentTitle, List<ExportColumn> parentColumns, ExportColumn exportClass) {
        if (exportClass.isChecked() && exportClass.hasChildren()) {
            // sheet
            String title = Optional.ofNullable(parentTitle).map(t -> t + ".").orElse("") +
                    StringUtils.defaultIfBlank(exportClass.getTitle(), exportClass.getName());
            SXSSFSheet sheet = workbook.createSheet(title);
            sheet.setDefaultColumnWidth(20);
            List<ExportColumn> titleColumn = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(parentColumns)) {
                titleColumn.addAll(parentColumns);
            }
            titleColumn.addAll(exportClass.getChildren());
            // title
            createTitleRow(sheet, exportClass.getExcelSheet().rowOffset(), exportClass.getExcelSheet().colOffset(), titleColumn);
            List<ExportColumn> innerParentColumns = exportClass.getChildren().stream()
                    .filter(c -> c.getExcelColumn().showInChildren() && !c.hasChildren())
                    .collect(Collectors.toList());
            // child
            exportClass.getChildren().forEach(child -> {
                if (child.hasChildren()) {
                    createSheetAndTitle0(workbook, title, innerParentColumns, child);
                }
            });
        }
    }

    /**
     * 创建标题行
     */
    private void createTitleRow(SXSSFSheet sheet, int rowOffset, int colOffset, List<ExportColumn> columns) {
        SXSSFRow titleRow = sheet.createRow(rowOffset);
        titleRow.setHeight((short) 350);
        int cells = 0;
        for (ExportColumn column : columns) {
            if (column.isChecked() && !column.getExcelColumn().child()) {
                int index = colOffset + cells;
                SXSSFCell cell = titleRow.createCell(index);
                cell.setCellStyle(titleCellStyle);
                // 值
                fillCellValue(sheet.getWorkbook(), cell, column.getTitle(), Collections.emptyList(), column.getExcelColumn(), true);

                // 宽度
                setCellWidth(sheet, column.getType(), colOffset + cells, column.getExcelColumn().width());

                // 设置值集下拉选项
                setOptions(sheet, column, rowOffset + 1, index);
                cells++;
            }
        }
    }

    private SXSSFSheet getFillSheet(String title) {
        for (int i = 0; i < getCurrentWorkbook().getNumberOfSheets(); i++) {
            if (getCurrentWorkbook().getSheetAt(i).getSheetName().equals(title)) {
                return getCurrentWorkbook().getSheetAt(i);
            }
        }
        return getCurrentWorkbook().getSheet(title);
    }

    /**
     * 生成行数据
     */
    private void fillRow(SXSSFWorkbook workbook, SXSSFRow row, int colOffset, Object parentData, List<ExportColumn> parentColumns, Object rowData, List<ExportColumn> columns) {
        int cells = 0;
        if (parentData != null && CollectionUtils.isNotEmpty(parentColumns)) {
            for (ExportColumn parentColumn : parentColumns) {
                if (!parentColumn.isChecked()) {
                    continue;
                }
                Object cellValue = null;
                try {
                    cellValue = FieldUtils.readField(parentData, parentColumn.getName(), true);
                } catch (Exception ev) {
                    logger.error("get value error.", ev);
                }
                fillCellValue(workbook, row.createCell(colOffset + cells++), cellValue, rowData,
                        parentColumn.getExcelColumn(), false);
            }
        }

        for (ExportColumn column : columns) {
            if (column.isChecked() && !column.hasChildren()) {
                Object cellValue = null;
                try {
                    cellValue = FieldUtils.readField(rowData, column.getName(), true);
                } catch (Exception ev) {
                    logger.error("get value error.", ev);
                }
                fillCellValue(workbook, row.createCell(colOffset + cells++), cellValue, rowData,
                        column.getExcelColumn(), false);
            }
        }
    }

    private List<ExportColumn> getCheckedChildren(ExportColumn root) {
        if (root != null && CollectionUtils.isNotEmpty(root.getChildren())) {
            return root.getChildren().stream().filter(column -> column.isChecked() && column.hasChildren()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private List<Object> getChildData(Object data, ExportColumn child) {
        String getter = "get" + child.getName();
        Method[] methods = data.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(getter)) {
                try {
                    method.setAccessible(true);
                    return (List<Object>) method.invoke(data) == null ? Collections.emptyList() : (List<Object>) method.invoke(data);
                } catch (Exception e) {
                    logger.error("get child data error.", e);
                }
            }
        }
        return Collections.emptyList();
    }

}
