package org.hzero.report.infra.constant;

import org.hzero.jdbc.constant.QueryConstants;

/**
 * 报表常量类
 *
 * @author xianzhi.chen@hand-china.com 2018年11月20日下午6:57:54
 */
public class HrptConstants {

    private HrptConstants() {
    }

    public static final String TEMPLATE_DIR = "hrpt01";
    public static final String REPORT_DIR = "hrpt02";

    public static final String CONFIG_PATH = "config";
    public static final String TTF_NAME = "SIMHEI.TTF";
    public static final String CFG_NAME = "xdo.cfg";


    public static final String PREDEFINED = "hrpt.info.predefined";
    public static final String CUSTOMIZE = "hrpt.info.customize";

    public static final String QR_CODE_URL = "QR_CODE_URL";

    /**
     * 数据源常量
     */
    public static final class Datasource {

        /**
         * 查询器模板
         */
        public static final String QUERYER_TEMPLATE = "org.hzero.report.infra.engine.query.%sQueryer";
    }

    /**
     * path分隔符
     */
    public static final String SEPARATOR = "$";

    /**
     * 报表
     */
    public static final String REPORT = "report";
    /**
     * 报表数据
     */
    public static final String REPORT_DATA = "reportData";
    /**
     * HTML信息
     */
    public static final String HTML_TABLE = "htmlTable";
    /**
     * 报表消息
     */
    public static final String REPORT_MSG = "reportMsg";
    /**
     * 标签信息
     */
    public static final String LABEL = "label";
    /**
     * 异步执行阈值条数
     */
    public static final Long ASYNC_THRESHOLD_VALUE = 10000L;

    public static final String NULL = "null";

    public static final String DATA = "DATA";

    /**
     * 固定参数
     */
    public static final class FixedParam {
        private FixedParam() {
        }

        /**
         * 前缀
         */
        public static final String PREFIX = "f-";
        /**
         * 统计列
         */
        public static final String STAT_COLUMNS = "f-statColumns";
        /**
         * 合并行标识 true|false
         */
        public static final String IS_ROW_SPAN = "f-isRowSpan";
        /**
         * 模板代码
         */
        public static final String TEMPLATE_CODE = "f-templateCode";
        /**
         * 模板语言
         */
        public static final String LANG = "f-lang";
        /**
         * 页码
         */
        public static final String PAGE = "f-page";
        /**
         * 页大小
         */
        public static final String SIZE = "f-size";
    }

    /**
     * 权限参数常量类
     */
    public static final class PermissionParam {
        private PermissionParam() {
        }

        /**
         * 前缀
         */
        public static final String PREFIX = "u-";
        /**
         * 用户 ID
         */
        public static final String USER_ID = "userId";
        /**
         * 会话语言
         */
        public static final String LANGUAGE = "language";
        /**
         * 角色ID
         */
        public static final String ROLE_ID = "roleId";
        /**
         * 可访问角色集合
         */
        public static final String ROLE_IDS = "roleIds";
        /**
         * 当前租户ID
         */
        public static final String TENANT_ID = "tenantId";
        /**
         * 可访问租户集合
         */
        public static final String TENANT_IDS = "tenantIds";
        /**
         * 所属租户ID
         */
        public static final String ORGANIZATION_ID = "organizationId";
        /**
         * 报表时间
         */
        public static final String DATE = "date";
        /**
         * mysql时区
         */
        public static final String MYSQL_TIMEZONE = "mysql-timezone";

    }

    /**
     * 数据集信息参数常量类
     */
    public static final class DataSetParam {
        private DataSetParam() {
        }

        /**
         * 前缀
         */
        public static final String PREFIX = "dataset-";
        public static final String DATASET_CODE = "code";
        public static final String DATASET_TENANT = "tenantId";
    }

    /**
     * 布局类型
     */
    public static final class LayoutType {
        private LayoutType() {
        }

        /**
         * 横向布局
         */
        public static final String HORIZONTAL = "H";
        /**
         * 纵向布局
         */
        public static final String VERTICAL = "V";
    }

    /**
     * 参数数据来源
     */
    public static final class ParamDataSource {
        private ParamDataSource() {
        }

        /**
         * SQL语句
         */
        public static final String SQL = "sql";
        /**
         * 文本字符串
         */
        public static final String TEXT = "text";
        /**
         * 无内容
         */
        public static final String NONE = "none";
    }

    /**
     * 参数表单组件
     */
    public static final class ParamFormElement {
        private ParamFormElement() {
        }

        /**
         * 下拉单选
         */
        public static final String SELECT = "select";
        /**
         * 下拉多选
         */
        public static final String SELECT_MUL = "selectMul";
        /**
         * 单选框
         */
        public static final String RADIO = "radio";
        /**
         * 复选框
         */
        public static final String CHECKBOX = "checkbox";
        /**
         * 文本框
         */
        public static final String TEXT = "text";
        /**
         * 多值文本框
         */
        public static final String TEXT_MUL = "textMul";
        /**
         * 日期
         */
        public static final String DATE = "date";
        /**
         * 日期时间
         */
        public static final String DATE_TIME = "datetime";
        /**
         * 值列表
         */
        public static final String LOV = "lov";
        /**
         * 数字输入框
         */
        public static final String INPUT_NUMBER = "number";
    }

    /**
     * 参数数据类型
     */
    public static final class ParamDataType {
        private ParamDataType() {
        }

        /**
         * 字符串
         */
        public static final String STRING = "String";
        /**
         * 浮点数
         */
        public static final String FLOAT = "float";
        /**
         * 整数
         */
        public static final String INTEGER = "integer";
        /**
         * 日期
         */
        public static final String DATE = "date";
    }

    /**
     * 报表列类型
     */
    public static final class ColumnType {
        private ColumnType() {
        }

        /**
         * 布局列（上侧索引）
         */
        public static final String LAYOUT = "L";
        /**
         * 维度列（左侧索引）
         */
        public static final String DIMENSION = "D";
        /**
         * 统计列（数值）
         */
        public static final String STATISTICAL = "S";
        /**
         * 计算列（自定义数值）
         */
        public static final String COMPUTED = "C";
    }

    /**
     * 报表列排序类型
     */
    public static final class ColumnSortType {
        private ColumnSortType() {
        }

        /**
         * 默认
         */
        public static final String DEFAULT = "D";
        /**
         * 数字优先升序
         */
        public static final String DIGIT_ASC = "DA";
        /**
         * 数字优先降序
         */
        public static final String DIGIT_DESC = "DD";
        /**
         * 字符优先升序
         */
        public static final String CHAR_ASC = "CA";
        /**
         * 字符优先降序
         */
        public static final String CHAR_DESC = "CD";
    }

    /**
     * 报表列数据类型
     */
    public static final class ColumnDataType {
        private ColumnDataType() {
        }

        /**
         * 日期
         */
        public static final String DATE = "DATE";
        /**
         * 字符串
         */
        public static final String VARCHAR = "VARCHAR";
        /**
         * 数值
         */
        public static final String DECIMAL = "DECIMAL";
    }

    /**
     * 表格报表数据
     */
    public static final class TableData {
        private TableData() {
        }

        /**
         * 元数据行数量
         */
        public static final String META_DATA_ROW_COUNT = "metaDataRowCount";
        /**
         * 元数据列数量
         */
        public static final String META_DATA_COLUMN_COUNT = "metaDataColumnCount";
        /**
         * 报表数据分页行数
         */
        public static final String META_DATA_PAGE_SIZE = "metaDataPageSize";
        /**
         * 报表数据行总条数
         */
        public static final String META_DATA_ROW_TOTAL = "metaDataRowTotal";

    }

    /**
     * 图形报表数据
     */
    public static final class ChartData {
        private ChartData() {
        }

        /**
         * 维度列MAP
         */
        public static final String DIM_COLUMN_MAP = "dimColumnMap";
        /**
         * 维度列
         */
        public static final String DIM_COLUMNS = "dimColumns";
        /**
         * 统计列
         */
        public static final String STAT_COLUMNS = "statColumns";
        /**
         * 数据行
         */
        public static final String DATA_ROWS = "dataRows";

    }

    /**
     * 模板报表数据
     */
    public static final class DocumentData {
        private DocumentData() {
        }

        /**
         * DOCX
         */
        public static final String OUTPUT_FORMAT_DOCX = "DOCX";
        /**
         * XLS
         */
        public static final String OUTPUT_FORMAT_XLS = "XLS";
        /**
         * XLSX
         */
        public static final String OUTPUT_FORMAT_XLSX = "XLSX";
        /**
         * PPTX
         */
        public static final String OUTPUT_FORMAT_PPTX = "PPTX";
        /**
         * PDF
         */
        public static final String OUTPUT_FORMAT_PDF = "PDF";
        /**
         * HTML
         */
        public static final String OUTPUT_FORMAT_HTML = "HTML";
        /**
         * CSV
         */
        public static final String OUTPUT_FORMAT_CSV = "CSV";
        /**
         * 在线打印
         */
        public static final String ONLINE_PRINT = "PRINT";

    }

    public static final class TemplateType {
        private TemplateType() {
        }

        public static final String RTF = "rtf";
        public static final String EXCEL = "xls";
        public static final String HTML = "html";
        public static final String DOC = "doc";
    }

    /**
     * 数据XML结构属性
     */
    public static final class DataXmlAttr {
        private DataXmlAttr() {
        }

        /**
         * 默认数据源
         */
        public static final String DEFAULT_DS = QueryConstants.DataXmlAttr.DEFAULT_DS;
        /**
         * 默认数据行
         */
        public static final String DEFAULT_ROW = "ROW";

    }

    /**
     * 报表请求状态
     */
    public static final class RequestStatus {
        private RequestStatus() {
        }

        /**
         * 运行
         */
        public static final String STATUS_R = "R";
        /**
         * 完成
         */
        public static final String STATUS_F = "F";
        /**
         * 警告
         */
        public static final String STATUS_W = "W";
        /**
         * 错误
         */
        public static final String STATUS_E = "E";
    }

    /**
     * 数据集类型
     */
    public static final class DataSetType {
        private DataSetType() {
        }

        /**
         * 标准sql
         */
        public static final String TYPE_S = "S";
        /**
         * 脚本sql
         */
        public static final String TYPE_C = "C";
        /**
         * API数据
         */
        public static final String TYPE_A = "A";
    }

    public static final class XmlData {
        private XmlData() {
        }

        public static final String HTTP = "http";
    }

    /**
     * 调度服务参数
     */
    public static final class Scheduler {
        private Scheduler() {
        }

        public static final String REPORT_UUID = "reportUuid";
        public static final String FORM_PARAMS = "formParams";
        public static final String END_EMAIL = "endEmail";

        public static final String SUCCESS_TEMPLATE_CODE = "HRPT.JOB_SUCCESS";
        public static final String FAILED_TEMPLATE_CODE = "HRPT.JOB_FAILED";
    }

    public static final class FontPath {
        private FontPath() {
        }

        /**
         * 分隔符
         */
        public static final String SEPARATOR = "\\|";

        public static final String DEFAULT_FONT_NAME = "黑体";
        public static final String DEFAULT_FONT_NAME_EN = "simhei";
        public static final String FONT_XML = "fontXml";
        public static final String FONT_NAME = "fontName";
        public static final String FONT_PATH = "fontPath";

        public static final String XML = "\n" +
                "         <font family=\"" + FONT_NAME + "\" style=\"normal\" weight=\"normal\">\n" +
                "         <truetype path=\"" + FONT_PATH + "\"/>\n" +
                "         </font>";


    }

    /**
     * 标签属性
     */
    public static final class LabelAttribute {
        private LabelAttribute() {
        }

        /**
         * 参数编码
         */
        public static final String DATA_CODE = "data-code";

        /**
         * 标签类型
         */
        public static final String DATA_TYPE = "data-type";

        /**
         * 属性值
         */
        public static final String DATA_VALUE = "data-value";

        /**
         * 宽
         */
        public static final String DATA_WIDTH = "data-width";

        /**
         * 高
         */
        public static final String DATA_HEIGHT = "data-height";

        /**
         * data-src
         */
        public static final String DATA_SRC = "data-src";

        /**
         * data-style
         */
        public static final String DATA_STYLE = "data-style";

        /**
         * src
         */
        public static final String SRC = "src";

        /**
         * 宽
         */
        public static final String WIDTH = "width:";

        /**
         * 高
         */
        public static final String HEIGHT = "height:";

        /**
         * style
         */
        public static final String STYLE = "style";

        /**
         * 画布
         */
        public static final String CANVAS = "canvas";

        /**
         * base64图片png格式src前缀
         */
        public static final String CODE_SRC_SUFFIX = "data:image/png;base64,";

        /**
         * base64图片src前缀
         */
        public static final String BASE64_SRC_SUFFIX = "data:image/";

        /**
         * 默认宽度
         */
        public static final Long DEFAULT_WIDTH = 210L;

        /**
         * 默认高度
         */
        public static final Long DEFAULT_HEIGHT = 297L;

        public static final String DEFAULT_DIV_STYLE = ";overflow: hidden; display: flex;flex-wrap: wrap; justify-content:space-evenly; align-items: flex-start; align-content: flex-start; width:";

        public static final String DEFAULT_MARGIN_TOP = ";margin-top:10mm";

        public static final String DEFAULT_PADDING_TOP = ";padding-top:10mm";

        public static final String PADDING_TOP_PREFIX = ";padding-top:";

        public static final String MARGIN_TOP_PREFIX = ";margin-top:";

        public static final String MM = "mm";

        public static final String DISPLAY_FLEX = "display:flex";

        public static final String DISPLAY_INLINE_TABLE = ";display:inline-table";

        public static final String PADDING_LEFT_PREFIX = "padding-left:";

        public static final String MARGIN_LEFT_PREFIX = "margin-left:";

        public static final String PADDING_RIGHT_PREFIX = ";padding-right:";

        public static final String MARGIN_RIGHT_PREFIX = ";margin-right:";

        public static final String PADDING_BOTTOM_PREFIX = ";padding-bottom:";

        public static final String MARGIN_BOTTOM_PREFIX = ";margin-bottom:";

        public static final String PAGE_BREAK_AFTER = "page-break-after: always;";

    }

    public static final class HtmlData {
        private HtmlData() {
        }

        public static final String NEW_LINE = "<br />";

        public static final String SLASH = "/";

        public static final String IMG = "img";

        public static final String BR = "br";

        public static final String MM = "mm;";

        public static final String DIV = "div";

        public static final String BODY_PREFIX = "<body style=\"font-family:SimHei\">";

        public static final String BODY_SUFFIX = "</body>";

    }

    /**
     * 图片类型
     */
    public static final class ImageType {
        private ImageType() {
        }

        public static final String PNG = "PNG";

        public static final String JPEG = "JPEG";
    }

    public static final class LabelPrintSetting {
        private LabelPrintSetting() {
        }

        /**
         * 横向
         */
        public static final Integer HORIZONTAL = 0;

        /**
         * 竖向
         */
        public static final Integer VERTICAL = 1;

        public static final Long DEFAULT_MARGIN_TOP = 10L;
        public static final Long DEFAULT_MARGIN_BOTTOM = 10L;
        public static final Long DEFAULT_MARGIN_LEFT = 10L;
        public static final Long DEFAULT_MARGIN_RIGHT = 10L;
        public static final Long DEFAULT_WITH_QTY = 1L;
    }

    /**
     * 默认纸张
     */
    public static final String DEFAULT_PAPER = "A4";

    public static final class Sides {
        private Sides() {
        }

        /**
         * 单面
         */
        public static final String ONE_SIDED = "ONE_SIDED";
        /**
         * 双面长边
         */
        public static final String TWO_SIDED_LONG_EDGE = "TWO_SIDED_LONG_EDGE";
        /**
         * 双面短边
         */
        public static final String TWO_SIDED_SHORT_EDGE = "TWO_SIDED_SHORT_EDGE";
    }

}
