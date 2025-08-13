package org.hzero.sso.azure;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.hzero.sso.core.constant.SsoEnum;

/**
 *
 * @author bojiangzhou 2020/08/19
 */
public class AzureAttributes {

    public static final Set<String> SSO_TYPE = ImmutableSet.of(SsoEnum.AZURE.code());

}
