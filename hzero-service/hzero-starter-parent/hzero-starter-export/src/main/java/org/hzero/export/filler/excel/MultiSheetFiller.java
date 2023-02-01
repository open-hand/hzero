package org.hzero.export.filler.excel;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.Assert;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.UUIDUtils;
import org.hzero.export.constant.CodeRender;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.filler.ExcelFiller;
import org.hzero.export.vo.ExcelSheetProperty;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * 头行分sheet页导出
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/12 11:17
 */
@SuppressWarnings("DuplicatedCode")
public class MultiSheetFiller extends ExcelFiller {

    public MultiSheetFiller() {
    }

    public MultiSheetFiller(ExportColumn root, ExportProperty exportProperty) {
        super(root, exportProperty);
    }

    @Override
    public String getFillerType() {
        return ExportConstants.FillerType.MULTI;
    }

    @Override
    public void createSheet(Workbook workbook) {
        ExportColumn root = getRootExportColumn();
        // 生成批次号
        String batch = UUIDUtils.generateUUID();
        // 逐层读取树形结构中的节点
        List<ExportColumn> titleColumns = collectAllTitleColumnFromRoot(root);
        // 需要在子级中展示的列集合
        Map<Long, List<ExportColumn>> showInChildColumns = new HashMap<>(16);
        for (ExportColumn exportColumn : titleColumns) {
            showInChildColumns.put(exportColumn.getId(), exportColumn.getChildren().stream().filter(c -> c.getExcelColumnProperty().isShowInChildren()).collect(Collectors.toList()));
        }
        for (int i = 0; i < titleColumns.size(); i++) {
            ExportColumn exportColumn = titleColumns.get(i);
            if (i == 0) {
                // 根节点指定名称创建sheet页
                createSheetAndTitle(workbook, StringUtils.defaultIfBlank(root.getTitle(), root.getName()), batch, null, exportColumn);
            } else {
                createSheetAndTitle(workbook, null, batch, showInChildColumns.get(exportColumn.getParentId()), exportColumn);
            }
        }
    }

    /**
     * 创建sheet页及标题行
     */
    private void createSheetAndTitle(Workbook workbook, String sheetName, String batch, List<ExportColumn> extendParentColumns, ExportColumn exportColumn) {
        // sheet名称
        if (StringUtils.isBlank(sheetName)) {
            sheetName = StringUtils.defaultIfBlank(exportColumn.getTitle(), exportColumn.getName());
        }
        ExcelSheetProperty excelSheetProperty = exportColumn.getExcelSheetProperty();
        // 创建sheet页
        Sheet sheet = workbook.createSheet(interceptSheetName(sheetName));
        if (excelSheetProperty != null && excelSheetProperty.isReadOnly()) {
            // 使用随机密码保护sheet页
            sheet.protectSheet(UUIDUtils.generateUUID());
        }
        sheet.setDefaultColumnWidth(20);
        if (!this.sheetBatch.containsValue(batch)) {
            // 记录第一个sheet的批次号
            this.sheetBatch.put(sheet, batch);
        }
        // 记录当前节点的批次号和sheet页名称
        Map<String, String> sheetNames = exportColumn.getSheetNames();
        sheetNames.put(batch, sheet.getSheetName());
        // 处理父级中标记了要在子级中展示的列
        List<ExportColumn> columnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(extendParentColumns)) {
            columnList.addAll(extendParentColumns);
        }
        columnList.addAll(exportColumn.getChildren());
        int rowOffset = 0;
        int colOffset = 0;
        // 创建标题行
        if (excelSheetProperty != null) {
            rowOffset = excelSheetProperty.getRowOffset();
            colOffset = excelSheetProperty.getColOffset();
        }
        // 渲染编码用于导入
        CodeRender render = CodeRender.NONE;
        if (this.exportProperty != null) {
            render = exportProperty.getCodeRenderMode();
        }
        Row codeRow = null;
        if (CodeRender.HEAD.equals(render)) {
            codeRow = sheet.createRow(rowOffset);
            codeRow.setHeight((short) 350);
            rowOffset++;
        }
        Row titleRow = sheet.createRow(rowOffset);
        titleRow.setHeight((short) 350);
        int leftIndex = -1;
        for (ExportColumn column : columnList) {
            if (column.isChecked() && !column.hasChildren()) {
                if (CodeRender.HEAD.equals(render)) {
                    // 创建编码单元格
                    Cell codeCell = codeRow.createCell(colOffset);
                    // 值
                    fillCellValue(workbook, codeCell, column.getName(), Collections.emptyList(), column.getExcelColumnProperty(), true);
                }
                if (CodeRender.SHEET.equals(render)) {
                    // 向隐藏sheet页写编码
                    saveCodeToHiddenSheet(workbook, workbook.getSheetIndex(sheet), column.getName(), column.getTitle());
                }
                // 创建标题单元格
                Cell cell = titleRow.createCell(colOffset);
                // 值
                fillCellValue(workbook, cell, column.getTitle(), Collections.emptyList(), column.getExcelColumnProperty(), true);
                // 宽度
                setCellWidth(sheet, column.getType(), colOffset, column.getExcelColumnProperty().getWidth());
                // 设置值集下拉选项
                setOptions(sheet, column, rowOffset + 1, colOffset);
                if (leftIndex < 0) {
                    // 只记录第一个单元格的下标
                    leftIndex = colOffset;
                }
                colOffset++;
            }
        }
        // 记录起始列下标
        exportColumn.setLeftIndex(leftIndex);
    }

    @Override
    protected void fillData(Sheet sheet, List<?> data) {
        // 获取sheet页的批次号
        String batch = super.sheetBatch.get(sheet);
        fillData(sheet, batch, getRootExportColumn(), null, null, data);
    }

    /**
     * 填充数据
     */
    private void fillData(Sheet sheet, String batch, ExportColumn exportColumn, Object parentData, List<ExportColumn> extendParentColumns, List<?> data) {
        Workbook workbook = sheet.getWorkbook();
        // 下级需要使用的父级列，只保留被勾选的导出列
        List<ExportColumn> extendParentColumnList = exportColumn.getChildren().stream().filter(c -> c.getExcelColumnProperty().isShowInChildren() && !c.hasChildren() && c.isChecked()).collect(Collectors.toList());
        // 处理每行数据
        for (Object itemData : data) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            // 填充行数据
            fillRow(workbook, row, exportColumn.getLeftIndex(), parentData, extendParentColumns, itemData, exportColumn.getChildren());
            // 处理下级节点及数据
            for (ExportColumn itemColumn : exportColumn.getChildren()) {
                if (itemColumn.isChecked() && itemColumn.hasChildren()) {
                    // 下级数据
                    List<Object> childData = getChildData(itemData, itemColumn);
                    // 根据sheet页名称获取需要操作的sheet页
                    String sheetName = itemColumn.getSheetNames().get(batch);
                    Sheet writeSheet = workbook.getSheet(sheetName);
                    Assert.notNull(writeSheet, BaseConstants.ErrorCode.NOT_NULL);
                    // 递归
                    fillData(writeSheet, batch, itemColumn, itemData, extendParentColumnList, childData);
                }
            }
        }
    }

    /**
     * 生成行数据
     */
    private void fillRow(Workbook workbook, Row row, int colOffset, Object parentData, List<ExportColumn> extendParentColumns, Object rowData, List<ExportColumn> exportColumns) {
        // 插入从父级继承来的数据
        if (parentData != null && CollectionUtils.isNotEmpty(extendParentColumns)) {
            for (ExportColumn item : extendParentColumns) {
                Object cellValue = getCellValue(parentData, item);
                // 创建单元格
                Cell cell = row.createCell(colOffset);
                colOffset++;
                fillCellValue(workbook, cell, cellValue, rowData, item.getExcelColumnProperty(), false);
            }
        }
        // 插入当前节点的数据
        for (ExportColumn column : exportColumns) {
            if (column.isChecked() && !column.hasChildren()) {
                Object cellValue = getCellValue(rowData, column);
                // 创建单元格
                Cell cell = row.createCell(colOffset);
                colOffset++;
                fillCellValue(workbook, cell, cellValue, rowData, column.getExcelColumnProperty(), false);
            }
        }
    }
}
