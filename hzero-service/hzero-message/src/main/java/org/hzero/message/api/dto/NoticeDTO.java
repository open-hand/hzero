package org.hzero.message.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.common.HZeroCacheKey;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.cache.CacheValue;
import org.hzero.core.cache.Cacheable;
import org.hzero.message.domain.entity.Notice;
import org.hzero.message.domain.entity.NoticeContent;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 公告DTO
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
@ApiModel("公告基础信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeDTO extends Notice implements Cacheable, SecurityToken {

    @Encrypt
    @ApiModelProperty("表ID，主键，供其他表做外键")
    private Long noticeId;
    @ApiModelProperty(value = "语言code")
    @LovValue(lovCode = "HPFM.LANGUAGE")
    private String lang;
    @ApiModelProperty(value = "公告主题")
    private String title;
    @ApiModelProperty(value = "接收方类型,值集：HMSG.NOTICE.RECERVER_TYPE")
    @LovValue(lovCode = "HMSG.NOTICE.RECERVER_TYPE")
    private String receiverTypeCode;
    @ApiModelProperty(value = "公告类别,值集：HMSG.NOTICE.NOTICE_CATEGORY")
    @LovValue(lovCode = "HMSG.NOTICE.NOTICE_CATEGORY")
    private String noticeCategoryCode;
    @ApiModelProperty(value = "公告类型,值集：HMSG.NOTICE.NOTICE_TYPE.CH")
    @LovValue(lovCode = "HMSG.NOTICE.NOTICE_TYPE.CH")
    private String noticeTypeCode;
    @ApiModelProperty(value = "有效期从")
    private Date startDate;
    @ApiModelProperty(value = "有效期至")
    private Date endDate;
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;
    @ApiModelProperty(value = "公告状态，值集：HMSG.NOTICE.STATUS")
    @LovValue(lovCode = "HMSG.NOTICE.STATUS")
    private String statusCode;
    @ApiModelProperty(value = "公告内容")
    private NoticeContent noticeContent;
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
    @ApiModelProperty(value = "发布日期从")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private Date publishedDateFrom;
    @ApiModelProperty(value = "发布日期至")
    @DateTimeFormat(pattern = BaseConstants.Pattern.DATE)
    private Date publishedDateTo;
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
    @CacheValue(key = HZeroCacheKey.USER, primaryKey = "tempPublishedBy", searchKey = "realName",
            structure = CacheValue.DataStructure.MAP_OBJECT, db = 1)
    private String publishedByUser;
    /**
     * 查询用户公告标识
     */
    private String userNotice;
    private Long userId;
    private Integer previewCount;

    @ApiModelProperty("创建时间从")
    private Date fromDate;
    @ApiModelProperty("创建时间至")
    private Date toDate;
    @ApiModelProperty("消息标题")
    private String subject;

    @Override
    public Long getNoticeId() {
        return noticeId;
    }

    @Override
    public NoticeDTO setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    @Override
    public String getLang() {
        return lang;
    }

    @Override
    public NoticeDTO setLang(String lang) {
        this.lang = lang;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public NoticeDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getReceiverTypeCode() {
        return receiverTypeCode;
    }

    @Override
    public NoticeDTO setReceiverTypeCode(String receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
        return this;
    }

    @Override
    public String getNoticeCategoryCode() {
        return noticeCategoryCode;
    }

    @Override
    public NoticeDTO setNoticeCategoryCode(String noticeCategoryCode) {
        this.noticeCategoryCode = noticeCategoryCode;
        return this;
    }

    @Override
    public String getNoticeTypeCode() {
        return noticeTypeCode;
    }

    @Override
    public NoticeDTO setNoticeTypeCode(String noticeTypeCode) {
        this.noticeTypeCode = noticeTypeCode;
        return this;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public NoticeDTO setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public NoticeDTO setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public NoticeDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public String getStatusCode() {
        return statusCode;
    }

    @Override
    public NoticeDTO setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public NoticeContent getNoticeContent() {
        return noticeContent;
    }

    public NoticeDTO setNoticeContent(NoticeContent noticeContent) {
        this.noticeContent = noticeContent;
        return this;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    @Override
    public String getAttachmentUuid() {
        return attachmentUuid;
    }

    @Override
    public NoticeDTO setAttachmentUuid(String attachmentUuid) {
        this.attachmentUuid = attachmentUuid;
        return this;
    }

    @Override
    public Date getPublishedDate() {
        return publishedDate;
    }

    @Override
    public NoticeDTO setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
        return this;
    }

    @Override
    public Long getPublishedBy() {
        return publishedBy;
    }

    @Override
    public NoticeDTO setPublishedBy(Long publishedBy) {
        this.publishedBy = publishedBy;
        return this;
    }

    public Date getPublishedDateFrom() {
        return publishedDateFrom;
    }

    public NoticeDTO setPublishedDateFrom(Date publishedDateFrom) {
        this.publishedDateFrom = publishedDateFrom;
        return this;
    }

    public Date getPublishedDateTo() {
        return publishedDateTo;
    }

    public NoticeDTO setPublishedDateTo(Date publishedDateTo) {
        this.publishedDateTo = publishedDateTo;
        return this;
    }

    public Integer getContainsDeletedDataFlag() {
        return containsDeletedDataFlag;
    }

    public NoticeDTO setContainsDeletedDataFlag(Integer containsDeletedDataFlag) {
        this.containsDeletedDataFlag = containsDeletedDataFlag;
        return this;
    }

    public String getReceiverTypeMeaning() {
        return receiverTypeMeaning;
    }

    public NoticeDTO setReceiverTypeMeaning(String receiverTypeMeaning) {
        this.receiverTypeMeaning = receiverTypeMeaning;
        return this;
    }

    public String getNoticeCategoryMeaning() {
        return noticeCategoryMeaning;
    }

    public NoticeDTO setNoticeCategoryMeaning(String noticeCategoryMeaning) {
        this.noticeCategoryMeaning = noticeCategoryMeaning;
        return this;
    }

    public String getNoticeTypeMeaning() {
        return noticeTypeMeaning;
    }

    public NoticeDTO setNoticeTypeMeaning(String noticeTypeMeaning) {
        this.noticeTypeMeaning = noticeTypeMeaning;
        return this;
    }

    public String getStatusMeaning() {
        return statusMeaning;
    }

    public NoticeDTO setStatusMeaning(String statusMeaning) {
        this.statusMeaning = statusMeaning;
        return this;
    }

    public String getLangMeaning() {
        return langMeaning;
    }

    public NoticeDTO setLangMeaning(String langMeaning) {
        this.langMeaning = langMeaning;
        return this;
    }

    public String getNoticeBody() {
        return noticeBody;
    }

    public NoticeDTO setNoticeBody(String noticeBody) {
        this.noticeBody = noticeBody;
        return this;
    }

    public Long getTempPublishedBy() {
        return tempPublishedBy;
    }

    public NoticeDTO setTempPublishedBy(Long tempPublishedBy) {
        this.tempPublishedBy = tempPublishedBy;
        return this;
    }

    public String getPublishedByUser() {
        return publishedByUser;
    }

    public NoticeDTO setPublishedByUser(String publishedByUser) {
        this.publishedByUser = publishedByUser;
        return this;
    }

    public String getUserNotice() {
        return userNotice;
    }

    public NoticeDTO setUserNotice(String userNotice) {
        this.userNotice = userNotice;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public NoticeDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getPreviewCount() {
        return previewCount;
    }

    public NoticeDTO setPreviewCount(Integer previewCount) {
        this.previewCount = previewCount;
        return this;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public NoticeDTO setFromDate(Date fromDate) {
        this.fromDate = fromDate;
        return this;
    }

    public Date getToDate() {
        return toDate;
    }

    public NoticeDTO setToDate(Date toDate) {
        this.toDate = toDate;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public NoticeDTO setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    @Override
    public String toString() {
        return "NoticeDTO{" +
                "noticeId=" + noticeId +
                ", lang='" + lang + '\'' +
                ", title='" + title + '\'' +
                ", receiverTypeCode='" + receiverTypeCode + '\'' +
                ", noticeCategoryCode='" + noticeCategoryCode + '\'' +
                ", noticeTypeCode='" + noticeTypeCode + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", tenantId=" + tenantId +
                ", statusCode='" + statusCode + '\'' +
                ", noticeContent=" + noticeContent +
                ", objectVersionNumber=" + objectVersionNumber +
                ", attachmentUuid='" + attachmentUuid + '\'' +
                ", publishedDate=" + publishedDate +
                ", publishedBy=" + publishedBy +
                ", publishedDateFrom=" + publishedDateFrom +
                ", publishedDateTo=" + publishedDateTo +
                ", containsDeletedDataFlag=" + containsDeletedDataFlag +
                ", receiverTypeMeaning='" + receiverTypeMeaning + '\'' +
                ", noticeCategoryMeaning='" + noticeCategoryMeaning + '\'' +
                ", noticeTypeMeaning='" + noticeTypeMeaning + '\'' +
                ", statusMeaning='" + statusMeaning + '\'' +
                ", langMeaning='" + langMeaning + '\'' +
                ", noticeBody='" + noticeBody + '\'' +
                ", tempPublishedBy=" + tempPublishedBy +
                ", publishedByUser='" + publishedByUser + '\'' +
                ", userNotice='" + userNotice + '\'' +
                ", userId=" + userId +
                ", previewCount=" + previewCount +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", subject='" + subject + '\'' +
                '}';
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return Notice.class;
    }
}
