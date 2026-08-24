package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/22
 */
public class GetUserDTO {

    /**
     * errcode : 0
     * errmsg : ok
     * userid : zhangsan
     * name : 李四
     * department : [1,2]
     * order : [1,2]
     * position : 后台工程师
     * mobile : 15913215421
     * gender : 1
     * email : zhangsan@gzdev.com
     * is_leader_in_dept : [1,0]
     * avatar : http://wx.qlogo.cn/mmopen/ajNVdqHZLLA3WJ6DSZUfiakYe37PKnQhBIeOQBO4czqrnZDS79FH5Wm5m4X69TBicnHFlhiafvDwklOpZeXYQQ2icg/0
     * telephone : 020-123456
     * enable : 1
     * alias : jackzhang
     * address : 广州市海珠区新港中路
     * extattr : {"attrs":[{"type":0,"name":"文本名称","text":{"value":"文本"}},{"type":1,"name":"网页名称","web":{"url":"http://www.test.com","title":"标题"}}]}
     * status : 1
     * qr_code : https://open.work.weixin.qq.com/wwopen/userQRCode?vcode=xxx
     * external_position : 产品经理
     * external_profile : {"external_corp_name":"企业简称","external_attr":[{"type":0,"name":"文本名称","text":{"value":"文本"}},{"type":1,"name":"网页名称","web":{"url":"http://www.test.com","title":"标题"}},{"type":2,"name":"测试app","miniprogram":{"appid":"wx8bd80126147df384","pagepath":"/index","title":"my miniprogram"}}]}
     */

    private long errcode;
    private String errmsg;
    private String userid;
    private String name;
    private String position;
    private String mobile;
    private String gender;
    private String email;
    private String avatar;
    private String telephone;
    private int enable;
    private String alias;
    private String address;
    private ExtattrBean extattr;
    private int status;
    private String qr_code;
    private String external_position;
    private ExternalProfileBean external_profile;
    private List<Long> department;
    private List<Integer> order;
    private List<Integer> is_leader_in_dept;

    public long getErrcode() {
        return errcode;
    }

    public void setErrcode(long errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ExtattrBean getExtattr() {
        return extattr;
    }

    public void setExtattr(ExtattrBean extattr) {
        this.extattr = extattr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getExternal_position() {
        return external_position;
    }

    public void setExternal_position(String external_position) {
        this.external_position = external_position;
    }

    public ExternalProfileBean getExternal_profile() {
        return external_profile;
    }

    public void setExternal_profile(ExternalProfileBean external_profile) {
        this.external_profile = external_profile;
    }


    public List<Long> getDepartment() {
        return department;
    }

    public GetUserDTO setDepartment(List<Long> department) {
        this.department = department;
        return this;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public void setOrder(List<Integer> order) {
        this.order = order;
    }

    public List<Integer> getIs_leader_in_dept() {
        return is_leader_in_dept;
    }

    public void setIs_leader_in_dept(List<Integer> is_leader_in_dept) {
        this.is_leader_in_dept = is_leader_in_dept;
    }

    public static class ExtattrBean {
        private List<AttrsBean> attrs;

        public List<AttrsBean> getAttrs() {
            return attrs;
        }

        public void setAttrs(List<AttrsBean> attrs) {
            this.attrs = attrs;
        }

        public static class AttrsBean {
            /**
             * type : 0
             * name : 文本名称
             * text : {"value":"文本"}
             * web : {"url":"http://www.test.com","title":"标题"}
             */

            private int type;
            private String name;
            private TextBean text;
            private WebBean web;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public TextBean getText() {
                return text;
            }

            public void setText(TextBean text) {
                this.text = text;
            }

            public WebBean getWeb() {
                return web;
            }

            public void setWeb(WebBean web) {
                this.web = web;
            }

            public static class TextBean {
                /**
                 * value : 文本
                 */

                private String value;

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class WebBean {
                /**
                 * url : http://www.test.com
                 * title : 标题
                 */

                private String url;
                private String title;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }

    public static class ExternalProfileBean {
        /**
         * external_corp_name : 企业简称
         * external_attr : [{"type":0,"name":"文本名称","text":{"value":"文本"}},{"type":1,"name":"网页名称","web":{"url":"http://www.test.com","title":"标题"}},{"type":2,"name":"测试app","miniprogram":{"appid":"wx8bd80126147df384","pagepath":"/index","title":"my miniprogram"}}]
         */

        private String external_corp_name;
        private List<ExternalAttrBean> external_attr;

        public String getExternal_corp_name() {
            return external_corp_name;
        }

        public void setExternal_corp_name(String external_corp_name) {
            this.external_corp_name = external_corp_name;
        }

        public List<ExternalAttrBean> getExternal_attr() {
            return external_attr;
        }

        public void setExternal_attr(List<ExternalAttrBean> external_attr) {
            this.external_attr = external_attr;
        }

        public static class ExternalAttrBean {
            /**
             * type : 0
             * name : 文本名称
             * text : {"value":"文本"}
             * web : {"url":"http://www.test.com","title":"标题"}
             * miniprogram : {"appid":"wx8bd80126147df384","pagepath":"/index","title":"my miniprogram"}
             */

            private int type;
            private String name;
            private TextBeanX text;
            private WebBeanX web;
            private MiniprogramBean miniprogram;

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public TextBeanX getText() {
                return text;
            }

            public void setText(TextBeanX text) {
                this.text = text;
            }

            public WebBeanX getWeb() {
                return web;
            }

            public void setWeb(WebBeanX web) {
                this.web = web;
            }

            public MiniprogramBean getMiniprogram() {
                return miniprogram;
            }

            public void setMiniprogram(MiniprogramBean miniprogram) {
                this.miniprogram = miniprogram;
            }

            public static class TextBeanX {
                /**
                 * value : 文本
                 */

                private String value;

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class WebBeanX {
                /**
                 * url : http://www.test.com
                 * title : 标题
                 */

                private String url;
                private String title;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }

            public static class MiniprogramBean {
                /**
                 * appid : wx8bd80126147df384
                 * pagepath : /index
                 * title : my miniprogram
                 */

                private String appid;
                private String pagepath;
                private String title;

                public String getAppid() {
                    return appid;
                }

                public void setAppid(String appid) {
                    this.appid = appid;
                }

                public String getPagepath() {
                    return pagepath;
                }

                public void setPagepath(String pagepath) {
                    this.pagepath = pagepath;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }
            }
        }
    }
}
