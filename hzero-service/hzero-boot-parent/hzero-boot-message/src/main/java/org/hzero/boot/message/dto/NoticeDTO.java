package org.hzero.boot.message.dto;

import java.time.LocalDate;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.core.base.BaseConstants;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 公告DTO
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
@ApiModel("公告基础信息")
public class NoticeDTO {

    private static final FastDateFormat dateTimeFormatter = FastDateFormat.getInstance(BaseConstants.Pattern.DATETIME);
    private static final FastDateFormat dateFormatter = FastDateFormat.getInstance(BaseConstants.Pattern.DATE);

    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long noticeId;
    @ApiModelProperty(value = "语言code")
    private String lang;
    @ApiModelProperty(value = "公告主题")
    private String title;
    @ApiModelProperty(value = "接收方类型,值集：HMSG.NOTICE.RECERVER_TYPE")
    private String receiverTypeCode;
    @ApiModelProperty(value = "公告类别,值集：HMSG.NOTICE.NOTICE_CATEGORY")
    private String noticeCategoryCode;
    @ApiModelProperty(value = "公告类型,值集：HMSG.NOTICE.NOTICE_TYPE.CH")
    private String noticeTypeCode;
    @ApiModelProperty(value = "有效期从")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate startDate;
    @ApiModelProperty(value = "有效期至")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    @JsonFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate endDate;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
    @ApiModelProperty(value = "公告状态，值集：HMSG.NOTICE.STATUS")
    private String statusCode;
    @ApiModelProperty(value = "版本编号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "附件uuid")
    private String attachmentUuid;
    @ApiModelProperty(value = "发布时间")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATETIME)
    @JsonFormat(pattern = BaseConstants.Pattern.DATETIME)
    private Date publishedDate;
    @ApiModelProperty(value = "发布人ID")
    private Long publishedBy;
    @ApiModelProperty(value = "创建日期从")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate creationDateFrom;
    @ApiModelProperty(value = "创建日期至")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate creationDateTo;
    @ApiModelProperty(value = "发布日期从")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate publishedDateFrom;
    @ApiModelProperty(value = "发布日期至")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private LocalDate publishedDateTo;
    @ApiModelProperty(value = "包含删除数据，1包含，0或者空不包含")
    private Integer containsDeletedDataFlag;

    private String receiverTypeMeaning;
    private String noticeCategoryMeaning;
    private String noticeTypeMeaning;
    private String statusMeaning;
    private String langMeaning;
    private String noticeBody;
    /**
     * FIX 03-21 临时存储发布人id,处理公告首页无法显示发布人信息问题
     */
    private Long tempPublishedBy;
    private String publishedByUser;

    private String userNotice;

    public NoticeDTO() {
    }

    public Long getTempPublishedBy() {
        return tempPublishedBy;
    }

    public void setTempPublishedBy(Long tempPublishedBy) {
        this.tempPublishedBy = tempPublishedBy;
    }

    public String getPublishedByUser() {
        return publishedByUser;
    }

    public String getPublishedDateTimeStr() {
        if (this.publishedDate != null) {
            return dateTimeFormatter.format(publishedDate);
        }
        return null;
    }

    public String getPublishedDateStr() {
        if (this.publishedDate != null) {
            return dateFormatter.format(publishedDate);
        }
        return null;
    }

    public static FastDateFormat getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public static FastDateFormat getDateFormatter() {
        return dateFormatter;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReceiverTypeCode() {
        return receiverTypeCode;
    }

    public void setReceiverTypeCode(String receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
    }

    public String getNoticeCategoryCode() {
        return noticeCategoryCode;
    }

    public void setNoticeCategoryCode(String noticeCategoryCode) {
        this.noticeCategoryCode = noticeCategoryCode;
    }

    public String getNoticeTypeCode() {
        return noticeTypeCode;
    }

    public void setNoticeTypeCode(String noticeTypeCode) {
        this.noticeTypeCode = noticeTypeCode;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getAttachmentUuid() {
        return attachmentUuid;
    }

    public void setAttachmentUuid(String attachmentUuid) {
        this.attachmentUuid = attachmentUuid;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Long getPublishedBy() {
        return publishedBy;
    }

    public void setPublishedBy(Long publishedBy) {
        this.publishedBy = publishedBy;
    }

    public LocalDate getCreationDateFrom() {
        return creationDateFrom;
    }

    public void setCreationDateFrom(LocalDate creationDateFrom) {
        this.creationDateFrom = creationDateFrom;
    }

    public LocalDate getCreationDateTo() {
        return creationDateTo;
    }

    public void setCreationDateTo(LocalDate creationDateTo) {
        this.creationDateTo = creationDateTo;
    }

    public LocalDate getPublishedDateFrom() {
        return publishedDateFrom;
    }

    public void setPublishedDateFrom(LocalDate publishedDateFrom) {
        this.publishedDateFrom = publishedDateFrom;
    }

    public LocalDate getPublishedDateTo() {
        return publishedDateTo;
    }

    public void setPublishedDateTo(LocalDate publishedDateTo) {
        this.publishedDateTo = publishedDateTo;
    }

    public Integer getContainsDeletedDataFlag() {
        return containsDeletedDataFlag;
    }

    public void setContainsDeletedDataFlag(Integer containsDeletedDataFlag) {
        this.containsDeletedDataFlag = containsDeletedDataFlag;
    }

    public String getReceiverTypeMeaning() {
        return receiverTypeMeaning;
    }

    public void setReceiverTypeMeaning(String receiverTypeMeaning) {
        this.receiverTypeMeaning = receiverTypeMeaning;
    }

    public String getNoticeCategoryMeaning() {
        return noticeCategoryMeaning;
    }

    public void setNoticeCategoryMeaning(String noticeCategoryMeaning) {
        this.noticeCategoryMeaning = noticeCategoryMeaning;
    }

    public String getNoticeTypeMeaning() {
        return noticeTypeMeaning;
    }

    public void setNoticeTypeMeaning(String noticeTypeMeaning) {
        this.noticeTypeMeaning = noticeTypeMeaning;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public void setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
    }

    public String getLangMeaning() {
        return langMeaning;
    }

    public void setLangMeaning(String langMeaning) {
        this.langMeaning = langMeaning;
    }

    public String getNoticeBody() {
        return noticeBody;
    }

    public void setNoticeBody(String noticeBody) {
        this.noticeBody = noticeBody;
    }

    public void setPublishedByUser(String publishedByUser) {
        this.publishedByUser = publishedByUser;
    }

    public String getUserNotice() {
        return userNotice;
    }

    public void setUserNotice(String userNotice) {
        this.userNotice = userNotice;
    }
}
