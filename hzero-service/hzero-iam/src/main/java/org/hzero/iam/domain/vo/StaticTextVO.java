package org.hzero.iam.domain.vo;

import java.util.Date;

import org.hzero.core.base.BaseConstants;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 平台静态信息
 *
 * @author bojiangzhou 2018/07/01
 */
public class StaticTextVO {

    public static final String FIELD_TEXT_ID = "textId";
    public static final String FIELD_TEXT_TYPE = "textType";
    public static final String FIELD_LANG = "lang";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_START_DATE = "startDate";
    public static final String FIELD_END_DATE = "endDate";

    @Encrypt
    private Long textId;
    private String textType;
    private String lang;
    private String text;
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date startDate;
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date endDate;

    public StaticTextVO() {
    }

    public StaticTextVO(Long textId, String textType, String lang, String text, Date startDate, Date endDate) {
        this.textId = textId;
        this.textType = textType;
        this.lang = lang;
        this.text = text;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * @return 文本ID
     */
    public Long getTextId() {
        return textId;
    }

    public void setTextId(Long textId) {
        this.textId = textId;
    }

    /**
     * @return 内容类型，值集：HPFM.TEXT_TYPE
     */
    public String getTextType() {
        return textType;
    }

    public void setTextType(String textType) {
        this.textType = textType;
    }

    /**
     * @return 语言
     */
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * @return 协议内容，富文本
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return 有效期从
     */
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return 有效期至
     */
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
