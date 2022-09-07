package org.hzero.sso.saml.metadata;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.xml.io.MarshallingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.saml.metadata.*;

import org.hzero.sso.core.common.SsoExtendedFilter;

/**
 *
 * @author bojiangzhou 2020/08/30
 */
public class SamlMetadataExtendedFilter extends MetadataDisplayFilter implements SsoExtendedFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, EntityDescriptor> cacheMap = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    private final MetadataGenerator metadataGenerator;

    public SamlMetadataExtendedFilter(MetadataManager manager, MetadataGenerator metadataGenerator) {
        super.setManager(manager);
        this.metadataGenerator = metadataGenerator;
        String baseUrl;
        if ((baseUrl = metadataGenerator.getEntityBaseURL()) != null) {
            try {
                cacheMap.put(baseUrl, manager.getAvailableProviders().get(0).getEntityDescriptor(manager.getHostedSPName()));
            } catch (MetadataProviderException e) {
                logger.error("Parse default EntityDescriptor error", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }

    @Override
    public boolean requiresFilter(HttpServletRequest request) {
        return super.processFilter(request);
    }

    @Override
    protected void processMetadataDisplay(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //SAMLMessageContext context = contextProvider.getLocalEntity(request, response);
        //String entityId = context.getLocalEntityId();
        response.setContentType("application/samlmetadata+xml"); // SAML_Meta, 4.1.1 - line 1235
        response.addHeader("Content-Disposition", "attachment; filename=\"spring_saml_metadata.xml\"");
        displayMetadata(manager.getHostedSPName(), response.getWriter());
    }

    @Override
    protected void displayMetadata(String spEntityName, PrintWriter writer) throws ServletException {
        String baseUrl = metadataGenerator.getEntityBaseURL();
        if (!cacheMap.containsKey(baseUrl)) {
            lock.lock();
            try {
                if (!cacheMap.containsKey(baseUrl)) {
                    EntityDescriptor descriptor = metadataGenerator.generateMetadata();
                    MetadataMemoryProvider memoryProvider = new MetadataMemoryProvider(descriptor);
                    memoryProvider.initialize();
                    MetadataProvider metadataProvider = new ExtendedMetadataDelegate(memoryProvider, metadataGenerator.generateExtendedMetadata());
                    cacheMap.put(baseUrl, metadataProvider.getEntityDescriptor(spEntityName));
                    logger.info("generate new EntityDescriptor, baseUrl is: {}", baseUrl);
                }
            } catch (MetadataProviderException e) {
                logger.error("Error retrieving metadata", e);
                throw new ServletException("Error retrieving metadata", e);
            } finally {
                lock.unlock();
            }
        }

        try {
            EntityDescriptor descriptor = cacheMap.get(baseUrl);
            if (descriptor == null) {
                throw new ServletException("Metadata entity with ID " + spEntityName + " wasn't found");
            } else {
                writer.print(getMetadataAsString(descriptor));
            }
        } catch (MarshallingException e) {
            logger.error("Error marshalling entity descriptor", e);
            throw new ServletException(e);
        }
    }

}
