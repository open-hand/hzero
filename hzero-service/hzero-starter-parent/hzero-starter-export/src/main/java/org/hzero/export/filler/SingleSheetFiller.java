package org.hzero.export.filler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hzero.core.jackson.JacksonConstant;
import org.hzero.export.entity.Node;
import org.hzero.export.vo.ExportColumn;
import org.springframework.util.Assert;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 单Sheet导出：一种数据类型一个Sheet页
 *
 * @author xcxcxcxcx
 */
public class SingleSheetFiller extends ExcelFiller {

    public static final String FILLER_TYPE = "single-sheet";

    private CellStyle oddTitleCellStyle;
    private CellStyle evenTitleCellStyle;

    public SingleSheetFiller() {
    }

    public SingleSheetFiller(ExportColumn root) {
        super(root);
    }

    @Override
    public String getFillerType() {
        return FILLER_TYPE;
    }

    @Override
    public void createSheetAndTitle0(SXSSFWorkbook workbook) {
        setOddTitleCellStyle(workbook);
        setEventTitleCellStyle(workbook);
        List<ExportColumn> titleColumns = new LinkedList<>();
        collectAllTitleColumn(titleColumns, getRootExportColumn());
        String title = StringUtils.defaultIfBlank(getRootExportColumn().getTitle(), getRootExportColumn().getName());
        SXSSFSheet sheet = workbook.createSheet(title);
        sheet.setDefaultColumnWidth(20);
        createTitleRow(sheet, getRootExportColumn().getExcelSheet().rowOffset(), getRootExportColumn().getExcelSheet().colOffset(), titleColumns);
    }

    @Override
    protected void fillData0(SXSSFSheet sheet, List<?> data) {
        // 处理每行数据
        int rows = 0;
        for (Object item : data) {
            Assert.isTrue(sheet.getLastRowNum() < singleSheetMaxRow, "export.too-many-data");
            SXSSFRow row = sheet.createRow(sheet.getLastRowNum() + 1);
            int colOffset = getRootExportColumn().getExcelSheet() == null ? 0 : getRootExportColumn().getExcelSheet().colOffset();
            // 第一级数据
            Node first = new Node(item, row.getRowNum(), colOffset, getRootExportColumn(), null, null);
            fillRow(sheet.getWorkbook(), row, colOffset, item, getRootExportColumn().getChildren());
            int count = fillChildren(first, sheet, item, row.getLastCellNum(), getRootExportColumn());
            rows += count + 1;
            if (rows >= ROW_ACCESS_WINDOW_SIZE) {
                rows = 0;
                try {
                    sheet.flushRows(ROW_ACCESS_WINDOW_SIZE);
                } catch (IOException e) {
                    logger.error("flush rows error.", e);
                }
            }
        }
    }

    private int fillChildren(Node parentNode, SXSSFSheet sheet, Object data, int colOffset, ExportColumn exportClass) {
        int maxChildDataCount = 0;
        int lastRowNum = sheet.getLastRowNum();
        int rowIndex = lastRowNum;
        List<ExportColumn> exportColumns = getChildrenCheckedAndHasChild(exportClass);
        for (ExportColumn child : exportColumns) {
            List<Object> childData = getChildData(data, child);
            List<ExportColumn> childColumns = child.getChildren();
            int checkedCount = countChildrenChecked(child);
            int innerMaxChildCount = 0;
            for (Object childItem : childData) {
                SXSSFRow row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                Node node = new Node(childItem, rowIndex, colOffset, child, parentNode, null);
                parentNode.setChild(node);
                // 填充数据
                fillRow(sheet.getWorkbook(), row, colOffset, childItem, childColumns);
                List<ExportColumn> list = childColumns.stream().filter(ExportColumn::hasChildren).collect(Collectors.toList());
                boolean mustReturn = list.stream().map(x -> getChildData(data, x)).allMatch(CollectionUtils::isEmpty);
                // 到达叶子节点，递归填充父级数据
                if (!hasChildren(childColumns) || mustReturn) {
                    fillParent(node, sheet.getWorkbook(), row);
                }
                int childCount = fillChildren(node, sheet, childItem, colOffset + checkedCount, child);
                innerMaxChildCount += childCount > 0 ? childCount : 1;
                rowIndex = childCount > 0 ? rowIndex + childCount : rowIndex + 1;
            }
            // 重置
            rowIndex = lastRowNum;
            colOffset += checkedCount;
            if (innerMaxChildCount > maxChildDataCount) {
                maxChildDataCount = innerMaxChildCount;
            }
        }
        return maxChildDataCount;
    }

    private boolean hasChildren(List<ExportColumn> columnList) {
        for (ExportColumn column : columnList) {
            if (column.hasChildren()) {
                return true;
            }
        }
        return false;
    }

    private void fillParent(Node node, SXSSFWorkbook workbook, SXSSFRow row) {
        Node parent = node.pre();
        if (parent == null) {
            return;
        }
        // 父级数据已经在该行写过的话，就不再写了
        if (parent.getRowIndex() != row.getRowNum()) {
            fillRow(workbook, row, parent.getColIndex(), parent.getData(), parent.getExportColumn().getChildren());
        }
        fillParent(parent, workbook, row);
    }

    private void collectAllTitleColumn(List<ExportColumn> titleColumns, ExportColumn exportClass) {
        if (exportClass.isChecked() && exportClass.hasChildren()) {
            titleColumns.add(exportClass);
            if (CollectionUtils.isNotEmpty(exportClass.getChildren())) {
                for (ExportColumn ec : exportClass.getChildren()) {
                    collectAllTitleColumn(titleColumns, ec);
                }
            }
        }
    }

    /**
     * 创建标题行
     */
    private void createTitleRow(SXSSFSheet sheet, int rowOffset, int colOffset, List<ExportColumn> titleColumns) {
        SXSSFRow titleRow = sheet.createRow(rowOffset);
        titleRow.setHeight((short) 350);

        CellStyle titleRowStyle = sheet.getWorkbook().createCellStyle();
        titleRowStyle.setLocked(true);
        titleRow.setRowStyle(titleRowStyle);

        for (int i = 0; i < titleColumns.size(); i++) {
            for (ExportColumn column : titleColumns.get(i).getChildren()) {
                if (column.isChecked() && !column.hasChildren()) {
                    SXSSFCell cell = titleRow.createCell(colOffset);
                    if (i % 2 == 0) {
                        cell.setCellStyle(oddTitleCellStyle);
                    } else {
                        cell.setCellStyle(evenTitleCellStyle);
                    }
                    // 值
                    fillCellValue(sheet.getWorkbook(), cell, column.getTitle(), Collections.emptyList(), column.getExcelColumn(), true);
                    // 宽度
                    setCellWidth(sheet, column.getType(), colOffset, column.getExcelColumn().width());

                    // 设置下拉选项
                    setOptions(sheet, column, rowOffset + 1, colOffset);
                    colOffset++;
                }
            }
        }
    }

    /**
     * 生成行数据
     */
    private void fillRow(SXSSFWorkbook workbook, SXSSFRow row, int colOffset, Object rowData, List<ExportColumn> columns) {
        int cells = 0;
        for (ExportColumn column : columns) {
            if (column.isChecked() && !column.hasChildren()) {
                Object cellValue = null;
                try {
                    cellValue = FieldUtils.readField(rowData, column.getName(), true);
                    // 日期类型需要进行时区转换
                    if (cellValue instanceof Date) {
                        String pattern = getPattern(column.getExcelColumn());
                        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(StringUtils.isBlank(pattern) ? JacksonConstant.DEFAULT_DATE_FORMAT : pattern);
                        if (!column.isIgnoreTimeZone()) {
                            CustomUserDetails details = DetailsHelper.getUserDetails();
                            if (details != null && details.getTimeZone() != null) {
                                dateFormatGmt.setTimeZone(TimeZone.getTimeZone(details.getTimeZone()));
                            }
                        }
                        cellValue = dateFormatGmt.format(cellValue);
                    }
                } catch (Exception ev) {
                    logger.error("get value error.", ev);
                }
                SXSSFCell cell = row.createCell(colOffset + cells);
                int index = colOffset + cells;
                fillCellValue(workbook, cell, cellValue, rowData, column.getExcelColumn(), false);
                cells++;
            }
        }
    }

    private List<ExportColumn> getChildrenCheckedAndHasChild(ExportColumn root) {
        if (root != null && CollectionUtils.isNotEmpty(root.getChildren())) {
            return root.getChildren().stream().filter(column -> column.isChecked() && column.hasChildren()).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private int countChildrenChecked(ExportColumn root) {
        if (root != null && CollectionUtils.isNotEmpty(root.getChildren())) {
            long count = root.getChildren().stream().filter(column -> column.isChecked() && !column.hasChildren()).count();
            return Integer.parseInt(String.valueOf(count));
        }
        return 0;
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

    private void setOddTitleCellStyle(SXSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setColor(Font.COLOR_NORMAL);
        font.setBold(true);

        oddTitleCellStyle = workbook.createCellStyle();
        oddTitleCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        oddTitleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        oddTitleCellStyle.setFont(font);
        oddTitleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        oddTitleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        oddTitleCellStyle.setLocked(true);
    }


    private void setEventTitleCellStyle(SXSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setColor(Font.COLOR_NORMAL);
        font.setBold(true);

        evenTitleCellStyle = workbook.createCellStyle();
        evenTitleCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
        evenTitleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        evenTitleCellStyle.setFont(font);
        evenTitleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        evenTitleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        oddTitleCellStyle.setLocked(true);
    }

}
