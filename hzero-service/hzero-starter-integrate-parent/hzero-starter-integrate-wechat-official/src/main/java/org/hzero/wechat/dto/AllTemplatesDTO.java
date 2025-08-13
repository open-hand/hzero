package org.hzero.wechat.dto;

import java.util.List;

/**
 * @Author J
 * @Date 2019/10/15
 */
public class AllTemplatesDTO extends DefaultResultDTO{

    private List<TemplateListBean> template_list;

    public List<TemplateListBean> getTemplate_list() {
        return template_list;
    }

    public void setTemplate_list(List<TemplateListBean> template_list) {
        this.template_list = template_list;
    }

    public static class TemplateListBean {
        /**
         * template_id : iPk5sOIt5X_flOVKn5GrTFpncEYTojx6ddbt8WYoV5s
         * title : 领取奖金提醒
         * primary_industry : IT科技
         * deputy_industry : 互联网|电子商务
         * content : { {result.DATA} }

         领奖金额:{ {withdrawMoney.DATA} }
         领奖  时间:    { {withdrawTime.DATA} }
         银行信息:{ {cardInfo.DATA} }
         到账时间:  { {arrivedTime.DATA} }
         { {remark.DATA} }
         * example : 您已提交领奖申请

         领奖金额：xxxx元
         领奖时间：2013-10-10 12:22:22
         银行信息：xx银行(尾号xxxx)
         到账时间：预计xxxxxxx

         预计将于xxxx到达您的银行卡
         */

        private String template_id;
        private String title;
        private String primary_industry;
        private String deputy_industry;
        private String content;
        private String example;

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrimary_industry() {
            return primary_industry;
        }

        public void setPrimary_industry(String primary_industry) {
            this.primary_industry = primary_industry;
        }

        public String getDeputy_industry() {
            return deputy_industry;
        }

        public void setDeputy_industry(String deputy_industry) {
            this.deputy_industry = deputy_industry;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getExample() {
            return example;
        }

        public void setExample(String example) {
            this.example = example;
        }
    }


}
