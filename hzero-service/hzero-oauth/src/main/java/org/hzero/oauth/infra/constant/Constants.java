package org.hzero.oauth.infra.constant;

import org.hzero.common.HZeroService;

/**
 * 常量
 *
 * @author bojiangzhou 2018/08/03
 */
public interface Constants {

    String APP_CODE = HZeroService.Oauth.CODE;

    String CAS = "CAS";

    String SAML = "SAML";

    String IDM = "IDM";

    String AUTH = "AUTH";

    String NULL = "NULL";

    String CAS2 = "CAS2";

    String CAS3 = "CAS3";

    interface CacheKey {

        /**
         * Token存储
         */
        String ACCESS_TOKEN = "access_token:";

    }

    interface AuditType {

        /**
         * 登录
         */
        String LOGIN = "LOGIN";
        /**
         * 登出
         */
        String LOGOUT = "LOGOUT";
        /**
         * 登出
         */
        String LOGIN_FAILURE = "LOGIN_FAILURE";

    }
}
