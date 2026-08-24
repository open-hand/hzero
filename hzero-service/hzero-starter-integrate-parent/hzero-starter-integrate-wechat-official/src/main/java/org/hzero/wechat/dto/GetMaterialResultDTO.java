package org.hzero.wechat.dto;

import java.util.List;
import java.util.Map;

public class GetMaterialResultDTO {
//            "title":TITLE,
//            "description":DESCRIPTION,
//            "down_url":DOWN_URL,
    private Map<String,List<NewsItemBean>> news_item;
    private Map<String,String> title;
    private Map<String,String> description;
    private Map<String,String> down_url;

    public Map<String, List<NewsItemBean>> getNews_item() {
        return news_item;
    }

    public void setNews_item(Map<String, List<NewsItemBean>> news_item) {
        this.news_item = news_item;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, String> getDown_url() {
        return down_url;
    }

    public void setDown_url(Map<String, String> down_url) {
        this.down_url = down_url;
    }

    public static class NewsItemBean {
        /**
         * title : TITLE
         * thumb_media_id : THUMB_MEDIA_ID
         * show_cover_pic : SHOW_COVER_PIC
         * author : AUTHOR
         * digest : DIGEST
         * content : CONTENT
         * url : URL
         * content_source_url : CONTENT_SOURCE_URL
         */

        private String title;
        private String thumb_media_id;
        private String show_cover_pic;
        private String author;
        private String digest;
        private String content;
        private String url;
        private String content_source_url;

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

        public String getShow_cover_pic() {
            return show_cover_pic;
        }

        public void setShow_cover_pic(String show_cover_pic) {
            this.show_cover_pic = show_cover_pic;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getContent_source_url() {
            return content_source_url;
        }

        public void setContent_source_url(String content_source_url) {
            this.content_source_url = content_source_url;
        }
    }
}
