package org.hzero.boot.admin;

import org.hzero.boot.admin.exception.AppNameNotFoundException;
import org.hzero.boot.admin.registration.Registration;
import org.hzero.boot.admin.transport.AddressService;
import org.hzero.boot.admin.transport.AdminClientProperties;
import org.hzero.boot.admin.transport.AdminTransport;
import org.hzero.boot.admin.transport.DiscoveryAddressService;
import org.hzero.boot.admin.transport.PropertiesAddressService;
import org.hzero.boot.admin.transport.Transport;
import org.hzero.core.HZeroAutoConfiguration;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.metadata.MetadataEntry;
import org.hzero.core.util.ServiceInstanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author XCXCXCXCX
 * @date 2020/6/10 5:17 下午
 */
@Configuration
@ConditionalOnProperty(value = "hzero.admin.auto-registry.enable", havingValue = "true", matchIfMissing = true)
@AutoConfigureAfter({
        HZeroAutoConfiguration.class,
        CompositeDiscoveryClientAutoConfiguration.class
})
@EnableConfigurationProperties(AdminClientProperties.class)
public class AdminRegistrationAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminRegistrationAutoConfiguration.class);

    private static final String NULL_VERSION = "null_version";

    @Bean
    @ConditionalOnMissingBean
    public Registration adminRegistration(@Value("${spring.application.name}") String applicationName,
                                          org.springframework.cloud.client.serviceregistry.Registration registration) {
        Map<String, String> metadata = registration.getMetadata();
        String version = metadata.getOrDefault(MetadataEntry.METADATA_VERSION, NULL_VERSION);
        // should not happened
        if (StringUtils.isEmpty(applicationName)) {
            throw new AppNameNotFoundException("spring.application.name must has value!");
        }
        if (NULL_VERSION.equals(version)) {
            LOGGER.warn("service version is null, default value is \"" + NULL_VERSION + "\"");
        }

        return new Registration() {

            @Override
            public String getServiceName() {
                return applicationName;
            }

            @Override
            public String getVersion() {
                return version;
            }

            @Override
            public String getHealthUrl() {
                return "http://" +
                        registration.getHost() +
                        ":" +
                        ServiceInstanceUtils.getManagementPortFromMetadata(registration) +
                        "/actuator/health";
            }

            @Override
            public Map<String, String> getMetadata() {
                return registration.getMetadata();
            }
        };
    }

    @Bean
    @ConditionalOnProperty(value = AdminClientProperties.PREFIX + ".discovery.enabled", havingValue = "true", matchIfMissing = true)
    public DiscoveryAddressService discoveryAddressService(DiscoveryClient discoveryClient, AdminClientProperties adminClientProperties) {
        return new DiscoveryAddressService(discoveryClient, adminClientProperties);
    }

    @Bean
    @ConditionalOnProperty(value = AdminClientProperties.PREFIX + ".discovery.enabled", havingValue = "false")
    public PropertiesAddressService propertiesAddressService(AdminClientProperties adminClientProperties) {
        return new PropertiesAddressService(adminClientProperties);
    }

    @Bean
    @ConditionalOnMissingBean(name = "registerTransport")
    public AdminTransport registerTransport(AddressService addressService, AdminClientProperties adminClientProperties) {
        return new AdminTransport(addressService, StaticEndpoint.ADMIN_SERVICE_REGISTER, adminClientProperties.getConnectTimeout(), adminClientProperties.getReadTimeout());
    }

    @Bean
    @ConditionalOnMissingBean(name = "unregisterTransport")
    public AdminTransport unregisterTransport(AddressService addressService, AdminClientProperties adminClientProperties) {
        return new AdminTransport(addressService, StaticEndpoint.ADMIN_SERVICE_UNREGISTER,  adminClientProperties.getConnectTimeout(), adminClientProperties.getReadTimeout());
    }

    @Bean
    public AdminAutoRegistration adminAutoRegistration(@Qualifier("registerTransport") Transport registerTransport,
                                                       @Qualifier("unregisterTransport") Transport unregisterTransport,
                                                       Registration registration,
                                                       AdminClientProperties properties) {
        return new AdminAutoRegistration(registerTransport, unregisterTransport, registration, properties);
    }

}
