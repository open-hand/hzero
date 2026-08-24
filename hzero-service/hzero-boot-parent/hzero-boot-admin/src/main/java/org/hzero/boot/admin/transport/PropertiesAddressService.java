package org.hzero.boot.admin.transport;

import org.hzero.core.util.ServiceInstanceUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author XCXCXCXCX
 * @date 2020/6/11 11:14 上午
 */
public class PropertiesAddressService implements AddressService {

    private AdminClientProperties properties;

    private List<ServiceInstance> instances;

    public PropertiesAddressService(AdminClientProperties properties) {
        this.properties = properties;
        this.instances = new ArrayList<>();
        for (String serverAddr : properties.getServerList()) {
            String[] parts = serverAddr.split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            this.instances.add(new ServiceInstance() {
                @Override
                public String getServiceId() {
                    return null;
                }

                @Override
                public String getHost() {
                    return host;
                }

                @Override
                public int getPort() {
                    return port;
                }

                @Override
                public boolean isSecure() {
                    return false;
                }

                @Override
                public URI getUri() {
                    return null;
                }

                @Override
                public Map<String, String> getMetadata() {
                    Map<String, String> metadata = new HashMap<>();
                    metadata.put(ServiceInstanceUtils.METADATA_MANAGEMENT_PORT, String.valueOf(port));
                    return metadata;
                }
            });
        }
    }

    @Override
    public List<ServiceInstance> getInstances() {
        return instances;
    }
}
