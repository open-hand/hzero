package org.hzero.admin.infra.constant;

/**
 * Governances
 *
 * @author bojiangzhou 2018/12/13
 */
public class Governance {

    public static final Long DEFAULT_ID = 0L;

    /**
     * 熔断配置刷新相关
     */
    public static final Integer FAIL_REFRESH = 0;
    public static final Integer SUCCESS_REFRESH = 1;
    public static final String ERROR_GETCONFIG_MSG = "无法获取相关服务信息";
    public static final String ERROR_REFRESH_MSG = "配置刷新失败";
    public static final String SUCCESS_REFRESH_MSG = "配置刷新成功";

    public static final Integer ENABLE_FLAG = 1;

}
