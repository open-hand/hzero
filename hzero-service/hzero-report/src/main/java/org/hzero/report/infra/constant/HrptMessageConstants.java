package org.hzero.report.infra.constant;

/**
 * 错误代码常量类
 *
 * @author xianzhi.chen@hand-china.com 2018年12月6日下午1:27:00
 */
public class HrptMessageConstants {

    private HrptMessageConstants() {
    }

    /**
     * 报表查询参数设置有错误,可能未设置SQL语句或SQL语句不正确，或不正确的参数设置:{0}
     */
    public static final String ERROR_REPORT_PARAMETER_SET = "hrpt.error.report.parameter.set";
    /**
     * 数据库资源释放异常
     */
    public static final String ERROR_RELEASE_JDBC_RESOURCE = "hrpt.error.release.jdbc.resource";
    /**
     * 未指定报表查询器对象!
     */
    public static final String ERROR_NOFUND_QUERYER = "hrpt.error.notfund.report.queryer";
    /**
     * 数据源不存在或已失效，请检查！
     */
    public static final String ERROR_NOFUND_DATASOURCE = "hrpt.error.notfund.datasource";
    /**
     * 数据源连接测试失败
     */
    public static final String ERROR_DS_TEST_CONNECTION = "hrpt.error.datasource.test.connection";
    /**
     * 报表生成失败
     */
    public static final String ERROR_REPORT_GENERATOR = "hrpt.error.report.generator";
    /**
     * 没有选择数据源
     */
    public static final String ERROR_NOSELECT_DATASOURCE = "hrpt.error.noselect.datasource";
    /**
     * 存在引用，不允许删除
     */
    public static final String ERROR_EXIST_REFERENCE = "hrpt.error.exist.reference";
    /**
     * 报表数据源SQL类型不匹配
     */
    public static final String ERROR_REPORT_DATASET_SQL_TYPE = "hrpt.error.report.dataset.sqlType";
    /**
     * 代码重复
     */
    public static final String ERROR_CODE_REPEAT = "hrpt.error.code.repeat";
    /**
     * 没有权限进行分配
     */
    public static final String ERROR_NEED_PRIVILEGES = "hrpt.error.need.privileges";
    /**
     * 报表不存在: {0}
     */
    public static final String ERROR_REPORT_NOT_EXIST = "hrpt.error.report.notExist";
    /**
     * 报表类型必须为单据类型
     */
    public static final String ERROR_REPORT_TYPE_DOCUMENT = "hrpt.error.report.typeMustDocment";
    /**
     * 解析XML数据失败
     */
    public static final String ERROR_PARSE_XML_DATA = "hrpt.error.parse.xmlData";
    /**
     * 模板语言重复
     */
    public static final String ERROR_TEMPLATE_LANG_REPEAT = "hrpt.error.template.lang.repeat";
    /**
     * 没找到报表中的布局列，请配置布局列!
     */
    public static final String ERROR_NOFUND_LAYOUT_COLUMN = "hrpt.error.nofund.layout.column";
    /**
     * 数据源连接池类加载错误
     */
    public static final String ERROR_POOL_FACTORY_LOAD_CLASS = "hrpt.error.poolFactory.LoadClass";
    /**
     * {0}: 数据源连接池创建错误
     */
    public static final String ERROR_DATASOURCE_POOL_CREATE = "hrpt.error.datasource.pool.create";
    /**
     * 模版不存在
     */
    public static final String ERROR_TEMPLATE_NOT_EXIST = "hrpt.error.template.notExist";
    /**
     * 模版内容不能为空
     */
    public static final String ERROR_TEMPLATE_CONTENT_NULL = "hrpt.error.template.content.null";
    /**
     * 模版文件不能为空
     */
    public static final String ERROR_TEMPLATE_FILE_NULL = "hrpt.error.template.file.null";
    /**
     * 报表生成失败，请检查报表定义设置
     */
    public static final String ERROR_REPORT_GENERATE = "hrpt.error.report.generate";
    /**
     * 模板报表，模板类型不能为空
     */
    public static final String ERROR_REPORT_TEMPLATE_TYPE = "hrpt.error.report.templateType";
    /**
     * 下载文件组装错误
     */
    public static final String ERROR_DOWNLOAD_FILE = "hrpt.error.download.file";
    /**
     * 参数名称不合规，请检查
     */
    public static final String ERROR_PARAMETER_NAME = "hrpt.error.parameter.name";
    /**
     * 异步请求未生成结果，请检查
     */
    public static final String ERROR_ASYNC_REPORT_REQUEST = "hrpt.error.async.reportRequest";
    /**
     * 由于数据量较大，已自动转换成异步请求，请到报表请求界面查询结果
     */
    public static final String INFO_ASYNC_REPORT_REQUEST = "hrpt.info.async.reportRequest";
    /**
     * 创建定时任务失败
     */
    public static final String CREATE_JOB = "hrpt.info.create_job";
    /**
     * sql异常
     */
    public static final String SQL_EXCEPTION = "hrpt.error.sql";
    /**
     * 不支持的条码类型
     */
    public static final String UNSUPPORTED_CODE_TYPE = "hrpt.unsupported.code.type";

    /**
     * 生成条码发生错误
     */
    public static final String ERROR_GENERATE_BARCODE = "hrpt.error.generate.bar-code";

    /**
     * 生成二维码发生错误
     */
    public static final String ERROR_GENERATE_QRCODE = "hrpt.error.generate.qr-code";

    /**
     * 纸张宽度不足
     */
    public static final String INSUFFICIENT_PAPER_WIDTH = "hrpt.insufficient.paper.width";

    /**
     * 纸张高度不足
     */
    public static final String INSUFFICIENT_PAPER_HEIGHT = "hrpt.insufficient.paper.height";

    /**
     * 获取标签二进制数据错误
     */
    public static final String ERROR_GET_LABEL_BYTES = "hrpt.error.get.label.bytes";
    /**
     * 打印标签时发生错误
     */
    public static final String ERROR_PRINT_LABEL = "hrpt.error.print.label";
    /**
     * 请求地址错误
     */
    public static final String REQUEST_URL = "hrpt.error.request_url";
    /**
     * 报表不存在或无权限操作
     */
    public static final String PERMISSION_NOT_PASS = "hrpt.error.report_permission.not_pass";
    /**
     * 标签无内容或者无权限
     */
    public static final String NO_CONTENT_OR_PERMISSION = "hrpt.error.label.notContentOrNoPermission";

    public static final String ASYNC_TIMEOUT = "hrpt.error.async.timeout";

    /**
     * 编码重复
     */
    public static final String ERROR_REPEAT_CODE = "hrpt.error.repeat.code";

    public static final String TEMPLATE_REPORT_UNAVAILABLE = "hrpt.error.template_report.render";

}
