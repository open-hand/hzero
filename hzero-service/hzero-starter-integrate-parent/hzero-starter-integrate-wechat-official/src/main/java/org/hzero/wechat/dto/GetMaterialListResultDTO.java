package org.hzero.wechat.dto;

import java.util.List;

public class GetMaterialListResultDTO extends DefaultResultDTO {
    /**
     * total_count : TOTAL_COUNT
     * item_count : ITEM_COUNT
     * item : [{"media_id":"MEDIA_ID","content":{"news_item":[{"title":"TITLE","thumb_media_id":"THUMB_MEDIA_ID","show_cover_pic":"SHOW_COVER_PIC(0 / 1)","author":"AUTHOR","digest":"DIGEST","content":"CONTENT","url":"URL","content_source_url":"CONTETN_SOURCE_URL"}]},"update_time":"UPDATE_TIME"}]
     */

    private String total_count;
    private String item_count;
    private List<ItemBean> item;


    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public String getItem_count() {
        return item_count;
    }

    public void setItem_count(String item_count) {
        this.item_count = item_count;
    }

    public List<ItemBean> getItem() {
        return item;
    }

    public void setItem(List<ItemBean> item) {
        this.item = item;
    }


    public static class ItemBean {
        /**
         * media_id : MEDIA_ID
         * content : {"news_item":[{"title":"TITLE","thumb_media_id":"THUMB_MEDIA_ID","show_cover_pic":"SHOW_COVER_PIC(0 / 1)","author":"AUTHOR","digest":"DIGEST","content":"CONTENT","url":"URL","content_source_url":"CONTETN_SOURCE_URL"}]}
         * update_time : UPDATE_TIME
         */

        private String media_id;
        private ContentBean content;
        private String update_time;

        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        public ContentBean getContent() {
            return content;
        }

        public void setContent(ContentBean content) {
            this.content = content;
        }

        public String getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(String update_time) {
            this.update_time = update_time;
        }

        public static class ContentBean {
            private List<NewsItemBean> news_item;

            public List<NewsItemBean> getNews_item() {
                return news_item;
            }

            public void setNews_item(List<NewsItemBean> news_item) {
                this.news_item = news_item;
            }

            public static class NewsItemBean {
                /**
                 * title : TITLE
                 * thumb_media_id : THUMB_MEDIA_ID
                 * show_cover_pic : SHOW_COVER_PIC(0 / 1)
                 * author : AUTHOR
                 * digest : DIGEST
                 * content : CONTENT
                 * url : URL
                 * content_source_url : CONTETN_SOURCE_URL
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
    }
}
