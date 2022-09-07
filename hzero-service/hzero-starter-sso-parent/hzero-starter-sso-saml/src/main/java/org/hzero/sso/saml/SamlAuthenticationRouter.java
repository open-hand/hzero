package org.hzero.sso.saml;

import java.io.IOException;
import java.util.Timer;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.common.SAMLException;
import org.opensaml.saml2.metadata.provider.HTTPMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.parse.ParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;

import org.hzero.sso.core.common.SsoAuthenticationRouter;
import org.hzero.sso.core.domain.Domain;
import org.hzero.sso.core.support.SsoContextHolder;

/**
 * @author bojiangzhou 2020/08/21
 */
public class SamlAuthenticationRouter extends SAMLEntryPoint implements SsoAuthenticationRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SamlAuthenticationRouter.class);

    protected ParserPool parserPool;
    protected ExtendedMetadata extendedMetadata;
    protected Timer backgroundTaskTimer = new Timer(true);
    protected HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

    public SamlAuthenticationRouter() {}

    /**
     * Useless
     */
    @Override
    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

    /**
     * Useless
     */
    @Override
    protected final boolean processFilter(HttpServletRequest request) {
        return false;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Domain domain = SsoContextHolder.getNonNullDomain();

        try {
            boolean addFlag = true;
            for (ExtendedMetadataDelegate dto : metadata.getAvailableProviders()) {
                if (dto.getDelegate() instanceof HTTPMetadataProvider) {
                    HTTPMetadataProvider provider = (HTTPMetadataProvider) dto.getDelegate();
                    if (provider.getMetadataURI().equals(domain.getSamlMetaUrl()) && !StringUtils.isBlank(metadata.getDefaultIDP())) {
                        addFlag = false;
                    }
                    /* else {
                        // what's this ?
                        metadata.removeMetadataProvider(dto);
                    }*/
                }
            }
            if (addFlag) {
                ExtendedMetadataDelegate newMetadataDelegate = newExtendedMetadataProvider(domain);
                metadata.addMetadataProvider(newMetadataDelegate);
                metadata.afterPropertiesSet();
            }


            SAMLMessageContext context = contextProvider.getLocalAndPeerEntity(request, response);

//            if (isECP(context)) {
//                initializeECP(context, e);
//            } else if (isDiscovery(context)) {
//                initializeDiscovery(context);
//            } else {
//                initializeSSO(context, e);
//            }
            initializeSSO(context, authException);

        } catch (SAMLException | MetadataProviderException | MessageEncodingException e1) {
            LOGGER.debug("Error initializing entry point", e1);
            throw new ServletException(e1);
        }
    }

    protected ExtendedMetadataDelegate newExtendedMetadataProvider(Domain domain) throws MetadataProviderException {
        String idpSSOCircleMetadataURL = domain.getSamlMetaUrl();
        HTTPMetadataProvider httpMetadataProvider = new HTTPMetadataProvider(this.backgroundTaskTimer, this.httpClient, idpSSOCircleMetadataURL);
        httpMetadataProvider.setParserPool(this.parserPool);
        ExtendedMetadataDelegate extendedMetadataDelegate = new ExtendedMetadataDelegate(httpMetadataProvider, this.extendedMetadata);
        extendedMetadataDelegate.setMetadataTrustCheck(false);
        extendedMetadataDelegate.setMetadataRequireSignature(false);
        this.backgroundTaskTimer.purge();
        return extendedMetadataDelegate;
    }

    public void setExtendedMetadata(ExtendedMetadata extendedMetadata) {
        this.extendedMetadata = extendedMetadata;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setParserPool(ParserPool parserPool) {
        this.parserPool = parserPool;
    }
}
