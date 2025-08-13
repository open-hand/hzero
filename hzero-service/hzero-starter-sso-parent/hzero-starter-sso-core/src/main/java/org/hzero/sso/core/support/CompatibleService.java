package org.hzero.sso.core.support;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.choerodon.core.convertor.ApplicationContextHelper;

import org.hzero.sso.core.configuration.SsoProperties;

/**
 * 兼容模式工具
 *
 * @author bojiangzhou 2020/10/22
 */
public class CompatibleService {

    private CompatibleService() {
    }

    public interface ISsoMatcher {
        String getSsoUri();

        String getSsoType();

        default RequestMatcher getSsoRequestMatcher() {
            return new AntPathRequestMatcher(this.getSsoUri());
        }

        ;
    }

    public enum SsoMatcher implements ISsoMatcher {
        CAS {
            @Override
            public String getSsoUri() {
                return "/login/cas";
            }

            @Override
            public String getSsoType() {
                return "cas2";
            }
        },

        OAUTH {
            @Override
            public String getSsoUri() {
                return "/login/auth2/**";
            }

            @Override
            public String getSsoType() {
                return "auth";
            }
        },

        SAML {
            @Override
            public String getSsoUri() {
                return "/saml/**";
            }

            @Override
            public String getSsoType() {
                return "saml";
            }
        },

        IDM {
            @Override
            public String getSsoUri() {
                return "/login/idm/**";
            }

            @Override
            public String getSsoType() {
                return "idm";
            }
        },

        AZURE {
            @Override
            public String getSsoUri() {
                return "/login/azure/**";
            }

            @Override
            public String getSsoType() {
                return "azure";
            }
        };

        private static final SsoMatcher[] MATCHERS = SsoMatcher.values();
        private static final String[] SSO_URIS = Arrays.stream(MATCHERS).map(ISsoMatcher::getSsoUri).toArray(String[]::new);

        public static SsoMatcher match(HttpServletRequest request) {
            for (SsoMatcher matcher : MATCHERS) {
                if (matcher.getSsoRequestMatcher().matches(request)) {
                    return matcher;
                }
            }
            return null;
        }


    }

    /**
     * @return 返回所有 sso uri
     */
    public static String[] getSsoUris() {
        return SsoMatcher.SSO_URIS;
    }

    /**
     * @return 判断兼容模式下的sso请求
     */
    public static boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (isEnableCompatibilityMode()) {
            return SsoMatcher.match(request) != null;
        }
        return false;
    }

    /**
     * @return 单点类型
     */
    public static String extractSsoType(HttpServletRequest request) {
        if (isEnableCompatibilityMode()) {
            SsoMatcher matcher = SsoMatcher.match(request);
            if (matcher != null) {
                return matcher.getSsoType();
            }
        }
        return null;
    }

    private static SsoProperties ssoProperties;

    /**
     * @return 是否启用了兼容模式
     */
    private static boolean isEnableCompatibilityMode() {
        if (ssoProperties == null) {
            synchronized (CompatibleService.class) {
                if (ssoProperties == null) {
                    ssoProperties = ApplicationContextHelper.getContext().getBean(SsoProperties.class);
                }
            }
        }
        return ssoProperties.isEnableCompatibilityMode();
    }

}
