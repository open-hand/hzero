package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/21
 */
public class NewsMessageDTO {


    /**
     * touser : UserID1|UserID2|UserID3
     * toparty : PartyID1 | PartyID2
     * totag : TagID1 | TagID2
     * msgtype : news
     * agentid : 1
     * news : {"articles":[{"title":"中秋节礼品领取","description":"今年中秋节公司有豪礼相送","url":"URL","picurl":"http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png"}]}
     * enable_id_trans : 0
     */

    private String touser;
    private String toparty;
    private String totag;
    private String msgtype;
    private long agentid;
    private NewsBean news;
    private int enable_id_trans;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getToparty() {
        return toparty;
    }

    public void setToparty(String toparty) {
        this.toparty = toparty;
    }

    public String getTotag() {
        return totag;
    }

    public void setTotag(String totag) {
        this.totag = totag;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public long getAgentid() {
        return agentid;
    }

    public void setAgentid(long agentid) {
        this.agentid = agentid;
    }

    public NewsBean getNews() {
        return news;
    }

    public void setNews(NewsBean news) {
        this.news = news;
    }

    public int getEnable_id_trans() {
        return enable_id_trans;
    }

    public void setEnable_id_trans(int enable_id_trans) {
        this.enable_id_trans = enable_id_trans;
    }

    public static class NewsBean {
        private List<ArticlesBean> articles;

        public List<ArticlesBean> getArticles() {
            return articles;
        }

        public void setArticles(List<ArticlesBean> articles) {
            this.articles = articles;
        }

        public static class ArticlesBean {
            /**
             * title : 中秋节礼品领取
             * description : 今年中秋节公司有豪礼相送
             * url : URL
             * picurl : http://res.mail.qq.com/node/ww/wwopenmng/images/independent/doc/test_pic_msg1.png
             */

            private String title;
            private String description;
            private String url;
            private String picurl;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getPicurl() {
                return picurl;
            }

            public void setPicurl(String picurl) {
                this.picurl = picurl;
            }
        }
    }
}
