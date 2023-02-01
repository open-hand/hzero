package org.hzero.export.filler.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.UUIDUtils;
import org.hzero.export.constant.CodeRender;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.entity.Csv;
import org.hzero.export.entity.CsvGroup;
import org.hzero.export.filler.CsvFiller;
import org.hzero.export.vo.ExcelSheetProperty;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * csv头行分页导出
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/12 11:17
 */
@SuppressWarnings("DuplicatedCode")
public class MultiCsvFiller extends CsvFiller {

    public MultiCsvFiller() {
    }

    public MultiCsvFiller(ExportColumn root, ExportProperty exportProperty) {
        super(root, exportProperty);
    }

    @Override
    public String getFillerType() {
        return ExportConstants.FillerType.MULTI;
    }

    @Override
    public void createCsv(CsvGroup csvGroup) {
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
                createCsvAndTitle(csvGroup, StringUtils.defaultIfBlank(root.getTitle(), root.getName()), batch, null, exportColumn);
            } else {
                createCsvAndTitle(csvGroup, null, batch, showInChildColumns.get(exportColumn.getParentId()), exportColumn);
            }
        }
    }

    /**
     * 创建csv及标题行
     */
    private void createCsvAndTitle(CsvGroup csvGroup, String csvName, String batch, List<ExportColumn> extendParentColumns, ExportColumn exportColumn) {
        // csv文件名
        if (StringUtils.isBlank(csvName)) {
            csvName = StringUtils.defaultIfBlank(exportColumn.getTitle(), exportColumn.getName());
        }
        // 创建csv
        String realName = interceptCsvName(csvName);
        Csv csv = csvGroup.createCsv(realName, batch);
        // 记录当前节点的批次号和对应的csv名称
        Map<String, String> csvNames = exportColumn.getSheetNames();
        csvNames.put(batch, realName);
        // 处理父级中标记了要在子级中展示的列
        List<ExportColumn> columnList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(extendParentColumns)) {
            columnList.addAll(extendParentColumns);
        }
        columnList.addAll(exportColumn.getChildren());
        // 渲染编码用于导入
        CodeRender render = CodeRender.NONE;
        if (this.exportProperty != null) {
            render = exportProperty.getCodeRenderMode();
        }
        // 创建标题行
        ExcelSheetProperty excelSheetProperty = exportColumn.getExcelSheetProperty();
        // 根据配置的起始行，在顶部插入空行
        for (int i = 0; i < excelSheetProperty.getRowOffset(); i++) {
            csv.writeRow(null);
        }
        int colOffset = excelSheetProperty.getColOffset();
        List<String> codes = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < colOffset; i++) {
            // 列偏移量插入空列
            if (CodeRender.HEAD.equals(render)) {
                codes.add(StringUtils.EMPTY);
            }
            titles.add(StringUtils.EMPTY);
        }
        for (ExportColumn column : columnList) {
            if (column.isChecked() && !column.hasChildren()) {
                // 值
                if (CodeRender.HEAD.equals(render)) {
                    codes.add(column.getName());
                }
                titles.add(column.getTitle());
            }
        }
        if (CodeRender.HEAD.equals(render)) {
            // 写入编码行
            csv.writeRow(codes);
        }
        // 写入标题行
        csv.writeHead(titles);
        // 记录起始列下标
        exportColumn.setLeftIndex(colOffset);
    }

    @Override
    protected void fillData(Csv csv, List<?> data) {
        // 获取csv的批次
        String batch = csv.getBatch();
        fillData(csv, batch, getRootExportColumn(), null, null, data);
    }

    /**
     * 填充数据
     */
    private void fillData(Csv csv, String batch, ExportColumn exportColumn, Object parentData, List<ExportColumn> extendParentColumns, List<?> data) {
        // 下级需要使用的父级列，只保留被勾选的导出列
        List<ExportColumn> extendParentColumnList = exportColumn.getChildren().stream().filter(c -> c.getExcelColumnProperty().isShowInChildren() && !c.hasChildren() && c.isChecked()).collect(Collectors.toList());
        // 处理每行数据
        for (Object itemData : data) {
            // 填充行数据
            fillRow(csv, exportColumn.getLeftIndex(), parentData, extendParentColumns, itemData, exportColumn.getChildren());
            // 处理下级节点及数据
            for (ExportColumn itemColumn : exportColumn.getChildren()) {
                if (itemColumn.isChecked() && itemColumn.hasChildren()) {
                    // 下级数据
                    List<Object> childData = getChildData(itemData, itemColumn);
                    // 根据csv名称获取需要操作的csv
                    String csvName = itemColumn.getSheetNames().get(batch);
                    Csv writeCsv = csv.getCsvGroup().getCsv(csvName);
                    Assert.notNull(writeCsv, BaseConstants.ErrorCode.NOT_NULL);
                    // 递归
                    fillData(writeCsv, batch, itemColumn, itemData, extendParentColumnList, childData);
                }
            }
        }
    }

    /**
     * 生成行数据
     */
    private void fillRow(Csv csv, int colOffset, Object parentData, List<ExportColumn> extendParentColumns, Object rowData, List<ExportColumn> exportColumns) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < colOffset; i++) {
            data.add(StringUtils.EMPTY);
        }
        // 记录从父级继承来的数据
        if (parentData != null && CollectionUtils.isNotEmpty(extendParentColumns)) {
            for (ExportColumn item : extendParentColumns) {
                data.add(buildValue(parentData, item));
            }
        }
        // 记录当前节点的数据
        for (ExportColumn column : exportColumns) {
            if (column.isChecked() && !column.hasChildren()) {
                data.add(buildValue(rowData, column));
            }
        }
        // 插入数据
        csv.writeRow(data);
    }
}
