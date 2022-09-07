package org.hzero.report.domain.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.dom4j.Document;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.entity.TemplateDtl;

import com.alibaba.fastjson.JSONObject;

/**
 * 模板报表领域服务接口类
 *
 * @author xianzhi.chen@hand-china.com 2018年12月6日上午11:47:17
 */
public interface IDocumentReportService {

    /**
     * 获取报表的XML数据
     *
     * @param report     报表
     * @param formParams 参数
     * @return xml数据
     */
    Document getReportXmlData(Report report, Map<String, Object> formParams);

    /**
     * 获取报表的Map数据
     *
     * @param report     报表
     * @param formParams 参数
     * @return map数据
     */
    Map<String, Object> getReportMapData(Report report, Map<String, Object> formParams);

    /**
     * 获取报表模板
     *
     * @param report       报表
     * @param templateCode 模板编码
     * @param tenantId     租户ID
     * @param lang         语言
     * @return 模板
     */
    TemplateDtl getReportTemplate(Report report, String templateCode, Long tenantId, String lang);

    /**
     * 获取模板文件流
     *
     * @param tenantId    租户ID
     * @param templateUrl 模板url
     * @return 模板文件流
     */
    InputStream getReportTemplateFile(Long tenantId, String templateUrl);

    /**
     * 获取模板文件流
     *
     * @param tenantId    租户ID
     * @param templateUrl 模板url
     * @return 模板文件流
     */
    InputStream getReportDocTemplateFile(Long tenantId, String templateUrl);

    /**
     * 生成模版报表
     *
     * @param report      报表
     * @param templateDtl 模板
     * @param data        data
     * @param formParams  参数
     */
    void generateDocument(Report report, TemplateDtl templateDtl, JSONObject data, Map<String, Object> formParams);

    /**
     * 生成基于RTF模板的报表
     *
     * @param report      报表
     * @param templateDtl 模板
     * @param outputType  输出类型
     * @param formParams  参数
     * @return 报表输出流
     */
    OutputStream generateDocumentByRtf(Report report, TemplateDtl templateDtl, String outputType, Map<String, Object> formParams);

    /**
     * 生成基于DOC模板的报表
     *
     * @param report      报表
     * @param templateDtl 模板
     * @param outputType  输出类型
     * @param formParams  参数
     * @return 报表输出流
     */
    OutputStream generateDocumentByDoc(Report report, TemplateDtl templateDtl, String outputType, Map<String, Object> formParams);

    /**
     * 生成基于Excel模板的报表
     *
     * @param report      报表
     * @param templateDtl 模板
     * @param formParams  参数
     * @return 报表输出流
     */
    OutputStream generateDocumentByExcel(Report report, TemplateDtl templateDtl, Map<String, Object> formParams);

    /**
     * 生成基于HTML模板的报表
     *
     * @param report      报表
     * @param templateDtl 模板
     * @param outputType  输出类型
     * @param formParams  参数
     * @return 报表输出流
     */
    OutputStream generateDocumentByHtml(Report report, TemplateDtl templateDtl, String outputType, Map<String, Object> formParams);

}
