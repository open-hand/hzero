package org.hzero.boot.imported.infra.execute;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hzero.boot.imported.config.ImportConfig;
import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.boot.imported.domain.entity.TemplateColumn;
import org.hzero.boot.imported.domain.repository.ImportDataRepository;
import org.hzero.boot.imported.domain.repository.ImportRepository;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.redis.AmountRedis;
import org.hzero.boot.imported.infra.util.StepUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.excel.supporter.ExcelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * 从Excel导入到ImportData执行器
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/14 9:04
 */
public class ExcelImportExecute implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ExcelImportExecute.class);

    private final InputStream fileIo;
    private final Template template;
    private final String batch;
    private final ImportDataRepository dataRepository;
    private final ImportRepository importRepository;
    private final Integer batchNumber;
    private final Integer bufferMemory;
    private Integer count = 0;
    private Integer stepSize;

    public ExcelImportExecute(InputStream fileIo,
                              Template template,
                              String batch,
                              ImportConfig importConfig,
                              ImportRepository importRepository,
                              ImportDataRepository dataRepository) {
        this.fileIo = fileIo;
        this.template = template;
        this.batch = batch;
        this.dataRepository = dataRepository;
        this.importRepository = importRepository;
        this.batchNumber = importConfig.getBatchSize();
        this.bufferMemory = importConfig.getBufferMemory();
    }

    @Override
    public void run() {
        try (Workbook workbook = StreamingReader.builder()
                .rowCacheSize(batchNumber)
                // 读取资源时，缓存到内存的字节大小，默认是1024
                .bufferSize(bufferMemory)
                // 打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
                .open(fileIo)) {
            // 获取数据总量
            Iterator<Sheet> iterator = workbook.sheetIterator();
            while (iterator.hasNext()) {
                Sheet sheet = iterator.next();
                count += sheet.getLastRowNum();
            }
            AmountRedis.refreshCount(null, batch, count);
            // 进度刷新步长
            stepSize = StepUtils.getStepSize(count);
            long start = System.nanoTime();
            Assert.notNull(template, HimpBootConstants.ErrorCode.LOCAL_TEMPLATE_NOT_EXISTS);
            Assert.notEmpty(template.getTemplatePageList(), HimpBootConstants.ErrorCode.TEMPLATE_PAGE_NOT_EXISTS);
            logger.debug("<<<<<<<<<<<<<<<     batch {} start     >>>>>>>>>>>>>>>", batch);
            template.getTemplatePageList().forEach(templatePage ->
                    readSheet(templatePage.getSheetIndex(), workbook.getSheetAt(templatePage.getSheetIndex()), templatePage.getTemplateColumnList(), templatePage.getStartLine())
            );
            logger.debug("<<<<<<<<<<<<<<<     batch {} upload success , time consuming : {}     >>>>>>>>>>>>>>>>", batch, System.nanoTime() - start);
        } catch (Exception e) {
            logger.error("<<<<<<<<<<<<<<<     batch {} upload failed     >>>>>>>>>>>>>>>", batch);
            logger.error("upload failed", e);
        } finally {
            // 更新状态
            Import imported = importRepository.selectOne(new Import().setBatch(batch));
            importRepository.updateOptional(
                    imported.setStatus(HimpBootConstants.ImportStatus.UPLOADED),
                    Import.FIELD_STATUS);
            // 清除进度缓存
            AmountRedis.clear(null, batch);
        }
    }

    private void readSheet(int sheetIndex, Sheet sheet, List<TemplateColumn> templateColumnList, Integer startLine) {
        List<ImportData> dataList = new ArrayList<>();
        templateColumnList = templateColumnList.stream().sorted(Comparator.comparing(TemplateColumn::getColumnIndex)).collect(Collectors.toList());
        Map<Integer, TemplateColumn> columnTemplateColumnMap = new HashMap<>(16);
        for (TemplateColumn column : templateColumnList) {
            columnTemplateColumnMap.put(column.getColumnIndex(), column);
        }
        // 默认起始行为1
        int starterLine = startLine - 1;
        for (Row row : sheet) {
            if (row.getRowNum() < starterLine) {
                continue;
            }
            ImportData importData = new ImportData().setDataStatus(DataStatus.NEW);
            String rowJson = readDataRow(row, columnTemplateColumnMap, importData);
            if (!rowJson.equals(HimpBootConstants.EMPTY_JSON)) {
                importData.setBatch(batch)
                        .setSheetIndex(sheetIndex)
                        .setTemplateCode(template.getTemplateCode())
                        .setData(rowJson);
                dataList.add(importData);
            }
            // 步长整数倍刷新进度
            if (CollectionUtils.isNotEmpty(dataList) && dataList.size() % stepSize == 0) {
                AmountRedis.refreshReady(null, batch, stepSize);
            }
            // 每次到达阈值进行存储，重置list
            if (dataList.size() == batchNumber) {
                saveImportData(dataList);
            }
        }
        // 存储未到阈值的最后一批数据
        saveImportData(dataList);
    }

    private void saveImportData(List<ImportData> dataList) {
        dataRepository.batchInsertSelective(dataList);
        // 更新数量
        Import imported = importRepository.selectOne(new Import().setBatch(batch));
        int dataCount = imported.getDataCount() + dataList.size();
        importRepository.updateOptional(imported.setDataCount(dataCount), Import.FIELD_DATA_COUNT);
        dataList.clear();
        // 刷新进度缓存
        AmountRedis.refresh(null, batch, dataCount);
    }

    private String readDataRow(Row dataRow, Map<Integer, TemplateColumn> columnTemplateColumnMap, ImportData importData) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Map<String, String>> tls = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        for (int cellIndex = dataRow.getFirstCellNum(); cellIndex <= dataRow.getLastCellNum(); ++cellIndex) {
            if (!columnTemplateColumnMap.containsKey(cellIndex)) {
                continue;
            }
            TemplateColumn templateColumn = columnTemplateColumnMap.get(cellIndex);
            String value = readDataCell(dataRow.getCell(cellIndex), templateColumn, importData);
            if (value == null) {
                continue;
            }
            String columnCode = templateColumn.getColumnCode();
            if (HimpBootConstants.ColumnType.MULTI.equals(templateColumn.getColumnType())) {
                String lang = columnCode.substring(columnCode.lastIndexOf(BaseConstants.Symbol.COLON) + 1);
                columnCode = columnCode.substring(0, columnCode.lastIndexOf(BaseConstants.Symbol.COLON));
                tls.computeIfAbsent(columnCode, k -> new HashMap<>(BaseConstants.Digital.SIXTEEN)).put(lang, value);
            }
            if (!jsonObject.containsKey(columnCode)) {
                jsonObject.put(columnCode, value);
            }
        }
        if (!tls.isEmpty()) {
            jsonObject.put(HimpBootConstants.TLS, tls);
        }
        return jsonObject.toJSONString();
    }

    private String readDataCell(Cell cell, TemplateColumn templateColumn, ImportData importData) {
        try {
            return ExcelReader.readDataCell(cell, templateColumn.toColumn());
        } catch (CommonException ce) {
            logger.error("exception", ce);
            importData.setDataStatus(DataStatus.ERROR);
            importData.addErrorMsg(MessageAccessor.getMessage(ce.getCode(), ce.getParameters()).desc());
        } catch (Exception e) {
            logger.error("exception", e);
            importData.setDataStatus(DataStatus.ERROR);
            importData.addErrorMsg(StringEscapeUtils.escapeJavaScript(ExceptionUtils.getMessage(e)));
        }
        return null;
    }
}
