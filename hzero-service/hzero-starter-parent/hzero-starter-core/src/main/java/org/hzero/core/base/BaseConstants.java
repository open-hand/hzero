package org.hzero.core.base;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Locale;

/**
 * 基础常量
 *
 * @author jiangzhou.bo@hand-china.com 2018年6月4日下午3:23:23
 */
public interface BaseConstants {

    /**
     * 默认租户ID
     */
    Long DEFAULT_TENANT_ID = 0L;

    /**
     * 匿名用户ID
     */
    Long ANONYMOUS_USER_ID = 0L;

    /**
     * 匿名用户名
     */
    String ANONYMOUS_USER_NAME = "ANONYMOUS";

    /**
     * 默认页码
     */
    String PAGE = "0";
    /**
     * 默认页面大小
     */
    String SIZE = "10";

    /**
     * 默认页码字段名
     */
    String PAGE_FIELD_NAME = "page";
    /**
     * 默认页面大小字段名
     */
    String SIZE_FIELD_NAME = "size";

    /**
     * -1
     */
    int NEGATIVE_ONE = -1;


    /**
     * 默认页码
     */
    int PAGE_NUM = 0;
    /**
     * 默认页面大小
     */
    int PAGE_SIZE = 10;

    /**
     * body
     */
    String FIELD_BODY = "body";
    /**
     * KEY content
     */
    String FIELD_CONTENT = "content";

    /**
     * 默认语言
     */
    Locale DEFAULT_LOCALE = Locale.CHINA;

    /**
     * 默认语言
     */
    String DEFAULT_LOCALE_STR = Locale.CHINA.toString();

    /**
     * KEY message
     */
    String FIELD_MSG = "message";
    /**
     * KEY failed
     */
    String FIELD_FAILED = "failed";
    /**
     * KEY success
     */
    String FIELD_SUCCESS = "success";
    /**
     * KEY errorMsg
     */
    String FIELD_ERROR_MSG = "errorMsg";
    /**
     * 默认编码
     */
    String DEFAULT_CHARSET = "UTF-8";

    /**
     * 默认环境
     */
    String DEFAULT_ENV = "dev";

    ObjectMapper MAPPER = new ObjectMapper();

    /**
     * platform service
     */
    interface Platform {
        String NAME = "${hzero.service.platform:hzero-platform}";
        String CODE = "hpfm";
        Integer PORT = 8100;
        Integer REDIS_DB = 1;
    }

    /**
     * 默认国际管码
     */
    String DEFAULT_CROWN_CODE = "+86";

    /**
     * 默认时区
     */
    String DEFAULT_TIME_ZONE = "GMT+8";

    /**
     * 路径分隔符：|
     */
    String PATH_SEPARATOR = Symbol.VERTICAL_BAR;

    /**
     * 默认用户类型
     */
    String DEFAULT_USER_TYPE = "P";

    /**
     * 日期时间匹配格式
     */
    interface Pattern {
        //
        // 常规模式
        // ----------------------------------------------------------------------------------------------------
        /**
         * yyyy-MM-dd
         */
        String DATE = "yyyy-MM-dd";
        /**
         * yyyy-MM-dd HH:mm:ss
         */
        String DATETIME = "yyyy-MM-dd HH:mm:ss";
        /**
         * yyyy-MM-dd HH:mm
         */
        String DATETIME_MM = "yyyy-MM-dd HH:mm";
        /**
         * yyyy-MM-dd HH:mm:ss.SSS
         */
        String DATETIME_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
        /**
         * HH:mm
         */
        String TIME = "HH:mm";
        /**
         * HH:mm:ss
         */
        String TIME_SS = "HH:mm:ss";

        //
        // 系统时间格式
        // ----------------------------------------------------------------------------------------------------
        /**
         * yyyy/MM/dd
         */
        String SYS_DATE = "yyyy/MM/dd";
        /**
         * yyyy/MM/dd HH:mm:ss
         */
        String SYS_DATETIME = "yyyy/MM/dd HH:mm:ss";
        /**
         * yyyy/MM/dd HH:mm
         */
        String SYS_DATETIME_MM = "yyyy/MM/dd HH:mm";
        /**
         * yyyy/MM/dd HH:mm:ss.SSS
         */
        String SYS_DATETIME_SSS = "yyyy/MM/dd HH:mm:ss.SSS";

        //
        // 无连接符模式
        // ----------------------------------------------------------------------------------------------------
        /**
         * yyyyMMdd
         */
        String NONE_DATE = "yyyyMMdd";
        /**
         * yyyyMMddHHmmss
         */
        String NONE_DATETIME = "yyyyMMddHHmmss";
        /**
         * yyyyMMddHHmm
         */
        String NONE_DATETIME_MM = "yyyyMMddHHmm";
        /**
         * yyyyMMddHHmmssSSS
         */
        String NONE_DATETIME_SSS = "yyyyMMddHHmmssSSS";

        /**
         * EEE MMM dd HH:mm:ss 'CST' yyyy
         */
        String CST_DATETIME = "EEE MMM dd HH:mm:ss 'CST' yyyy";

        //
        // 数字格式
        // ------------------------------------------------------------------------------
        /**
         * 无小数位 0
         */
        String NONE_DECIMAL = "0";
        /**
         * 一位小数 0.0
         */
        String ONE_DECIMAL = "0.0";
        /**
         * 两位小数 0.00
         */
        String TWO_DECIMAL = "0.00";
        /**
         * 千分位表示 无小数 #,##0
         */
        String TB_NONE_DECIMAL = "#,##0";
        /**
         * 千分位表示 一位小数 #,##0.0
         */
        String TB_ONE_DECIMAL = "#,##0.0";
        /**
         * 千分位表示 两位小数 #,##0.00
         */
        String TB_TWO_DECIMAL = "#,##0.00";

    }

    /**
     * 1/0
     */
    interface Flag {
        /**
         * 1
         */
        Integer YES = 1;
        /**
         * 0
         */
        Integer NO = 0;
    }

    /**
     * 常用数字
     */
    interface Digital {
        int NEGATIVE_ONE = -1;
        int ZERO = 0;
        int ONE = 1;
        int TWO = 2;
        int FOUR = 4;
        int EIGHT = 8;
        int SIXTEEN = 16;
    }

    /**
     * 基础异常编码
     */
    interface ErrorCode {
        /**
         * 数据校验不通过
         */
        String DATA_INVALID = "error.data_invalid";
        /**
         * 资源不存在
         */
        String NOT_FOUND = "error.not_found";
        /**
         * 程序出现错误，请联系管理员
         */
        String ERROR = "error.error";
        /**
         * 网络异常，请稍后重试
         */
        String ERROR_NET = "error.network";
        /**
         * 记录不存在或版本不一致
         */
        String OPTIMISTIC_LOCK = "error.optimistic_lock";
        /**
         * 数据已存在，请不要重复提交
         */
        String DATA_EXISTS = "error.data_exists";
        /**
         * 数据不存在
         */
        String DATA_NOT_EXISTS = "error.data_not_exists";
        /**
         * 资源禁止访问
         */
        String FORBIDDEN = "error.forbidden";
        /**
         * 数据库异常：编码重复
         */
        String ERROR_CODE_REPEAT = "error.code_repeat";
        /**
         * 数据库异常：编号重复
         */
        String ERROR_NUMBER_REPEAT = "error.number_repeat";
        /**
         * SQL执行异常
         */
        String ERROR_SQL_EXCEPTION = "error.sql_exception";
        /**
         * 请登录后再进行操作！
         */
        String NOT_LOGIN = "error.not_login";
        /**
         * 不能为空
         */
        String NOT_NULL = "error.not_null";
        /**
         * 响应超时
         */
        String TIMEOUT = "error.timeout";
        /**
         * 服务器繁忙，请稍后重试
         */
        String SERVER_BUSY = "error.serverBusy";
    }


    interface HeaderParam {

        /**
         * header传输的参数统一前缀
         */
        String REQUEST_HEADER_PARAM_PREFIX = "param-";
    }

    /**
     * 符号常量
     */
    interface Symbol {
        /**
         * 感叹号：!
         */
        String SIGH = "!";
        /**
         * 符号：@
         */
        String AT = "@";
        /**
         * 井号：#
         */
        String WELL = "#";
        /**
         * 美元符：$
         */
        String DOLLAR = "$";
        /**
         * 人民币符号：￥
         */
        String RMB = "￥";
        /**
         * 空格：
         */
        String SPACE = " ";
        /**
         * 换行符：\r\n
         */
        String LB = System.getProperty("line.separator");
        /**
         * 百分号：%
         */
        String PERCENTAGE = "%";
        /**
         * 符号：&amp;
         */
        String AND = "&";
        /**
         * 星号
         */
        String STAR = "*";
        /**
         * 中横线：-
         */
        String MIDDLE_LINE = "-";
        /**
         * 下划线：_
         */
        String LOWER_LINE = "_";
        /**
         * 等号：=
         */
        String EQUAL = "=";
        /**
         * 加号：+
         */
        String PLUS = "+";
        /**
         * 冒号：:
         */
        String COLON = ":";
        /**
         * 分号：;
         */
        String SEMICOLON = ";";
        /**
         * 逗号：,
         */
        String COMMA = ",";
        /**
         * 点号：.
         */
        String POINT = ".";
        /**
         * 斜杠：/
         */
        String SLASH = "/";
        /**
         * 竖杠：|
         */
        String VERTICAL_BAR = "|";
        /**
         * 双斜杠：//
         */
        String DOUBLE_SLASH = "//";
        /**
         * 反斜杠
         */
        String BACKSLASH = "\\";
        /**
         * 问号：?
         */
        String QUESTION = "?";
        /**
         * 左花括号：{
         */
        String LEFT_BIG_BRACE = "{";
        /**
         * 右花括号：}
         */
        String RIGHT_BIG_BRACE = "}";
        /**
         * 左中括号：[
         */
        String LEFT_MIDDLE_BRACE = "[";
        /**
         * 右中括号：[
         */
        String RIGHT_MIDDLE_BRACE = "]";
        /**
         * 反引号：`
         */
        String BACKQUOTE = "`";
    }
}
