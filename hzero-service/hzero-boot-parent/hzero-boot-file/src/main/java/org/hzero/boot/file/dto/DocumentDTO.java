package org.hzero.boot.file.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 文档属性
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午4:45:27
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentDTO {

    /**
     * 文档显示的标题
     */
    private String title;
    /**
     * 文档的地址
     */
    private String url;
    /**
     * 文档的唯一标识
     */
    private String fileId;

    public String getTitle() {
        return title;
    }

    public DocumentDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DocumentDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public DocumentDTO setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    @Override
    public String toString() {
        return "Document{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", fileId='" + fileId + '\'' +
                '}';
    }
}
