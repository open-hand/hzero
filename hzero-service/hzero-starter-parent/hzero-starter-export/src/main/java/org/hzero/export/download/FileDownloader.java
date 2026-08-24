package org.hzero.export.download;

/**
 * 文件下载器
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/06 15:59
 */
public interface FileDownloader {

    /**
     * 下载文件
     */
    void download();

    /**
     * 获取文件名
     *
     * @return 文件名
     */
    String getFileName();
}
