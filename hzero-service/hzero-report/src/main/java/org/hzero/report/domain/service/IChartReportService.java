package org.hzero.report.domain.service;

import java.util.List;
import java.util.Map;

import org.hzero.report.infra.engine.data.ReportDataSet;
import org.hzero.report.infra.meta.chart.TextValue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 图表报表服务接口
 *
 * @author xianzhi.chen@hand-china.com 2018年10月22日下午5:17:53
 */
public interface IChartReportService {

    /**
     * 获取所有非统计列去重数据集合
     *
     * @param reportDataSet 数据集
     * @return map
     */
    Map<String, List<TextValue>> getDimColumnMap(ReportDataSet reportDataSet);

    /**
     * 获取报表所有统计列(含计算列)列表
     *
     * @param reportDataSet 数据集
     * @return json
     */
    JSONArray getStatColumns(ReportDataSet reportDataSet);

    /**
     * 获取报表所有非统计列列表
     *
     * @param reportDataSet 数据集
     * @return json
     */
    JSONArray getDimColumns(ReportDataSet reportDataSet);

    /**
     * 获取报表数据行
     *
     * @param reportDataSet 数据集
     * @return map
     */
    Map<String, JSONObject> getDataRows(ReportDataSet reportDataSet);

}
