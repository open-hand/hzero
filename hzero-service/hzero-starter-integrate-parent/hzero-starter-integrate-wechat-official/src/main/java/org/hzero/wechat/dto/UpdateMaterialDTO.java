package org.hzero.wechat.dto;

public class UpdateMaterialDTO  {
    /**
     * media_id : MEDIA_ID
     * index : INDEX
     * articles : {"title":"TITLE","thumb_media_id":"THUMB_MEDIA_ID","author":"AUTHOR","digest":"DIGEST","show_cover_pic":0,"content":"CONTENT","content_source_url":"CONTENT_SOURCE_URL"}
     */

    private String media_id;
    private Long index;
    private ArticlesBean articles;

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public ArticlesBean getArticles() {
        return articles;
    }

    public void setArticles(ArticlesBean articles) {
        this.articles = articles;
    }


    public static class ArticlesBean {
        /**
         * title : TITLE
         * thumb_media_id : THUMB_MEDIA_ID
         * author : AUTHOR
         * digest : DIGEST
         * show_cover_pic : 0
         * content : CONTENT
         * content_source_url : CONTENT_SOURCE_URL
         */

        private String title;
        private String thumb_media_id;
        private String author;
        private String digest;
        private int show_cover_pic;
        private String content;
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

        public int getShow_cover_pic() {
            return show_cover_pic;
        }

        public void setShow_cover_pic(int show_cover_pic) {
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
    }
}
