package org.hzero.starter.sms.entity;

/**
 * 短信接收人
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/23 17:04
 */
public class SmsReceiver {

    /**
     * 短信接收者：电话号码
     */
    private String phone;
    /**
     * 短信接收者：国际冠码
     */
    private String idd = "+86";

    public String getPhone() {
        return phone;
    }

    public SmsReceiver setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getIdd() {
        return idd;
    }

    public SmsReceiver setIdd(String idd) {
        this.idd = idd;
        return this;
    }
}
