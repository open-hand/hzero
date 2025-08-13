package org.hzero.sso.saml;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.common.SAMLException;
import org.opensaml.common.SAMLRuntimeException;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.SAMLConstants;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.processor.SAMLProcessor;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.util.Assert;

import org.hzero.sso.core.common.SsoAuthenticationProvider;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.exception.SsoServiceException;
import org.hzero.sso.core.exception.SsoUserNotFoundException;
import org.hzero.sso.core.security.SsoAuthenticationToken;
import org.hzero.sso.core.service.SsoUserDetailsService;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 *
 * @author bojiangzhou 2020/08/21
 */
public class SamlAuthenticationProvider extends SAMLAuthenticationProvider implements SsoAuthenticationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(SamlAuthenticationProvider.class);

    protected SAMLProcessor processor;
    protected SAMLContextProvider contextProvider;

    private static final String ASSERTION_URI = "/saml/SSO";

    protected final SsoUserDetailsService userDetailsService;

    public SamlAuthenticationProvider(SsoUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Useless
     */
    @Override
    public final boolean supports(Class aClass) {
        return false;
    }

    @Override
    public Authentication authenticate(HttpServletRequest request, HttpServletResponse response) {
        if (!isSamlAssertionURI(request)) {
            throw new SsoServiceException("Invalid saml sso login request");
        }
        Domain domain = SsoContextHolder.getNonNullDomain();

        Authentication authRequest = extractAuthentication(request, response, domain);

        return attemptAuthentication(request, authRequest, domain);
    }

    protected Authentication extractAuthentication(HttpServletRequest request, HttpServletResponse response, Domain domain) throws AuthenticationException {
        try {

            LOGGER.debug("Attempting SAML2 authentication using profile {}", getProfileName());
            SAMLMessageContext context = contextProvider.getLocalEntity(request, response);
            processor.retrieveMessage(context);

            // Override set values
            context.setCommunicationProfileId(getProfileName());
            context.setLocalEntityEndpoint(SAMLUtil.getEndpoint(context.getLocalEntityRoleMetadata().getEndpoints(), context.getInboundSAMLBinding(), context.getInboundMessageTransport()));

            return new SAMLAuthenticationToken(context);

        } catch (SAMLException | SecurityException e) {
            LOGGER.debug("Incoming SAML message is invalid", e);
            throw new AuthenticationServiceException("Incoming SAML message is invalid", e);
        } catch (MetadataProviderException e) {
            LOGGER.debug("Error determining metadata contracts", e);
            throw new AuthenticationServiceException("Error determining metadata contracts", e);
        } catch (MessageDecodingException e) {
            LOGGER.debug("Error decoding incoming SAML message", e);
            throw new AuthenticationServiceException("Error decoding incoming SAML message", e);
        }
    }

    protected Authentication attemptAuthentication(HttpServletRequest request, Authentication authentication, Domain domain) throws AuthenticationException {
        SAMLAuthenticationToken token = (SAMLAuthenticationToken) authentication;
        SAMLMessageContext context = token.getCredentials();

        if (context == null) {
            throw new AuthenticationServiceException(
                    "SAML message context is not available in the authentication token");
        }

        SAMLCredential credential;

        try {
            if (SAMLConstants.SAML2_WEBSSO_PROFILE_URI.equals(context.getCommunicationProfileId())) {
                credential = consumer.processAuthenticationResponse(context);
            } else if (SAMLConstants.SAML2_HOK_WEBSSO_PROFILE_URI.equals(context.getCommunicationProfileId())) {
                credential = hokConsumer.processAuthenticationResponse(context);
            } else {
                throw new SAMLException(
                        "Unsupported profile encountered in the context " + context.getCommunicationProfileId());
            }
        } catch (SAMLRuntimeException | SAMLException e) {
            LOGGER.debug("Error validating SAML message", e);
            samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
            throw new AuthenticationServiceException("Error validating SAML message", e);
        } catch (ValidationException | SecurityException e) {
            LOGGER.debug("Error validating signature", e);
            samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
            throw new AuthenticationServiceException("Error validating SAML message signature", e);
        } catch (DecryptionException e) {
            LOGGER.debug("Error decrypting SAML message", e);
            samlLogger.log(SAMLConstants.AUTH_N_RESPONSE, SAMLConstants.FAILURE, context, e);
            throw new AuthenticationServiceException("Error decrypting SAML message", e);
        }
        //SAMLPrincipal sAMLPrincipal = (SAMLPrincipal) getUserDetails().loadUserBySAML(credential);
        String username = credential.getNameID().getValue();
        UserDetails user = this.userDetailsService.retrieveUser(username, domain.getTenants());
        if (user == null) {
            throw new SsoUserNotFoundException("User account is Not Exists");
        }
        Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        return new SsoAuthenticationToken(user, authentication, user.getAuthorities());
    }

    protected boolean isSamlAssertionURI(HttpServletRequest request) {
        return request.getRequestURI().endsWith(ASSERTION_URI);
    }

    protected String getProfileName() {
        return SAMLConstants.SAML2_WEBSSO_PROFILE_URI;
    }

    public void setSAMLProcessor(SAMLProcessor processor) {
        Assert.notNull(processor, "SAML Processor can't be null");
        this.processor = processor;
    }

    public void setContextProvider(SAMLContextProvider contextProvider) {
        Assert.notNull(contextProvider, "Context provider can't be null");
        this.contextProvider = contextProvider;
    }

}
