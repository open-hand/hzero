package org.hzero.message.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 公告发布记录
 *
 * @author minghui.qiu@hand-china.com 2019-06-10 16:18:09
 */
@ApiModel("公告发布记录")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_notice_published")
public class NoticePublished extends AuditDomain {

    public static final String FIELD_PUBLISHED_ID = "publishedId";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_RECEIVER_TYPE_CODE = "receiverTypeCode";
    public static final String FIELD_NOTICE_ID = "noticeId";
    public static final String FIELD_NOTICE_CONTENT_ID = "noticeContentId";
    public static final String FIELD_PUBLISHED_STATUS = "publishedStatus";
    public static final String FIELD_PUBLISHED_VERSION = "publishedVersion";
    public static final String FIELD_PUBLISHED_BY = "publishedBy";
    public static final String FIELD_PUBLISHED_DATE = "publishedDate";
    public static final String FIELD_TENANT_ID = "tenantId";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------

    @Id
    @GeneratedValue
    @Encrypt
    private Long publishedId;
    @NotNull
    @Encrypt
    private Long noticeId;
    @ApiModelProperty(value = "公告主题", required = true)
    @NotBlank
    private String title;
    @ApiModelProperty(value = "接收方类型,值集：HMSG.NOTICE.RECERVER_TYPE", required = true)
    @LovValue(lovCode = "HMSG.NOTICE.NOTICE_CATEGORY")
    @NotBlank
    private String receiverTypeCode;
    private String noticeBody;
    @ApiModelProperty(value = "附件uuid", required = true)
    private String attachmentUuid;
    @ApiModelProperty(value = "公告类别,值集：HMSG.NOTICE.NOTICE_CATEGORY", required = true)
    @LovValue(lovCode = "HMSG.NOTICE.NOTICE_CATEGORY")
    private String noticeCategoryCode;
    @ApiModelProperty(value = "公告类型,值集：HMSG.NOTICE.NOTICE_TYPE", required = true)
    @LovValue(lovCode = "HMSG.NOTICE.NOTICE_TYPE")
    private String noticeTypeCode;
    @ApiModelProperty(value = "有效期从", required = true)
    @NotNull
    private Date startDate;
    @ApiModelProperty(value = "有效期至")
    private Date endDate;
    @ApiModelProperty(value = "公告状态，值集：HMSG.NOTICE.STATUS", required = true)
    @LovValue(lovCode = "HMSG.NOTICE.STATUS")
    private String publishedStatusCode;
    private Long publishedVersion;
    private Long publishedBy;
    private Date publishedDate;
    @NotNull
    private Long tenantId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    @Transient
    private String noticeCategoryMeaning;
    @Transient
    private String noticeTypeMeaning;
    @Transient
    private String publishedStatusMeaning;
    @Transient
    private String publisherName;
    @Transient
    private String receiverTypeMeaning;
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public NoticePublished setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getReceiverTypeCode() {
        return receiverTypeCode;
    }

    public NoticePublished setReceiverTypeCode(String receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
        return this;
    }

    public String getReceiverTypeMeaning() {
        return receiverTypeMeaning;
    }

    public void setReceiverTypeMeaning(String receiverTypeMeaning) {
        this.receiverTypeMeaning = receiverTypeMeaning;
    }

    /**
     * @return
     */
    public Long getPublishedId() {
        return publishedId;
    }

    public NoticePublished setPublishedId(Long publishedId) {
        this.publishedId = publishedId;
        return this;
    }

    /**
     * @return 发布版本
     */
    public Long getPublishedVersion() {
        return publishedVersion;
    }

    public NoticePublished setPublishedVersion(Long publishedVersion) {
        this.publishedVersion = publishedVersion;
        return this;
    }

    /**
     * @return 发布人
     */
    public Long getPublishedBy() {
        return publishedBy;
    }

    public NoticePublished setPublishedBy(Long publishedBy) {
        this.publishedBy = publishedBy;
        return this;
    }

    /**
     * @return 发布时间
     */
    public Date getPublishedDate() {
        return publishedDate;
    }

    public NoticePublished setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
        return this;
    }

    /**
     * @return 租户ID
     */
    public Long getTenantId() {
        return tenantId;
    }

    public NoticePublished setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public NoticePublished setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public String getPublishedStatusCode() {
        return publishedStatusCode;
    }

    public NoticePublished setPublishedStatusCode(String publishedStatusCode) {
        this.publishedStatusCode = publishedStatusCode;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public NoticePublished setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public NoticePublished setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getNoticeTypeCode() {
        return noticeTypeCode;
    }

    public NoticePublished setNoticeTypeCode(String noticeTypeCode) {
        this.noticeTypeCode = noticeTypeCode;
        return this;
    }

    public String getNoticeCategoryCode() {
        return noticeCategoryCode;
    }

    public NoticePublished setNoticeCategoryCode(String noticeCategoryCode) {
        this.noticeCategoryCode = noticeCategoryCode;
        return this;
    }

    public String getAttachmentUuid() {
        return attachmentUuid;
    }

    public NoticePublished setAttachmentUuid(String attachmentUuid) {
        this.attachmentUuid = attachmentUuid;
        return this;
    }

    public String getNoticeBody() {
        return noticeBody;
    }

    public NoticePublished setNoticeBody(String noticeBody) {
        this.noticeBody = noticeBody;
        return this;
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

    public String getPublishedStatusMeaning() {
        return publishedStatusMeaning;
    }

    public void setPublishedStatusMeaning(String publishedStatusMeaning) {
        this.publishedStatusMeaning = publishedStatusMeaning;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

}
