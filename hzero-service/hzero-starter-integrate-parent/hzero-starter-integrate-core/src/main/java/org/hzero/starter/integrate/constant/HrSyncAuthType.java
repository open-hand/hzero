package org.hzero.starter.integrate.constant;

/**
 * Hr组织信息同步授权类型
 *
 * @author zifeng.ding@hand-china.com 2020/01/14 10:30
 */
public class HrSyncAuthType {

    private HrSyncAuthType() {
    }

    /**
     * 用户凭证授权
     */
    public static final String SELF = "SELF";
    /**
     * 第三方授权
     */
    public static final String THIRD = "THIRD";
}
