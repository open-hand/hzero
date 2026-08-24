package org.hzero.wechat.dto;

import java.util.List;

public class GetCurrentAutoReplyInfoResultDTO {

    /**
     * is_add_friend_reply_open : 1
     * is_autoreply_open : 1
     * add_friend_autoreply_info : {"type":"text","content":"Thanks for your attention!"}
     * message_default_autoreply_info : {"type":"text","content":"Hello, this is autoreply!"}
     * keyword_autoreply_info : {"list":[{"rule_name":"autoreply-news","create_time":1423028166,"reply_mode":"reply_all","keyword_list_info":[{"type":"text","match_mode":"contain","content":"news测试"}],"reply_list_info":[{"type":"news","news_info":{"list":[{"title":"it's news","author":"jim","digest":"it's digest","show_cover":1,"cover_url":"http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQH  euPKmFLK0ZQ/0","content_url":"http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=203929886&idx=1&sn=628f964cf0c6d84c026881b6959aea8b#rd","source_url":"http://www.url.com"}]}},{"type":"news","content":"KQb_w_Tiz-nSdVLoTV35Psmty8hGBulGhEdbb9SKs-o","news_info":{"list":[{"title":"MULTI_NEWS","author":"JIMZHENG","digest":"text","show_cover":0,"cover_url":"http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfK0HKuBIa1A1cypS0uY1wickv70iaY1gf3I1DTszuJoS3lAVLv  hTcm9sDA/0","content_url":"http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=204013432&idx=1&sn=80ce6d9abcb832237bf86c87e50fda15#rd","source_url":""},{"title":"MULTI_NEWS4","author":"JIMZHENG","digest":"MULTI_NEWSMULTI_NEWSMULTI_NEWSMULTI_NEWSMULTI_NEWSMULT","show_cover":1,"cover_url":"http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQ  HeuPKmFLK0ZQ/0","content_url":"http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=204013432&idx=5&sn=b4ef73a915e7c2265e437096582774af#rd","source_url":""}]}}]},{"rule_name":"autoreply-voice","create_time":1423027971,"reply_mode":"random_one","keyword_list_info":[{"type":"text","match_mode":"contain","content":"voice测试"}],"reply_list_info":[{"type":"voice","content":"NESsxgHEvAcg3egJTtYj4uG1PTL6iPhratdWKDLAXYErhN6oEEfMdVyblWtBY5vp"}]},{"rule_name":"autoreply-text","create_time":1423027926,"reply_mode":"random_one","keyword_list_info":[{"type":"text","match_mode":"contain","content":"text测试"}],"reply_list_info":[{"type":"text","content":"hello!text!"}]},{"rule_name":"autoreply-video","create_time":1423027801,"reply_mode":"random_one","keyword_list_info":[{"type":"text","match_mode":"equal","content":"video测试"}],"reply_list_info":[{"type":"video","content":"http://61.182.133.153/vweixinp.tc.qq.com/1007_114bcede9a2244eeb5ab7f76d951df5f.f10.mp4?vkey=7183E5C952B16C3AB1991BA8138673DE1037CB82A29801A504B64A77F691BF9DF7AD054A9B7FE683&sha=0&save=1"}]}]}
     */

    private int is_add_friend_reply_open;
    private int is_autoreply_open;
    private AddFriendAutoreplyInfoBean add_friend_autoreply_info;
    private MessageDefaultAutoreplyInfoBean message_default_autoreply_info;
    private KeywordAutoreplyInfoBean keyword_autoreply_info;

    public int getIs_add_friend_reply_open() {
        return is_add_friend_reply_open;
    }

    public void setIs_add_friend_reply_open(int is_add_friend_reply_open) {
        this.is_add_friend_reply_open = is_add_friend_reply_open;
    }

    public int getIs_autoreply_open() {
        return is_autoreply_open;
    }

    public void setIs_autoreply_open(int is_autoreply_open) {
        this.is_autoreply_open = is_autoreply_open;
    }

    public AddFriendAutoreplyInfoBean getAdd_friend_autoreply_info() {
        return add_friend_autoreply_info;
    }

    public void setAdd_friend_autoreply_info(AddFriendAutoreplyInfoBean add_friend_autoreply_info) {
        this.add_friend_autoreply_info = add_friend_autoreply_info;
    }

    public MessageDefaultAutoreplyInfoBean getMessage_default_autoreply_info() {
        return message_default_autoreply_info;
    }

    public void setMessage_default_autoreply_info(MessageDefaultAutoreplyInfoBean message_default_autoreply_info) {
        this.message_default_autoreply_info = message_default_autoreply_info;
    }

    public KeywordAutoreplyInfoBean getKeyword_autoreply_info() {
        return keyword_autoreply_info;
    }

    public void setKeyword_autoreply_info(KeywordAutoreplyInfoBean keyword_autoreply_info) {
        this.keyword_autoreply_info = keyword_autoreply_info;
    }

    public static class AddFriendAutoreplyInfoBean {
        /**
         * type : text
         * content : Thanks for your attention!
         */

        private String type;
        private String content;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class MessageDefaultAutoreplyInfoBean {
        /**
         * type : text
         * content : Hello, this is autoreply!
         */

        private String type;
        private String content;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class KeywordAutoreplyInfoBean {
        private List<ListBeanX> list;

        public List<ListBeanX> getList() {
            return list;
        }

        public void setList(List<ListBeanX> list) {
            this.list = list;
        }

        public static class ListBeanX {
            /**
             * rule_name : autoreply-news
             * create_time : 1423028166
             * reply_mode : reply_all
             * keyword_list_info : [{"type":"text","match_mode":"contain","content":"news测试"}]
             * reply_list_info : [{"type":"news","news_info":{"list":[{"title":"it's news","author":"jim","digest":"it's digest","show_cover":1,"cover_url":"http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQH  euPKmFLK0ZQ/0","content_url":"http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=203929886&idx=1&sn=628f964cf0c6d84c026881b6959aea8b#rd","source_url":"http://www.url.com"}]}},{"type":"news","content":"KQb_w_Tiz-nSdVLoTV35Psmty8hGBulGhEdbb9SKs-o","news_info":{"list":[{"title":"MULTI_NEWS","author":"JIMZHENG","digest":"text","show_cover":0,"cover_url":"http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfK0HKuBIa1A1cypS0uY1wickv70iaY1gf3I1DTszuJoS3lAVLv  hTcm9sDA/0","content_url":"http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=204013432&idx=1&sn=80ce6d9abcb832237bf86c87e50fda15#rd","source_url":""},{"title":"MULTI_NEWS4","author":"JIMZHENG","digest":"MULTI_NEWSMULTI_NEWSMULTI_NEWSMULTI_NEWSMULTI_NEWSMULT","show_cover":1,"cover_url":"http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQ  HeuPKmFLK0ZQ/0","content_url":"http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=204013432&idx=5&sn=b4ef73a915e7c2265e437096582774af#rd","source_url":""}]}}]
             */

            private String rule_name;
            private int create_time;
            private String reply_mode;
            private List<KeywordListInfoBean> keyword_list_info;
            private List<ReplyListInfoBean> reply_list_info;

            public String getRule_name() {
                return rule_name;
            }

            public void setRule_name(String rule_name) {
                this.rule_name = rule_name;
            }

            public int getCreate_time() {
                return create_time;
            }

            public void setCreate_time(int create_time) {
                this.create_time = create_time;
            }

            public String getReply_mode() {
                return reply_mode;
            }

            public void setReply_mode(String reply_mode) {
                this.reply_mode = reply_mode;
            }

            public List<KeywordListInfoBean> getKeyword_list_info() {
                return keyword_list_info;
            }

            public void setKeyword_list_info(List<KeywordListInfoBean> keyword_list_info) {
                this.keyword_list_info = keyword_list_info;
            }

            public List<ReplyListInfoBean> getReply_list_info() {
                return reply_list_info;
            }

            public void setReply_list_info(List<ReplyListInfoBean> reply_list_info) {
                this.reply_list_info = reply_list_info;
            }

            public static class KeywordListInfoBean {
                /**
                 * type : text
                 * match_mode : contain
                 * content : news测试
                 */

                private String type;
                private String match_mode;
                private String content;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public String getMatch_mode() {
                    return match_mode;
                }

                public void setMatch_mode(String match_mode) {
                    this.match_mode = match_mode;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }
            }

            public static class ReplyListInfoBean {
                /**
                 * type : news
                 * news_info : {"list":[{"title":"it's news","author":"jim","digest":"it's digest","show_cover":1,"cover_url":"http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQH  euPKmFLK0ZQ/0","content_url":"http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=203929886&idx=1&sn=628f964cf0c6d84c026881b6959aea8b#rd","source_url":"http://www.url.com"}]}
                 * content : KQb_w_Tiz-nSdVLoTV35Psmty8hGBulGhEdbb9SKs-o
                 */

                private String type;
                private NewsInfoBean news_info;
                private String content;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public NewsInfoBean getNews_info() {
                    return news_info;
                }

                public void setNews_info(NewsInfoBean news_info) {
                    this.news_info = news_info;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public static class NewsInfoBean {
                    private List<ListBean> list;

                    public List<ListBean> getList() {
                        return list;
                    }

                    public void setList(List<ListBean> list) {
                        this.list = list;
                    }

                    public static class ListBean {
                        /**
                         * title : it's news
                         * author : jim
                         * digest : it's digest
                         * show_cover : 1
                         * cover_url : http://mmbiz.qpic.cn/mmbiz/GE7et87vE9vicuCibqXsX9GPPLuEtBfXfKbE8sWdt2DDcL0dMfQWJWTVn1N8DxI0gcRmrtqBOuwQH  euPKmFLK0ZQ/0
                         * content_url : http://mp.weixin.qq.com/s?__biz=MjM5ODUwNTM3Ng==&mid=203929886&idx=1&sn=628f964cf0c6d84c026881b6959aea8b#rd
                         * source_url : http://www.url.com
                         */

                        private String title;
                        private String author;
                        private String digest;
                        private int show_cover;
                        private String cover_url;
                        private String content_url;
                        private String source_url;

                        public String getTitle() {
                            return title;
                        }

                        public void setTitle(String title) {
                            this.title = title;
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

                        public int getShow_cover() {
                            return show_cover;
                        }

                        public void setShow_cover(int show_cover) {
                            this.show_cover = show_cover;
                        }

                        public String getCover_url() {
                            return cover_url;
                        }

                        public void setCover_url(String cover_url) {
                            this.cover_url = cover_url;
                        }

                        public String getContent_url() {
                            return content_url;
                        }

                        public void setContent_url(String content_url) {
                            this.content_url = content_url;
                        }

                        public String getSource_url() {
                            return source_url;
                        }

                        public void setSource_url(String source_url) {
                            this.source_url = source_url;
                        }
                    }
                }
            }
        }
    }
}
