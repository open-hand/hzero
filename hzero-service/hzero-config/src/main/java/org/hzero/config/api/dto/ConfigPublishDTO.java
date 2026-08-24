package org.hzero.config.api.dto;

/**
 * @author XCXCXCXCX
 * @date 2020/5/11 1:36 下午
 */
public class ConfigPublishDTO {

    private String serviceName;
    private String label;
    private String fileType;
    private String content;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ConfigPublishDTO{" +
                "serviceName='" + serviceName + '\'' +
                ", label='" + label + '\'' +
                ", fileType='" + fileType + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
