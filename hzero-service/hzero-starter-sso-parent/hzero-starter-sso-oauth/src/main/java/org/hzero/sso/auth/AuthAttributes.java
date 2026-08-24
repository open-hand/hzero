package org.hzero.sso.auth;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.hzero.sso.core.constant.SsoEnum;

/**
 *
 * @author bojiangzhou 2020/08/19
 */
public class AuthAttributes {

    public static final Set<String> SSO_TYPE = ImmutableSet.of(SsoEnum.AUTH.code());

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BASIC = "Basic ";
    public static final String HEADER_BEARER = "Bearer ";

}
