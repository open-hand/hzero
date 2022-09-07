package org.hzero.boot.imported.infra.constant;

/**
 * @author shuangfei.zhu@hand-china.com
 */
public class HimpBootConstants {

    private HimpBootConstants() {
    }

    public static final String TEMPLATE_PAGE = "templatePage";

    public static final String DATA_ID = "dataId";

    public static final String ARGS = "args";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String EMPTY_JSON = "{}";

    public static final String IMPORT_DATA = "importData";
    /**
     * 通用导入服务端注册的模板编码
     */
    public static final String SERVICE_IMPORT = "service_import";

    /**
     * 导入行多语言
     */
    public static final String TLS = "_tls";

    /**
     * 模板列类型值集
     */
    public static final class ColumnType {
        private ColumnType() {
        }

        /**
         * 值集编码
         */
        public static final String CODE = "HIMP.TEMPLATE.COLUMNTYPE";

        /**
         * 字符串
         */
        public static final String STRING = "String";
        /**
         * 整数
         */
        public static final String LONG = "Long";
        /**
         * 浮点数
         */
        public static final String DECIMAL = "Decimal";
        /**
         * 日期
         */
        public static final String DATE = "Date";
        /**
         * 序列
         */
        public static final String SEQUENCE = "Sequence";
        /**
         * 多语言
         */
        public static final String MULTI = "Multi";
    }

    public static final class TemplateType {
        private TemplateType() {
        }

        /**
         * 值集编码
         */
        public static final String CODE = "HIMP.TEMPLATE.TEMPLATETYPE";
        /**
         * 服务端
         */
        public static final String SERVER = "S";
        /**
         * 客户端
         */
        public static final String CLIENT = "C";
    }

    public static final class Export {
        private Export() {
        }

        /**
         * 数据起始行
         */
        public static final Integer FIRST_ROW = 1;
        /**
         * 数据终止行
         * excel2007最大行号1048576
         */
        public static final Integer END_ROW = 1048575;
    }

    /**
     * 数据导入状态
     */
    public static final class ImportStatus {
        private ImportStatus() {
        }

        /**
         * 值集编码
         */
        public static final String CODE = "HIMP.IMPORT_STATUS";
        /**
         * 数据上传中
         */
        public static final String UPLOADING = "UPLOADING";
        /**
         * 数据上传完成
         */
        public static final String UPLOADED = "UPLOADED";
        /**
         * 数据校验中
         */
        public static final String CHECKING = "CHECKING";
        /**
         * 数据校验完成
         */
        public static final String CHECKED = "CHECKED";
        /**
         * 数据导入中
         */
        public static final String IMPORTING = "IMPORTING";
        /**
         * 数据导入完成
         */
        public static final String IMPORTED = "IMPORTED";
    }

    public static final class ErrorCode {
        public static final String ERROR_MORE_THAN = "himp.error.more_than";
        public static final String ERROR_FIELD = "himp.error.field";
        public static final String ERROR_LESS_THAN = "himp.error.less_than";
        public static final String ERROR_NOT_NUMBER = "himp.error.not_number";
        public static final String ERROR_NOT_DATE = "himp.error.not_date";
        public static final String ERROR_NOT_DECIMAL = "himp.error.not_decimal";
        public static final String ERROR_LENGTH_EXCEEDS_LIMIT = "himp.error.length_exceeds_limit";
        public static final String ERROR_REGULAR_MISMATCH = "himp.error.regular_mismatch";
        public static final String ERROR_LOV_MISMATCH = "himp.error.lov_mismatch";
        public static final String ERROR_NOT_NULL = "himp.error.not_null";

        private ErrorCode() {
        }

        /**
         * 模板和EXCEL不匹配，请下载最新模板
         */
        public static final String TEMPLATE_MATCH = "himp.error.template.match";

        /**
         * 文件读取失败
         */
        public static final String READ_FILE = "himp.error.readFile";

        /**
         * 参数错误
         */
        public static final String PARAM = "himp.error.param";

        /**
         * Excel为空，请填写导入数据
         */
        public static final String EXCEL_DATA = "himp.error.excel.data";

        /**
         * 导入方法未定义
         */
        public static final String IMPORT_DATA = "himp.error.import.data";

        /**
         * feign调用失败
         */
        public static final String FEIGN_TEMPLATE = "himp.error.feign.template";

        /**
         * 获取模板信息失败
         */
        public static final String GET_ATTACHMENT = "himp.error.get.contract.attachment";

        /**
         * 模板已存在
         */
        public static final String LOCAL_TEMPLATE_EXISTS = "himp.error.local_template.exists";

        /**
         * 模板不存在
         */
        public static final String LOCAL_TEMPLATE_NOT_EXISTS = "himp.error.local_template.not_exists";
        /**
         * 模板页不存在
         */
        public static final String TEMPLATE_PAGE_NOT_EXISTS = "himp.error.template.page.not_exists";
        /**
         * 不支持公式
         */
        public static final String BATCH_NOT_EXISTS = "himp.error.batch.not_exists";

        /**
         * 数据上传
         */
        public static final String UPLOADING = "himp.error.uploading";

        /**
         * 数据校验
         */
        public static final String CHECKING = "himp.error.checking";

        /**
         * 数据导入
         */
        public static final String IMPORTING = "himp.error.importing";
        /**
         * 打开Sheet页失败
         */
        public static final String OPEN_SHEET = "himp.error.openSheet";
        /**
         * 日期格式不符合
         */
        public static final String DATE_FORMAT = "himp.error.dataFormat";
        /**
         * 不支持公式
         */
        public static final String FORMULA = "himp.error.formula";
        /**
         * 单元格发生错误
         */
        public static final String CELL_ERROR = "himp.error.cell";
        /**
         * 获取数据失败
         */
        public static final String VALUE_ERROR = "himp.error.value";
        /**
         * 数据与模板不匹配
         */
        public static final String DATA_MATCH = "himp.error.data.match";
        public static final String DATA_VALIDATE = "himp.error.data.validate";
        public static final String DATA_IMPORT = "himp.error.data.import";


    }

    public static final class ExportTitle{
        private ExportTitle(){}

        public static final String DATA_STATUS = "himp.export.temp_data_status";
        public static final String FAIL_REASON = "himp.export.temp_data.failed_reason";
    }
}
