package org.hzero.boot.platform.profile.constant;

import org.hzero.common.HZeroService;

/**
 * description
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/08 11:26
 */
public class ProfileConstants {

    /**
     * 配置维护值应用层级-角色级
     */
    public static final String ROLE = "ROLE";

    /**
     * 配置维护值应用层级-用户级
     */
    public static final String USER = "USER";

    /**
     * 配置维护值应用层级-全局(对应该租户下的所有)
     */
    public static final String GLOBAL = "GLOBAL";

    /**
     * 配置维护
     */
    public static final String PROFILE_KEY = HZeroService.Platform.CODE + ":profile";

    public static class ErrorCode{
        public static final String PROFILE_FEIGN_FAIL = "hpfm.error.profile_value.feign_fail";
    }


}
