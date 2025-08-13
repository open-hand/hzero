package org.hzero.boot.file.dto;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/19 16:42
 */
public class ConverterParamDTO {

    /**
     * 是否异步
     */
    private Boolean async;
    /**
     * 文件原类型
     */
    private String filetype;
    /**
     * 文件的唯一标识
     */
    private String key;
    /**
     * 生成的文件类型
     */
    private String outputtype;
    /**
     * 文件名
     */
    private String title;
    /**
     * 可访问文件的路径
     */
    private String url;

    public Boolean getAsync() {
        return async;
    }

    public ConverterParamDTO setAsync(Boolean async) {
        this.async = async;
        return this;
    }

    public String getFiletype() {
        return filetype;
    }

    public ConverterParamDTO setFiletype(String filetype) {
        this.filetype = filetype;
        return this;
    }

    public String getKey() {
        return key;
    }

    public ConverterParamDTO setKey(String key) {
        this.key = key;
        return this;
    }

    public String getOutputtype() {
        return outputtype;
    }

    public ConverterParamDTO setOutputtype(String outputtype) {
        this.outputtype = outputtype;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ConverterParamDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ConverterParamDTO setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return "ConverterDTO{" +
                "async=" + async +
                ", filetype='" + filetype + '\'' +
                ", key='" + key + '\'' +
                ", outputtype='" + outputtype + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
