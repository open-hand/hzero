package org.hzero.sso.cas;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.hzero.common.HZeroService;
import org.hzero.sso.core.constant.SsoEnum;

/**
 *
 * @author bojiangzhou 2020/08/19
 */
public class CasAttributes {

    public static final Set<String> SSO_TYPE = ImmutableSet.of(SsoEnum.CAS.code(), SsoEnum.CAS2.code(), SsoEnum.CAS3.code());

    public static final String ATTRIBUTE_CAS_TICKET = "ATTRIBUTE_CAS_TICKET";

    public static final String KEY_CAS_TICKET_TOKEN = HZeroService.Oauth.CODE + ":cas:cas_ticket_token:";
    public static final String KEY_CAS_TOKEN_TICKET = HZeroService.Oauth.CODE + ":cas:cas_token_ticket:";

    /**
     * 配置字段：urlSuffix
     */
    public static final String CONFIG_FIELD_URL_SUFFIX = "urlSuffix";
}
