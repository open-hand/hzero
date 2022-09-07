package org.hzero.sso.core.support;

import java.util.*;
import javax.annotation.Nonnull;

import org.springframework.util.CollectionUtils;

import org.hzero.sso.core.common.SsoAuthenticationFactory;
import org.hzero.sso.core.constant.SsoEnum;
import org.hzero.sso.core.domain.Domain;

/**
 * 认证服务定位器
 *
 * @author bojiangzhou 2020/08/17
 */
public class SsoAuthenticationLocator {

    private final Map<String, SsoAuthenticationService> map = new HashMap<>();
    private final Set<String> ssoTypes;

    public SsoAuthenticationLocator(List<SsoAuthenticationFactory> factoryList) {
        for (SsoAuthenticationFactory factory : factoryList) {
            Set<String> types = factory.supportiveSsoType();
            if (CollectionUtils.isEmpty(types)) {
                throw new IllegalArgumentException("SsoAuthenticationFactory's supportiveSsoType is empty.");
            }

            SsoAuthenticationService authenticationService = new SsoAuthenticationService(factory);

            for (String type : types) {
                if (map.containsKey(type.toLowerCase())) {
                    throw new IllegalArgumentException("SsoType duplicated for [" + type + "]");
                }
                map.put(type.toLowerCase(), authenticationService);
            }
        }

        ssoTypes = map.keySet();
    }

    @Nonnull
    public SsoAuthenticationService getAuthenticationService() {
        String ssoType = SsoContextHolder.getSsoType();

        if (SsoEnum.NULL.code().equalsIgnoreCase(ssoType)) {
            return map.get(SsoEnum.STANDARD.code());
        }

        if (ssoType != null) {
            return map.get(ssoType);
        }

        SsoAuthenticationService service;
        Domain domain = SsoContextHolder.getDomain();
        if (domain == null
                || SsoEnum.NULL.code().equalsIgnoreCase(domain.getSsoTypeCode())
                || (service = map.get(domain.getSsoTypeCode())) == null) {
            service = map.get(SsoEnum.STANDARD.code());
        }

        return service;
    }

    @Nonnull
    public Set<String> registeredSsoTypes() {
        return ssoTypes;
    }


}
