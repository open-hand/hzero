package org.hzero.report.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger Api 描述配置
 *
 * @author hzero
 */
@Configuration
public class ReportSwaggerApiConfig {

    public static final String CODE = "Code";
    public static final String DATASET = "Dataset";
    public static final String DATASET_SITE = "Dataset(Site Level)";
    public static final String TEMPLATE = "Template";
    public static final String TEMPLATE_SITE = "Template(Site Level)";
    public static final String REPORT_TEMPLATE = "Report Template";
    public static final String REPORT_TEMPLATE_SITE = "Report Template(Site Level)";
    public static final String REPORT_TEMPLATE_DTL = "Report Template Detail";
    public static final String REPORT_TEMPLATE_DTL_SITE = "Report Template Detail(Site Level)";
    public static final String REPORT_DESIGNER = "Report Designer";
    public static final String REPORT_DESIGNER_SITE = "Report Designer(Site Level)";
    public static final String REPORT = "Report";
    public static final String REPORT_SITE = "Report(Site Level)";
    public static final String REPORT_VIEW = "Report View";
    public static final String REPORT_VIEW_SITE = "Report View(Site Level)";
    public static final String REPORT_PERMISSION = "Report Permission";
    public static final String REPORT_PERMISSION_SITE = "Report Permission(Site Level)";
    public static final String REPORT_REQUEST = "Report Request";
    public static final String REPORT_REQUEST_SITE = "Report Request(Site Level)";
    public static final String LABEL_TEMPLATE = "Label Template";
    public static final String LABEL_TEMPLATE_SITE = "Label Template(Site Level)";
    public static final String LABEL_PRINT = "Label Print";
    public static final String LABEL_PRINT_SITE = "Label Print(Site Level)";
    public static final String LABEL_PERMISSION = "Label Permission";
    public static final String LABEL_PERMISSION_SITE = "Label Permission(Site Level)";
    public static final String LABEL_PARAMETER = "Label Parameter";
    public static final String LABEL_PARAMETER_SITE = "Label Parameter(Site Level)";

    @Autowired
    public ReportSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(CODE, "图形码"),
                new Tag(DATASET, "数据集"),
                new Tag(DATASET_SITE, "数据集(平台级)"),
                new Tag(TEMPLATE, "模板"),
                new Tag(TEMPLATE_SITE, "模板(平台级)"),
                new Tag(REPORT_TEMPLATE, "报表模板"),
                new Tag(REPORT_TEMPLATE_SITE, "报表模板(平台级)"),
                new Tag(REPORT_TEMPLATE_DTL, "报表模板明细"),
                new Tag(REPORT_TEMPLATE_DTL_SITE, "报表模板明细(平台级)"),
                new Tag(REPORT_DESIGNER, "报表定义 "),
                new Tag(REPORT_DESIGNER_SITE, "报表定义(平台级)"),
                new Tag(REPORT, "报表查询"),
                new Tag(REPORT_SITE, "报表查询(平台级)"),
                new Tag(REPORT_VIEW, "报表预览"),
                new Tag(REPORT_VIEW_SITE, "报表预览(平台级)"),
                new Tag(REPORT_PERMISSION, "报表权限"),
                new Tag(REPORT_PERMISSION_SITE, "报表权限(平台级)"),
                new Tag(REPORT_REQUEST, "报表请求"),
                new Tag(REPORT_REQUEST_SITE, "报表请求(平台级)"),
                new Tag(LABEL_TEMPLATE, "标签模板"),
                new Tag(LABEL_TEMPLATE_SITE, "标签模板(平台级)"),
                new Tag(LABEL_PRINT, "标签打印"),
                new Tag(LABEL_PRINT_SITE, "标签打印(平台级)"),
                new Tag(LABEL_PERMISSION, "标签权限"),
                new Tag(LABEL_PERMISSION_SITE, "标签权限(平台级)"),
                new Tag(LABEL_PARAMETER, "标签参数"),
                new Tag(LABEL_PARAMETER_SITE, "标签参数(平台级)")
        );
    }
}
