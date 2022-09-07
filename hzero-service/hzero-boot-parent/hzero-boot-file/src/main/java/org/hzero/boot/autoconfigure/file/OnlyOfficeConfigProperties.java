package org.hzero.boot.autoconfigure.file;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OnlyOffice配置属性类
 *
 * @author xianzhi.chen@hand-china.com 2019年4月29日下午3:09:46
 */
@Configuration
@ConfigurationProperties(prefix = OnlyOfficeConfigProperties.PREFIX)
public class OnlyOfficeConfigProperties {

    /**
     * 配置前缀
     */
    static final String PREFIX = "hzero.file.only-office";

    /**
     * 在线编辑器
     */
    private String editType = "hoffice";
    /**
     * 文件key缓存过期时间（天）
     */
    private Long fileKeyExpire = 30L;
    /**
     * document Sever token
     */
    private String token;
    /**
     * document Sever 路径
     */
    private String docServerUrl;
    /**
     * 回调地址所在服务路径
     */
    private String callBackUrl;
    /**
     * 生成预览文件接口
     */
    private String converterUrl;
    /**
     * hoffice的logo
     */
    private String logoImage = "";
    /**
     * hoffice的logo链接
     */
    private String logoLink = "";
    /**
     * hoffice的加载logo
     */
    private String preloaderLogoImage = "";

    public String getEditType() {
        return editType;
    }

    public OnlyOfficeConfigProperties setEditType(String editType) {
        this.editType = editType;
        return this;
    }

    public Long getFileKeyExpire() {
        return fileKeyExpire;
    }

    public OnlyOfficeConfigProperties setFileKeyExpire(Long fileKeyExpire) {
        this.fileKeyExpire = fileKeyExpire;
        return this;
    }

    public String getToken() {
        return token;
    }

    public OnlyOfficeConfigProperties setToken(String token) {
        this.token = token;
        return this;
    }

    public String getDocServerUrl() {
        return docServerUrl;
    }

    public OnlyOfficeConfigProperties setDocServerUrl(String docServerUrl) {
        this.docServerUrl = docServerUrl;
        return this;
    }

    public String getCallBackUrl() {
        return callBackUrl;
    }

    public OnlyOfficeConfigProperties setCallBackUrl(String callBackUrl) {
        this.callBackUrl = callBackUrl;
        return this;
    }

    public String getConverterUrl() {
        return converterUrl;
    }

    public OnlyOfficeConfigProperties setConverterUrl(String converterUrl) {
        this.converterUrl = converterUrl;
        return this;
    }

    public String getLogoImage() {
        return logoImage;
    }

    public OnlyOfficeConfigProperties setLogoImage(String logoImage) {
        this.logoImage = logoImage;
        return this;
    }

    public String getLogoLink() {
        return logoLink;
    }

    public OnlyOfficeConfigProperties setLogoLink(String logoLink) {
        this.logoLink = logoLink;
        return this;
    }

    public String getPreloaderLogoImage() {
        return preloaderLogoImage;
    }

    public OnlyOfficeConfigProperties setPreloaderLogoImage(String preloaderLogoImage) {
        this.preloaderLogoImage = preloaderLogoImage;
        return this;
    }
}
