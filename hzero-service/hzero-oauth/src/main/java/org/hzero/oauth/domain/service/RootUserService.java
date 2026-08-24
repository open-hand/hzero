package org.hzero.oauth.domain.service;

import org.apache.commons.lang3.BooleanUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.boot.oauth.config.BootOauthProperties;

/**
 * Root 用户服务
 *
 * @author bojiangzhou 2020/05/12
 */
public class RootUserService {

    private static BootOauthProperties properties;

    /**
     * If enabled root func ( config key <i>hzero.user.enable-root=true</i> ), and current user is admin, then return true, otherwise return false.
     *
     * @return true - Root enabled
     */
    public static boolean isRootUser() {
        CustomUserDetails self = DetailsHelper.getUserDetails();
        return isRootUser(self);
    }

    /**
     * If enabled root func ( config key <i>hzero.user.enable-root=true</i> ), and current user is admin, then return true, otherwise return false.
     *
     * @param self CustomUserDetails
     * @return true - Root enabled
     */
    public static boolean isRootUser(CustomUserDetails self) {
        if (self == null) {
            return false;
        }

        if (!BooleanUtils.isTrue(self.getAdmin())) {
            return false;
        }

        if (properties == null) {
            synchronized (RootUserService.class) {
                properties = ApplicationContextHelper.getContext().getBean(BootOauthProperties.class);
            }
        }

        return properties.getUser().isEnableRoot();
    }

}
