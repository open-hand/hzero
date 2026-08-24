package org.hzero.report.domain.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.hzero.boot.file.FileClient;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.entity.TemplateDtl;
import org.hzero.report.domain.repository.ReportTemplateRepository;
import org.hzero.report.domain.service.IDocumentReportService;
import org.hzero.report.domain.service.IFileChangeService;
import org.hzero.report.domain.service.IReportMetaService;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.ReportDataSource;
import org.hzero.report.infra.engine.data.ReportSqlTemplate;
import org.hzero.report.infra.engine.query.QueryerFactory;
import org.hzero.report.infra.enums.TemplateTypeEnum;
import org.hzero.report.infra.util.PdfUtils;
import org.hzero.report.infra.util.VelocityUtils;
import org.hzero.starter.report.domain.service.ITemplateReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * 模板报表领域服务实现类
 *
 * @author xianzhi.chen@hand-china.com 2018年12月6日上午11:46:52
 */
@Service
public class DocumentReportServiceImpl implements IDocumentReportService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileClient fileClient;
    private final IReportMetaService reportMetaService;
    private final IFileChangeService fileChangeService;
    private final ReportTemplateRepository reportTemplateRepository;

    @Autowired
    public DocumentReportServiceImpl(FileClient fileClient,
                                     IReportMetaService reportMetaService,
                                     IFileChangeService fileChangeService,
                                     ReportTemplateRepository reportTemplateRepository) {
        this.fileClient = fileClient;
        this.reportMetaService = reportMetaService;
        this.fileChangeService = fileChangeService;
        this.reportTemplateRepository = reportTemplateRepository;
    }

    private ITemplateReportService getTemplateReportService() {
        try {
            return ApplicationContextHelper.getContext().getBean(ITemplateReportService.class);
        } catch (NoSuchBeanDefinitionException e) {
            throw new CommonException(HrptMessageConstants.TEMPLATE_REPORT_UNAVAILABLE, e);
        }
    }


    @Override
    public void generateDocument(Report report, TemplateDtl templateDtl, JSONObject data,
                                 Map<String, Object> formParams) {
        TemplateTypeEnum templateTypeEnum = TemplateTypeEnum.valueOf2(report.getTemplateTypeCode());
        // 根据配置生成模板报表
        // excel模板报表不支持预览
        switch (templateTypeEnum) {
            case RTF:
                OutputStream os = generateDocumentByRtf(report, templateDtl, HrptConstants.DocumentData.OUTPUT_FORMAT_HTML, formParams);
                data.put(HrptConstants.HTML_TABLE, os.toString());
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error("OutputStream Close Failed!", e);
                }
                break;
            case DOC:
                OutputStream outputStream = generateDocumentByDoc(report, templateDtl, HrptConstants.DocumentData.OUTPUT_FORMAT_HTML, formParams);
                data.put(HrptConstants.HTML_TABLE, outputStream.toString());
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("OutputStream Close Failed!", e);
                }
                break;
            case HTML:
                data.put(HrptConstants.HTML_TABLE, generateDocumentByHtml(report, templateDtl.getTemplateContent(), formParams));
                break;
            default:
                break;
        }
    }

    @Override
    public OutputStream generateDocumentByRtf(Report report, TemplateDtl templateDtl, String outputType,
                                              Map<String, Object> formParams) {
        Document document = getReportXmlData(report, formParams);
        InputStream template = getReportTemplateFile(templateDtl.getTenantId(), templateDtl.getTemplateUrl());
        return getTemplateReportService().generateDocumentByRtf(document, template, outputType);
    }

    @Override
    public OutputStream generateDocumentByDoc(Report report, TemplateDtl templateDtl, String outputType,
                                              Map<String, Object> formParams) {
        Document document = getReportXmlData(report, formParams);
        InputStream template = getReportDocTemplateFile(templateDtl.getTenantId(), templateDtl.getTemplateUrl());
        return getTemplateReportService().generateDocumentByRtf(document, template, outputType);
    }

    @Override
    public OutputStream generateDocumentByExcel(Report report, TemplateDtl templateDtl,
                                                Map<String, Object> formParams) {
        Document document = getReportXmlData(report, formParams);
        InputStream template = getReportTemplateFile(templateDtl.getTenantId(), templateDtl.getTemplateUrl());
        return getTemplateReportService().generateDocumentByExcel(document, template);
    }

    @Override
    public OutputStream generateDocumentByHtml(Report report, TemplateDtl templateDtl, String outputType,
                                               Map<String, Object> formParams) {
        // 获取HTML数据
        String htmlData = generateDocumentByHtml(report, templateDtl.getTemplateContent(), formParams);
        // 绑定HTML样式
        htmlData = PdfUtils.getStyleHtml(htmlData);
        OutputStream osStream = new ByteArrayOutputStream();
        if (StringUtils.isBlank(htmlData)) {
            return osStream;
        }
        switch (outputType) {
            case HrptConstants.DocumentData.OUTPUT_FORMAT_HTML:
            case HrptConstants.DocumentData.OUTPUT_FORMAT_XLS:
                try {
                    osStream.write(htmlData.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    logger.error("Html To OutputStream error", e);
                }
                break;
            case HrptConstants.DocumentData.OUTPUT_FORMAT_PDF:
            case HrptConstants.DocumentData.ONLINE_PRINT:
                try {
                    PdfUtils.htmlToPdf(htmlData, osStream);
                } catch (IOException e) {
                    logger.error("Html To Pdf error", e);
                }
                break;
            default:
                break;
        }
        return osStream;
    }

    @Override
    public Document getReportXmlData(Report report, Map<String, Object> formParams) {
        ReportDataSource reportDataSource = this.reportMetaService.getReportDataSource(report.getTenantId(), report.getDatasourceCode());
        String sqlText = this.getSqlText(report.getSqlText(), formParams);
        String sqlType = Dataset.checkSqlType(sqlText);
        return QueryerFactory.create(reportDataSource).getMetaDataXml(sqlType, sqlText);
    }

    @Override
    public Map<String, Object> getReportMapData(Report report, Map<String, Object> formParams) {
        ReportDataSource reportDataSource = this.reportMetaService.getReportDataSource(report.getTenantId(), report.getDatasourceCode());
        String sqlText = this.getSqlText(report.getSqlText(), formParams);
        String sqlType = Dataset.checkSqlType(sqlText);
        return QueryerFactory.create(reportDataSource).getMetaDataMap(sqlType, sqlText);
    }

    @Override
    public InputStream getReportTemplateFile(Long tenantId, String templateUrl) {
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        return this.fileClient.downloadFile(tenantId, HZeroService.Report.BUCKET_NAME, templateUrl);
    }

    @Override
    public InputStream getReportDocTemplateFile(Long tenantId, String templateUrl) {
        return fileChangeService.changeToRtf(tenantId, templateUrl);
    }

    @Override
    public TemplateDtl getReportTemplate(Report report, String templateCode, Long tenantId, String lang) {
        List<TemplateDtl> templateDtls = reportTemplateRepository.selectTemplateDtls(tenantId, report.getReportId(), templateCode, lang);
        if (CollectionUtils.isEmpty(templateDtls) && !Objects.equals(tenantId, BaseConstants.DEFAULT_TENANT_ID)) {
            templateDtls = reportTemplateRepository.selectTemplateDtls(BaseConstants.DEFAULT_TENANT_ID, report.getReportId(), templateCode, lang);
        }
        return (CollectionUtils.isEmpty(templateDtls)) ? null : templateDtls.get(0);
    }

    /**
     * 获取HTML渲染数据
     */
    private String generateDocumentByHtml(Report report, String template, Map<String, Object> formParams) {
        if (StringUtils.isBlank(template)) {
            return null;
        }
        Map<String, Object> dataMap = this.getReportMapData(report, formParams);
        return VelocityUtils.parse(template, dataMap);
    }

    /**
     * 获取替换参数的SQL语句
     */
    private String getSqlText(String sqlText, Map<String, Object> formParams) {
        return new ReportSqlTemplate(sqlText, formParams).execute();
    }
}
