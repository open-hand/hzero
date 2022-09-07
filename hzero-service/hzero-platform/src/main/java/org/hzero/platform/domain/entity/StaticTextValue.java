package org.hzero.platform.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 平台静态信息
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:14:29
 */
@Table(name = "hpfm_static_text_value")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaticTextValue {

    public static final String FIELD_TEXT_ID = "textId";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_TEXT = "text";

    @Id
    @GeneratedValue
    @Encrypt
    private Long textValueId;
    @Encrypt
    private Long textId;
    private String lang;
    private String text;
    private String title;
    private String description;
    @ApiModelProperty(value = "租户Id")
    @NotNull
    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return 主键ID
     */
    public Long getTextValueId() {
        return textValueId;
    }

    public void setTextValueId(Long textValueId) {
        this.textValueId = textValueId;
    }

    /**
     * @return 表ID
     */
    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    /**
     * @return 语言 HPFM.LANGUAGE
     */
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * @return 文本，富文本
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
