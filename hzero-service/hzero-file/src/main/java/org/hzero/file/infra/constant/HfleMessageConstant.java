package org.hzero.file.infra.constant;

/**
 * 错误码常量
 *
 * @author xianzhi.chen@hand-china.com 2019年1月17日上午10:54:05
 */
public class HfleMessageConstant {

    private HfleMessageConstant() {
    }

    /**
     * 数据已存在，请不要重复提交
     */
    public static final String ERROR_DATA_EXISTS = "hfle.error.data_exists";
    /**
     * 数据不存在
     */
    public static final String ERROR_DATA_NOT_EXISTS = "hfle.error.data_not_exists";
    /**
     * 附件文件不允许为空
     */
    public static final String ERROR_ATTACHMENT_FILE_NOT_NULL = "hfle.error.attachmentFile.notNull";
    /**
     * 区间错误
     */
    public static final String ERROR_RANGE = "hfle.error.range";
    /**
     * 获取云配置信息失败
     */
    public static final String ERROR_FILE_STORE_CONFIG = "hfle.error.file_store_config";
    /**
     * 获取文件名失败
     */
    public static final String ERROR_FILE_NAME = "hfle.error.file_name";
    /**
     * 目录不合法
     */
    public static final String ERROR_FILE_PATH = "hfle.error.file_path";
    /**
     * 不可取消默认配置
     */
    public static final String DEFAULT_CONFIG = "hfle.error.default_config";
    /**
     * 文件名长度超过限制
     */
    public static final String ERROR_FILE_NAME_TOO_LONG = "hfle.error.fileName_too_long";


    /**
     * 文件大小不能超过{0}
     */
    public static final String ERROR_FILE_SIZE = "hfle.error.file_size_error";
    /**
     * 文件大小不能小于{0}
     */
    public static final String ERROR_FILE_SIZE_SMALL = "hfle.error.file_size_error_small";
    /**
     * 最大容量不能超过{0}
     */
    public static final String ERROR_FILE_CAPACITY_SIZE = "hfle.error.file_capacity_size_error";
    /**
     * 后台配置文件错误
     */
    public static final String ERROR_YML_ERROR = "hfle.error.yml_error";
    /**
     * 租户未定义全局文件配置
     */
    public static final String ERROR_TENANT_CONFIG_NOT_SET = "hfle.error.tenant_config_not_set";
    /**
     * 文件格式不支持
     */
    public static final String ERROR_FILE_FORMAT_NOT_SITE = "hfle.error.file_format_not_site";
    /**
     * 租户剩余存储容量不足
     */
    public static final String ERROR_FILE_CAPACITY = "hfle.error.file_capacity_error";
    /**
     * 读取文件类型失败
     */
    public static final String ERROR_LOAD_FILE_TYPE = "hfle.error.load_file_type";
    /**
     * 腾讯云配置信息错误"
     */
    public static final String ERROR_QCLOUD_CONFIG = "hfle.error.qcloudConfig";
    /**
     * 获取文件url失败
     */
    public static final String ERROR_FILE_URL = "hfle.error.file.url";
    /**
     * 文件目录不应包含 \\ : * ? " < > |,分隔目录使用 /
     */
    public static final String ERROR_DIRECTORY_FORBIDDEN = "hfle.error.directory.forbidden";
    /**
     * 文件上传失败,请检查配置信息
     */
    public static final String ERROR_FILE_UPDATE = "hfle.error.file.upload";
    /**
     * 文件对象不存在
     */
    public static final String ERROR_FILE_NOT_EXISTS = "hfle.error.file.notExist";
    /**
     * 存储目录不存在
     */
    public static final String ERROR_BUCKET_NAME_NOT_EXISTS = "hfle.error.bucketName.notExist";
    /**
     * 下载文件失败
     */
    public static final String ERROR_DOWNLOAD_FILE = "hfle.error.download.file";
    /**
     * 删除文件失败
     */
    public static final String ERROR_DELETE_FILE = "hfle.error.delete.file";
    /**
     * 下载URL错误
     */
    public static final String ERROR_URL_VALUE = "hfle.error.url.value";
    /**
     * 下载URL不能为空
     */
    public static final String ERROR_URL_NOT_NULL = "hfle.error.url.notNull";
    /**
     * 文件名不合法
     */
    public static final String FILE_NAME_ERROR = "hfle.error.file_name_error";
    /**
     * 附件ID错误
     */
    public static final String FILE_UUID = "hfle.error.file_uuid";
    /**
     * 客户端初始化失败
     */
    public static final String CLIENT_INIT = "hfle.error.client_init";
    /**
     * ftp连接失败
     */
    public static final String FTP_CONNECTION = "hfle.error.ftp.connection";
    /**
     * 文件已存在
     */
    public static final String FTP_EXISTS = "hfle.error.ftp.exists";
    /**
     * 文件上传
     */
    public static final String FTP_UPLOAD = "hfle.error.ftp.upload";
    /**
     * ftp路径
     */
    public static final String FTP_PATH = "hfle.error.ftp.path";
    /**
     * 创建文件夹失败
     */
    public static final String FTP_MKDIR = "hfle.error.ftp.mkdir";
    /**
     * ftp文件删除
     */
    public static final String FTP_DELETE = "hfle.error.ftp.delete";
    /**
     * ftp文件下载
     */
    public static final String FTP_DOWNLOAD = "hfle.error.ftp.downLoad";
    /**
     * 来源类型错误
     */
    public static final String SOURCE_TYPE = "hfle.error.config.sourceType";
    /**
     * 预览失败
     */
    public static final String PREVIEW = "hfle.error.preview";
    /**
     * onlyOffice未配置
     */
    public static final String ONLY_OFFICE_CONFIG = "hfle.error.onlyOffice.config";
    /**
     * 桶不存在
     */
    public static final String BUCKET_NOT_EXISTS = "hfle.error.bucket_not_exists";
    /**
     * 文件类型错误
     */
    public static final String FILE_TYPE = "hfle.error.file_type";
    /**
     * 文件分片不存在
     */
    public static final String SLICE_NOT_EXIST = "hfle.error.slice.not_exist";
    /**
     * 不支持分片操作
     */
    public static final String SLICE_NOT_SUPPORT = "hfle.error.slice.not_support";
    /**
     * 不支持的协议类型
     */
    public static final String UNSUPPORTED_PROTOCOL = "hfle.error.unsupported_protocol";
}
