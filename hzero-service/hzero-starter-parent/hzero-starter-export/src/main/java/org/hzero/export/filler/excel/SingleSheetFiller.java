package org.hzero.export.filler.excel;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.util.Assert;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.UUIDUtils;
import org.hzero.export.constant.CodeRender;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.entity.Node;
import org.hzero.export.filler.ExcelFiller;
import org.hzero.export.vo.ExcelSheetProperty;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * 头行打平导出 (按照甄云的要求，父子打平，兄弟仍然分sheet页)
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/04 20:30
 */
@SuppressWarnings("DuplicatedCode")
public class SingleSheetFiller extends ExcelFiller {

    public SingleSheetFiller() {
    }

    public SingleSheetFiller(ExportColumn root, ExportProperty exportProperty) {
        super(root, exportProperty);
    }

    @Override
    public String getFillerType() {
        return ExportConstants.FillerType.SINGLE;
    }

    @Override
    public void createSheet(Workbook workbook) {
        ExportColumn root = getRootExportColumn();
        // 生成批次号
        String batch = UUIDUtils.generateUUID();
        List<ExportColumn> titleColumns = collectAllTitleColumnFromRoot(root);
        // 按照parentId 分组
        Map<Long, List<ExportColumn>> parentMap = new HashMap<>(16);
        // 根据id分组
        Map<Long, ExportColumn> idMap = new HashMap<>(16);
        for (ExportColumn item : titleColumns) {
            idMap.put(item.getId(), item);
            List<ExportColumn> list = parentMap.getOrDefault(item.getParentId(), new ArrayList<>());
            list.add(item);
            parentMap.put(item.getParentId(), list);
        }
        List<ExportColumn> leafList = new ArrayList<>();
        // 判断有多少叶子节点，每个叶子节点建立一个sheet页
        for (ExportColumn item : titleColumns) {
            Long id = item.getId();
            if (parentMap.containsKey(id)) {
                // 非叶子结点，跳过
                continue;
            }
            leafList.add(item);
        }
        // 若只有一个叶子节点，sheet页使用根节点命名，否则使用叶子节点命名
        if (leafList.size() == 1) {
            Stack<ExportColumn> stack = new Stack<>();
            // 构建堆
            buildStack(idMap, leafList.get(0), stack);
            String defaultSheetName = StringUtils.defaultIfBlank(root.getTitle(), root.getName());
            createSheetAndTitle(workbook, batch, defaultSheetName, root, leafList.get(0), stack);
            return;
        }
        // 存在多个叶子节点
        for (ExportColumn item : leafList) {
            Stack<ExportColumn> stack = new Stack<>();
            // 构建堆
            buildStack(idMap, item, stack);
            // 创建sheet页及标题行
            createSheetAndTitle(workbook, batch, null, root, item, stack);
        }
    }

    private void buildStack(Map<Long, ExportColumn> idMap, ExportColumn column, Stack<ExportColumn> stack) {
        stack.add(column);
        Long parentId = column.getParentId();
        if (idMap.containsKey(parentId)) {
            buildStack(idMap, idMap.get(parentId), stack);
        }
    }

    /**
     * 创建sheet页及标题行
     */
    private void createSheetAndTitle(Workbook workbook, String batch, String sheetName, ExportColumn root, ExportColumn leaf, Stack<ExportColumn> stack) {
        if (CollectionUtils.isEmpty(stack)) {
            // 没有列，不创建sheet页
            return;
        }
        // 未指定sheet页名称，使用叶子节点
        if (StringUtils.isBlank(sheetName)) {
            sheetName = StringUtils.defaultIfBlank(leaf.getTitle(), leaf.getName());
        }
        ExcelSheetProperty excelSheetProperty = root.getExcelSheetProperty();
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
        Map<String, String> sheetNames = leaf.getSheetNames();
        sheetNames.put(batch, sheet.getSheetName());
        if (excelSheetProperty == null) {
            createTitleRow(sheet, 0, 0, stack);
        } else {
            createTitleRow(sheet, excelSheetProperty.getRowOffset(), excelSheetProperty.getColOffset(), stack);
        }
    }

    /**
     * 创建标题行
     */
    private void createTitleRow(Sheet sheet, int rowOffset, int colOffset, Stack<ExportColumn> stack) {
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

        Workbook workbook = sheet.getWorkbook();
        CellStyle titleRowStyle = workbook.createCellStyle();
        titleRowStyle.setLocked(true);
        titleRow.setRowStyle(titleRowStyle);

        while (CollectionUtils.isNotEmpty(stack)) {
            ExportColumn exportColumn = stack.pop();
            int leftIndex = -1;
            for (ExportColumn column : exportColumn.getChildren()) {
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
                    // 设置下拉选项
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
    }

    @Override
    protected void fillData(Sheet sheet, List<?> data) {
        // 获取sheet页的批次号
        String batch = super.sheetBatch.get(sheet);
        // 根节点
        ExportColumn root = getRootExportColumn();
        // 判断是否是叶子节点
        boolean isLeaf = true;
        for (ExportColumn son : root.getChildren()) {
            if (son.isChecked() && son.hasChildren()) {
                isLeaf = false;
                break;
            }
        }
        for (Object item : data) {
            // 第一级数据
            Node first = new Node(item, root, null, null);
            if (isLeaf) {
                // 只有一个层级，写入数据
                creatRow(batch, sheet, root, first);
            } else {
                // 含有下级数据，递归写入数据
                fillData(first, sheet, batch, item, getRootExportColumn());
            }
        }
    }

    /**
     * 处理下级节点的数据
     */
    private void fillData(Node parentNode, Sheet sheet, String batch, Object data, ExportColumn parent) {
        for (ExportColumn item : parent.getChildren()) {
            if (!item.isChecked() || !item.hasChildren()) {
                // 只处理被勾选且包含下级的节点
                continue;
            }
            // 判断是否是叶子节点
            boolean isLeaf = true;
            for (ExportColumn son : item.getChildren()) {
                if (son.isChecked() && son.hasChildren()) {
                    isLeaf = false;
                    break;
                }
            }
            List<Object> itemDataList = getChildData(data, item);
            if (CollectionUtils.isEmpty(itemDataList)) {
                // 子级数据不存在
                Node node = new Node(null, item, parentNode, null);
                parentNode.setChild(node);
                // 到达叶子节点，递归填充父级数据
                if (isLeaf) {
                    creatRow(batch, sheet, item, node);
                } else {
                    // 非叶子节点、递归填充数据
                    fillData(node, sheet, batch, null, item);
                }
            } else {
                for (Object itemData : itemDataList) {
                    Node node = new Node(itemData, item, parentNode, null);
                    parentNode.setChild(node);
                    // 到达叶子节点，递归填充父级数据
                    if (isLeaf) {
                        creatRow(batch, sheet, item, node);
                    } else {
                        // 非叶子节点、递归填充数据
                        fillData(node, sheet, batch, itemData, item);
                    }
                }
            }
        }
    }

    /**
     * 获取目标sheet并创建行
     */
    private void creatRow(String batch, Sheet sheet, ExportColumn exportColumn, Node node) {
        Workbook workbook = sheet.getWorkbook();
        // 根据sheet页名称获取需要操作的sheet页
        String sheetName = exportColumn.getSheetNames().get(batch);
        Sheet writeSheet = workbook.getSheet(sheetName);
        Assert.notNull(writeSheet, BaseConstants.ErrorCode.NOT_NULL);
        // 创建行
        Row row = writeSheet.createRow(writeSheet.getLastRowNum() + 1);
        loopFillData(node, writeSheet, row);
    }

    /**
     * 递归填充数据
     */
    private void loopFillData(Node node, Sheet sheet, Row row) {
        if (node == null) {
            return;
        }
        ExportColumn exportColumn = node.getExportColumn();
        fillRow(sheet.getWorkbook(), row, exportColumn.getLeftIndex(), node.getData(), exportColumn.getChildren());
        loopFillData(node.pre(), sheet, row);
    }

    /**
     * 生成行数据
     */
    private void fillRow(Workbook workbook, Row row, int colOffset, Object rowData, List<ExportColumn> columns) {
        int cells = 0;
        for (ExportColumn column : columns) {
            if (column.isChecked() && !column.hasChildren()) {
                Object cellValue = getCellValue(rowData, column);
                Cell cell = row.createCell(colOffset + cells);
                fillCellValue(workbook, cell, cellValue, rowData, column.getExcelColumnProperty(), false);
                cells++;
            }
        }
    }
}
