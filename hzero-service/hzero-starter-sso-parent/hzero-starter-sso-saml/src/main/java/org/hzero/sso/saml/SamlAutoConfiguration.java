package org.hzero.sso.saml;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.StaticBasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.saml.SAMLBootstrap;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.key.JKSKeyManager;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.log.SAMLDefaultLogger;
import org.springframework.security.saml.metadata.*;
import org.springframework.security.saml.parser.ParserPoolHolder;
import org.springframework.security.saml.processor.*;
import org.springframework.security.saml.trust.httpclient.TLSProtocolConfigurer;
import org.springframework.security.saml.util.VelocityFactory;
import org.springframework.security.saml.websso.*;

import org.hzero.sso.core.configuration.SsoProperties;
import org.hzero.sso.core.configuration.SsoPropertyService;
import org.hzero.sso.core.service.SsoUserDetailsService;
import org.hzero.sso.saml.metadata.CustomMetadataGenerator;
import org.hzero.sso.saml.metadata.SamlMetadataExtendedFilter;
import org.hzero.sso.saml.util.KeyStoreLocator;

@Configuration
@EnableConfigurationProperties(SsoSamlProperties.class)
public class SamlAutoConfiguration {

    @Autowired
    private SsoSamlProperties ssoSamlProperties;
    @Autowired
    private SsoProperties ssoProperties;
    @Autowired
    private SsoUserDetailsService userDetailsService;
    @Autowired
    private SsoPropertyService ssoPropertyService;

    @Bean(name = "samlAuthenticationRouter")
    @ConditionalOnMissingBean(SamlAuthenticationRouter.class)
    public SamlAuthenticationRouter samlAuthenticationRouter() {
        SamlAuthenticationRouter authenticationRouter = new SamlAuthenticationRouter();
        authenticationRouter.setExtendedMetadata(extendedMetadata());
        authenticationRouter.setHttpClient(httpClient());
        authenticationRouter.setParserPool(parserPool());
        return authenticationRouter;
    }

    @Bean
    @ConditionalOnMissingBean(SamlAuthenticationProvider.class)
    public SamlAuthenticationProvider samlAuthenticationProvider() {
        SamlAuthenticationProvider authenticationProvider = new SamlAuthenticationProvider(userDetailsService);
        authenticationProvider.setForcePrincipalAsString(false);
        authenticationProvider.setExcludeCredential(true);
        authenticationProvider.setSAMLProcessor(processor());
        authenticationProvider.setContextProvider(contextProvider());
        return authenticationProvider;
    }

    @Bean
    @ConditionalOnMissingBean(SamlAuthorizeSuccessHandler.class)
    public SamlAuthorizeSuccessHandler samlAuthorizeSuccessHandler() {
        return new SamlAuthorizeSuccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(SamlServerLogoutHandler.class)
    public SamlServerLogoutHandler samlServerLogoutHandler() {
        return new SamlServerLogoutHandler();
    }

    @Bean
    @ConditionalOnMissingBean(SamlAuthenticationFactory.class)
    public SamlAuthenticationFactory samlAuthenticationFactory() {
        return new SamlAuthenticationFactory();
    }

    @Bean
    @ConditionalOnMissingBean(ExtendedMetadata.class)
    public ExtendedMetadata extendedMetadata() {
        ExtendedMetadata extendedMetadata = new ExtendedMetadata();
        extendedMetadata.setIdpDiscoveryEnabled(false);
        extendedMetadata.setSignMetadata(true);
        extendedMetadata.setEcpEnabled(true);

        return extendedMetadata;
    }

    @Bean
    @ConditionalOnMissingBean(SamlMetadataExtendedFilter.class)
    public SamlMetadataExtendedFilter metadataExtendedFilter(MetadataManager metadataManager, MetadataGenerator metadataGenerator) {
        SamlMetadataExtendedFilter displayFilter = new SamlMetadataExtendedFilter(metadataManager, metadataGenerator);
        displayFilter.setFilterProcessesUrl(ssoSamlProperties.getMetadataDisplayProcessUrl());

        return displayFilter;
    }

    @Bean
    @ConditionalOnMissingBean(MetadataGenerator.class)
    public MetadataGenerator metadataGenerator() {
        CustomMetadataGenerator metadataGenerator = new CustomMetadataGenerator(ssoPropertyService, ssoProperties);

        if (StringUtils.isNotBlank(ssoPropertyService.getBaseUrl())) {
            metadataGenerator.setEntityBaseURL(ssoPropertyService.getBaseUrl());
        } else {
            metadataGenerator.setEntityBaseURL(CustomMetadataGenerator.DEFAULT_BASE_URL);
        }

        metadataGenerator.setEntityId(ssoSamlProperties.getEntityId());
        metadataGenerator.setExtendedMetadata(extendedMetadata());
        metadataGenerator.setIncludeDiscoveryExtension(false);
        metadataGenerator.setKeyManager(keyManager());

        return metadataGenerator;
    }

    @Bean
    @ConditionalOnMissingBean(MetadataManager.class)
    public MetadataManager metadataManager(MetadataGenerator metadataGenerator) throws MetadataProviderException {
        List<MetadataProvider> providers = new ArrayList<>();
        CachingMetadataManager manager = new CachingMetadataManager(providers);

        manager.setHostedSPName(metadataGenerator.getEntityId());

        EntityDescriptor descriptor = metadataGenerator.generateMetadata();
        MetadataMemoryProvider memoryProvider = new MetadataMemoryProvider(descriptor);
        memoryProvider.initialize();
        MetadataProvider metadataProvider = new ExtendedMetadataDelegate(memoryProvider, metadataGenerator.generateExtendedMetadata());
        manager.addMetadataProvider(metadataProvider);

        return manager;
    }

    @Bean
    public VelocityEngine velocityEngine() {
        return VelocityFactory.getEngine();
    }

    @Bean
    @ConditionalOnMissingBean(ParserPool.class)
    public ParserPool parserPool() {
        StaticBasicParserPool parserPool = new StaticBasicParserPool();
        try {
            parserPool.initialize();
        } catch (XMLParserException e) {
            e.printStackTrace();
        }
        return parserPool;
    }

    @Bean
    @ConditionalOnMissingBean(ParserPoolHolder.class)
    public ParserPoolHolder parserPoolHolder() {
        return new ParserPoolHolder();
    }

    @Bean
    @ConditionalOnMissingBean(SAMLContextProvider.class)
    public SAMLContextProvider contextProvider() {
        return new SAMLContextProviderImpl();
    }

    @Bean
    @ConditionalOnMissingBean(KeyManager.class)
    public KeyManager keyManager() {
        KeyStore keyStore = KeyStoreLocator.createKeyStore(ssoSamlProperties.getPassphrase());
        try {
            KeyStoreLocator.addPrivateKey(keyStore, ssoSamlProperties.getEntityId(), ssoSamlProperties.getPrivateKey(), ssoSamlProperties.getCertificate(), ssoSamlProperties.getPassphrase());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return new JKSKeyManager(keyStore, Collections.singletonMap(ssoSamlProperties.getEntityId(), ssoSamlProperties.getPassphrase()), ssoSamlProperties.getEntityId());
    }

    @Bean
    @ConditionalOnMissingBean(WebSSOProfileOptions.class)
    public WebSSOProfileOptions webSSOProfileOptions() {
        WebSSOProfileOptions webSSOProfileOptions = new WebSSOProfileOptions();
        webSSOProfileOptions.setIncludeScoping(false);
        return webSSOProfileOptions;
    }

    @Bean("samlHttpClient")
    @ConditionalOnMissingBean(name = "samlHttpClient")
    public HttpClient httpClient() {
        return new HttpClient(new MultiThreadedHttpConnectionManager());
    }

    @Bean
    public HTTPArtifactBinding artifactBinding() {
        final ArtifactResolutionProfileImpl artifactResolutionProfile = new ArtifactResolutionProfileImpl(httpClient());
        artifactResolutionProfile.setProcessor(new SAMLProcessorImpl(httpSOAP11Binding()));

        return new HTTPArtifactBinding(parserPool(), velocityEngine(), artifactResolutionProfile);
    }

    @Bean
    public HTTPPostBinding httpPostBinding() {
        return new HTTPPostBinding(parserPool(), velocityEngine());
    }

    @Bean
    public HTTPRedirectDeflateBinding httpRedirectDeflateBinding() {
        return new HTTPRedirectDeflateBinding(parserPool());
    }

    @Bean
    public HTTPSOAP11Binding httpSOAP11Binding() {
        return new HTTPSOAP11Binding(parserPool());
    }

    @Bean
    public HTTPPAOS11Binding httpPAOS11Binding() {
        return new HTTPPAOS11Binding(parserPool());
    }

    @Bean
    public SAMLProcessorImpl processor() {
        Collection<SAMLBinding> bindings = new ArrayList<SAMLBinding>();
        bindings.add(httpRedirectDeflateBinding());
        bindings.add(httpPostBinding());
        bindings.add(artifactBinding());
        bindings.add(httpSOAP11Binding());
        bindings.add(httpPAOS11Binding());
        return new SAMLProcessorImpl(bindings);
    }

    // Logger for SAML messages and events
    @Bean
    public SAMLDefaultLogger samlLogger() {
        return new SAMLDefaultLogger();
    }

    // SAML 2.0 WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumer webSSOprofileConsumer() {
        return new WebSSOProfileConsumerImpl();
    }

    // SAML 2.0 Holder-of-Key WebSSO Assertion Consumer
    @Bean
    public WebSSOProfileConsumerHoKImpl hokWebSSOprofileConsumer() {
        return new WebSSOProfileConsumerHoKImpl();
    }


    //SAML 2.0 Web SSO profile
    @Bean("webSSOprofile")
    public WebSSOProfile webSSOprofile() {
        return new WebSSOProfileImpl();
    }

    // SAML 2.0 ECP profile
    @Bean("ecpprofile")
    public WebSSOProfileECPImpl ecpprofile() {
        return new WebSSOProfileECPImpl();
    }

    @Bean
    public TLSProtocolConfigurer tlsProtocolConfigurer() {
        return new TLSProtocolConfigurer();
    }

    // Initialization of OpenSAML library
    @Bean
    @ConditionalOnMissingBean(SAMLBootstrap.class)
    public static SAMLBootstrap sAMLBootstrap() {
        return new CustomSAMLBootstrap("sha1");
    }

}
