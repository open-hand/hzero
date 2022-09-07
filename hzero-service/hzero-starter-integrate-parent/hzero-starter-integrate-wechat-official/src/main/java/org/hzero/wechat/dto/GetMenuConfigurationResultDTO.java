package org.hzero.wechat.dto;

import java.util.List;

public class GetMenuConfigurationResultDTO {

    /**
     * menu : {"button":[{"type":"click","name":"今日歌曲","key":"V1001_TODAY_MUSIC","sub_button":[]}],"menuid":208396938}
     * conditionalmenu : [{"button":[{"type":"click","name":"今日歌曲","key":"V1001_TODAY_MUSIC","sub_button":[]},{"name":"菜单","sub_button":[{"type":"view","name":"搜索","url":"http://www.soso.com/","sub_button":[]},{"type":"view","name":"视频","url":"http://v.qq.com/","sub_button":[]},{"type":"click","name":"赞一下我们","key":"V1001_GOOD","sub_button":[]}]}],"matchrule":{"group_id":2,"sex":1,"country":"中国","province":"广东","city":"广州","client_platform_type":2},"menuid":208396993}]
     */

    private MenuBean menu;
    private List<ConditionalmenuBean> conditionalmenu;

    public MenuBean getMenu() {
        return menu;
    }

    public void setMenu(MenuBean menu) {
        this.menu = menu;
    }

    public List<ConditionalmenuBean> getConditionalmenu() {
        return conditionalmenu;
    }

    public void setConditionalmenu(List<ConditionalmenuBean> conditionalmenu) {
        this.conditionalmenu = conditionalmenu;
    }

    public static class MenuBean {
        /**
         * button : [{"type":"click","name":"今日歌曲","key":"V1001_TODAY_MUSIC","sub_button":[]}]
         * menuid : 208396938
         */

        private int menuid;
        private List<ButtonBean> button;

        public int getMenuid() {
            return menuid;
        }

        public void setMenuid(int menuid) {
            this.menuid = menuid;
        }

        public List<ButtonBean> getButton() {
            return button;
        }

        public void setButton(List<ButtonBean> button) {
            this.button = button;
        }

        public static class ButtonBean {
            /**
             * type : click
             * name : 今日歌曲
             * key : V1001_TODAY_MUSIC
             * sub_button : []
             */

            private String type;
            private String name;
            private String key;
            private List<?> sub_button;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public List<?> getSub_button() {
                return sub_button;
            }

            public void setSub_button(List<?> sub_button) {
                this.sub_button = sub_button;
            }
        }
    }

    public static class ConditionalmenuBean {
        /**
         * button : [{"type":"click","name":"今日歌曲","key":"V1001_TODAY_MUSIC","sub_button":[]},{"name":"菜单","sub_button":[{"type":"view","name":"搜索","url":"http://www.soso.com/","sub_button":[]},{"type":"view","name":"视频","url":"http://v.qq.com/","sub_button":[]},{"type":"click","name":"赞一下我们","key":"V1001_GOOD","sub_button":[]}]}]
         * matchrule : {"group_id":2,"sex":1,"country":"中国","province":"广东","city":"广州","client_platform_type":2}
         * menuid : 208396993
         */

        private MatchruleBean matchrule;
        private int menuid;
        private List<ButtonBeanX> button;

        public MatchruleBean getMatchrule() {
            return matchrule;
        }

        public void setMatchrule(MatchruleBean matchrule) {
            this.matchrule = matchrule;
        }

        public int getMenuid() {
            return menuid;
        }

        public void setMenuid(int menuid) {
            this.menuid = menuid;
        }

        public List<ButtonBeanX> getButton() {
            return button;
        }

        public void setButton(List<ButtonBeanX> button) {
            this.button = button;
        }

        public static class MatchruleBean {
            /**
             * group_id : 2
             * sex : 1
             * country : 中国
             * province : 广东
             * city : 广州
             * client_platform_type : 2
             */

            private int group_id;
            private int sex;
            private String country;
            private String province;
            private String city;
            private int client_platform_type;

            public int getGroup_id() {
                return group_id;
            }

            public void setGroup_id(int group_id) {
                this.group_id = group_id;
            }

            public int getSex() {
                return sex;
            }

            public void setSex(int sex) {
                this.sex = sex;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getProvince() {
                return province;
            }

            public void setProvince(String province) {
                this.province = province;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public int getClient_platform_type() {
                return client_platform_type;
            }

            public void setClient_platform_type(int client_platform_type) {
                this.client_platform_type = client_platform_type;
            }
        }

        public static class ButtonBeanX {
            /**
             * type : click
             * name : 今日歌曲
             * key : V1001_TODAY_MUSIC
             * sub_button : []
             */

            private String type;
            private String name;
            private String key;
            private List<?> sub_button;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public List<?> getSub_button() {
                return sub_button;
            }

            public void setSub_button(List<?> sub_button) {
                this.sub_button = sub_button;
            }
        }
    }
}
