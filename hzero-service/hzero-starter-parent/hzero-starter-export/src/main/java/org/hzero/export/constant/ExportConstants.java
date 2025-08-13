package org.hzero.export.constant;

/**
 * 导出常量
 *
 * @author shuangfei.zhu@hand-china.com 2020/10/26 11:47
 */
public class ExportConstants {

    public static final String CODE_SHEET_NAME = "hzero-import-field-mapping";

    /**
     * 根节点的父级ID
     */
    public static final Long ROOT_PARENT_ID = 0L;
    /**
     * sheet页名称最大长度
     */
    public static final int SHEET_NAME_MAX_LENGTH = 31;
    /**
     * 默认分页大小
     */
    public static final int PAGE_SIZE = 5000;
    /**
     * sheet名称后缀分隔符
     */
    public static final String FILE_SUFFIX_SEPARATE = "-";

    public static final String TXT_SUFFIX = ".txt";
    public static final String ZIP_SUFFIX = ".zip";

    public static final class TemplateType {
        /**
         * 实体类
         */
        public static final String CLASS = "Class";
        /**
         * 导入模板
         */
        public static final String TEMPLATE = "Template";
    }

    public static final class FillerType {
        public static final String  SINGLE = "single-sheet";
        public static final String  MULTI = "multi-sheet";
    }
}
