package org.hzero.starter.file.constant;

/**
 * 文件服务常量类
 *
 * @author shuangfei.zhu@hand-chian.com 2018/09/20 11:44
 */
public class FileConstant {
    private FileConstant() {
    }

    /**
     * 默认文件分隔符
     */
    public static final String DIRECTORY_SEPARATOR = "/";

    /**
     * 默认Multi上传文件类型
     */
    public static final String DEFAULT_MULTI_TYPE = "application/octet-stream";

    /**
     * 代理地址桶名占位符
     */
    public static final String DOMAIN_BUCKET_NAME = "{bucketName}";

    /**
     * Minio桶权限
     */
    public static final class MinioAccessControl {
        private MinioAccessControl() {
        }

        public static final String NONE = "none";
        public static final String READ_ONLY = "read-only";
        public static final String WRITE_ONLY = "write-only";
        public static final String READ_WRITE = "read-write";
    }

    /**
     * 阿里桶权限
     */
    public static final class AliyunAccessControl {
        private AliyunAccessControl() {
        }

        public static final String DEFAULT = "default";
        public static final String PRIVATE = "private";
        public static final String PUBLIC_READ = "public-read";
        public static final String PUBLIC_READ_WRITE = "public-read-write";
    }

    /**
     * 华为桶权限
     */
    public static final class HuaweiAccessControl {
        private HuaweiAccessControl() {
        }

        public static final String PRIVATE = "private";
        public static final String PUBLIC_READ = "public-read";
        public static final String PUBLIC_READ_WRITE = "public-read-write";
    }

    /**
     * 七牛桶权限
     */
    public static final class QcloudAccessControl {
        private QcloudAccessControl() {
        }

        public static final String DEFAULT = "default";
        public static final String PRIVATE = "private";
        public static final String PUBLIC_READ = "public-read";
        public static final String PUBLIC_READ_WRITE = "public-read-write";
    }

    /**
     * 本地存储桶权限
     */
    public static final class LocalAccessController {
        private LocalAccessController() {
        }

        public static final String PUBLIC_READ_WRITE = "public-read-write";
    }

    /**
     * AWS S3和京东权限
     */
    public static final class AwsAccessControl {
        private AwsAccessControl() {
        }

        public static final String PRIVATE = "private";
        public static final String PUBLIC_READ = "public-read";
        public static final String PUBLIC_READ_WRITE = "public-read-write";
    }

    /**
     * 百度 桶权限
     */
    public static final class BaiduAccessControl {
        private BaiduAccessControl() {
        }

        public static final String PRIVATE = "private";
        public static final String PUBLIC_READ = "public-read";
        public static final String PUBLIC_READ_WRITE = "public-read-write";
    }

    /**
     * 微软 容器权限
     */
    public static final class MicrosoftControl {
        private MicrosoftControl(){
        }
        /**
         * 禁止匿名读
         */
        public static final String OFF = "off";
        /**
         * 允许匿名读(容器级别)
         */
        public static final String CONTAINER = "container";
        /**
         * 允许匿名读(blob级别)
         */
        public static final String BLOB = "blob";
    }
    public static final class Protocol {
        private Protocol(){
        }

        public static final String HTTP = "http";
        public static final String HTTPS = "https";
    }
}
