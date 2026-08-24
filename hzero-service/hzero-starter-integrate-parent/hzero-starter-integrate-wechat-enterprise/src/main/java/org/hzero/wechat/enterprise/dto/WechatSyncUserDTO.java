package org.hzero.wechat.enterprise.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/8/28
 */
public class WechatSyncUserDTO {


    /**
     * userid : zhangsan
     * name : 张三
     * alias : jackzhang
     * mobile : 15913215421
     * department : [1,2]
     * order : [10,40]
     * position : 产品经理
     * gender : 1
     * email : zhangsan@gzdev.com
     * is_leader_in_dept : [1,0]
     * enable : 1
     * avatar_mediaid : 2-G6nrLmr5EC3MNb_-zL1dDdzkd0p7cNliYu9V5w7o8K0
     * telephone : 020-123456
     * address : 广州市海珠区新港中路
     * extattr : {"attrs":[{"type":0,"name":"文本名称","text":{"value":"文本"}},{"type":1,"name":"网页名称","web":{"url":"http://www.test.com","title":"标题"}}]}
     * to_invite : true
     * external_position : 高级产品经理
     * external_profile : {"external_corp_name":"企业简称","external_attr":[{"type":0,"name":"文本名称","text":{"value":"文本"}},{"type":1,"name":"网页名称","web":{"url":"http://www.test.com","title":"标题"}},{"type":2,"name":"测试app","miniprogram":{"appid":"wx8bd80126147df384","pagepath":"/index","title":"my miniprogram"}}]}
     */

    /**
     * userid	是	成员UserID。对应管理端的帐号，企业内必须唯一。不区分大小写，长度为1~64个字节。只能由数字、字母和“_-@.”四种字符组成，且第一个字符必须是数字或字母。
     * name	是	成员名称。长度为1~64个utf8字符
     * alias	否	成员别名。长度1~32个utf8字符
     * mobile	否	手机号码。企业内必须唯一，mobile/email二者不能同时为空
     * department	是	成员所属部门id列表,不超过20个
     * order	否	部门内的排序值，默认为0，成员次序以创建时间从小到大排列。数量必须和department一致，数值越大排序越前面。有效的值范围是[0, 2^32)
     * position	否	职务信息。长度为0~128个字符
     * gender	否	性别。1表示男性，2表示女性
     * email	否	邮箱。长度6~64个字节，且为有效的email格式。企业内必须唯一，mobile/email二者不能同时为空
     * telephone	否	座机。32字节以内，由纯数字或’-‘号组成。
     * is_leader_in_dept	否	个数必须和department一致，表示在所在的部门内是否为上级。1表示为上级，0表示非上级。在审批等应用里可以用来标识上级审批人
     * avatar_mediaid	否	成员头像的mediaid，通过素材管理接口上传图片获得的mediaid
     * enable	否	启用/禁用成员。1表示启用成员，0表示禁用成员
     * extattr	否	自定义字段。自定义字段需要先在WEB管理端添加，见扩展属性添加方法，否则忽略未知属性的赋值。与对外属性一致，不过只支持type=0的文本和type=1的网页类型，详细描述查看对外属性
     * to_invite	否	是否邀请该成员使用企业微信（将通过微信服务通知或短信或邮件下发邀请，每天自动下发一次，最多持续3个工作日），默认值为true。
     * external_profile	否	成员对外属性，字段详情见对外属性
     * external_position	否	对外职务，如果设置了该值，则以此作为对外展示的职务，否则以position来展示。长度12个汉字内
     * address	否	地址。长度最大128个字符
     */

    private String userid;
    private String name;
    private String alias;
    private String mobile;
    private String position;
    private int gender;
    private String email;
    private int enable;
    private String avatar_mediaid;
    private String telephone;
    private String address;
    private ExtattrBean extattr;
    private boolean to_invite;
    private String external_position;
    private ExternalProfileBean external_profile;
    private List<Long> department;
    private List<Integer> order;
    private List<Integer> is_leader_in_dept;
    /**
     * 对象的同步操作，新增：create、更新：update、删除：delete
     */
    private String syncType;
    /**
     * 增量同步时是否已经把平台部门id映射成微信部门id标志，与department一一对应
     */
    private List<Boolean> isDepartIdsMap;

    public String getUserid() {
        return userid;
    }

    public WechatSyncUserDTO setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    public String getName() {
        return name;
    }

    public WechatSyncUserDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getAlias() {
        return alias;
    }

    public WechatSyncUserDTO setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public WechatSyncUserDTO setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getPosition() {
        return position;
    }

    public WechatSyncUserDTO setPosition(String position) {
        this.position = position;
        return this;
    }


    public int getGender() {
        return gender;
    }

    public WechatSyncUserDTO setGender(int gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public WechatSyncUserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public int getEnable() {
        return enable;
    }

    public WechatSyncUserDTO setEnable(int enable) {
        this.enable = enable;
        return this;
    }

    public String getAvatar_mediaid() {
        return avatar_mediaid;
    }

    public WechatSyncUserDTO setAvatar_mediaid(String avatar_mediaid) {
        this.avatar_mediaid = avatar_mediaid;
        return this;
    }

    public String getTelephone() {
        return telephone;
    }

    public WechatSyncUserDTO setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public WechatSyncUserDTO setAddress(String address) {
        this.address = address;
        return this;
    }

    public ExtattrBean getExtattr() {
        return extattr;
    }

    public WechatSyncUserDTO setExtattr(ExtattrBean extattr) {
        this.extattr = extattr;
        return this;
    }

    public boolean isTo_invite() {
        return to_invite;
    }

    public WechatSyncUserDTO setTo_invite(boolean to_invite) {
        this.to_invite = to_invite;
        return this;
    }

    public String getExternal_position() {
        return external_position;
    }

    public WechatSyncUserDTO setExternal_position(String external_position) {
        this.external_position = external_position;
        return this;
    }

    public ExternalProfileBean getExternal_profile() {
        return external_profile;
    }

    public WechatSyncUserDTO setExternal_profile(ExternalProfileBean external_profile) {
        this.external_profile = external_profile;
        return this;
    }


    public List<Long> getDepartment() {
        return department;
    }

    public WechatSyncUserDTO setDepartment(List<Long> department) {
        this.department = department;
        return this;
    }

    public List<Integer> getOrder() {
        return order;
    }

    public WechatSyncUserDTO setOrder(List<Integer> order) {
        this.order = order;
        return this;
    }

    public List<Integer> getIs_leader_in_dept() {
        return is_leader_in_dept;
    }

    public WechatSyncUserDTO setIs_leader_in_dept(List<Integer> is_leader_in_dept) {
        this.is_leader_in_dept = is_leader_in_dept;
        return this;
    }

    public List<Boolean> getIsDepartIdsMap() {
        return isDepartIdsMap;
    }

    public WechatSyncUserDTO setIsDepartIdsMap(List<Boolean> isDepartIdsMap) {
        this.isDepartIdsMap = isDepartIdsMap;
        return this;
    }

    public String getSyncType() {
        return syncType;
    }

    public WechatSyncUserDTO setSyncType(String syncType) {
        this.syncType = syncType;
        return this;
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

    @Override
    public String toString() {
        return "WechatSyncUserDTO{" +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", mobile='" + mobile + '\'' +
                ", position='" + position + '\'' +
                ", gender=" + gender +
                ", email='" + email + '\'' +
                ", enable=" + enable +
                ", avatar_mediaid='" + avatar_mediaid + '\'' +
                ", telephone='" + telephone + '\'' +
                ", address='" + address + '\'' +
                ", extattr=" + extattr +
                ", to_invite=" + to_invite +
                ", external_position='" + external_position + '\'' +
                ", external_profile=" + external_profile +
                ", department=" + department +
                ", order=" + order +
                ", is_leader_in_dept=" + is_leader_in_dept +
                ", syncType='" + syncType + '\'' +
                ", isDepartIdsMap=" + isDepartIdsMap +
                '}';
    }
}
