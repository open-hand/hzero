package org.hzero.file.infra.constant;

import java.io.File;
import java.util.Arrays;

/**
 * 文件服务常量类
 *
 * @author shuangfei.zhu@hand-chian.com 2018/09/20 11:44
 */
public class HfleConstant {
    private HfleConstant() {
    }

    public static final String ROOT = System.getProperty("user.dir") + File.separator + "file";
    public static final String TEMP = "temp";
    public static final String REAL = "real";

    /**
     * 默认附件UUID
     */
    public static final String DEFAULT_ATTACHMENT_UUID = "$";
    /**
     * 默认字符集
     */
    public static final String DEFAULT_CHARACTER_SET = "UTF-8";
    /**
     * 默认拼接符号
     */
    public static final String DEFAULT_SPLIT_SYMBOL = "@";
    /**
     * 默认Multi上传文件类型
     */
    public static final String DEFAULT_MULTI_TYPE = "application/octet-stream";
    /**
     * 默认文件分隔符
     */
    public static final String DIRECTORY_SEPARATOR = "/";

    public static final String LINUX_SEPARATOR = "/";
    public static final String WINDOWS_SEPARATOR = "\\";
    /**
     * 上传目录禁止出现的字符
     */
    private static final String[] FORBIDDEN_SYMBOLS = {"\\", ":", "*", "?", "\"", "<", ">", "|"};

    /**
     * KB/MB 进率
     */
    public static final Integer ENTERING = 1024;

    /**
     * 匹配小写中划线的正则
     */
    public static final String LOW_STRIKE = "^[a-z0-9-]*$";

    /**
     * 数字
     */
    public static final class Digital {
        private Digital() {
        }

        public static final Integer SIX = 6;
        public static final Integer FOUR_HUNDRED_AND_EIGHTY = 480;

    }

    /**
     * 值集编码
     */
    public static final class Lov {
        private Lov() {
        }

        public static final String CONTENT_TYPE = "HFLE.CONTENT_TYPE";
    }

    /**
     * 文件上传配置列表接口，转译对象
     */
    public static final String BODY_LIST_CONFIG = "body.listConfig";

    /**
     * 存储单位
     */
    public static final class StorageUnit {
        private StorageUnit() {
        }

        public static final String KB = "KB";
        public static final String MB = "MB";
    }

    /**
     * 文件类型
     */
    public static final class ContentType {
        private ContentType() {
        }

        public static final String LOV = "HFLE.CONTENT_TYPE";
        public static final String APPLICATION = "application";
        public static final String AUDIO = "audio";
        public static final String VIDEO = "video";
        public static final String IMAGE = "image";
        public static final String TEXT = "text";
    }

    public static final class PreviewFileType {
        private PreviewFileType() {
        }

        /**
         * 图片
         */
        public static final String IMAGE = "image";
        /**
         * PDF
         */
        public static final String PDF = "pdf";
    }

    /**
     * 文件名前缀策略
     */
    public static final class PrefixStrategy {

        private PrefixStrategy() {
        }

        /**
         * uuid
         */
        public static final String UUID = "uuid";
        /**
         * 目录
         */
        public static final String FOLDER = "folder";
        /**
         * 不使用前缀
         */
        public static final String NONE = "none";
    }

    /**
     * 服务器上传类型
     */
    public static final class SourceType {
        private SourceType() {
        }

        public static final String CODE = "HFLE.SERVER.SOURCE_TYPE";
        public static final String SERVER = "S";
        public static final String CLUSTER = "C";
    }

    public static final class Protocol {
        private Protocol() {
        }

        public static final String FTP = "FTP";
        public static final String SFTP = "SFTP";
        public static final String CPT = "CPT";
    }

    public static final class PreviewType {
        private PreviewType() {
        }

        public static final String AS = "aspose";
        public static final String KK = "kkFileView";
        public static final String ON = "onlyOffice";
    }

    public static String[] getForbiddenSymbols() {
        return Arrays.copyOf(FORBIDDEN_SYMBOLS, FORBIDDEN_SYMBOLS.length);
    }

    /**
     * 水印类型
     */
    public static final class WatermarkType {
        private WatermarkType() {
        }

        public static final String TEXT = "TEXT";
        public static final String IMAGE = "IMAGE";
        public static final String TILE_TEXT = "TILE_TEXT";
        public static final String TILE_IMAGE = "TILE_IMAGE";
    }

    public static final String WATERMARK_DIRECTORY = "hfle01";

    public static final class Color {
        private Color() {
        }

        public static final String WHITE = "WHITE";
        public static final String BLACK = "BLACK";
        public static final String RED = "RED";
        public static final String ORANGE = "ORANGE";
        public static final String YELLOW = "YELLOW";
        public static final String GREEN = "GREEN";
        public static final String BLUE = "BLUE";
        public static final String PURPLE = "PURPLE";
        public static final String BROWN = "BROWN";
    }
}
