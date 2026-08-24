package org.hzero.boot.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * 加密上传返回DTO
 * 
 * @author xianzhi.chen@hand-china.com 2019年1月31日上午11:13:52
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileSimpleDTO {

    private String md5;
    private String fileKey;
    private String fileTokenUrl;

    public String getMd5() {
        return md5;
    }

    public FileSimpleDTO setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public FileSimpleDTO setFileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public String getFileTokenUrl() {
        return fileTokenUrl;
    }

    public FileSimpleDTO setFileTokenUrl(String fileTokenUrl) {
        this.fileTokenUrl = fileTokenUrl;
        return this;
    }

    @Override
    public String toString() {
        return "FileSimpleDTO{" +
                "md5='" + md5 + '\'' +
                ", fileKey='" + fileKey + '\'' +
                ", fileTokenUrl='" + fileTokenUrl + '\'' +
                '}';
    }
}
