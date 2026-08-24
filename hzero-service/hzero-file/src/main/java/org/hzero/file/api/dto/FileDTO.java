package org.hzero.file.api.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * 文件返回DTO
 * 
 * @author xianzhi.chen@hand-china.com 2018年7月11日下午3:01:28
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDTO {

    private String fileUrl;
    private String fileType;
    private String fileName;
    private Long fileSize;
    private String bucketName;
    private Date creationDate;
    private Long createdBy;
    private String loginName;
    private String realName;


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "fileUrl='" + fileUrl + '\'' +
                ", fileType='" + fileType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", bucketName='" + bucketName + '\'' +
                ", creationDate=" + creationDate +
                ", createdBy=" + createdBy +
                ", loginName='" + loginName + '\'' +
                ", realName='" + realName + '\'' +
                '}';
    }
}
