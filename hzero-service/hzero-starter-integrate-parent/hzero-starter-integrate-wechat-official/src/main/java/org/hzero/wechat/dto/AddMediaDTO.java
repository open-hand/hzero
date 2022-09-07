package org.hzero.wechat.dto;

import java.util.List;

public class AddMediaDTO {

    private List<ArticlesBean> articles;

    public List<ArticlesBean> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticlesBean> articles) {
        this.articles = articles;
    }


    public static class ArticlesBean {
        /**
         * title : TITLE
         * thumb_media_id : THUMB_MEDIA_ID
         * author : AUTHOR
         * digest : DIGEST
         * show_cover_pic : SHOW_COVER_PIC(0 / 1)
         * content : CONTENT
         * content_source_url : CONTENT_SOURCE_URL
         * need_open_comment : 1
         * only_fans_can_comment : 1
         */

        private String title;
        private String thumb_media_id;
        private String author;
        private String digest;
        private String show_cover_pic;
        private String content;
        private String content_source_url;
        private int need_open_comment;
        private int only_fans_can_comment;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumb_media_id() {
            return thumb_media_id;
        }

        public void setThumb_media_id(String thumb_media_id) {
            this.thumb_media_id = thumb_media_id;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getDigest() {
            return digest;
        }

        public void setDigest(String digest) {
            this.digest = digest;
        }

        public String getShow_cover_pic() {
            return show_cover_pic;
        }

        public void setShow_cover_pic(String show_cover_pic) {
            this.show_cover_pic = show_cover_pic;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getContent_source_url() {
            return content_source_url;
        }

        public void setContent_source_url(String content_source_url) {
            this.content_source_url = content_source_url;
        }

        public int getNeed_open_comment() {
            return need_open_comment;
        }

        public void setNeed_open_comment(int need_open_comment) {
            this.need_open_comment = need_open_comment;
        }

        public int getOnly_fans_can_comment() {
            return only_fans_can_comment;
        }

        public void setOnly_fans_can_comment(int only_fans_can_comment) {
            this.only_fans_can_comment = only_fans_can_comment;
        }
    }
}
