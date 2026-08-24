package org.hzero.sso.cas;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import org.hzero.sso.cas.validator.ConfigurableTicketValidator;
import org.hzero.sso.cas.validator.CustomCas10TicketValidator;
import org.hzero.sso.cas.validator.CustomCas20ProxyTicketValidator;
import org.hzero.sso.cas.validator.CustomCas30ProxyTicketValidator;
import org.hzero.sso.core.common.SsoAuthenticationProviderAdapter;
import org.hzero.sso.core.common.config.SsoConfigManager;
import org.hzero.sso.core.constant.SsoEnum;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.security.SsoAuthenticationToken;
import org.hzero.sso.core.service.SsoUserDetailsService;

public class CasAuthenticationProvider extends SsoAuthenticationProviderAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CasAuthenticationProvider.class);

    public static final String CAS_STATEFUL_IDENTIFIER = "_cas_stateful_";

    private final CasServiceHelper casServiceHelper;
    /**
     * 单点配置管理器
     */
    private SsoConfigManager ssoConfigManager;

    private String ticketParameter = ServiceProperties.DEFAULT_CAS_ARTIFACT_PARAMETER;

    public CasAuthenticationProvider(SsoUserDetailsService userDetailsService,
                                     CasServiceHelper casServiceHelper) {
        super(userDetailsService);
        this.casServiceHelper = casServiceHelper;
    }

    @Autowired
    public void setSsoConfigManager(SsoConfigManager ssoConfigManager) {
        this.ssoConfigManager = ssoConfigManager;
    }

    /**
     * 提取票据，构造 SsoAuthenticationToken
     */
    @Override
    protected Authentication extractAuthentication(final HttpServletRequest request, final HttpServletResponse response, final Domain domain) throws AuthenticationException {
        String username = CAS_STATEFUL_IDENTIFIER;
        String password = request.getParameter(ticketParameter);
        if (password == null) {
            LOGGER.debug("Failed to obtain an artifact (cas ticket)");
            password = "";
        }

        return new SsoAuthenticationToken(username, password);
    }

    /**
     * 票据认证，加载用户信息
     */
    @Override
    protected Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication, final Domain domain) throws AuthenticationException {
        SsoAuthenticationToken result;
        try {
            TicketValidator ticketValidator = loadTicketValidator(domain);

            // 校验票据
            String serviceUrl = casServiceHelper.createServiceUrl(request, response, domain);
            String ticket = authentication.getCredentials().toString();

            LOGGER.debug("cas ticket validating, ticket: [{}], serviceUrl: [{}]", ticket, serviceUrl);

            Assertion assertion = ticketValidator.validate(ticket, serviceUrl);

            LOGGER.debug("cas ticket validate success, ticket: [{}]", ticket);

            // ticket 放到 session 中
            request.getSession().setAttribute(CasAttributes.ATTRIBUTE_CAS_TICKET, ticket);

            // 加载用户信息
            final UserDetails userDetails = loadUserByAssertion(assertion, domain);

            // 创建 token
            result = new SsoAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        } catch (final TicketValidationException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 根据域名配置选择合适的校验器
     */
    protected TicketValidator loadTicketValidator(Domain domain) {
        String serverUrl = domain.getSsoServerUrl();
        String ssoType = domain.getSsoTypeCode();

        AbstractUrlBasedTicketValidator ticketValidator = null;
        if (SsoEnum.CAS.code().equalsIgnoreCase(ssoType)) {
            ticketValidator = new CustomCas10TicketValidator(serverUrl);
        } else {
            Cas20ProxyTicketValidator proxyTicketValidator = null;
            if (SsoEnum.CAS2.code().equalsIgnoreCase(ssoType)) {
                proxyTicketValidator = new CustomCas20ProxyTicketValidator(serverUrl);
            } else if (SsoEnum.CAS3.code().equalsIgnoreCase(ssoType)) {
                proxyTicketValidator = new CustomCas30ProxyTicketValidator(serverUrl);
            }

            if (proxyTicketValidator == null) {
                throw new AuthenticationServiceException("Obtain ticketValidator was failed.");
            }

            proxyTicketValidator.setAcceptAnyProxy(true);
            proxyTicketValidator.setProxyGrantingTicketStorage(new ProxyGrantingTicketStorageImpl());
            ticketValidator = proxyTicketValidator;
        }

        this.setConfiguration(ticketValidator, domain);
        return ticketValidator;
    }

    /**
     * 加载用户信息
     */
    protected UserDetails loadUserByAssertion(final Assertion assertion, Domain domain) {
        String loginNameField = domain.getLoginNameField();
        String username = null;
        if (StringUtils.isNotBlank(loginNameField)) {
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            if (MapUtils.isNotEmpty(attributes) && attributes.get(loginNameField) != null) {
                username = attributes.get(loginNameField).toString();
            }
        }
        if (StringUtils.isBlank(username)) {
            username = assertion.getPrincipal().getName();
        }

        return getUserDetailsService().retrieveUser(username, domain.getTenants());
    }

    public void setTicketParameter(String ticketParameter) {
        this.ticketParameter = ticketParameter;
    }

    protected void setConfiguration(AbstractUrlBasedTicketValidator ticketValidator, Domain domain) {
        if (ticketValidator instanceof ConfigurableTicketValidator) {
            ConfigurableTicketValidator configurableTicketValidator = (ConfigurableTicketValidator) ticketValidator;
            // 设置url后缀
            String urlSuffix = this.ssoConfigManager.getConfigValue(domain, CasAttributes.CONFIG_FIELD_URL_SUFFIX);
            configurableTicketValidator.setUrlSuffix(urlSuffix);
        }
    }
}