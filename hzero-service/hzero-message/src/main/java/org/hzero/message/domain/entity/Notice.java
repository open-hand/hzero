package org.hzero.message.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.redis.RedisHelper;
import org.hzero.message.api.dto.NoticeDTO;
import org.hzero.message.domain.repository.NoticeRepository;
import org.hzero.message.domain.vo.NoticeCacheVO;
import org.hzero.message.infra.constant.HmsgConstant;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.hzero.core.base.BaseConstants.ErrorCode.DATA_NOT_EXISTS;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 公告基础信息
 *
 * @author runbai.chen@hand-china.com 2018-08-02 15:23:14
 */
@ApiModel("公告基础信息")
@VersionAudit
@ModifyAudit
@Table(name = "hmsg_notice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notice extends AuditDomain {

    /**
     * 首页公告-平台公告
     */
    public static final String NOTICE_PLATFORM = "GOING-BUY";
    /**
     * 首页公告-公司新闻
     */
    public static final String NOTICE_NEWS = "NEWS";
    /**
     * 首页公告-招标寻源
     */
    public static final String NOTICE_BIDDING = "BIDDING";

    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_PUBLISHED = "PUBLISHED";
    public static final String STATUS_DELETED = "DELETED";


    public static final String FIELD_NOTICE_ID = "noticeId";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_PUBLISHED_DATE = "publishedDate";

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(BaseConstants.Pattern.DATE);
    private static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance(BaseConstants.Pattern.NONE_DATETIME_MM);
    private static final Logger logger = LoggerFactory.getLogger(Notice.class);



    public static Double getNowTimeScore() {
        return Double.valueOf(DATE_TIME_FORMAT.format(new Date()));
    }

    /**
     * 缓存公告数据
     */
    public void refreshCachePublishedNotices(RedisHelper redisHelper, ObjectMapper objectMapper) {
        if (Notice.STATUS_PUBLISHED.equals(getStatusCode())) {
            redisHelper.zSetRemove(Notice.getOrderCacheKey(Notice.NOTICE_BIDDING, this.tenantId, lang), String.valueOf(this.noticeId));
            redisHelper.zSetRemove(Notice.getOrderCacheKey(Notice.NOTICE_NEWS, this.tenantId, lang), String.valueOf(this.noticeId));
            redisHelper.zSetRemove(Notice.getOrderCacheKey(Notice.NOTICE_PLATFORM, this.tenantId, lang), String.valueOf(this.noticeId));
            redisHelper.delKey(HmsgConstant.CacheKey.PUBLISHED_NOTICE + ":" + this.noticeId);

            if (endDate != null && endDate.getTime() <= System.currentTimeMillis()) {
                return;
            }

            // 以发布时间作为顺序排序 (页面显示时间取发布时间和起始时间的最大值)
            Date showDate = startDate.after(this.publishedDate) ? startDate : this.publishedDate;
            String showDateTime = DATE_TIME_FORMAT.format(showDate);
            // 缓存顺序
            String orderKey = Notice.getOrderCacheKey(this.noticeCategoryCode, this.tenantId, lang);
            redisHelper.zSetAdd(orderKey, String.valueOf(this.noticeId), Double.parseDouble(showDateTime));

            // 公告过期时间
            long expire = Optional.ofNullable(endDate).map(d -> d.getTime() - startDate.getTime()).orElse(-1L);
            NoticeDTO noticeDTO = CommonConverter.beanConvert(NoticeDTO.class, this);
            // 缓存已发布的公告
            NoticeCacheVO noticeCacheVO = new NoticeCacheVO(this.noticeId, this.title, DATE_FORMAT.format(showDate), noticeDTO.getNoticeTypeMeaning());
            try {
                redisHelper.strSet(HmsgConstant.CacheKey.PUBLISHED_NOTICE + ":" + this.noticeId,
                        objectMapper.writeValueAsString(noticeCacheVO), expire, TimeUnit.MILLISECONDS);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 删除缓存公告
     */
    public void deleteCachePublishedNotice(RedisHelper redisHelper) {
        String orderKey = Notice.getOrderCacheKey(this.noticeCategoryCode, this.tenantId, lang);
        redisHelper.zSetRemove(orderKey, String.valueOf(noticeId));
        redisHelper.delKey(HmsgConstant.CacheKey.PUBLISHED_NOTICE + ":" + this.noticeId);
    }

    /**
     * 获取缓存顺序的KEY
     *
     * @param noticeCategoryCode 公告类别
     * @param tenantId           租户ID
     * @param lang               语言
     * @return KEY
     */
    public static String getOrderCacheKey(String noticeCategoryCode, Long tenantId, String lang) {
        return HmsgConstant.CacheKey.PUBLISHED_NOTICE_ORDER +
                ":" + tenantId +
                ":" + noticeCategoryCode +
                ":" + lang;
    }

    /**
     * 判断当前状态跟修改状态
     *
     * @param currentStatusCode 当前状态
     * @param statusCode        修改状态
     * @return 是否允许跟新
     */
    public static Boolean checkStatusCode(String currentStatusCode, String statusCode) {
        Boolean situationA = currentStatusCode.equals(STATUS_DRAFT)
                && (statusCode.equals(STATUS_DELETED) || statusCode.equals(STATUS_PUBLISHED));
        Boolean situationB = currentStatusCode.equals(STATUS_DELETED)
                && statusCode.equals(STATUS_DRAFT);
        Boolean situationC = currentStatusCode.equals(STATUS_PUBLISHED)
                && statusCode.equals(STATUS_DELETED);
        if (situationA || situationB || situationC) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 更新状态
     *
     * @param noticeRepository Repository
     * @param noticeId         公告/通知ID
     * @param statusCode       状态
     * @return 公告/通知
     */
    public static Notice updateStatus(NoticeRepository noticeRepository, Long noticeId, String statusCode) {
        Notice notice = noticeRepository.selectByPrimaryKey(noticeId);
        Assert.notNull(notice, DATA_NOT_EXISTS);
        notice.setStatusCode(statusCode);
        if (STATUS_PUBLISHED.equals(statusCode)) {
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            notice.setPublishedDate(new Date());
            notice.setPublishedBy(userDetails.getUserId());
        }
        noticeRepository.updateByPrimaryKey(notice);
        return notice;
    }

    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Id
    @GeneratedValue
    @Encrypt
    private Long noticeId;
    @ApiModelProperty(value = "语言code", required = true)
    @NotBlank
    private String lang;
    @ApiModelProperty(value = "公告主题", required = true)
    @NotBlank
    private String title;
    @ApiModelProperty(value = "公告发布对象类别(公告、通知),值集：HMSG.NOTICE.RECEIVER_TYPE", required = true)
    @NotBlank
    private String receiverTypeCode;
    @ApiModelProperty(value = "公告类别,值集：HMSG.NOTICE.NOTICE_CATEGORY", required = true)
    private String noticeCategoryCode;
    @ApiModelProperty(value = "公告类型,值集：HMSG.NOTICE.NOTICE_TYPE", required = true)
    @NotBlank
    private String noticeTypeCode;
    @ApiModelProperty(value = "有效期从", required = true)
    @NotNull
    private Date startDate;
    private Date endDate;
    @ApiModelProperty(value = "租户ID", required = true)
    @NotNull
    private Long tenantId;
    @ApiModelProperty(value = "公告状态，值集：HMSG.NOTICE.STATUS", required = true)
    @NotBlank
    private String statusCode;
    @ApiModelProperty(value = "附件uuid", required = true)
    private String attachmentUuid;
    @ApiModelProperty(value = "发布时间")
    private Date publishedDate;
    @ApiModelProperty(value = "发布人ID")
    private Long publishedBy;
    @ApiModelProperty(value = "顶部公告标识")
    private Integer stickyFlag;

    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public static FastDateFormat getDateFormat() {
        return DATE_FORMAT;
    }

    public static FastDateFormat getDateTimeFormat() {
        return DATE_TIME_FORMAT;
    }

    public static Logger getLogger() {
        return logger;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public Notice setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public Notice setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Notice setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getReceiverTypeCode() {
        return receiverTypeCode;
    }

    public Notice setReceiverTypeCode(String receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
        return this;
    }

    public String getNoticeCategoryCode() {
        return noticeCategoryCode;
    }

    public Notice setNoticeCategoryCode(String noticeCategoryCode) {
        this.noticeCategoryCode = noticeCategoryCode;
        return this;
    }

    public String getNoticeTypeCode() {
        return noticeTypeCode;
    }

    public Notice setNoticeTypeCode(String noticeTypeCode) {
        this.noticeTypeCode = noticeTypeCode;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Notice setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Notice setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Notice setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public Notice setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getAttachmentUuid() {
        return attachmentUuid;
    }

    public Notice setAttachmentUuid(String attachmentUuid) {
        this.attachmentUuid = attachmentUuid;
        return this;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public Notice setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
        return this;
    }

    public Long getPublishedBy() {
        return publishedBy;
    }

    public Notice setPublishedBy(Long publishedBy) {
        this.publishedBy = publishedBy;
        return this;
    }

    public Integer getStickyFlag() {
        return stickyFlag;
    }

    public Notice setStickyFlag(Integer stickyFlag) {
        this.stickyFlag = stickyFlag;
        return this;
    }
}
