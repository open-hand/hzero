package org.hzero.starter.file.constant;

/**
 * 错误码常量
 *
 * @author xianzhi.chen@hand-china.com 2019年1月17日上午10:54:05
 */
public class FileMessageConstant {

    private FileMessageConstant() {
    }


    /**
     * 文件上传失败,请检查配置信息
     */
    public static final String ERROR_FILE_UPDATE = "hfle.error.file.upload";
    /**
     * 下载文件失败
     */
    public static final String ERROR_DOWNLOAD_FILE = "hfle.error.download.file";
    /**
     * 删除文件失败
     */
    public static final String ERROR_DELETE_FILE = "hfle.error.delete.file";
    /**
     * 桶不存在
     */
    public static final String BUCKET_NOT_EXISTS = "hfle.error.bucket_not_exists";
}
