package org.hzero.swagger.app.impl;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.models.auth.OAuth2Definition;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import org.hzero.common.HZeroService;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StringStaticEndpointHttpRequest;
import org.hzero.swagger.app.DocumentService;
import org.hzero.swagger.app.SwaggerService;
import org.hzero.swagger.config.SwaggerProperties;
import org.hzero.swagger.domain.entity.ServiceRoute;
import org.hzero.swagger.domain.entity.Swagger;
import org.hzero.swagger.domain.repository.ServiceRouteRepository;
import org.hzero.swagger.domain.repository.SwaggerRepository;
import org.hzero.swagger.infra.constant.Versions;
import org.hzero.swagger.infra.util.RequestUtil;

/**
 * 实现类
 */
@Component
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final String DEFAULT = "default";
    private static final String FAILED_TAG = "\"failed\":true";

    @Value("${hzero.swagger.authorize-url:}")
    private String authorizeUrl;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private SwaggerProperties swaggerProperties;
    @Autowired
    private SwaggerService swaggerService;
    @Autowired
    private SwaggerRepository swaggerRepository;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private ServiceRouteRepository serviceRouteRepository;
    @Autowired
    private StringHttpTransporter httpTransporter;

    @Override
    public String getSwaggerJson(String name, String version) throws IOException {
        MultiKeyMap multiKeyMap = serviceRouteRepository.getAllRunningInstances();
        ServiceRoute route = (ServiceRoute) multiKeyMap.get(name, version);
        if (route == null) {
            return "";
        }
        String basePath = route.getPath().replace("/**", "");

        ObjectNode root = getSwaggerJsonByIdAndVersion(route.getServiceCode(), version);
        root.put("basePath", basePath);
        root.putArray("schemes").add(RequestUtil.getProtocol());
        String host = RequestUtil.getRequestDomain();
        host = host.replaceFirst(RequestUtil.PROTOCOL_HTTP, "").replaceFirst(RequestUtil.PROTOCOL_HTTPS, "");
        root.put("host", host);
        LOGGER.debug("put schemes:{}, basePath:{}, host:{}", root.get("schemes"), basePath, root.get("host"));
        return MAPPER.writeValueAsString(root);
    }


    @Override
    public ObjectNode getSwaggerJsonByIdAndVersion(String service, String version) throws IOException {
        String json = fetchSwaggerJsonByService(service, version);
        if (StringUtils.isEmpty(json)) {
            throw new RemoteAccessException("fetch swagger json failed");
        }
        ObjectNode node = (ObjectNode) MAPPER.readTree(json);
        List<Map<String, List<String>>> security = new LinkedList<>();
        Map<String, List<String>> clients = new TreeMap<>();
        clients.put(swaggerProperties.getClient(), Collections.singletonList(DEFAULT));
        security.add(clients);
        OAuth2Definition definition = new OAuth2Definition();

        definition.setAuthorizationUrl(getAuthorizeUrl());

        definition.setType("oauth2");
        definition.setFlow("implicit");
        definition.setScopes(Collections.singletonMap(DEFAULT, "default scope"));
        LOGGER.info("{}", definition.getScopes());
        node.putPOJO("securityDefinitions", Collections.singletonMap(swaggerProperties.getClient(), definition));
        Iterator<Map.Entry<String, JsonNode>> pathIterator = node.get("paths").fields();
        while (pathIterator.hasNext()) {
            Map.Entry<String, JsonNode> pathNode = pathIterator.next();
            Iterator<Map.Entry<String, JsonNode>> methodIterator = pathNode.getValue().fields();
            while (methodIterator.hasNext()) {
                Map.Entry<String, JsonNode> methodNode = methodIterator.next();
                ((ObjectNode) methodNode.getValue()).putPOJO("security", security);
            }
        }
        return node;
    }

    private String getAuthorizeUrl() {
        if (StringUtils.isNotBlank(authorizeUrl)) {
            return authorizeUrl;
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(HZeroService.getRealName(HZeroService.Oauth.NAME));
        String oauthContext = null;
        if (CollectionUtils.isNotEmpty(instances)) {
            oauthContext = instances.get(0).getMetadata().get("CONTEXT");
        }
        if (StringUtils.isBlank(oauthContext)) {
            oauthContext = "/oauth";
        }
        return RequestUtil.getRequestDomain() + oauthContext + "/oauth/authorize";
    }

    @Override
    public String fetchSwaggerJsonByService(String service, String version) {
        Swagger param = new Swagger();
        param.setServiceName(service);
        param.setServiceVersion(version);
        Swagger swagger = swaggerRepository.selectOne(param);
        if (swagger == null || StringUtils.isEmpty(swagger.getValue())) {
            String json = fetchJsonByNameAndVersion(service, version);
            if (StringUtils.isNotBlank(json)) {
                swaggerService.updateOrInsertSwagger(service, version, json);
            }
            return json;
        } else {
            return swagger.getValue();
        }
    }

    private String fetchJsonByNameAndVersion(String service, String version) {
        List<ServiceInstance> instances = discoveryClient.getInstances(service);
        for (ServiceInstance instance : instances) {
            String mdVersion = instance.getMetadata().get(Versions.METADATA_VERSION);
            if (StringUtils.isEmpty(mdVersion)) {
                mdVersion = Versions.NULL_VERSION;
            }
            if (version.equals(mdVersion)) {
                return fetch(instance);
            }
        }
        return null;
    }

    @Override
    public void manualRefresh(String serviceName, String version) {
        serviceName = StringUtils.lowerCase(serviceName);
        String json = fetchJsonByNameAndVersion(serviceName, version);
        if (StringUtils.isEmpty(json)) {
            throw new RemoteAccessException("fetch swagger json failed");
        }
        swaggerService.updateOrInsertSwagger(serviceName, version, json);
    }

    @Async("swaggerRefreshAsyncExecutor")
    @Override
    public void autoRefresh(String serviceName, String version) {
        retry(() -> {
            manualRefresh(serviceName, version);
        }, 5, 3000);
    }

    private void retry(Runnable runnable, int times, long sleep) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(sleep);
                runnable.run();
                return;
            } catch (Throwable e) {
                LOGGER.error("execute failed, continue to retry...", e);
            }
            if (times-- == 0) {
                return;
            }
        }
    }

    private String fetch(ServiceInstance instance) {
        LOGGER.info("service: {} metadata : {}", instance.getServiceId(), instance.getMetadata());
        try {
            String value = httpTransporter.transport(new StringStaticEndpointHttpRequest(instance, StaticEndpoint.CHOERODON_API_DOCS));
            if (value != null && value.contains(FAILED_TAG)) {
                throw new RemoteAccessException("fetch failed, instance: " + instance.getServiceId() + ", response=" + value);
            }
            return value;
        } catch (RestClientException e) {
            LOGGER.error("fetch error, ex={}", e.getMessage());
            throw new RemoteAccessException("fetch failed, instance: " + instance.getServiceId() + ", ex=" + e.getMessage());
        }
    }

    @Override
    public String fetchSwaggerJsonByIp(final ServiceInstance serviceInstance) {
        return fetch(serviceInstance);
    }

}
