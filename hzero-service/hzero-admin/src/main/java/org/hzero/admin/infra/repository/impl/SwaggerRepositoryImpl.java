package org.hzero.admin.infra.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.admin.api.dto.swagger.*;
import org.hzero.admin.config.ConfigProperties;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.entity.Swagger;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.admin.domain.repository.SwaggerRepository;
import org.hzero.admin.infra.mapper.SwaggerMapper;
import org.hzero.admin.infra.util.MyLinkedList;
import org.hzero.admin.infra.util.VersionUtil;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.redis.safe.SafeRedisHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger.web.SwaggerResource;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 资源库实现
 *
 * @author bo.he02@hand-china.com 2020-05-09 11:00:41
 */
@Component
public class SwaggerRepositoryImpl extends BaseRepositoryImpl<Swagger> implements SwaggerRepository {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerRepositoryImpl.class);

    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final String KEY = "key";
    private static final String CHILDREN = "children";
    private static final String COLON = BaseConstants.Symbol.COLON;
    private static final String UNDERLINE = BaseConstants.Symbol.MIDDLE_LINE;
    private static final String SERVICE = "service";
    private static final String PATHS = "paths";
    private static final String OPERATION_ID = "operationId";
    private static final String ARRAY = "array";
    private static final String OBJECT = "object";
    private static final String CONTEXT = "CONTEXT";
    private static final String API_DOC_URL = "/v2/choerodon/api-docs";
    private static final String LOCATION_SPLIT_REGX = "\\?version=";
    private static final String REF_CONTROLLER = "refController";
    private static final String METHOD = "method";
    private static final String VERSION = "version";
    private static final String SUMMARY = "summary";
    private static final String SERVICE_PREFIX = "servicePrefix";
    private static final String TAGS = "tags";
    private static final String NAME = "name";
    private static final String SUFFIX_CONTROLLER = "-controller";
    private static final String SUFFIX_ENDPOINT = "-endpoint";
    private static final String BASE_PATH = "basePath";
    private static final String DEFINITIONS = "definitions";
    private static final String PROPERTIES = "properties";
    private static final String TYPE = "type";
    private static final String VAR_REF = "$ref";
    private static final String ITEMS = "items";
    private static final String RESPONSES = "responses";
    private static final String SCHEMA = "schema";
    private static final String CONSUMES = "consumes";
    private static final String PRODUCES = "produces";
    private static final String PARAMETERS = "parameters";
    private static final String BODY = "body";
    private static final String INTEGER = "integer";
    private static final String STRING = "string";
    private static final String BOOLEAN = "boolean";

    /**
     * {{name}}:{{serviceId}}
     */
    private static final String SWAGGER_RESOURCE_NAME_TEMPLATE = "%s:%s";
    private static final String SWAGGER_RESOURCE_VERSION = "2.0";
    /**
     * {{name}}:{{version}}
     */
    private static final String SWAGGER_RESOURCE_LOCATION_TEMPLATE = "/docs/%s?version=%s";

    /**
     * 匹配对象
     */
    private final AntPathMatcher matcher = new AntPathMatcher();

    /**
     * swagger的mapper对象
     */
    private final SwaggerMapper swaggerMapper;
    /**
     * RestTemplate对象
     */
    private final RestTemplate restTemplate;
    /**
     * redisHelper
     */
    private final RedisHelper redisHelper;
    /**
     * 对象映射对象
     */
    private final ObjectMapper objectMapper;
    /**
     * DiscoveryClient对象
     */
    private final DiscoveryClient discoveryClient;
    /**
     * 属性配置对象
     */
    private final ConfigProperties configProperties;
    /**
     * 服务路由资源库对象
     */
    private final ServiceRouteRepository serviceRouteRepository;

    @Autowired
    public SwaggerRepositoryImpl(SwaggerMapper swaggerMapper,
                                 RestTemplate restTemplate,
                                 RedisHelper redisHelper,
                                 ObjectMapper objectMapper,
                                 DiscoveryClient discoveryClient,
                                 ConfigProperties configProperties,
                                 ServiceRouteRepository serviceRouteRepository) {
        this.swaggerMapper = swaggerMapper;
        this.restTemplate = restTemplate;
        this.redisHelper = redisHelper;
        this.objectMapper = objectMapper;
        this.discoveryClient = discoveryClient;
        this.configProperties = configProperties;
        this.serviceRouteRepository = serviceRouteRepository;
    }

    @Override
    public String fetchSwaggerJsonByService(String service, String version) {
        Swagger query = new Swagger();
        query.setServiceName(service);
        query.setServiceVersion(version);
        Swagger data = this.swaggerMapper.selectOne(query);
        if (data == null || StringUtils.isEmpty(data.getValue())) {
            String json = this.fetchFromDiscoveryClient(service, version);
            if (json != null && data == null) {
                //insert
                Swagger insertSwagger = new Swagger();
                insertSwagger.setServiceName(service);
                insertSwagger.setServiceVersion(version);
                insertSwagger.setValue(json);
                if (this.swaggerMapper.insertSelective(insertSwagger) != 1) {
                    LOGGER.warn("insert swagger error, swagger : {}", insertSwagger);
                }
            } else if (json != null && StringUtils.isEmpty(data.getValue())) {
                //update
                query.setValue(json);
                if (this.swaggerMapper.updateByPrimaryKeySelective(query) != 1) {
                    LOGGER.warn("update swagger error, swagger : {}", query);
                }
            }
            return json;
        } else {
            return data.getValue();
        }
    }

    @Override
    public List<SwaggerResource> getSwaggerResource() {
        List<SwaggerResource> swaggerResources = this.processSwaggerResource();
        swaggerResources.sort(Comparator.comparing(SwaggerResource::getName));
        return swaggerResources;
    }

    @Override
    public MultiKeyMap<String, Set<String>> getServiceMetaDataMap() {
        MultiKeyMap<String, Set<String>> serviceMap = new MultiKeyMap<>();

        List<SwaggerResource> resources = this.getSwaggerResource();
        for (SwaggerResource resource : resources) {
            String name = resource.getName();
            String[] nameArray = name.split(COLON);
            String location = resource.getLocation();
            String[] locationArray = location.split(LOCATION_SPLIT_REGX);
            if (nameArray.length != 2 || locationArray.length != 2) {
                LOGGER.warn("the resource name is not match xx:xx or location is not match /doc/xx?version=xxx , name : {}, location: {}",
                        name, location);
                continue;
            }
            String routeName = nameArray[0];
            String service = nameArray[1];
            String version = locationArray[1];
            if (!serviceMap.containsKey(routeName, service)) {
                Set<String> versionSet = new HashSet<>();
                versionSet.add(version);
                serviceMap.put(routeName, service, versionSet);
            } else {
                Set<String> versionSet = serviceMap.get(routeName, service);
                versionSet.add(version);
            }
        }

        return serviceMap;
    }

    @Override
    public Map<String, List<Map<String, Object>>> queryTreeMenu() {
        Map<String, List<Map<String, Object>>> treeMenu = new HashMap<>(2);

        MultiKeyMap<String, Set<String>> serviceMetaDataMap = this.getServiceMetaDataMap();
        if (MapUtils.isEmpty(serviceMetaDataMap)) {
            return treeMenu;
        }

        List<Map<String, Object>> serviceApis = new ArrayList<>();
        serviceMetaDataMap.forEach(((multiKey, versions) -> {
            String routeName = multiKey.getKey(0);
            String service = multiKey.getKey(1);

            Map<String, Object> serviceMap = new HashMap<>();
            serviceMap.put(TITLE, service);
            List<Map<String, Object>> children = new ArrayList<>();
            int versionNum = this.processTreeOnVersionNode(routeName, service, versions, children);
            serviceMap.put(CHILDREN, children);
            if (versionNum > 0) {
                serviceApis.add(serviceMap);
            }
        }));

        treeMenu.put(SERVICE, serviceApis);
        this.processKey(treeMenu);
        return treeMenu;
    }

    @Override
    public ControllerDTO queryPathDetail(String serviceName, String version, String controllerName, String operationId) {
        // 数据缓存的key
        String dataCacheKey = this.getPathDetailDataRedisKey(serviceName, version, controllerName, operationId);
        // 判断缓存中当前缓存是否有效
        if (this.pathDetailCacheIsEffective(dataCacheKey, serviceName, version)) {
            String value = this.redisHelper.strGet(dataCacheKey);
            try {
                return objectMapper.readValue(value, ControllerDTO.class);
            } catch (IOException e) {
                LOGGER.error("object mapper read redis cache value {} to ControllerDTO error, so process from db or swagger, exception: {} ",
                        value, e);
            }
        }

        try {
            return this.processPathDetailFromSwagger(serviceName, version, controllerName, operationId);
        } catch (IOException e) {
            LOGGER.error("fetch swagger json error, service: {}, version: {}, exception: {}", serviceName, version, e.getMessage());
            throw new CommonException("error.service.not.run", serviceName, version);
        }
    }

    /**
     * 根据服务名称和版本获取服务客户端信息
     *
     * @param service 服务名称
     * @param version 版本
     * @return 服务客户端信息
     */
    private String fetchFromDiscoveryClient(String service, String version) {
        List<ServiceInstance> instances = this.discoveryClient.getInstances(service);
        List<String> mdVersions = new ArrayList<>();
        for (ServiceInstance instance : instances) {
            String mdVersion = instance.getMetadata().get(VersionUtil.METADATA_VERSION);
            mdVersions.add(mdVersion);
            if (StringUtils.isEmpty(mdVersion)) {
                mdVersion = VersionUtil.NULL_VERSION;
            }
            if (version.equals(mdVersion)) {
                return this.fetch(instance);
            }
        }
        LOGGER.warn("service {} running instances {} do not contain the version {} ", service, mdVersions, version);
        return null;
    }

    /**
     * 获取实例相关数据
     *
     * @param instance 实例对象
     * @return 实例相关数据
     */
    private String fetch(ServiceInstance instance) {
        ResponseEntity<String> response;
        String contextPath = instance.getMetadata().get(CONTEXT);
        if (contextPath == null) {
            contextPath = "";
        }
        LOGGER.info("service: {} metadata : {}", instance.getServiceId(), instance.getMetadata());
        try {
            response = this.restTemplate.getForEntity(
                    instance.getUri() + contextPath + API_DOC_URL,
                    String.class);
        } catch (RestClientException e) {
            String msg = "fetch failed, instance:" + instance.getServiceId() + ", uri: " + instance.getUri() + ", contextPath: " + contextPath;
            throw new RemoteAccessException(msg);
        } catch (IllegalStateException e) {
            // 服务不可达，说明服务已经下线或者不可访问,直接返回空数据
            return null;
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteAccessException("fetch failed : " + response);
        }
        return response.getBody();
    }

    /**
     * 处理swagger资源
     *
     * @return swagger资源
     */
    private List<SwaggerResource> processSwaggerResource() {
        List<SwaggerResource> resources = new LinkedList<>();
        //key1:服务名 key2:版本 value:route
        MultiKeyMap<String, List<ServiceRoute>> allRunningInstances = Optional
                .ofNullable(this.serviceRouteRepository.getAllRunningInstances()).orElse(new MultiKeyMap<>());
        allRunningInstances.forEach(((multiKey, serviceRoutes) -> {
            String serviceId = serviceRoutes.get(0).getServiceCode();
            String name = serviceRoutes.get(0).getPath().replace("/**", "").replace("/", "");
            if (serviceId != null) {
                boolean isSkip = Arrays.stream(this.configProperties.getRoute().getSkipParseServices())
                        .anyMatch(t -> this.matcher.match(t, serviceId));
                if (!isSkip) {
                    SwaggerResource resource = new SwaggerResource();
                    resource.setName(String.format(SWAGGER_RESOURCE_NAME_TEMPLATE, name, serviceId));
                    resource.setSwaggerVersion(SWAGGER_RESOURCE_VERSION);
                    resource.setLocation(String.format(SWAGGER_RESOURCE_LOCATION_TEMPLATE, name, multiKey.getKey(1)));
                    resources.add(resource);
                }
            }
        }));

        return resources;
    }

    /**
     * 处理树型结构的每个版本的节点
     *
     * @param routeName   路由名称
     * @param serviceName 服务名称
     * @param versions    服务版本
     * @param children    子节点
     * @return 版本的个数
     */
    private int processTreeOnVersionNode(String routeName, String serviceName, Set<String> versions, List<Map<String, Object>> children) {
        int versionNum = versions.size();
        for (String version : versions) {
            boolean legalVersion;
            Map<String, Object> versionMap = new HashMap<>(16);
            versionMap.put(TITLE, version);
            List<Map<String, Object>> versionChildren = new ArrayList<>();
            versionMap.put(CHILDREN, versionChildren);
            if (this.apiTreeCacheIsEffective(serviceName, version)) {
                String apiTreeDataCacheKey = this.getApiTreeDataCacheKey(serviceName, version);
                String childrenStr = this.redisHelper.strGet(apiTreeDataCacheKey);
                try {
                    List<Map<String, Object>> list = this.objectMapper.readValue(childrenStr,
                            new TypeReference<List<Map<String, Object>>>() {
                            });
                    versionChildren.addAll(list);
                    legalVersion = true;
                } catch (IOException e) {
                    LOGGER.error("object mapper read redis cache value {} to List<Map<String, Object>> error, so process children version from db or swagger, exception: {} ", childrenStr, e);
                    legalVersion = this.processChildrenFromSwaggerJson(routeName, serviceName, version, versionChildren);
                }
            } else {
                legalVersion = this.processChildrenFromSwaggerJson(routeName, serviceName, version, versionChildren);
            }
            if (legalVersion) {
                children.add(versionMap);
            } else {
                versionNum--;
            }
        }
        return versionNum;
    }

    /**
     * 处理key
     *
     * @param map 结果数据
     */
    private void processKey(Map<String, List<Map<String, Object>>> map) {
        List<Map<String, Object>> serviceList = map.get(SERVICE);
        int serviceCount = 0;
        for (Map<String, Object> service : serviceList) {
            String serviceKey = serviceCount + "";
            service.put(KEY, serviceKey);
            List<Map<String, Object>> versions = this.getChildren(service);
            this.recursion(serviceKey, versions);
            serviceCount++;
        }
    }

    /**
     * 递归处理
     *
     * @param key  key
     * @param list list
     */
    private void recursion(String key, List<Map<String, Object>> list) {
        int count = 0;
        for (Map<String, Object> map : list) {
            String mapKey = key + UNDERLINE + count;
            map.put(KEY, mapKey);
            if (map.get(CHILDREN) != null) {
                List<Map<String, Object>> children = this.getChildren(map);
                recursion(mapKey, children);
            }
            count++;
        }
    }

    /**
     * 使用swagger的json数据解析子节点
     *
     * @param routeName       路由
     * @param service         服务
     * @param version         版本
     * @param versionChildren 子节点版本
     * @return 处理结果：是否处理成功
     */
    private boolean processChildrenFromSwaggerJson(String routeName, String service, String version,
                                                   List<Map<String, Object>> versionChildren) {
        boolean done = false;
        try {
            String json = this.fetchSwaggerJsonByService(service, version);
            if (StringUtils.isBlank(json)) {
                LOGGER.warn("the swagger json of service {} version {} is empty, skip", service, version);
            } else {
                JsonNode node = this.objectMapper.readTree(json);
                this.processTreeOnControllerNode(routeName, service, version, node, versionChildren);
            }
            done = true;
        } catch (IOException e) {
            LOGGER.error("object mapper read tree error, service: {}, version: {}", service, version);
        } catch (RemoteAccessException e) {
            LOGGER.error(e.getMessage());
        }
        return done;
    }

    /**
     * 处理树节点中的Controller节点
     *
     * @param routeName 路由
     * @param service   服务
     * @param version   版本
     * @param node      节点
     * @param children  子节点
     */
    private void processTreeOnControllerNode(String routeName, String service, String version, JsonNode node,
                                             List<Map<String, Object>> children) {
        Map<String, Map<String, Object>> controllerMap = this.processControllerNode(node);
        Map<String, List<Map<String, Object>>> pathMap = this.processPathMap(routeName, service, version, node);

        controllerMap.forEach((name, nodeData) -> {
            List<Map<String, Object>> controllerChildren = this.getChildren(nodeData);
            List<Map<String, Object>> list = pathMap.get(name);
            if (list != null) {
                children.add(nodeData);
                String refControllerName = name.replaceAll(BaseConstants.Symbol.SPACE,
                        BaseConstants.Symbol.MIDDLE_LINE);
                for (Map<String, Object> path : list) {
                    path.put(REF_CONTROLLER, refControllerName);
                    controllerChildren.add(path);
                }
            }
        });

        this.cacheApiTreeDoc(service, version, children);
    }

    /**
     * 缓存api树结构文档数据
     *
     * @param serviceName 服务名
     * @param version     服务版本
     * @param children    缓存值
     */
    private void cacheApiTreeDoc(String serviceName, String version, Object children) {
        try {
            String value = this.objectMapper.writeValueAsString(children);
            this.redisHelper.strSet(this.getApiTreeDataCacheKey(serviceName, version), value, 10, TimeUnit.DAYS);
            this.redisHelper.strSet(this.getApiTreeRefreshTimeCacheKey(serviceName, version), String.valueOf(System.currentTimeMillis()),
                    10, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            LOGGER.warn("read object to string error while caching to redis, exception", e);
        }
    }

    /**
     * 判断ApiTree缓存是否有效
     *
     * @param serviceName 服务名称
     * @param version     服务版本
     * @return true 缓存有效 false 缓存无效
     */
    private boolean apiTreeCacheIsEffective(String serviceName, String version) {
        String apiTreeDataCacheKey = this.getApiTreeDataCacheKey(serviceName, version);
        if (BooleanUtils.isNotTrue(this.redisHelper.hasKey(apiTreeDataCacheKey))) {
            return false;
        }

        // 获取服务swagger文档刷新时间
        long swaggerRefreshTime = this.getServiceSwaggerRefreshTime(serviceName, version);

        // ApiTree刷新时间
        long apiTreeRefreshTime = 0L;
        String apiTreeRefreshTimeCacheKey = this.getApiTreeRefreshTimeCacheKey(serviceName, version);
        String refreshTime = this.redisHelper.strGet(apiTreeRefreshTimeCacheKey);
        if (StringUtils.isNotBlank(refreshTime)) {
            apiTreeRefreshTime = Long.parseLong(refreshTime);
        }

        // ApiTree刷新时间大于swagger文档刷新时间，才认为缓存有效
        return apiTreeRefreshTime > swaggerRefreshTime;
    }

    /**
     * 获取api树结构文档数据缓存key
     *
     * @param serviceName 服务
     * @param version     版本
     * @return key
     */
    private String getApiTreeDataCacheKey(String serviceName, String version) {
        return String.format("%s:swagger:api-tree-doc:%s:%s:data", HZeroService.Admin.CODE, serviceName, version);
    }

    /**
     * 获取api树结构文档刷新时间缓存key
     *
     * @param serviceName 服务
     * @param version     版本
     * @return key
     */
    private String getApiTreeRefreshTimeCacheKey(String serviceName, String version) {
        return String.format("%s:swagger:api-tree-doc:%s:%s:refresh-time", HZeroService.Admin.CODE, serviceName, version);
    }

    /**
     * 处理path数据
     *
     * @param routeName 路由
     * @param service   服务
     * @param version   版本
     * @param node      节点对象
     * @return path的数据
     */
    private Map<String, List<Map<String, Object>>> processPathMap(String routeName, String service,
                                                                  String version, JsonNode node) {
        Map<String, List<Map<String, Object>>> pathMap = new HashMap<>(16);
        JsonNode pathNode = node.get(PATHS);
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            String url = urlIterator.next();
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                String method = methodIterator.next();
                JsonNode jsonNode = methodNode.findValue(method);
                if (jsonNode.get(DESCRIPTION) == null) {
                    continue;
                }
                Map<String, Object> path = new HashMap<>(16);
                path.put(TITLE, url);
                path.put(METHOD, method);
                path.put(OPERATION_ID, Optional.ofNullable(jsonNode.get(OPERATION_ID)).map(JsonNode::asText).orElse(null));
                path.put(SERVICE, service);
                path.put(VERSION, version);
                path.put(DESCRIPTION, Optional.ofNullable(jsonNode.get(SUMMARY)).map(JsonNode::asText).orElse(null));
                path.put(SERVICE_PREFIX, routeName);
                JsonNode tagNode = jsonNode.get(TAGS);
                for (int i = 0; i < tagNode.size(); i++) {
                    String tag = tagNode.get(i).asText();
                    if (pathMap.get(tag) == null) {
                        List<Map<String, Object>> list = new ArrayList<>();
                        list.add(path);
                        pathMap.put(tag, list);
                    } else {
                        pathMap.get(tag).add(path);
                    }
                }
            }
        }
        return pathMap;
    }

    /**
     * 处理Controller数据
     *
     * @param node 节点
     * @return 处理结果   key ---> value === name ---> nodeData
     */
    private Map<String, Map<String, Object>> processControllerNode(JsonNode node) {
        Map<String, Map<String, Object>> controllerMap = new HashMap<>(16);
        JsonNode tagNodes = node.get(TAGS);
        for (JsonNode jsonNode : tagNodes) {
            String name = jsonNode.findValue(NAME).asText();
            Map<String, Object> controller = new HashMap<>(16);
            controller.put(TITLE, name);
            controller.put(CHILDREN, new ArrayList<>());

            controllerMap.put(name, controller);
        }
        return controllerMap;
    }

    /**
     * 获取swagger的json数据
     *
     * @param name    服务名称
     * @param version 服务版本
     * @return 获取到的json数据
     */
    private String getSwaggerJson(String name, String version) {
        String serviceName = this.getRouteName(name);
        String json = this.fetchSwaggerJsonByService(serviceName, version);
        try {
            if (json != null) {
                //自定义扩展swaggerJson
                json = this.expandSwaggerJson(name, version, json);
            }
        } catch (IOException e) {
            LOGGER.error("fetch swagger json error, service: {}, version: {}, exception: {}", name, version, e.getMessage());
            throw new CommonException(e, "error.service.not.run", name, version);
        }
        return json;
    }

    /**
     * 获取路由
     *
     * @param name 服务名称
     * @return 路由
     */
    private String getRouteName(String name) {
        String serviceName;
        List<ServiceRoute> serviceRoutes = this.serviceRouteRepository.select(ServiceRoute.FIELD_SERVICE_CODE, name);
        if (CollectionUtils.isEmpty(serviceRoutes)) {
            throw new CommonException("error.route.not.found.routeName{" + name + "}");
        } else {
            serviceName = serviceRoutes.get(0).getServiceCode();
        }
        return serviceName;
    }

    /**
     * 扩展处理swagger的json数据
     *
     * @param name    服务
     * @param version 版本
     * @param json    json数据
     * @return 处理结果
     * @throws IOException 处理异常
     */
    public String expandSwaggerJson(String name, String version, String json) throws IOException {
        MultiKeyMap<String, List<ServiceRoute>> allRunningInstances = this.serviceRouteRepository.getAllRunningInstances();
        List<ServiceRoute> serviceRoutes = allRunningInstances.get(name, version);
        if (CollectionUtils.isEmpty(serviceRoutes)) {
            return "";
        }
        String basePath = serviceRoutes.get(0).getPath().replace("/**", "");
        ObjectNode root = (ObjectNode) this.objectMapper.readTree(json);
        root.put(BASE_PATH, basePath);
        return this.objectMapper.writeValueAsString(root);
    }

    /**
     * 从swagger数据中处理路径的详细数据
     *
     * @param serviceName    服务
     * @param version        版本
     * @param controllerName controller名字
     * @param operationId    操作ID
     * @return 处理结果的controller数据对象
     * @throws IOException 处理异常
     */
    private ControllerDTO processPathDetailFromSwagger(String serviceName, String version, String controllerName, String operationId) throws IOException {
        String json = this.getSwaggerJson(serviceName, version);
        if (StringUtils.isBlank(json)) {
            throw new CommonException("error.controller.not.found", controllerName);
        }

        JsonNode node = this.objectMapper.readTree(json);
        List<ControllerDTO> controllers = this.processControllers(node);
        List<ControllerDTO> targetControllers = controllers.stream()
                .filter(c -> StringUtils.isNotBlank(c.getName()))
                .filter(c -> controllerName.equals(c.getName().replaceAll(BaseConstants.Symbol.SPACE,
                        BaseConstants.Symbol.MIDDLE_LINE)))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(targetControllers)) {
            throw new CommonException("error.controller.not.found", controllerName);
        }

        Map<String, Map<String, FieldDTO>> map = this.processDefinitions(node);
        Map<String, String> dtoMap = this.convertMap2JsonWithComments(map);
        JsonNode pathNode = node.get(PATHS);
        String basePath = node.get(BASE_PATH).asText();
        ControllerDTO controller = this.queryPathDetailByOptions(serviceName, pathNode, targetControllers, operationId, dtoMap, basePath);
        String dataCacheKey = this.getPathDetailDataRedisKey(serviceName, version, controllerName, operationId);
        this.cachePathDetail(serviceName, version, dataCacheKey, controller);
        return controller;
    }

    /**
     * 将数据缓存到redis中
     *
     * @param serviceName 服务名称
     * @param version     服务版本
     * @param key         缓存的key
     * @param value       缓存的值
     */
    private void cachePathDetail(String serviceName, String version, String key, Object value) {
        try {
            //缓存10天
            this.redisHelper.strSet(key, this.objectMapper.writeValueAsString(value), 10, TimeUnit.DAYS);
            this.redisHelper.strSet(this.getPathDetailRefreshTimeRedisKey(serviceName, version), String.valueOf(System.currentTimeMillis()),
                    10, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            LOGGER.warn("read object to string error while caching to redis, exception", e);
        }
    }

    /**
     * 判断PathDetail缓存是否有效
     *
     * @param serviceName 服务名称
     * @param version     服务版本
     * @return true 缓存有效 false 缓存无效
     */
    private boolean pathDetailCacheIsEffective(String dataCacheKey, String serviceName, String version) {
        if (BooleanUtils.isNotTrue(this.redisHelper.hasKey(dataCacheKey))) {
            return false;
        }

        // 获取服务swagger文档刷新时间
        long swaggerRefreshTime = this.getServiceSwaggerRefreshTime(serviceName, version);

        // 路径详情刷新时间
        long pathDetailRefreshTime = 0L;
        String refreshTime = this.redisHelper.strGet(this.getPathDetailRefreshTimeRedisKey(serviceName, version));
        if (StringUtils.isNotBlank(refreshTime)) {
            pathDetailRefreshTime = Long.parseLong(refreshTime);
        }

        // 服务刷新时间大于swagger文档刷新时间，才认为缓存有效
        return pathDetailRefreshTime > swaggerRefreshTime;
    }

    /**
     * 获取路径详情数据的redis缓存key
     *
     * @param serviceName    服务名称
     * @param version        服务版本
     * @param controllerName controller名称
     * @param operationId    操作ID
     * @return 缓存key
     */
    private String getPathDetailDataRedisKey(String serviceName, String version, String controllerName, String operationId) {
        return String.format("%s:swagger:path-detail:%s:%s:data:%s:%s", HZeroService.Admin.CODE,
                serviceName, version, controllerName, operationId);
    }

    /**
     * 获取路径详情缓存时间的redis缓存key
     *
     * @param serviceName 服务名称
     * @param version     服务版本
     * @return 缓存key
     */
    private String getPathDetailRefreshTimeRedisKey(String serviceName, String version) {
        return String.format("%s:swagger:path-detail:%s:%s:refresh-time", HZeroService.Admin.CODE, serviceName, version);
    }

    /**
     * 将map数据转换成带有注释的json数据
     *
     * @param map map数据
     * @return 转换的结果json数据
     */
    private Map<String, String> convertMap2JsonWithComments(Map<String, Map<String, FieldDTO>> map) {
        Map<String, String> returnMap = new HashMap<>();
        map.forEach((className, value) -> {
            StringBuilder sb = new StringBuilder();
            //dto引用链表，用于判断是否有循环引用
            MyLinkedList<String> linkedList = new MyLinkedList<>();
            linkedList.addNode(className);
            this.process2String(className, map, sb, linkedList);
            returnMap.put(className, sb.toString());
        });
        return returnMap;
    }

    /**
     * 将数据对象转换成字符串
     *
     * @param ref        引用对象
     * @param map        元数据
     * @param sb         字符串缓存对象
     * @param linkedList 用于判断是否循环引用的对象
     */
    private void process2String(String ref, Map<String, Map<String, FieldDTO>> map, StringBuilder sb,
                                MyLinkedList<String> linkedList) {
        String className = this.subString4ClassName(ref);
        if (map.containsKey(className)) {
            sb.append("{\n");
            Map<String, FieldDTO> fields = map.get(className);
            //两个空格为缩进单位
            if (fields != null) {
                for (Map.Entry<String, FieldDTO> entry1 : fields.entrySet()) {
                    String field = entry1.getKey();
                    FieldDTO dto = entry1.getValue();
                    //如果是集合类型，注释拼到字段的上一行
                    String type = dto.getType();
                    if (ARRAY.equals(type)) {
                        //处理集合引用的情况，type为array
                        if (dto.getComment() != null) {
                            sb.append("//");
                            sb.append(dto.getComment());
                            sb.append("\n");
                        }
                        this.appendField(sb, field);
                        sb.append("[\n");
                        if (dto.getRef() != null) {
                            this.processRefField(map, sb, linkedList, dto);
                        } else {
                            sb.append(type);
                            sb.append("\n");
                        }
                        sb.append("]\n");
                    } else if (StringUtils.isEmpty(type)) {
                        //单一对象引用的情况，只有ref
                        if (dto.getRef() != null) {
                            if (dto.getComment() != null) {
                                sb.append("//");
                                sb.append(dto.getComment());
                                sb.append("\n");
                            }
                            this.appendField(sb, field);
                            this.processRefField(map, sb, linkedList, dto);
                        } else {
                            sb.append("{}\n");
                        }
                    } else {
                        if (INTEGER.equals(type) || STRING.equals(type) || BOOLEAN.equals(type)) {
                            this.appendField(sb, field);
                            sb.append("\"");
                            sb.append(type);
                            sb.append("\"");
                            //拼注释
                            this.appendComment(sb, dto);
                            sb.append("\n");
                        }
                        if (OBJECT.equals(type)) {
                            this.appendField(sb, field);
                            sb.append("\"{}\"");
                            //拼注释
                            this.appendComment(sb, dto);
                            sb.append("\n");
                        }
                    }
                }
            }
            sb.append("}");
        }
    }

    /**
     * 处理引用字段
     *
     * @param map        元数据
     * @param sb         字符串缓存对象
     * @param linkedList 用于判断是否循环引用的对象
     * @param dto        当前字段dto
     */
    private void processRefField(Map<String, Map<String, FieldDTO>> map, StringBuilder sb,
                                 MyLinkedList<String> linkedList, FieldDTO dto) {
        String refClassName = this.subString4ClassName(dto.getRef());
        //linkedList深拷贝一份，处理同一个对象对另一个对象的多次引用的情况
        MyLinkedList<String> copyLinkedList = linkedList.deepCopy();
        copyLinkedList.addNode(refClassName);
        //循环引用直接跳出递归
        if (copyLinkedList.isLoop()) {
            sb.append("{}");
        } else {
            //递归解析
            this.process2String(refClassName, map, sb, copyLinkedList);
        }
    }

    /**
     * 根据引用获取类明
     *
     * @param ref 引用
     * @return 类名
     */
    private String subString4ClassName(String ref) {
        //截取#/definitions/RouteDTO字符串，拿到类名
        String[] arr = ref.split("/");
        return arr.length > 1 ? arr[arr.length - 1] : arr[0];
    }

    /**
     * 追加字段
     *
     * @param sb    字符串缓存对象
     * @param field 字段
     */
    private void appendField(StringBuilder sb, String field) {
        sb.append("\"");
        sb.append(field);
        sb.append("\"");
        sb.append(COLON);
    }

    /**
     * 追加注释
     *
     * @param sb  字符串缓存对象
     * @param dto 字段数据对象
     */
    private void appendComment(StringBuilder sb, FieldDTO dto) {
        if (dto.getComment() != null) {
            sb.append(" //");
            sb.append(dto.getComment());
        }
    }

    /**
     * 解析Controoler节点
     *
     * @param node 节点
     * @return Controller实体对象
     */
    private List<ControllerDTO> processControllers(JsonNode node) {
        List<ControllerDTO> controllers = new ArrayList<>();
        JsonNode tagNodes = node.get(TAGS);
        for (JsonNode jsonNode : tagNodes) {
            String name = jsonNode.findValue(NAME).asText();
            String description = jsonNode.findValue(DESCRIPTION).asText();
            ControllerDTO controller = new ControllerDTO();
            controller.setName(name);
            controller.setDescription(description);
            controller.setPaths(new ArrayList<>());
            controllers.add(controller);
        }
        return controllers;
    }

    /**
     * 处理定义节点
     *
     * @param node 节点
     * @return Definitions
     */
    private Map<String, Map<String, FieldDTO>> processDefinitions(JsonNode node) {
        Map<String, Map<String, FieldDTO>> map = new HashMap<>();
        //definitions节点是controller里面的对象json集合
        JsonNode definitionNodes = node.get(DEFINITIONS);
        if (definitionNodes != null) {
            Iterator<String> classNameIterator = definitionNodes.fieldNames();
            while (classNameIterator.hasNext()) {
                String className = classNameIterator.next();
                JsonNode jsonNode = definitionNodes.get(className);
                JsonNode propertyNode = jsonNode.get(PROPERTIES);
                if (propertyNode == null) {
                    String type = jsonNode.get(TYPE).asText();
                    if (OBJECT.equals(type)) {
                        map.put(className, null);
                    }
                    continue;
                }
                Iterator<String> filedNameIterator = propertyNode.fieldNames();
                Map<String, FieldDTO> fieldMap = new HashMap<>();
                while (filedNameIterator.hasNext()) {
                    FieldDTO field = new FieldDTO();
                    String filedName = filedNameIterator.next();
                    JsonNode fieldNode = propertyNode.get(filedName);
                    String type = Optional.ofNullable(fieldNode.get(TYPE)).map(JsonNode::asText).orElse(null);
                    field.setType(type);
                    String description = Optional.ofNullable(fieldNode.get(DESCRIPTION)).map(JsonNode::asText).orElse(null);
                    field.setComment(description);
                    field.setRef(Optional.ofNullable(fieldNode.get(VAR_REF)).map(JsonNode::asText).orElse(null));
                    JsonNode itemNode = fieldNode.get(ITEMS);
                    Optional.ofNullable(itemNode).ifPresent(i -> {
                        if (i.get(TYPE) != null) {
                            field.setItemType(i.get(TYPE).asText());
                        }
                        if (i.get(VAR_REF) != null) {
                            field.setRef(i.get(VAR_REF).asText());
                        }
                    });
                    fieldMap.put(filedName, field);
                }
                map.put(className, fieldMap);
            }
        }
        return map;
    }

    /**
     * 通过选项查询路径详情
     *
     * @param name              服务名
     * @param pathNode          路径节点
     * @param targetControllers 目标Controllers
     * @param operationId       操作ID
     * @param dtoMap            数据map
     * @param basePath          基础路径
     * @return 查询结果对象
     */
    private ControllerDTO queryPathDetailByOptions(String name, JsonNode pathNode, List<ControllerDTO> targetControllers,
                                                   String operationId, Map<String, String> dtoMap, String basePath) {
        String serviceName = this.getRouteName(name);
        Iterator<String> urlIterator = pathNode.fieldNames();
        while (urlIterator.hasNext()) {
            String url = urlIterator.next();
            JsonNode methodNode = pathNode.get(url);
            Iterator<String> methodIterator = methodNode.fieldNames();
            while (methodIterator.hasNext()) {
                String method = methodIterator.next();
                JsonNode pathDetailNode = methodNode.get(method);
                String pathOperationId = pathDetailNode.get(OPERATION_ID).asText();
                if (operationId.equals(pathOperationId)) {
                    this.processPathDetail(serviceName, targetControllers, dtoMap, url, methodNode, method, basePath);
                }
            }
        }
        return targetControllers.get(0);
    }

    /**
     * 处理路径详情
     *
     * @param serviceName 服务名
     * @param controllers Controller对象s
     * @param dtoMap      dtomap
     * @param url         url
     * @param methodNode  方法节点
     * @param method      方法
     * @param basePath    基准路径
     */
    private void processPathDetail(String serviceName, List<ControllerDTO> controllers, Map<String, String> dtoMap,
                                   String url, JsonNode methodNode, String method, String basePath) {
        PathDTO path = new PathDTO();
        path.setBasePath(basePath);
        path.setUrl(url);
        path.setMethod(method);
        JsonNode jsonNode = methodNode.findValue(method);
        JsonNode tagNode = jsonNode.get(TAGS);

        path.setInnerInterface(false);
        this.setCodeOfPathIfExists(serviceName, path, jsonNode.get(DESCRIPTION), tagNode);

        for (int i = 0; i < tagNode.size(); i++) {
            String tag = tagNode.get(i).asText();
            controllers.forEach(c -> {
                List<PathDTO> paths = c.getPaths();
                if (tag.equals(c.getName())) {
                    path.setRefController(c.getName());
                    paths.add(path);
                }
            });
        }
        path.setRemark(Optional.ofNullable(jsonNode.get(SUMMARY)).map(JsonNode::asText).orElse(null));
        path.setDescription(Optional.ofNullable(jsonNode.get(DESCRIPTION)).map(JsonNode::asText).orElse(null));
        path.setOperationId(Optional.ofNullable(jsonNode.get(OPERATION_ID)).map(JsonNode::asText).orElse(null));
        this.processConsumes(path, jsonNode);
        this.processProduces(path, jsonNode);
        this.processResponses(path, jsonNode, dtoMap);
        this.processParameters(path, jsonNode, dtoMap);
    }

    /**
     * 处理响应数据节点
     *
     * @param path           路径对象
     * @param jsonNode       json节点
     * @param controllerMaps Controller的Maps
     */
    private void processResponses(PathDTO path, JsonNode jsonNode, Map<String, String> controllerMaps) {
        JsonNode responseNode = jsonNode.get(RESPONSES);
        List<ResponseDTO> responses = new ArrayList<>();
        Iterator<String> responseIterator = responseNode.fieldNames();
        while (responseIterator.hasNext()) {
            String status = responseIterator.next();
            JsonNode node = responseNode.get(status);
            ResponseDTO response = new ResponseDTO();
            response.setHttpStatus(status);
            response.setDescription(node.get(DESCRIPTION).asText());
            JsonNode schemaNode = node.get(SCHEMA);
            if (schemaNode != null) {
                JsonNode refNode = schemaNode.get(VAR_REF);
                if (refNode != null) {
                    for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
                        String className = this.subString4ClassName(refNode.asText());
                        if (className.equals(entry.getKey())) {
                            response.setBody(entry.getValue());
                        }
                    }
                } else {
                    String type = Optional.ofNullable(schemaNode.get(TYPE)).map(JsonNode::asText).orElse(null);
                    String ref = Optional.ofNullable(schemaNode.get(ITEMS))
                            .flatMap(itemNode -> Optional.ofNullable(itemNode.get(VAR_REF))
                                    .map(JsonNode::asText))
                            .orElse(null);
                    if (ref != null) {
                        response.setBody(this.processRef(controllerMaps, type, ref).toString());
                    } else {
                        if (OBJECT.equals(type)) {
                            response.setBody("{}");
                        } else {
                            response.setBody(type);
                        }
                    }
                }
            }
            responses.add(response);
        }
        path.setResponses(responses);
    }

    /**
     * set the code field of the instance of {@link PathDTO} if the extraDataNode parameter
     * is not null
     *
     * @param serviceName   the name of the service
     * @param path          the dto
     * @param extraDataNode the extra data node
     * @param tagNode       the tag node
     */
    private void setCodeOfPathIfExists(String serviceName, PathDTO path, JsonNode extraDataNode, JsonNode tagNode) {
        if (extraDataNode != null) {
            try {
                SwaggerExtraData extraData;
                String resourceCode = this.processResourceCode(tagNode);
                extraData = new ObjectMapper().readValue(extraDataNode.asText(), SwaggerExtraData.class);
                PermissionData permission = extraData.getPermission();
                String action = permission.getAction();
                path.setInnerInterface(permission.isPermissionWithin());
                path.setCode(String.format("%s.%s.%s", serviceName, resourceCode, action));
            } catch (IOException e) {
                LOGGER.info("extraData read failed.", e);
            }
        }
    }

    /**
     * 改造：适应空格等方式的Controller
     *
     * @param tags tags节点
     * @return resourceCode
     */
    private String processResourceCode(JsonNode tags) {
        String resourceCode = null;
        for (int i = 0; i < tags.size(); i++) {
            String tag = tags.get(i).asText();
            // 添加choerodon-eureka例外的以-endpoint结尾的tag，
            if (tag.endsWith(SUFFIX_CONTROLLER)) {
                resourceCode = tag.substring(0, tag.length() - SUFFIX_CONTROLLER.length());
            } else if (tag.endsWith(SUFFIX_ENDPOINT)) {
                resourceCode = tag.substring(0, tag.length() - SUFFIX_ENDPOINT.length());
            } else {
                resourceCode = tag.replace(" ", "-").replace("(", "-").replace(")", "").replaceAll("-+", "-")
                        .toLowerCase();
            }
        }

        return resourceCode;
    }

    /**
     * 处理Consumers
     *
     * @param path     path
     * @param jsonNode jsonNode
     */
    private void processConsumes(PathDTO path, JsonNode jsonNode) {
        JsonNode consumeNode = jsonNode.get(CONSUMES);
        List<String> consumes = new ArrayList<>();
        for (int i = 0; i < consumeNode.size(); i++) {
            consumes.add(consumeNode.get(i).asText());
        }
        path.setConsumes(consumes);
    }

    /**
     * 处理Produces
     *
     * @param path     path
     * @param jsonNode jsonNode
     */
    private void processProduces(PathDTO path, JsonNode jsonNode) {
        JsonNode produceNode = jsonNode.get(PRODUCES);
        List<String> produces = new ArrayList<>();
        for (int i = 0; i < produceNode.size(); i++) {
            produces.add(produceNode.get(i).asText());
        }
        path.setProduces(produces);
    }

    /**
     * 处理参数
     *
     * @param path           路径对象
     * @param jsonNode       jsonNode
     * @param controllerMaps ControllerMaps
     */
    private void processParameters(PathDTO path, JsonNode jsonNode, Map<String, String> controllerMaps) {
        JsonNode parameterNode = jsonNode.get(PARAMETERS);
        List<ParameterDTO> parameters = new ArrayList<>();
        if (parameterNode != null) {
            for (int i = 0; i < parameterNode.size(); i++) {
                try {
                    ParameterDTO parameter = this.objectMapper.treeToValue(parameterNode.get(i), ParameterDTO.class);
                    SchemaDTO schema = parameter.getSchema();
                    if (BODY.equals(parameter.getIn()) && schema != null) {
                        String ref = schema.getRef();
                        if (ref != null) {
                            for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
                                String className = this.subString4ClassName(ref);
                                if (className.equals(entry.getKey())) {
                                    String body = entry.getValue();
                                    parameter.setBody(body);
                                }
                            }
                        } else {
                            String type = schema.getType();
                            String itemRef = Optional.ofNullable(schema.getItems()).map(m -> m.get(VAR_REF)).orElse(null);
                            if (itemRef != null) {
                                parameter.setBody(this.processRef(controllerMaps, type, itemRef).toString());
                            } else {
                                if (!OBJECT.equals(type)) {
                                    parameter.setBody(type);
                                } else {
                                    Map<String, String> map = schema.getAdditionalProperties();
                                    if (map != null && ARRAY.equals(map.get(TYPE))) {
                                        parameter.setBody("[{}]");
                                    } else {
                                        parameter.setBody("{}");
                                    }
                                }
                            }
                        }
                    }
                    parameters.add(parameter);
                } catch (JsonProcessingException e) {
                    LOGGER.info("jsonNode to parameterDTO failed, exception: {}", e.getMessage());
                }
            }
        }
        path.setParameters(parameters);
    }

    /**
     * 处理关联
     *
     * @param controllerMaps controllerMaps
     * @param type           type
     * @param itemRef        itemRef
     * @return 结果数据
     */
    private StringBuilder processRef(Map<String, String> controllerMaps, String type, String itemRef) {
        String body = "";
        for (Map.Entry<String, String> entry : controllerMaps.entrySet()) {
            String className = this.subString4ClassName(itemRef);
            if (className.equals(entry.getKey())) {
                body = entry.getValue();
            }
        }
        //给array前面的注释加上缩进，即满足\n//\\S+\n的注释
        return this.arrayTypeAppendBrackets(type, body);
    }

    /**
     * 处理数组类型的数据
     *
     * @param type 类型
     * @param body 数据体
     * @return 处理结果
     */
    private StringBuilder arrayTypeAppendBrackets(String type, String body) {
        StringBuilder sb = new StringBuilder();
        if (ARRAY.equals(type)) {
            sb.append("[\n");
            sb.append(body);
            sb.append("\n]");
        } else {
            sb.append(body);
        }
        return sb;
    }

    /**
     * 获取子节点
     *
     * @param parent 父节点
     * @return 子节点数据
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getChildren(Map<String, Object> parent) {
        return (List<Map<String, Object>>) parent.get(CHILDREN);
    }

    /**
     * 获取指定服务的swagger文档刷新时间
     *
     * @param serviceName 服务名称
     * @param version     服务版本
     * @return 服务swagger文档刷新时间
     */
    private long getServiceSwaggerRefreshTime(String serviceName, String version) {
        // 获取服务swagger文档刷新时间
        return SafeRedisHelper.execute(HZeroService.Swagger.REDIS_DB, () -> {
            // 获取刷新时间
            String refreshTime = this.redisHelper.hshGet(String.format("%s:swagger-refresh:service:%s",
                    HZeroService.Swagger.CODE, serviceName), version);
            if (StringUtils.isNotBlank(refreshTime)) {
                return Long.valueOf(refreshTime);
            } else {
                return 0L;
            }
        });
    }
}
