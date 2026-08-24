package org.hzero.report.domain.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hzero.report.domain.service.IChartReportService;
import org.hzero.report.infra.engine.data.ReportDataCell;
import org.hzero.report.infra.engine.data.ReportDataColumn;
import org.hzero.report.infra.engine.data.ReportDataRow;
import org.hzero.report.infra.engine.data.ReportDataSet;
import org.hzero.report.infra.meta.chart.TextValue;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * 图表报表服务类
 * 
 * @author xianzhi.chen@hand-china.com 2018年10月22日下午5:42:38
 */
@Service
public class ChartReportServiceImpl implements IChartReportService {

    @Override
    public Map<String, List<TextValue>> getDimColumnMap(ReportDataSet reportDataSet) {
        Map<String, List<String>> map = reportDataSet.getUnduplicatedNonStatColumnDataMap();
        if (map.size() < 1) {
            return new HashMap<>(0);
        }

        List<ReportDataColumn> nonStatColumns = reportDataSet.getNonStatColumns();
        Map<String, List<TextValue>> dimColumnMap = new HashMap<>(nonStatColumns.size());
        for (ReportDataColumn column : nonStatColumns) {
            List<TextValue> options = new ArrayList<>(map.get(column.getName()).size() + 1);
            options.add(new TextValue("全部", "all", true));
            options.addAll(map.get(column.getName()).stream()
                            .map(columnValue -> new TextValue(columnValue, columnValue)).collect(Collectors.toList()));
            dimColumnMap.put(column.getName(), options);
        }
        return dimColumnMap;
    }

    @Override
    public JSONArray getStatColumns(ReportDataSet reportDataSet) {
        return this.getJsonArray(reportDataSet.getStatColumns());
    }

    @Override
    public JSONArray getDimColumns(ReportDataSet reportDataSet) {
        return this.getJsonArray(reportDataSet.getNonStatColumns());
    }

    @Override
    public Map<String, JSONObject> getDataRows(ReportDataSet reportDataSet) {
        Map<String, ReportDataRow> dataRows = reportDataSet.getRowMap();
        List<ReportDataColumn> statColumns = reportDataSet.getStatColumns();
        Map<String, JSONObject> rowMap = new LinkedHashMap<>(dataRows.size());
        for (Entry<String, ReportDataRow> set : dataRows.entrySet()) {
            JSONObject object = new JSONObject(statColumns.size());
            for (ReportDataColumn statColumn : statColumns) {
                ReportDataCell cell = set.getValue().getCell(statColumn.getName());
                object.put(cell.getName(), cell.getValue());
            }
            rowMap.put(set.getKey(), object);
        }
        return rowMap;
    }

    private JSONArray getJsonArray(List<ReportDataColumn> columns) {
        JSONArray jsonArray = new JSONArray(columns.size());
        for (ReportDataColumn column : columns) {
            JSONObject object = new JSONObject();
            object.put(ReportDataColumn.FILED_NAME, column.getName());
            object.put(ReportDataColumn.FILED_TEXT, column.getText());
            if(StringUtils.isNotBlank(column.geLinkReportCode()) && StringUtils.isNotBlank(column.geLinkReportParam())) {
                object.put(ReportDataColumn.FILED_LINK_REPORT_CODE, column.geLinkReportCode());
                object.put(ReportDataColumn.FILED_LINK_REPORT_PARAM, column.geLinkReportParam());
            }
            jsonArray.add(object);
        }
        return jsonArray;
    }

}
