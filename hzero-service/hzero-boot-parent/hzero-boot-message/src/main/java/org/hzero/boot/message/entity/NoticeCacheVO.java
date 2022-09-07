package org.hzero.boot.message.entity;

import org.apache.commons.lang3.time.FastDateFormat;
import org.hzero.core.base.BaseConstants;

import java.util.Date;

/**
 * 缓存
 *
 * @author bojiangzhou 2018/08/14
 */
public class NoticeCacheVO {

    private Long noticeId;
    private String title;
    private String publishedDate;
    private String noticeType;
    private static final FastDateFormat dateFormat = FastDateFormat.getInstance(BaseConstants.Pattern.DATE);
    private static final FastDateFormat dateTimeFormat = FastDateFormat.getInstance(BaseConstants.Pattern.NONE_DATETIME_MM);
    public static Double getNowTimeScore() {
        return Double.valueOf(dateTimeFormat.format(new Date()));
    }

    public NoticeCacheVO() {
    }

    public NoticeCacheVO(Long noticeId, String title, String publishedDate, String noticeType) {
        this.noticeId = noticeId;
        this.title = title;
        this.publishedDate = publishedDate;
        this.noticeType = noticeType;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }
}
