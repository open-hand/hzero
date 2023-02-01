package org.hzero.export.exporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 文件导出
 *
 * @author XCXCXCXCX 2020/7/21 2:18
 */
public interface IFileExporter extends AutoCloseable {

    /**
     * 填充标题
     */
    void fillTitle();

    /**
     * 填充数据
     *
     * @param exportData 数据
     */
    void fillData(List<?> exportData);

    /**
     * 记录错误信息
     *
     * @param errorMessage 错误信息
     */
    void setError(String errorMessage);

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    String getError();

    /**
     * 获取文件流
     *
     * @return 文件流
     * @throws IOException exception
     */
    InputStream export() throws IOException;

    /**
     * 获取文件二进制数据
     *
     * @return 二进制数据
     * @throws IOException exception
     */
    byte[] exportBytes() throws IOException;

    /**
     * 导出文件
     *
     * @param outputStream 输出流
     * @param md5          文件的md5
     * @return 文件的md5值
     * @throws IOException exception
     */
    String export(ByteArrayOutputStream outputStream, boolean md5) throws IOException;

    /**
     * 获取标题
     *
     * @return 标题
     */
    String getTitle();

    /**
     * 获取文件后缀名
     *
     * @return 后缀名
     */
    String getOutputFileSuffix();
}
