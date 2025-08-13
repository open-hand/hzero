package org.hzero.file.domain.vo;

import java.io.InputStream;

/**
 * 文件值对象
 *
 * @author xianzhi.chen@hand-china.com 2019年2月15日上午10:39:24
 */
public class FileVO {

    private String fileName;
    private Long fileSize;
    private String fileType;
    private InputStream inputStream;

    public String getFileName() {
        return fileName;
    }

    public FileVO setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public FileVO setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public FileVO setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public FileVO setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    @Override
    public String toString() {
        return "FileVO{" +
                "fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", inputStream=" + inputStream +
                '}';
    }
}
