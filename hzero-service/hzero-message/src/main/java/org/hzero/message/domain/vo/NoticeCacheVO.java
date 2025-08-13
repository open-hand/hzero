package org.hzero.message.domain.vo;

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
