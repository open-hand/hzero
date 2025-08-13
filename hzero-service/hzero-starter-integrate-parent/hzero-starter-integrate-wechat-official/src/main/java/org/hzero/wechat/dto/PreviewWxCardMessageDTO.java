package org.hzero.wechat.dto;

public class PreviewWxCardMessageDTO {
    /**
     * touser : OPENID
     * wxcard : {"card_id":"123dsdajkasd231jhksad","card_ext":{"code":"","openid":"","timestamp":"1402057159","signature":"017bb17407c8e0058a66d72dcc61632b70f511ad"}}
     * msgtype : wxcard
     */

    private String touser;
    private WxcardBean wxcard;
    private String msgtype;

    /**
     * { "touser":"OPENID",
     *   "wxcard":{
     *            "card_id":"123dsdajkasd231jhksad",
     *             "card_ext": "{"code":"","openid":"","timestamp":"1402057159","signature":"017bb17407c8e0058a66d72dcc61632b70f511ad"}"
     *             },
     *   "msgtype":"wxcard"
     * }
     */
    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public WxcardBean getWxcard() {
        return wxcard;
    }

    public void setWxcard(WxcardBean wxcard) {
        this.wxcard = wxcard;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }


    public static class WxcardBean {
        /**
         * card_id : 123dsdajkasd231jhksad
         * card_ext : {"code":"","openid":"","timestamp":"1402057159","signature":"017bb17407c8e0058a66d72dcc61632b70f511ad"}
         */

        private String card_id;
        private CardExtBean card_ext;

        public String getCard_id() {
            return card_id;
        }

        public void setCard_id(String card_id) {
            this.card_id = card_id;
        }

        public CardExtBean getCard_ext() {
            return card_ext;
        }

        public void setCard_ext(CardExtBean card_ext) {
            this.card_ext = card_ext;
        }

        public static class CardExtBean {
            /**
             * code :
             * openid :
             * timestamp : 1402057159
             * signature : 017bb17407c8e0058a66d72dcc61632b70f511ad
             */

            private String code;
            private String openid;
            private String timestamp;
            private String signature;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getOpenid() {
                return openid;
            }

            public void setOpenid(String openid) {
                this.openid = openid;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            public String getSignature() {
                return signature;
            }

            public void setSignature(String signature) {
                this.signature = signature;
            }
        }
    }
}
