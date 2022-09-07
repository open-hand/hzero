package org.hzero.report.domain.service;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.bstek.ureport.export.ExportManager;
import com.bstek.ureport.provider.report.ReportProvider;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.infra.meta.form.FormElement;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/08/04 19:50
 */
public interface IUreportService {

    /**
     * 获取平台默认的ureport存储类
     *
     * @return ureport存储类
     */
    ReportProvider getReportProvider();

    /**
     * 获取报表生成驱动类
     *
     * @return 报表生成驱动类
     */
    ExportManager getExportManager();

    /**
     * 初始化ureport报表文件
     *
     * @param reportCode 报表编码
     */
    void initReportFile(String reportCode);

    /**
     * 移除ureport报表文件
     *
     * @param reportCode 报表编码
     */
    void clearReportFile(String reportCode);

    /**
     * ureport编辑器页面
     *
     * @param request  request
     * @param response response
     * @throws IOException IOException
     */
    void designer(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 生成预览页面
     *
     * @param report  报表数据
     * @param data    报表数据
     * @param request request
     */
    void previewReport(Report report, JSONObject data, HttpServletRequest request);

    /**
     * 导出报表文件
     *
     * @param report     报表
     * @param outputType 导出类型
     * @param request    request
     * @param response   response
     * @throws IOException IOException
     */
    void exportReport(Report report, String outputType, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 报表查询表单
     *
     * @param report 报表
     * @return 表单参数
     */
    List<FormElement> getForm(Report report);
}
