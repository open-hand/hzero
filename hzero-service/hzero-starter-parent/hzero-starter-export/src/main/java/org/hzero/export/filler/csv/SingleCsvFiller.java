package org.hzero.export.filler.csv;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.UUIDUtils;
import org.hzero.export.constant.CodeRender;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.entity.Csv;
import org.hzero.export.entity.CsvGroup;
import org.hzero.export.entity.Node;
import org.hzero.export.filler.CsvFiller;
import org.hzero.export.vo.ExcelSheetProperty;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * 头行打平导出
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/04 20:30
 */
@SuppressWarnings("DuplicatedCode")
public class SingleCsvFiller extends CsvFiller {

    public SingleCsvFiller() {
    }

    public SingleCsvFiller(ExportColumn root, ExportProperty exportProperty) {
        super(root, exportProperty);
    }

    @Override
    public String getFillerType() {
        return ExportConstants.FillerType.SINGLE;
    }

    @Override
    public void createCsv(CsvGroup csvGroup) {
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
        // 判断有多少叶子节点，每个叶子节点建立一个csv
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
            createCsvAndTitle(csvGroup, batch, defaultSheetName, root, leafList.get(0), stack);
            return;
        }
        // 存在多个叶子节点
        for (ExportColumn item : leafList) {
            Stack<ExportColumn> stack = new Stack<>();
            // 构建堆
            buildStack(idMap, item, stack);
            // 创建sheet页及标题行
            createCsvAndTitle(csvGroup, batch, null, root, item, stack);
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
     * 创建csv及标题行
     */
    private void createCsvAndTitle(CsvGroup csvGroup, String batch, String csvName, ExportColumn root, ExportColumn leaf, Stack<ExportColumn> stack) {
        if (CollectionUtils.isEmpty(stack)) {
            // 没有列，不创建sheet页
            return;
        }
        // 未指定csv名称，使用叶子节点
        if (StringUtils.isBlank(csvName)) {
            csvName = StringUtils.defaultIfBlank(leaf.getTitle(), leaf.getName());
        }
        String realName = interceptCsvName(csvName);
        Csv csv = csvGroup.createCsv(realName, batch);
        // 记录当前节点的批次号和对应的csv名称
        Map<String, String> sheetNames = leaf.getSheetNames();
        sheetNames.put(batch, realName);
        ExcelSheetProperty excelSheetProperty = root.getExcelSheetProperty();
        if (excelSheetProperty == null) {
            createTitleRow(csv, 0, 0, stack);
        } else {
            createTitleRow(csv, excelSheetProperty.getRowOffset(), excelSheetProperty.getColOffset(), stack);
        }
    }

    /**
     * 创建标题行
     */
    private void createTitleRow(Csv csv, int rowOffset, int colOffset, Stack<ExportColumn> stack) {
        for (int i = 0; i < rowOffset; i++) {
            // 行偏移量插入空行
            csv.writeRow(null);
        }

        // 渲染编码用于导入
        CodeRender render = CodeRender.NONE;
        if (this.exportProperty != null) {
            render = exportProperty.getCodeRenderMode();
        }
        List<String> codes = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < colOffset; i++) {
            // 列偏移量插入空列
            if (CodeRender.HEAD.equals(render)) {
                codes.add(StringUtils.EMPTY);
            }
            titles.add(StringUtils.EMPTY);
        }
        while (CollectionUtils.isNotEmpty(stack)) {
            ExportColumn exportColumn = stack.pop();
            int leftIndex = -1;
            for (ExportColumn column : exportColumn.getChildren()) {
                if (column.isChecked() && !column.hasChildren()) {
                    if (leftIndex < 0) {
                        // 只记录第一个单元格的下标
                        leftIndex = titles.size();
                    }
                    if (CodeRender.HEAD.equals(render)) {
                        codes.add(column.getName());
                    }
                    titles.add(column.getTitle());
                }
            }
            // 记录各个层级起始列下标
            exportColumn.setLeftIndex(leftIndex);
        }
        if (CodeRender.HEAD.equals(render)) {
            // 写入编码行
            csv.writeRow(codes);
        }
        // 写入标题行
        csv.writeHead(titles);
    }

    @Override
    protected void fillData(Csv csv, List<?> data) {
        // 获取csv的批次
        String batch = csv.getBatch();
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
                creatRow(batch, csv, root, first);
            } else {
                // 含有下级数据，递归写入数据
                fillData(first, csv, batch, item, getRootExportColumn());
            }
        }
    }

    /**
     * 处理下级节点的数据
     */
    private void fillData(Node parentNode, Csv csv, String batch, Object data, ExportColumn parent) {
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
            for (Object itemData : itemDataList) {
                Node node = new Node(itemData, item, parentNode, null);
                parentNode.setChild(node);
                // 到达叶子节点，递归填充父级数据
                if (isLeaf) {
                    creatRow(batch, csv, item, node);
                    continue;
                }
                // 非叶子节点、递归填充数据
                fillData(node, csv, batch, itemData, item);
            }
        }
    }

    /**
     * 获取目标csv并创建行
     */
    private void creatRow(String batch, Csv csv, ExportColumn exportColumn, Node node) {
        CsvGroup csvGroup = csv.getCsvGroup();
        // 根据csv名称获取需要操作的csv
        String csvName = exportColumn.getSheetNames().get(batch);
        Csv writeCsv = csvGroup.getCsv(csvName);
        Assert.notNull(writeCsv, BaseConstants.ErrorCode.NOT_NULL);
        // 数据行
        List<String> data = new ArrayList<>();
        loopFillData(node, data);
        // 写入数据
        writeCsv.writeRow(data);
    }

    /**
     * 递归填充数据
     */
    private void loopFillData(Node node, List<String> data) {
        if (node == null) {
            return;
        }
        ExportColumn exportColumn = node.getExportColumn();
        fillRow(data, exportColumn.getLeftIndex(), node.getData(), exportColumn.getChildren());
        loopFillData(node.pre(), data);
    }

    /**
     * 生成行数据
     */
    private void fillRow(List<String> data, int colOffset, Object rowData, List<ExportColumn> columns) {
        int cells = 0;
        for (ExportColumn column : columns) {
            if (column.isChecked() && !column.hasChildren()) {
                int index = colOffset + cells;
                while (index >= data.size()) {
                    data.add(StringUtils.EMPTY);
                }
                data.set(index, buildValue(rowData, column));
                cells++;
            }
        }
    }
}
