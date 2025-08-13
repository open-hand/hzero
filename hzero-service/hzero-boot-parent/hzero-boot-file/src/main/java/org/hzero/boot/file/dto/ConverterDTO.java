package org.hzero.boot.file.dto;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/22 11:48
 */
public class ConverterDTO {


    private String fileUrl;
    private Integer percent;
    private Boolean endConvert;

    public String getFileUrl() {
        return fileUrl;
    }

    public ConverterDTO setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public Integer getPercent() {
        return percent;
    }

    public ConverterDTO setPercent(Integer percent) {
        this.percent = percent;
        return this;
    }

    public Boolean getEndConvert() {
        return endConvert;
    }

    public ConverterDTO setEndConvert(Boolean endConvert) {
        this.endConvert = endConvert;
        return this;
    }

    @Override
    public String toString() {
        return "ConverterDTO{" +
                "fileUrl='" + fileUrl + '\'' +
                ", percent=" + percent +
                ", endConvert=" + endConvert +
                '}';
    }
}
