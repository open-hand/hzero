package org.hzero.admin.infra.repository.impl;

import static org.hzero.admin.infra.util.VersionUtil.METADATA_VERSION;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.api.dto.InstanceDTO;
import org.hzero.admin.api.dto.InstanceDetailDTO;
import org.hzero.admin.api.dto.YamlDTO;
import org.hzero.admin.api.dto.condition.InstanceQueryDTO;
import org.hzero.admin.domain.repository.InstanceRepository;
import org.hzero.admin.infra.util.ManualPageHelper;
import org.hzero.admin.infra.util.RelaxedNames;
import org.hzero.admin.infra.util.config.ConfigUtil;
import org.hzero.core.base.BaseConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.codearte.props2yaml.Props2YAML;

/**
 * 服务实例资源库接口实现
 *
 * @author bo.he02@hand-china.com 2020/05/12 16:04
 */
@Repository
public class InstanceRepositoryImpl implements InstanceRepository {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceRepositoryImpl.class);

    /**
     * 实例ID分割后的长度
     */
    private static final int INSTANCE_ID_SPLIT_LENGTH = 3;
    /**
     * 内部实例ID模板
     */
    private static final String INNER_INSTANCE_ID_TEMPLATE = "%s:%s:%s";
    private static final String VALUE = "value";
    private static final String PROPERTIES = "properties";
    private static final String DEFAULT_PROFILE = "default";
    private static final Pattern HEALTH_CHECK_URL_PATTERN = Pattern.compile("http://(\\d+\\.){3}\\d+:\\d+/");
    /**
     * spring boot 2.0 endpoint
     */
    private static final String ACTUATOR_ENV = "actuator/env";
    private static final String TYPE_YAML = "yaml";
    private static final String PROPERTY_SOURCES = "propertySources";
    private static final String NAME = "name";
    private static final String PREFIX_APPLICATION_CONFIG = "applicationConfig: [classpath:/";
    private static final String SUFFIX_PROPERTIES = ".properties]";
    private static final String SUFFIX_YML = ".yml]";
    private static final String PREFIX_CONFIG_SERVICE = "configService:";
    private static final String KEY_CONFIG_SERVER = "config-server";
    private static final String PREFIX_JAVA = "java";
    private static final String ACTIVE_PROFILES = "activeProfiles";
    private static final String APPLICATION_CONFIG_PREFIX = "applicationConfig: [classpath:/application-";
    private static final String DEFAULT_PROPERTIES = "defaultProperties";
    private static final String APPLICATION_CONFIG_BOOTSTRAP_PROPERTIES = "applicationConfig: [classpath:/bootstrap.properties]";
    private static final String APPLICATION_CONFIG_BOOTSTRAP_YAML = "applicationConfig: [classpath:/bootstrap.yml]";
    private static final String KAFKA_BINDER_DEFAULT_PROPERTIES = "kafkaBinderDefaultProperties";
    private static final String APPLICATION_CONFIG_APPLICATION_PROPERTIES = "applicationConfig: [classpath:/application.properties]";
    private static final String APPLICATION_CONFIG_APPLICATION_YAML = "applicationConfig: [classpath:/application.yml]";
    private static final String RANDOM = "random";
    private static final String SYSTEM_ENVIRONMENT = "systemEnvironment";
    private static final String SYSTEM_PROPERTIES = "systemProperties";
    private static final String SERVLET_CONFIG_INIT_PARAMS = "servletConfigInitParams";
    private static final String COMMAND_LINE_ARGS = "commandLineArgs";
    private static final String SERVER_PORTS = "server.ports";
    private static final String LOCAL_SERVER_PORT = "local.server.port";
    private static final String SERVER_PORT = "server.port";
    private static final String LOCAL_MANAGEMENT_PORT = "local.management.port";
    private static final String MANAGEMENT_PORT = "management.port";
    private static final String GO_REGISTER_SERVER = "go-register-server";

    /**
     * restTemplate对象
     */
    private final RestTemplate restTemplate;
    /**
     * objectMapper对象
     */
    private final ObjectMapper objectMapper;
    /**
     * discoveryClient对象
     */
    private final DiscoveryClient discoveryClient;

    @Autowired
    public InstanceRepositoryImpl(ObjectMapper objectMapper,
                                  DiscoveryClient discoveryClient) {
        // 此处新建一个RestTemplate，下面逻辑中会使用ip调用，
        // 而自动注入的RestTemplate使用了@LoadBalanced注解，会按照服务名找实体，用ip调用会报错(No instances available for XXX)
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Page<InstanceDTO> listByOptions(String service, InstanceQueryDTO instanceQueryDTO, PageRequest pageRequest) {
        List<InstanceDTO> serviceInstances = new ArrayList<>();
        if (StringUtils.isBlank(service)) {
            // 获取所有服务
            List<String> services = this.discoveryClient.getServices();
            if (CollectionUtils.isNotEmpty(services)) {
                services.forEach(s -> serviceInstances.addAll(this.toInstanceDTOList(this.discoveryClient.getInstances(s))));
            }
        } else {
            // 按照服务名查询
            serviceInstances.addAll(this.toInstanceDTOList(this.discoveryClient.getInstances(service)));
        }

        // 手动分页
        return ManualPageHelper.postPage(serviceInstances, pageRequest, instanceQueryDTO.toMap());
    }

    @Override
    public Page<InstanceDTO> listByOptions(String service, String version, PageRequest pageRequest) {
        List<InstanceDTO> serviceInstances = new ArrayList<>();
        // 获取所有服务
        List<String> services = this.discoveryClient.getServices();
        if (CollectionUtils.isEmpty(services)) {
            return new Page<>();
        }
        for (String item : services) {
            if (StringUtils.isNotBlank(service) && !item.contains(service)) {
                continue;
            }
            List<InstanceDTO> instanceList = this.toInstanceDTOList(this.discoveryClient.getInstances(item));
            for (InstanceDTO dto : instanceList) {
                if (StringUtils.isNotBlank(version)) {
                    if (StringUtils.isBlank(dto.getVersion()) || !dto.getVersion().contains(version)) {
                        continue;
                    }
                }
                serviceInstances.add(dto);
            }
        }
        // 按照服务和版本去重
        serviceInstances = serviceInstances.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getService() + ";" + o.getVersion()))), ArrayList::new));
        return ManualPageHelper.postPage(serviceInstances, pageRequest, null);
    }

    @Override
    public InstanceDetailDTO query(String instanceId) {
        for (String service : this.discoveryClient.getServices()) {
            for (ServiceInstance serviceInstance : this.discoveryClient.getInstances(service)) {
                if (this.getServiceInstanceId(serviceInstance).equals(instanceId)) {
                    return this.processInstanceDetail(serviceInstance, instanceId);
                }
            }
        }
        return null;
    }

    /**
     * 处理实例详情
     *
     * @param serviceInstance 服务实例
     * @param instanceId      实例ID
     * @return 实例详情
     */
    private InstanceDetailDTO processInstanceDetail(ServiceInstance serviceInstance, String instanceId) {
        InstanceDetailDTO instanceDetail = new InstanceDetailDTO();

        instanceDetail.setInstanceId(instanceId);
        instanceDetail.setHostName(serviceInstance.getHost());
        instanceDetail.setIpAddr(serviceInstance.getHost());
        instanceDetail.setApp(serviceInstance.getServiceId());
        instanceDetail.setPort(String.valueOf(serviceInstance.getPort()));

        Map<String, String> metadata = serviceInstance.getMetadata();
        instanceDetail.setVersion(metadata.get(METADATA_VERSION));
        instanceDetail.setMetadata(metadata);

        // 数据先置空
        instanceDetail.setConfigInfoYml(new YamlDTO());
        instanceDetail.setEnvInfoYml(new YamlDTO());

        // 暂不处理
//        if (serviceInstance instanceof EurekaDiscoveryClient.EurekaServiceInstance) {
//            InstanceInfo instanceInfo = ((EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance).getInstanceInfo();
//            instanceDetail.setRegistrationTime(new Date(instanceInfo.getLeaseInfo().getRegistrationTimestamp()));
//
//            String healthCheckUrl = instanceInfo.getHealthCheckUrl();
//            this.fetchEnvInfo(healthCheckUrl, instanceDetail);
//        }

        return instanceDetail;
    }

    /**
     * 获取环境信息
     *
     * @param healthCheckUrl 服务健康检查url
     * @param instanceDetail 实例详细信息对象
     */
    private void fetchEnvInfo(String healthCheckUrl, InstanceDetailDTO instanceDetail) {
        Matcher matcher = HEALTH_CHECK_URL_PATTERN.matcher(healthCheckUrl);
        String url;
        if (matcher.find()) {
            url = matcher.group();
        } else {
            throw new CommonException("error.illegal.management.url", healthCheckUrl);
        }

        // 获取环境变量的url
        String envUrl = url + ACTUATOR_ENV;
        ResponseEntity<String> response;
        try {
            // 请求数据
            response = this.restTemplate.getForEntity(envUrl, String.class);
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                this.processEnvJson(instanceDetail, response.getBody());
            } else {
                throw new CommonException("error.config.fetchEnv");
            }
        } catch (Exception e) {
            LOGGER.warn("can not fetch env info, exception message : {}", e.getMessage());
            throw new CommonException("error.config.fetchEnv");
        }
    }

    /**
     * 处理环境信息json
     *
     * @param instanceDetail 实例详细信息
     * @param json           待处理的环境信息json字符串
     */
    private void processEnvJson(InstanceDetailDTO instanceDetail, String json) {
        try {
            JsonNode node = this.objectMapper.readTree(json);

            // 所有配置
            String allConfigYaml = this.getAllConfigYaml(node);
            instanceDetail.setEnvInfoYml(new YamlDTO(allConfigYaml, ConfigUtil.appearNumber(allConfigYaml, "\n") + 1));

            // 激活的配置
            String activeConfigYaml;
            activeConfigYaml = this.getActiveConfigYaml(node);

            // 设置配置
            instanceDetail.setConfigInfoYml(new YamlDTO(activeConfigYaml, ConfigUtil.appearNumber(activeConfigYaml, "\n") + 1));
        } catch (IOException e) {
            LOGGER.info("error.restTemplate.fetchEnvInfo {}", e.getMessage());
            throw new CommonException("error.parse.envJson");
        }
    }

    /**
     * 获取所有的yaml配置
     *
     * @param root 根节点
     * @return 获取的结果
     */
    private String getAllConfigYaml(final JsonNode root) {
        Map<String, Object> map = new HashMap<>();
        this.processConfigYaml(map, root);
        return ConfigUtil.convertMapToText(map, TYPE_YAML);
    }

    /**
     * 解析Json节点
     *
     * @param map  解析结果
     * @param root json节点
     */
    private void processConfigYaml(Map<String, Object> map, JsonNode root) {
        JsonNode propertySources = root.findValue(PROPERTY_SOURCES);
        for (JsonNode node : propertySources) {
            String key = node.get(NAME).asText();
            JsonNode jsonNode = node.get(PROPERTIES);
            if (key.startsWith(PREFIX_APPLICATION_CONFIG)) {
                key = key.replace(PREFIX_APPLICATION_CONFIG, "").replace(SUFFIX_PROPERTIES, "")
                        .replace(SUFFIX_YML, "");
            }
            if (key.startsWith(PREFIX_CONFIG_SERVICE)) {
                key = KEY_CONFIG_SERVER;
            }
            Iterator<Map.Entry<String, JsonNode>> vit = jsonNode.fields();
            while (vit.hasNext()) {
                Map.Entry<String, JsonNode> value = vit.next();
                String jsonValue = value.getValue().get(VALUE).asText();
                if (!StringUtils.isEmpty(jsonValue) && !value.getKey().startsWith(PREFIX_JAVA)) {
                    map.put(key + BaseConstants.Symbol.POINT + value.getKey(), jsonValue);
                }
            }
        }
    }

    /**
     * 获取当前激活的yaml配置
     *
     * @param root 根节点
     * @return yaml配置
     */
    private String getActiveConfigYaml(final JsonNode root) {
        JsonNode propertySources = root.findValue(PROPERTY_SOURCES);
        Iterator<JsonNode> iterator = propertySources.iterator();
        String config = null;
        while (iterator.hasNext()) {
            JsonNode node = iterator.next();
            String name = node.findValue(NAME).asText();
            if (name.startsWith(PREFIX_CONFIG_SERVICE)) {
                config = name;
                break;
            }
        }
        String activeProfile = DEFAULT_PROFILE;
        JsonNode profileNode = root.findValue(ACTIVE_PROFILES);
        if (profileNode.get(0) != null && !StringUtils.isEmpty(profileNode.get(0).asText())) {
            activeProfile = profileNode.get(0).asText();
        }
        activeProfile = APPLICATION_CONFIG_PREFIX + activeProfile;
        Map<String, Data> map =
                PropertySourceBuilder.newInstance(propertySources)
                        .appendApplySpringBoot2(DEFAULT_PROPERTIES)
                        .appendApplySpringBoot2(APPLICATION_CONFIG_BOOTSTRAP_PROPERTIES)
                        .appendApplySpringBoot2(APPLICATION_CONFIG_BOOTSTRAP_YAML)
                        .appendApplySpringBoot2(KAFKA_BINDER_DEFAULT_PROPERTIES)
                        .appendApplySpringBoot2(APPLICATION_CONFIG_APPLICATION_PROPERTIES)
                        .appendApplySpringBoot2(APPLICATION_CONFIG_APPLICATION_YAML)
                        .appendApplySpringBoot2(activeProfile + SUFFIX_PROPERTIES)
                        .appendApplySpringBoot2(activeProfile + SUFFIX_YML)
                        .appendApplySpringBoot2(RANDOM)
                        .appendApplySpringBoot2(config)
                        .coverApplySpringBoot2(SYSTEM_ENVIRONMENT)
                        .coverApplySpringBoot2(SYSTEM_PROPERTIES)
                        .appendApplySpringBoot2(SERVLET_CONFIG_INIT_PARAMS)
                        .appendApplySpringBoot2(COMMAND_LINE_ARGS)
                        .coverApplyServerPortSpringBoot2()
                        .data();
        return this.convertDataMapToYaml(map);
    }

    /**
     * 将服务实例转换成实例DTO
     *
     * @param serviceInstances 服务实例
     * @return 转换结果
     */
    private List<InstanceDTO> toInstanceDTOList(final List<ServiceInstance> serviceInstances) {
        List<InstanceDTO> instanceInfoList = new ArrayList<>();
        for (ServiceInstance serviceInstance : serviceInstances) {
            if (GO_REGISTER_SERVER.equalsIgnoreCase(serviceInstance.getServiceId())) {
                continue;
            }
            String instanceId = this.getServiceInstanceId(serviceInstance);
            String serviceName = serviceInstance.getServiceId().toLowerCase();
            String version = serviceInstance.getMetadata().get(METADATA_VERSION);
            String port = String.valueOf(serviceInstance.getPort());

            String status = null;
            Date registrationTime = null;
            // 暂不处理
//            if (serviceInstance instanceof EurekaDiscoveryClient.EurekaServiceInstance) {
//                EurekaDiscoveryClient.EurekaServiceInstance eurekaServiceInstance =
//                        (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance;
//                InstanceInfo info = eurekaServiceInstance.getInstanceInfo();
//
//                status = info.getStatus().name();
//                registrationTime = new Date(info.getLeaseInfo().getRegistrationTimestamp());
//            }

            instanceInfoList.add(new InstanceDTO(instanceId, serviceName, version, status, port, registrationTime));
        }
        return instanceInfoList;
    }

    /**
     * 获取服务实例ID
     *
     * @param serviceInstance 服务实例对象
     * @return 服务实例ID
     */
    private String getServiceInstanceId(ServiceInstance serviceInstance) {
        return String.format(INNER_INSTANCE_ID_TEMPLATE, serviceInstance.getServiceId().toLowerCase(),
                serviceInstance.getHost(), serviceInstance.getPort());
    }

    /**
     * 转换map对象为yaml字符串
     *
     * @param dataMap 数据源map对象
     * @return 转换成功yaml字符串
     */
    private String convertDataMapToYaml(final Map<String, Data> dataMap) {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, Data> entry : dataMap.entrySet()) {
            res.append(entry.getKey());
            res.append("=");
            res.append(entry.getValue().getValue());
            res.append("\n");
        }
        return Props2YAML.fromContent(res.toString())
                .convert();
    }

    /**
     * 属性构建器
     */
    private static class PropertySourceBuilder {
        private final JsonNode root;
        private final Map<String, Data> map = new HashMap<>();

        public PropertySourceBuilder(JsonNode node) {
            this.root = node;
        }

        public static PropertySourceBuilder newInstance(final JsonNode node) {
            return new PropertySourceBuilder(node);
        }

        public PropertySourceBuilder appendApply(final String property) {
            if (Objects.isNull(property)) {
                return this;
            }
            JsonNode value = root.findValue(property);
            if (value == null) {
                return this;
            }

            Iterator<Map.Entry<String, JsonNode>> it = value.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                Data data = getDataByRelaxedNames(entry.getKey());
                if (data == null) {
                    map.put(entry.getKey(), new Data(entry.getValue().asText(), property));
                } else {
                    data.setValue(entry.getValue().asText());
                }
            }
            return this;
        }

        public PropertySourceBuilder appendApplySpringBoot2(final String property) {
            if (Objects.isNull(property)) {
                return this;
            }
            JsonNode propertySourceNode = this.getPropertySourceNode(property);
            if (propertySourceNode == null) {
                return this;
            }

            JsonNode properties = propertySourceNode.findValue(PROPERTIES);
            Iterator<Map.Entry<String, JsonNode>> propertiesIterator = properties.fields();
            while (propertiesIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = propertiesIterator.next();
                Data data = getDataByRelaxedNames(entry.getKey());
                String value = entry.getValue().findValue(VALUE).asText();
                if (data == null) {
                    map.put(entry.getKey(), new Data(value, property));
                } else {
                    data.setValue(value);
                }
            }
            return this;
        }

        private JsonNode getPropertySourceNode(String property) {
            for (JsonNode node : root) {
                String name = node.findValue(NAME).asText();
                if (property.equalsIgnoreCase(name)) {
                    return node;
                }
            }
            return null;
        }

        public PropertySourceBuilder coverApplyServerPortSpringBoot2() {
            JsonNode propertySourceNode = getPropertySourceNode(SERVER_PORTS);
            if (propertySourceNode == null) {
                return this;
            }
            JsonNode properties = propertySourceNode.findValue(PROPERTIES);
            Iterator<Map.Entry<String, JsonNode>> propertiesIterator = properties.fields();
            while (propertiesIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = propertiesIterator.next();
                String key = entry.getKey();
                JsonNode value = entry.getValue();
                if (LOCAL_SERVER_PORT.equalsIgnoreCase(key)) {
                    map.put(SERVER_PORT, new Data(value.findValue(VALUE).asText(), SERVER_PORTS));
                }
                if (LOCAL_MANAGEMENT_PORT.equalsIgnoreCase(key)) {
                    map.put(MANAGEMENT_PORT, new Data(value.findValue(VALUE).asText(), MANAGEMENT_PORT));
                }

            }
            return this;
        }

        public PropertySourceBuilder coverApply(final String property) {
            if (Objects.isNull(property)) {
                return this;
            }
            JsonNode value = root.findValue(property);
            if (value == null) {
                return this;
            }

            Iterator<Map.Entry<String, JsonNode>> it = value.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String mapExistKey = getKeyByRelaxedNames(entry.getKey());
                if (mapExistKey != null) {
                    Data data = map.get(mapExistKey);
                    data.setValue(entry.getValue().asText());
                }
            }
            return this;
        }

        public PropertySourceBuilder coverApplySpringBoot2(final String property) {
            if (Objects.isNull(property)) {
                return this;
            }

            JsonNode propertySourceNode = getPropertySourceNode(property);
            if (propertySourceNode == null) {
                return this;
            }
            JsonNode properties = propertySourceNode.findValue(PROPERTIES);
            Iterator<Map.Entry<String, JsonNode>> propertiesIterator = properties.fields();
            while (propertiesIterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = propertiesIterator.next();
                String mapExistKey = getKeyByRelaxedNames(entry.getKey());
                if (mapExistKey != null) {
                    Data data = map.get(mapExistKey);
                    String value = entry.getValue().findValue(VALUE).asText();
                    data.setValue(value);
                }
            }
            return this;
        }

        public Map<String, Data> data() {
            return this.map;
        }


        private String getKeyByRelaxedNames(final String key) {
            for (String i : map.keySet()) {
                RelaxedNames relaxedNames = RelaxedNames.forCamelCase(i);
                for (String j : relaxedNames) {
                    if (j.contains(key)) {
                        return i;
                    }
                }
            }
            return null;
        }

        private Data getDataByRelaxedNames(final String key) {
            RelaxedNames relaxedNames = RelaxedNames.forCamelCase(key);
            for (String i : relaxedNames) {
                if (map.containsKey(i)) {
                    return map.get(i);
                }
            }
            return null;
        }

    }

    /**
     * 数据对象
     */
    public static class Data {
        private Object value;
        private String source;

        public Data(Object value, String source) {
            this.value = value;
            this.source = source;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "value=" + value +
                    ", source='" + source + '\'' +
                    '}';
        }
    }
}
