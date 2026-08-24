package org.hzero.boot.imported.infra.execute;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.boot.imported.config.ImportConfig;
import org.hzero.boot.imported.domain.entity.*;
import org.hzero.boot.imported.domain.repository.ImportDataRepository;
import org.hzero.boot.imported.domain.repository.ImportRepository;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.core.exception.CommonException;

/**
 * csv导入线程
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/09 13:49
 */
public class CsvImportExecute implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CsvImportExecute.class);
    private static final FastDateFormat DEFAULT_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private final InputStream fileIo;
    private final Template template;
    private final String batch;
    private final ImportDataRepository dataRepository;
    private final ImportRepository importRepository;
    private final ImportConfig importConfig;

    public CsvImportExecute(InputStream fileIo,
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
        this.importConfig = importConfig;
    }


    @Override
    public void run() {
        long start = System.nanoTime();
        // csv模板只取第一个sheet页的模板数据
        TemplatePage templatePage = template.getTemplatePageList().get(0);
        List<TemplateColumn> templateColumnList = templatePage.getTemplateColumnList().stream().sorted(Comparator.comparing(TemplateColumn::getColumnIndex)).collect(Collectors.toList());
        // 模板信息
        Map<Integer, TemplateColumn> templateMap = new HashMap<>(16);
        for (int i = 0; i < templateColumnList.size(); i++) {
            templateMap.put(i, templateColumnList.get(i));
        }
        try (InputStreamReader reader = new InputStreamReader(fileIo, StandardCharsets.UTF_8)) {
            List<ImportData> dataList = new ArrayList<>();
            CsvReader csvReader = new CsvReader(reader);
            // 第一行忽略
            csvReader.readHeaders();
            logger.debug("<<<<<<<<<<<<<<<     batch {} start     >>>>>>>>>>>>>>>", batch);
            int startLine = templatePage.getStartLine();
            // 读内容
            while (csvReader.readRecord()) {
                if (startLine > 2) {
                    startLine--;
                    continue;
                }
                String[] data = csvReader.getValues();
                String jsonData = buildJson(data, templateMap);
                ImportData importData = new ImportData().setDataStatus(DataStatus.NEW);
                if (!jsonData.equals(HimpBootConstants.EMPTY_JSON)) {
                    importData.setBatch(batch)
                            .setSheetIndex(0)
                            .setTemplateCode(template.getTemplateCode())
                            .setData(jsonData);
                    dataList.add(importData);
                }
                // 每次到达阈值进行存储，重置list
                if (dataList.size() == importConfig.getBatchSize()) {
                    saveImportData(dataList);
                }
            }
            // 存储未到阈值的最后一批数据
            saveImportData(dataList);
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
            try {
                fileIo.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void saveImportData(List<ImportData> dataList) {
        dataRepository.batchInsertSelective(dataList);
        // 更新数量
        Import imported = importRepository.selectOne(new Import().setBatch(batch));
        Integer oldCount = imported.getDataCount();
        importRepository.updateOptional(imported.setDataCount(oldCount + dataList.size()), Import.FIELD_DATA_COUNT);
        dataList.clear();
    }

    /**
     * 构建json
     *
     * @param data        数据
     * @param templateMap 模板
     * @return json
     */
    private String buildJson(String[] data, Map<Integer, TemplateColumn> templateMap) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Map<String, String>> tls = new HashMap<>(BaseConstants.Digital.SIXTEEN);
        for (int i = 0; i < data.length; i++) {
            TemplateColumn column = templateMap.get(i);
            String columnCode = column.getColumnCode();
            String value = readValue(data[i], column);
            if (value == null) {
                continue;
            }
            if (HimpBootConstants.ColumnType.MULTI.equals(column.getColumnType())) {
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

    /**
     * 根据数据类型判断数据是否需要拼双引号
     *
     * @param value          值
     * @param templateColumn 模板
     * @return 处理后的值
     */
    private String readValue(String value, TemplateColumn templateColumn) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        switch (templateColumn.getColumnType()) {
            case HimpBootConstants.ColumnType.DATE:
                FastDateFormat dateFormat;
                if (StringUtils.isNotBlank(templateColumn.getFormatMask())) {
                    dateFormat = FastDateFormat.getInstance(templateColumn.getFormatMask());
                } else {
                    dateFormat = DEFAULT_DATE_FORMAT;
                }
                try {
                    dateFormat.parse(value);
                    return value;
                } catch (ParseException e) {
                    throw new CommonException(HimpBootConstants.ErrorCode.DATE_FORMAT, value, dateFormat.getPattern());
                }
            case HimpBootConstants.ColumnType.LONG:
                return String.valueOf(Long.parseLong(value));
            case HimpBootConstants.ColumnType.DECIMAL:
                return String.valueOf(Double.parseDouble(value));
            case HimpBootConstants.ColumnType.STRING:
            case HimpBootConstants.ColumnType.SEQUENCE:
            case HimpBootConstants.ColumnType.MULTI:
                return value;
            default:
                return null;
        }
    }
}
