package org.hzero.export.filler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

import org.hzero.export.constant.ExportConstants;
import org.hzero.export.constant.FileType;
import org.hzero.export.entity.Csv;
import org.hzero.export.entity.CsvGroup;
import org.hzero.export.render.ValueRenderer;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * Csv 数据填充器
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/10 9:47
 */
@SuppressWarnings("DuplicatedCode")
public abstract class CsvFiller extends FileFiller implements ICsvFiller {


    private final Map<Class<? extends ValueRenderer>, ValueRenderer> renderers = new ConcurrentHashMap<>();

    protected static final Logger LOGGER = LoggerFactory.getLogger(CsvFiller.class);

    private final List<CsvGroup> csvGroups = new ArrayList<>();

    private ExportColumn root;
    /**
     * 文件类型
     */
    private FileType fileType;
    /**
     * 单组csv的最大数量
     */
    private Integer singleMaxPage;
    /**
     * 单个csv的最大记录数
     */
    private Integer singleMaxRow;
    /**
     * 单组excel的最大记录数
     */
    private Integer countMaxRow;
    /**
     * csvGroup剩余容纳数据量
     */
    private int remainCapacityOfCsvGroup;
    /**
     * csv可容纳数据量
     */
    private int remainCapacityOfCsv;
    /**
     * 当前csvGroup下标
     */
    private int csvGroupIndex;
    /**
     * 当前csv下标
     */
    private int csvIndex;

    /**
     * csv名称缓存，文件名全局唯一
     */
    private final Map<String, Integer> names = new HashMap<>(16);

    /**
     * 无参构造用于实例化到容器
     */
    public CsvFiller() {
    }

    public CsvFiller(ExportColumn root, ExportProperty exportProperty) {
        this.root = root;
        this.exportProperty = exportProperty;
    }

    @Override
    public void configure(int singleMaxPage, int singleMaxRow, FileType fileType) {
        // csv没有sheet页，固定为最大值
        this.singleMaxPage = 1;
        this.singleMaxRow = singleMaxRow;
        this.countMaxRow = singleMaxRow;
        this.fileType = fileType;
    }

    /**
     * 创建csv的标题行
     *
     * @param csvGroup csvGroup
     */
    public void createCsv(CsvGroup csvGroup) {
        throw new CommonException("Unsupported file export type.");
    }

    /**
     * 填充csv的数据
     *
     * @param csv  csv
     * @param data 数据
     */
    protected void fillData(Csv csv, List<?> data) {
        throw new CommonException("Unsupported file export type.");
    }

    @Override
    public void fillTitle() {
        if (CollectionUtils.isEmpty(csvGroups)) {
            csvGroups.add(new CsvGroup());
        }
        CsvGroup csvGroup = csvGroups.get(0);
        createCsv(csvGroup);
    }

    /**
     * 创建csv并填充数据
     *
     * @param data 数据
     */
    @Override
    public void fillData(List<?> data) {
        int append = data.size();
        // 根据数据量生成csv，每组csv存储的数据量为 单sheet最大行数
        List<CsvGroup> csvGroups = getAppendCsvGroup(append);
        // 划分每组的数据
        List<List<?>> shardData = shardData(remainCapacityOfCsv, countMaxRow, data);
        // 填充工作簿数据
        Assert.isTrue(csvGroups.size() == shardData.size(), "csvGroup.size() != shardData.size()");
        for (int i = 0; i < csvGroups.size(); i++) {
            CsvGroup csvGroup = csvGroups.get(i);
            List<?> item = shardData.get(i);
            fillCsvData(csvGroup, item);
        }
    }

    private List<CsvGroup> getAppendCsvGroup(int append) {
        int remainCapacity = remainCapacityOfCsvGroup;
        if (remainCapacity < append) {
            // 需要扩容
            int remainAppend = append - remainCapacity;
            int appendExcel = (int) Math.ceil((double) remainAppend / countMaxRow);
            for (int i = 0; i < appendExcel; i++) {
                csvGroups.add(new CsvGroup());
            }
        }
        return csvGroups.subList(csvGroupIndex, csvGroups.size());
    }

    /**
     * 创建csv标题并填充数据
     */
    private void fillCsvData(CsvGroup csvGroup, List<?> data) {
        int append = data.size();
        // 根据数据量生成csv，每个sheet页存储的数据量为 单sheet最大行数
        List<Csv> csvList = getAppendWriteCsv(csvGroup, append);
        // 划分每个sheet页的数据
        List<List<?>> shardData = shardData(remainCapacityOfCsv, singleMaxRow, data);
        // 插入数据
        Assert.isTrue(csvList.size() == shardData.size(), "csvList.size() != shardData.size()");
        for (int i = 0; i < csvList.size(); i++) {
            Csv csv = csvList.get(i);
            List<?> item = shardData.get(i);
            fillData(csv, item);
            resetRemainValue(item.size());
        }
    }

    /**
     * 数据按照每页数量限制，动态计算创建sheet页（头行分sheet页导出，每组数据会创建多个sheet）
     */
    private List<Csv> getAppendWriteCsv(CsvGroup csvGroup, int append) {
        // 若小于数据总量，则需要扩容
        if (remainCapacityOfCsv < append) {
            // 需要扩容的大小
            int remainAppend = append - remainCapacityOfCsv;
            // 计算需要创建的sheet页数量
            int appendSheet = (int) Math.ceil((double) remainAppend / singleMaxRow);
            for (int i = 1; i <= appendSheet; i++) {
                createCsv(csvGroup);
            }
        }
        // csv创建完成，获取csv列表
        List<Csv> result = new ArrayList<>();
        // 相同批次的csv，只记录第一个csv
        List<String> stored = new ArrayList<>();
        for (Csv csv : getCsvList(csvGroup)) {
            String batch = csv.getBatch();
            if (StringUtils.isBlank(batch)) {
                continue;
            }
            if (stored.contains(batch)) {
                // 该后缀已记录，跳过
                continue;
            }
            result.add(csv);
            stored.add(batch);
        }
        return result;
    }

    /**
     * 获取本次要操作的csv
     */
    private List<Csv> getCsvList(CsvGroup csvGroup) {
        List<Csv> allCsv = csvGroup.getCsvList();
        return allCsv.subList(csvIndex, allCsv.size());
    }

    /**
     * 记录可容纳数据量
     */
    private void resetRemainValue(int append) {
        int oldRemainCapacityOfCsvGroup = remainCapacityOfCsvGroup;
        int oldRemainCapacityOfCsv = remainCapacityOfCsv;
        if (append > oldRemainCapacityOfCsvGroup) {
            append = append - oldRemainCapacityOfCsvGroup;
            remainCapacityOfCsvGroup = (countMaxRow - append % countMaxRow) % countMaxRow;
            remainCapacityOfCsv = (singleMaxRow - append % singleMaxRow) % singleMaxRow;
        } else {
            remainCapacityOfCsvGroup = oldRemainCapacityOfCsvGroup - append;
            if (append > oldRemainCapacityOfCsv) {
                remainCapacityOfCsv = (singleMaxRow - (append - oldRemainCapacityOfCsv) % singleMaxRow) % singleMaxRow;
            } else {
                remainCapacityOfCsv = oldRemainCapacityOfCsv - append;
            }
        }
        if (append > oldRemainCapacityOfCsv) {
            csvIndex = csvIndex + (int) Math.floor((double) (append - oldRemainCapacityOfCsv) / singleMaxRow);
        } else if (append == oldRemainCapacityOfCsv && append > 0) {
            csvIndex = csvIndex + 1;
        }
        csvGroupIndex = csvGroupIndex + (int) Math.floor((double) csvIndex / singleMaxPage);
        csvIndex = csvIndex % singleMaxPage;
    }

    @Override
    public List<CsvGroup> getCsvGroups() {
        return csvGroups;
    }

    @Override
    public List<Csv> getCsv() {
        List<Csv> csvList = new ArrayList<>();
        for (CsvGroup csvGroup : csvGroups) {
            csvList.addAll(csvGroup.getCsvList());
        }
        return csvList;
    }

    @Override
    public ExportColumn getRootExportColumn() {
        return root;
    }


    /**
     * 处理Csv页名称，若sheet页名称重复，添加 (x)后缀
     */
    protected String interceptCsvName(String csvName) {
        // 不带后缀的名称
        String nameWithoutSuffix = csvName;
        if (csvName.contains(ExportConstants.FILE_SUFFIX_SEPARATE)) {
            nameWithoutSuffix = csvName.substring(0, csvName.lastIndexOf(ExportConstants.FILE_SUFFIX_SEPARATE));
        }
        if (StringUtils.isBlank(nameWithoutSuffix)) {
            nameWithoutSuffix = "Csv";
        }
        // 判断csv名称是否已经存在
        int num = 1;
        if (names.containsKey(nameWithoutSuffix)) {
            num = names.get(nameWithoutSuffix);
            num++;
        }
        names.put(nameWithoutSuffix, num);
        return nameWithoutSuffix + ExportConstants.FILE_SUFFIX_SEPARATE + num;
    }

    /**
     * 构建CSV单元格的值
     */
    protected String buildValue(Object data, ExportColumn column) {
        Object cellValue = getCellValue(data, column);
        cellValue = this.doRender(cellValue, data, column.getExcelColumnProperty().getRenderers());
        return cellValue == null ? "" : cellValue.toString();
    }
}
