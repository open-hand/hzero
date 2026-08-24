package org.hzero.iam.domain.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.core.swagger.SwaggerExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.hzero.core.endpoint.request.StaticEndpoint;
import org.hzero.core.endpoint.request.StringStaticEndpointHttpRequest;
import org.hzero.core.util.CommonExecutor;
import org.hzero.iam.config.IamProperties;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.LabelRel;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.repository.LabelRelRepository;
import org.hzero.iam.domain.repository.LabelRepository;
import org.hzero.iam.domain.repository.MenuPermissionRepository;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.hzero.iam.domain.service.ParseService;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.hzero.iam.infra.constant.LabelAssignType.AUTO;
import static org.hzero.iam.infra.constant.LabelAssignType.MANUAL;

/**
 * 解析服务API权限，将API刷新到 iam_permission 表，并将权限刷新到 redis 缓存中
 *
 * @author modified by bergturing on 2020/05/13
 * @author bojiangzhou 2019/12/11
 */
@Component
public class ParseServicePermissionImpl implements ParseService, InitializingBean {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseServicePermissionImpl.class);
    private final StringHttpTransporter httpTransporter;
    private final IamProperties properties;
    private final MenuPermissionRepository menuPermissionRepository;
    private final PermissionRepository permissionRepository;
    private final LabelRepository labelRepository;
    private final LabelRelRepository labelRelRepository;
    /**
     * 需要跳过解析的服务
     */
    private List<String> skipServices;
    /**
     * objectMapper
     */
    private ObjectMapper objectMapper;
    /**
     * 解析文档的线程池
     */
    private ThreadPoolExecutor parserExecutor;
    /**
     * 权限处理器
     */
    private PermissionHandler permissionHandler;

    @Autowired
    public ParseServicePermissionImpl(StringHttpTransporter httpTransporter,
                                      IamProperties properties,
                                      MenuPermissionRepository menuPermissionRepository,
                                      PermissionRepository permissionRepository,
                                      LabelRepository labelRepository,
                                      LabelRelRepository labelRelRepository) {
        this.httpTransporter = httpTransporter;
        this.properties = properties;
        this.menuPermissionRepository = menuPermissionRepository;
        this.permissionRepository = permissionRepository;
        this.labelRepository = labelRepository;
        this.labelRelRepository = labelRelRepository;
    }

    @Override
    public void afterPropertiesSet() {
        // 需要跳过解析的服务
        this.skipServices = Arrays.asList(StringUtils.split(this.properties.getPermission().getSkipServices(),
                BaseConstants.Symbol.COMMA));

        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        int cpu = CommonExecutor.getCpuProcessors();

        this.parserExecutor = new ThreadPoolExecutor(cpu, cpu, 30,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(1 << 15),
                new ThreadFactoryBuilder().setNameFormat("parser-ps-%d").build());
        this.parserExecutor.allowCoreThreadTimeOut(true);
        CommonExecutor.ExecutorManager.registerAndMonitorThreadPoolExecutor("ParseServicePermission", this.parserExecutor);
        // 初始化PermissionHandler对象
        this.permissionHandler = new PermissionHandlerInit().permissionHandler();
    }

    @Override
    public void parser(String serviceName, ServiceInstance instance, Boolean cleanPermission) {
        LOGGER.info("--->>>>>> start refresh service permission, service is {}", serviceName);
        // 跳过解析的服务
        boolean needSkip = StringUtils.isBlank(serviceName)
                || this.skipServices.stream().anyMatch((ss) -> serviceName.contains(ss.trim()));
        if (needSkip) {
            LOGGER.info("skip parse permission services, serviceName={}, skipService={}", serviceName, skipServices);
            return;
        }

        try {
            // 获取权限数据
            List<Permission> permissions = this.permissionHandler.handle(serviceName, instance);
            if (CollectionUtils.isEmpty(permissions)) {
                LOGGER.info("Permission Data Is Empty, serviceName={}", serviceName);
                return;
            }

            // 处理权限数据(新增或更新)
            List<Permission> processedPermission = this.processPermissions(serviceName, permissions, cleanPermission);
            // 处理权限标签
            this.handlePermissionsLabels(processedPermission);

            // 缓存权限
            this.permissionRepository.cacheServicePermissions(serviceName, true);
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonException("hiam.warn.permission.refreshFailed", ExceptionUtils.getRootCauseMessage(e), e);
        }
        LOGGER.info("--->>>>>> end refresh service permission, service is {}", serviceName);
    }

    /**
     * 保存权限数据
     *
     * @param serviceName     服务名称
     * @param permissions     权限
     * @param cleanPermission 是否清楚过期权限
     * @return 进行处理了的权限数据
     */
    protected List<Permission> processPermissions(String serviceName, List<Permission> permissions, Boolean cleanPermission) {
        // 权限数据
        permissions = permissions.parallelStream()
                .filter(permission -> !StringUtils.equalsAnyIgnoreCase(permission.getMethod(), "options", "head"))
                .collect(toList());
        Map<String, Permission> permissionCodeMap = permissions.parallelStream().collect(toMap(Permission::getCode, p -> p));

        // 数据库中的权限数据
        List<Permission> dbPermissions = Optional.ofNullable(this.permissionRepository.listByServiceName(serviceName))
                .orElse(Collections.emptyList());
        Map<String, Permission> dbPermissionCodeMap = dbPermissions.parallelStream().collect(toMap(Permission::getCode, p -> p));

        // 处理需要新增的权限数据
        this.processNeedAddedPermissions(serviceName, permissions, dbPermissionCodeMap);
        // 处理需要更新权限数据
        this.processNeedUpdatedPermissions(serviceName, permissions, dbPermissionCodeMap);

        LOGGER.info("Clean Deprecated Permission: {}", cleanPermission);
        if (Boolean.TRUE.equals(cleanPermission)) {
            // 处理需要移除的权限数据
            this.processNeedRemovedPermission(serviceName, dbPermissions, permissionCodeMap);
        }

        // 返回处理的数据
        return permissions;
    }

    /**
     * 处理需要新增的权限数据
     *
     * @param serviceName         服务名称
     * @param permissions         新的权限数据
     * @param dbPermissionCodeMap 数据库中的权限数据map
     */
    private void processNeedAddedPermissions(String serviceName, List<Permission> permissions,
                                             Map<String, Permission> dbPermissionCodeMap) {
        // 需要添加的权限
        List<Permission> needAddedPermissions = permissions.parallelStream()
                // 数据库中不包含的数据
                .filter(permission -> !dbPermissionCodeMap.containsKey(permission.getCode()))
                .collect(toList());

        if (CollectionUtils.isNotEmpty(needAddedPermissions)) {
            LOGGER.info("@@@ service {} add new permission, new permission codes is {}", serviceName,
                    needAddedPermissions.stream().map(Permission::getCode).collect(toList()));

            // 新增数据
            this.permissionRepository.batchInsertSelective(needAddedPermissions);
        }
    }

    /**
     * 处理需要更新权限数据
     *
     * @param serviceName         服务名称
     * @param permissions         新的权限数据
     * @param dbPermissionCodeMap 数据库中的权限数据map
     */
    private void processNeedUpdatedPermissions(String serviceName, List<Permission> permissions,
                                               Map<String, Permission> dbPermissionCodeMap) {
        // 需要更新的权限
        List<Permission> needUpdatedPermissions = permissions.parallelStream()
                // 数据库中有当前数据
                .filter(permission -> dbPermissionCodeMap.containsKey(permission.getCode()))
                // 数据库中的数据和当前数据不一样
                .filter(permission -> {
                    // 数据库中的权限数据
                    Permission dbPermission = dbPermissionCodeMap.get(permission.getCode());
                    // 设置当前权限数据的id
                    permission.setId(dbPermission.getId());
                    // 设置版本号
                    permission.setObjectVersionNumber(dbPermission.getObjectVersionNumber());

                    return !permission.equals(dbPermission);
                })
                .collect(toList());

        if (CollectionUtils.isNotEmpty(needUpdatedPermissions)) {
            LOGGER.info("@@@ service {} update permission, update permission codes is {}", serviceName,
                    needUpdatedPermissions.stream().map(Permission::getCode).collect(toList()));

            // 更新数据
            this.permissionRepository.batchUpdateByPrimaryKey(needUpdatedPermissions);
        }
    }

    /**
     * 处理需要移除权限数据
     *
     * @param serviceName       服务名称
     * @param dbPermissions     数据库中的权限数据
     * @param permissionCodeMap 新的权限数据map
     */
    private void processNeedRemovedPermission(String serviceName, List<Permission> dbPermissions,
                                              Map<String, Permission> permissionCodeMap) {
        // 需要删除的权限
        List<Permission> needRemovedPermissions = dbPermissions.parallelStream()
                // 权限数据中没有
                .filter(permission -> !permissionCodeMap.containsKey(permission.getCode()))
                .collect(toList());

        if (CollectionUtils.isNotEmpty(needRemovedPermissions)) {
            // 需要删除的权限code
            List<String> deprecatedPermissions = needRemovedPermissions.stream().map(Permission::getCode).collect(toList());
            LOGGER.info("@@@ service {} delete deprecated permission, deprecated permission codes is {}", serviceName,
                    deprecatedPermissions);

            // 清除菜单权限集中的权限数据
            int deleteCount = this.menuPermissionRepository.deleteByPermissionCodes(serviceName, deprecatedPermissions);
            LOGGER.info("clean deprecated menu_permission finished, total: {}", deleteCount);
            // 移除待删除的API的标签
            this.labelRelRepository.recycleAllLabels(Permission.LABEL_DATA_TYPE,
                    needRemovedPermissions.stream().map(Permission::getId).collect(toSet()));

            // 删除数据
            this.permissionRepository.batchDeleteByPrimaryKey(needRemovedPermissions);
        }
    }

    /**
     * 处理权限标签
     *
     * @param permissions 权限数据对象
     */
    private void handlePermissionsLabels(List<Permission> permissions) {
        // 权限标签数据   key ---> value === PermissionId ---> Set<LabelId>
        Map<Long, Set<Long>> permissionLabelMap = this.getPermissionLabelMap(permissions);
        // 权限自动分配的标签 key ---> value === PermissionId ---> Map<LabelId, LabelRelId>
        Map<Long, Map<Long, LabelRel>> assignedLabelRelMap = this.getAssignedLabelMap(permissions);

        // 需要删除的标签关系
        List<Long> needRemovedLabelRels = this
                .getNeedRemovedLabelRels(permissions, permissionLabelMap, assignedLabelRelMap);
        // 处理需要移除的数据
        if (CollectionUtils.isNotEmpty(needRemovedLabelRels)) {
            this.labelRelRepository.batchDeleteByIds(needRemovedLabelRels);
        }

        // 必须先处理需要删除的数据，再处理需要新增的标签关系(数据之间有重复)
        List<LabelRel> needAddedLabelRels = this
                .getNeedAddedLabelRels(permissions, permissionLabelMap, assignedLabelRelMap);
        // 处理需要添加的数据
        if (CollectionUtils.isNotEmpty(needAddedLabelRels)) {
            this.labelRelRepository.batchInsertSelective(needAddedLabelRels);
        }
    }

    /**
     * 获取权限相关的标签数据
     *
     * @param permissions 权限
     * @return 标签  key ---> value === PermissionId ---> Set<LabelId>
     */
    private Map<Long, Set<Long>> getPermissionLabelMap(List<Permission> permissions) {
        // 待处理的权限中所有的标签名称
        List<String> permissionLabelNames = permissions.parallelStream().map(Permission::getTags)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .distinct()
                .collect(toList());
        if (CollectionUtils.isEmpty(permissionLabelNames)) {
            return Collections.emptyMap();
        }

        // 权限相关的标签数据    key ---> value === LabelName ---> LabelId
        Map<String, Long> labelMap = ListUtils.partition(permissionLabelNames, 1000)
                // 将数据按照1000条进行分组处理
                .stream().map(subPermissionLabelNames -> this.labelRepository
                        .selectByCondition(Condition.builder(Label.class)
                                .select(Label.FIELD_ID, Label.FIELD_NAME)
                                .andWhere(Sqls.custom()
                                        .andIn(Label.FIELD_NAME, subPermissionLabelNames)
                                ).build()))
                // 过滤掉查询结果为空的数据
                .filter(CollectionUtils::isNotEmpty)
                // 合成一个Stream
                .flatMap(Collection::stream)
                // 转换成并行流
                .parallel()
                // 分组
                .collect(toMap(Label::getName, Label::getId));
        if (MapUtils.isEmpty(labelMap)) {
            return Collections.emptyMap();
        }

        // 权限标签视图
        return permissions.parallelStream()
                .filter(permission -> ArrayUtils.isNotEmpty(permission.getTags()))
                .collect(toMap(Permission::getId, permission -> Arrays.stream(permission.getTags())
                        // 系统维护了的标签数据
                        .filter(labelMap::containsKey)
                        // 获取标签ID
                        .map(labelMap::get)
                        .collect(toSet())));
    }

    /**
     * 获取权限已经分配的标签
     *
     * @param permissions 权限
     * @return 权限已经自动分配了的标签   key ---> value === PermissionId ---> Map<LabelId, LabelRel(LabelRelId,AssignType)>
     */
    private Map<Long, Map<Long, LabelRel>> getAssignedLabelMap(List<Permission> permissions) {
        // 数据IDs
        List<Long> permissionIds = permissions.parallelStream().map(Permission::getId).collect(toList());
        if (CollectionUtils.isEmpty(permissionIds)) {
            return Collections.emptyMap();
        }

        // 查询所有已自动分配的标签 key ---> value === PermissionId ---> Map<LabelId, LabelRelId>
        return ListUtils.partition(permissionIds, 1000).stream()
                // 将数据按照1000条进行分组处理
                .map(subPermissionIds -> this.labelRelRepository.selectByCondition(Condition.builder(LabelRel.class)
                        .select(LabelRel.FIELD_LABEL_REL_ID, LabelRel.FIELD_DATA_ID, LabelRel.FIELD_ASSIGN_TYPE, LabelRel.FIELD_LABEL_ID)
                        .andWhere(Sqls.custom()
                                .andEqualTo(LabelRel.FIELD_DATA_TYPE, Permission.LABEL_DATA_TYPE)
                                .andIn(LabelRel.FIELD_DATA_ID, subPermissionIds)
                        ).build()))
                // 过滤掉查询结果为空的数据
                .filter(CollectionUtils::isNotEmpty)
                // 合成一个Stream
                .flatMap(Collection::stream)
                // 转换成并行流
                .parallel()
                // 分组
                .collect(groupingBy(LabelRel::getDataId, toMap(LabelRel::getLabelId, t -> t)));
    }

    /**
     * 获取需要移除的标签关系数据
     *
     * @param permissions         权限
     * @param permissionLabelMap  权限标签数据   key ---> value === PermissionId ---> Set<LabelId>
     * @param assignedLabelRelMap 权限自动分配的标签 key ---> value === PermissionId ---> Map<LabelId, LabelRel>
     * @return 需要移除的标签关系数据
     */
    private List<Long> getNeedRemovedLabelRels(List<Permission> permissions,
                                               Map<Long, Set<Long>> permissionLabelMap,
                                               Map<Long, Map<Long, LabelRel>> assignedLabelRelMap) {
        // 筛选出需要移除的数据
        return permissions.parallelStream()
                // 当前权限拥有已分配的权限
                .filter(permission -> assignedLabelRelMap.containsKey(permission.getId()))
                // 找出需要移除的标签关系IDs
                .map(permission -> {
                    // 需要移除的标签关系IDs
                    List<Long> needRemovedRelIds = new ArrayList<>();

                    // 当前权限分配的标签  key ---> value === LabelId---> LabelRelId
                    Map<Long, LabelRel> assignedLabelMap = Optional.ofNullable(assignedLabelRelMap.get(permission.getId()))
                            .orElse(Collections.emptyMap());
                    // 当前权限标签视图
                    Set<Long> viewLabelIds = permissionLabelMap.get(permission.getId());
                    if (CollectionUtils.isEmpty(viewLabelIds)) {
                        // 视图为空，就需要清理所有自动分配的标签
                        return assignedLabelMap.values().stream()
                                .filter(labelRel -> AUTO.getCode().equals(labelRel.getAssignType()))
                                .map(LabelRel::getLabelRelId).collect(toList());
                    }

                    // 遍历已分配的数据，找出标签视图中不存在的标签，即为需要删除的标签
                    assignedLabelMap.forEach((labelId, labelRel) -> {
                        if (AUTO.getCode().equals(labelRel.getAssignType()) && !viewLabelIds.contains(labelId)) {
                            // 自动分配且视图中不存在的标签
                            needRemovedRelIds.add(labelRel.getLabelRelId());
                        } else if (MANUAL.getCode().equals(labelRel.getAssignType()) && viewLabelIds.contains(labelId)) {
                            // 视图中存在且手动分配的标签
                            needRemovedRelIds.add(labelRel.getLabelRelId());
                        }
                    });

                    return needRemovedRelIds;
                })
                // 筛选出不为空的数据
                .filter(CollectionUtils::isNotEmpty)
                // 将List转换成Stream，再将数据合并到一个Stream中
                .flatMap(Collection::stream)
                // 转换成List
                .collect(toList());
    }

    /**
     * 获取需要增加的标签关系数据
     *
     * @param permissions         权限
     * @param permissionLabelMap  权限标签数据   key ---> value === PermissionId ---> Set<LabelId>
     * @param assignedLabelRelMap 权限自动分配的标签 key ---> value === PermissionId ---> Map<LabelId, LabelRel>
     * @return 需要增加的标签关系数据
     */
    private List<LabelRel> getNeedAddedLabelRels(List<Permission> permissions,
                                                 Map<Long, Set<Long>> permissionLabelMap,
                                                 Map<Long, Map<Long, LabelRel>> assignedLabelRelMap) {
        // 筛选出需要添加的数据
        return permissions.parallelStream()
                // 当前权限视图拥有数据
                .filter(permission -> permissionLabelMap.containsKey(permission.getId()))
                // 找出需要添加的标签关系数据
                .flatMap(permission -> {
                    // 当前权限标签视图
                    Set<Long> viewLabelIds = Optional.ofNullable(permissionLabelMap.get(permission.getId()))
                            .orElse(Collections.emptySet());
                    // 当前权限分配的标签  key ---> value === LabelId---> LabelRelId
                    Map<Long, LabelRel> assignedLabelMap = Optional.ofNullable(assignedLabelRelMap.get(permission.getId()))
                            .orElse(Collections.emptyMap());

                    // 遍历已分配的数据，找出已分配标签中不存在的标签，即为需要添加的标签
                    return viewLabelIds.stream()
                            .filter(labelId -> {
                                if (!assignedLabelMap.containsKey(labelId)) {
                                    return true;
                                }

                                // 存在就获取数据
                                LabelRel labelRel = assignedLabelMap.get(labelId);
                                if (Objects.isNull(labelRel)) {
                                    return true;
                                }

                                // 判断是否是手动分配的数据，手动分配的数据需要重新分配为自动分配的数据
                                return MANUAL.getCode().equals(labelRel.getAssignType());
                            })
                            .map(labelId -> LabelRel.built(Permission.LABEL_DATA_TYPE,
                                    permission.getId(), labelId, AUTO.getCode()));
                }).collect(toList());
    }

    /**
     * <p>
     * 权限数据处理器接口
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/05/13 14:49
     */
    private interface PermissionHandler {
        /**
         * 设置下一个处理器
         *
         * @param nextHandler 下一个处理器
         */
        void setNext(PermissionHandler nextHandler);

        /**
         * 获取权限数据
         *
         * @param serviceName 服务名称
         * @param instance    服务实例
         * @return 权限数据
         */
        List<Permission> handle(String serviceName, ServiceInstance instance);
    }

    /**
     * <p>
     * 权限数据处理器初始化对象
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/05/13 14:54
     */
    public class PermissionHandlerInit {
        /**
         * 权限数据处理器
         *
         * @return 权限数据处理器
         */
        public PermissionHandler permissionHandler() {
            // 权限数据处理器的actuator实现
            PermissionHandler actuatorPermissionHandler = new ActuatorPermissionHandler();
            // 权限数据处理器的swagger实现
            SwaggerPermissionHandler swaggerPermissionHandler = new SwaggerPermissionHandler();

            // 处理逻辑为：先使用actuator实现进行处理，如果actuator处理失败，就使用swagger实现进行处理
            actuatorPermissionHandler.setNext(swaggerPermissionHandler);
            // 返回bean
            return actuatorPermissionHandler;
        }
    }

    /**
     * <p>
     * 权限数据处理器接口的抽象实现
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/05/13 15:12
     */
    private abstract class AbstractPermissionHandler implements PermissionHandler {
        /**
         * 下一个处理器
         */
        private PermissionHandler nextHandler;

        @Override
        public final void setNext(PermissionHandler nextHandler) {
            this.nextHandler = nextHandler;
        }

        @Override
        public final List<Permission> handle(String serviceName, ServiceInstance instance) {
            if (this.canHandle(serviceName, instance)) {
                try {
                    // 处理数据
                    return this.doHandle(serviceName, instance);
                } catch (Exception e) {
                    if (Objects.nonNull(this.nextHandler)) {
                        // 捕获处理过程中的所有异常，如果有异常抛出，就代表当前处理器处理失败
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Handler [{}] Process Failure: Service Name Is [{}], Service Instance Is [{}], Exception: {}",
                                    this.getClass().getName(), serviceName, instance, e);
                        }
                        // 继续向下寻找能处理的处理器
                        return this.nextHandler.handle(serviceName, instance);
                    } else {
                        // 抛出异常
                        throw e;
                    }
                }
            } else {
                // 继续处理
                if (Objects.nonNull(this.nextHandler)) {
                    return this.nextHandler.handle(serviceName, instance);
                }
            }

            // 没能处理，就返回空结果
            return Collections.emptyList();
        }

        /**
         * 判断是否可以处理当前请求
         *
         * @param serviceName 服务名称
         * @param instance    服务实例
         * @return 判断结果， true 能处理  false 不能处理，默认是能处理，子类可以覆写该方法
         */
        protected boolean canHandle(String serviceName, ServiceInstance instance) {
            LOGGER.debug("Can Handler Judge Parameter: Service Name Is [{}], ServiceInstance Is [{}]",
                    serviceName, instance);
            return true;
        }

        /**
         * 获取objectMapper对象
         *
         * @return objectMapper对象
         */
        protected ObjectMapper objectMapper() {
            return ParseServicePermissionImpl.this.objectMapper;
        }

        /**
         * 获取httpTransporter对象
         *
         * @return httpTransporter对象
         */
        protected StringHttpTransporter httpTransporter() {
            return ParseServicePermissionImpl.this.httpTransporter;
        }

        /**
         * 获取parserExecutor对象
         *
         * @return parserExecutor对象
         */
        protected ThreadPoolExecutor parserExecutor() {
            return ParseServicePermissionImpl.this.parserExecutor;
        }

        /**
         * 实际处理的逻辑，返回处理的权限数据
         *
         * @param serviceName 服务名称
         * @param instance    服务实例
         * @return 权限数据
         */
        protected abstract List<Permission> doHandle(String serviceName, ServiceInstance instance);
    }

    /**
     * <p>
     * 权限数据处理器的actuator实现
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/05/13 14:52
     */
    private class ActuatorPermissionHandler extends AbstractPermissionHandler {
        /**
         * PermissionData的Map类型，即Map<String, PermissionData>
         */
        private final JavaType PERMISSION_DATA_MAP = this.objectMapper().getTypeFactory()
                .constructParametricType(Map.class, String.class, PermissionData.class);

        @Override
        protected List<Permission> doHandle(String serviceName, ServiceInstance instance) {
            // 1. 获取permissionData数据
            String permissionData = this.fetchPermissionDataByIp(instance);
            // 2. 解析权限数据，并返回结果
            return this.parsePermissionData(serviceName, permissionData);
        }

        /**
         * 获取实例的 permissionData 信息，内部调用服务 /v2/actuator/permission 接口获取权限信息
         *
         * @param instance 服务实例对象
         * @return permissionData信息
         */
        private String fetchPermissionDataByIp(ServiceInstance instance) {
            LOGGER.info("service: {} metadata : {}", instance.getServiceId(), instance.getMetadata());
            try {
                // 获取数据
                return this.httpTransporter().transport(new StringStaticEndpointHttpRequest(instance,
                        StaticEndpoint.ACTUATOR_PERMISSION));
            } catch (RestClientException e) {
                LOGGER.error("fetch error, ex={}", e.getMessage());
                throw new RemoteAccessException("fetch failed, instance: " + instance.getServiceId() + ", ex=" + e.getMessage());
            }
        }

        /**
         * 解析permissionData为Permission对象
         *
         * @param permissionDataString permissionData数据
         * @return 解析出来的Permission对象
         */
        private List<Permission> parsePermissionData(String serviceName, String permissionDataString) {
            if (StringUtils.isBlank(permissionDataString)) {
                LOGGER.info("Fetch Actuator Permission Is Blank, ServiceName={}", serviceName);
                return Collections.emptyList();
            }

            try {
                // 解析数据得到PermissionDataMap
                Map<String, PermissionData> permissionDataMap = this.objectMapper()
                        .readValue(permissionDataString, PERMISSION_DATA_MAP);
                if (MapUtils.isEmpty(permissionDataMap)) {
                    return Collections.emptyList();
                }

                // 权限数据
                List<Permission> permissions = new ArrayList<>(permissionDataMap.size());
                // 遍历PermissionData数据
                permissionDataMap.forEach((key, permissionData) -> {
                    // 创建权限对象
                    permissions.add(new Permission()
                            // 此处的key，设置全局的key，即map的key值
                            .setCode(key)
                            .setPath(permissionData.getPath())
                            .setMethod(permissionData.getMethod())
                            .setLevel(permissionData.getPermissionLevel())
                            .setDescription(permissionData.getDescription())
                            .setAction(permissionData.getAction())
                            .setResource(permissionData.getResourceCode())
                            .setPublicAccess(permissionData.isPermissionPublic())
                            .setLoginAccess(permissionData.isPermissionLogin())
                            .setWithin(permissionData.isPermissionWithin())
                            .setSignAccess(permissionData.isPermissionSign())
                            .setServiceName(serviceName)
                            .setTags(permissionData.getTags()));
                });

                // 返回权限数据
                return permissions;
            } catch (Exception e) {
                throw new CommonException("hiam.error.parse_permission_data.failure");
            }
        }
    }

    /**
     * <p>
     * 权限数据处理器的swagger实现
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/05/13 14:51
     */
    private class SwaggerPermissionHandler extends AbstractPermissionHandler {
        @Override
        protected List<Permission> doHandle(String serviceName, ServiceInstance instance) {
            // 获取服务文档
            String json = this.fetchSwaggerJsonByIp(instance);

            if (StringUtils.isBlank(json)) {
                LOGGER.info("fetch swagger json is blank, serviceName={}", serviceName);
                return Collections.emptyList();
            }

            // 解析权限
            List<Permission> permissions = this.parseJsonPermission(serviceName, json);
            // 编码重复，加上 HttpMethod 后缀
            this.processDuplicatedPermission(permissions);

            return permissions;
        }

        /**
         * 获取实例的 swagger 文档信息，内部调用服务 /v2/choerodon/api-docs 接口获取文档信息
         *
         * @param instance 服务实例
         * @return swagger json
         */
        private String fetchSwaggerJsonByIp(ServiceInstance instance) {
            LOGGER.info("service: {} metadata : {}", instance.getServiceId(), instance.getMetadata());
            try {
                // 获取数据
                return this.httpTransporter().transport(new StringStaticEndpointHttpRequest(instance,
                        StaticEndpoint.CHOERODON_API_DOCS));
            } catch (RestClientException e) {
                LOGGER.error("fetch error, ex={}", e.getMessage());
                throw new RemoteAccessException("fetch failed, instance: " + instance.getServiceId() + ", ex=" + e.getMessage());
            }
        }

        /**
         * 从 json 中解析出API权限
         *
         * @param serviceName 服务名称
         * @param json        swagger json
         * @return Permission
         */
        private List<Permission> parseJsonPermission(String serviceName, String json) {
            try {
                CompletionService<String> cs = new ExecutorCompletionService<>(this.parserExecutor(),
                        new LinkedBlockingQueue<>(2048));

                JsonNode node = this.objectMapper().readTree(json);
                Iterator<Map.Entry<String, JsonNode>> pathIterator = node.get("paths").fields();
                List<ChoerodonRouteData> routeDataList = this.resolveChoerodonRouteData(node);
                if (routeDataList.isEmpty()) {
                    LOGGER.warn("service route not found, should add a instance of ExtraDataManager");
                    throw new CommonException("hiam.warn.permission.serviceNotDefineExtraDataManager");
                }
                Map<String, String> urlPackagesMap = resolveUrlPackagesMap(node);
                List<Permission> permissions = new Vector<>(512);
                int count = 0;
                while (pathIterator.hasNext()) {
                    Map.Entry<String, JsonNode> pathNode = pathIterator.next();
                    Iterator<Map.Entry<String, JsonNode>> methodIterator = pathNode.getValue().fields();
                    count++;
                    // 使用线程池加速处理
                    cs.submit(() -> this.parserMethod(methodIterator, pathNode, serviceName, urlPackagesMap, permissions));
                }
                // 等待处理结束
                this.waitSuccess(count, cs);

                return permissions;
            } catch (IOException e) {
                throw new CommonException("hiam.error.parse_permission_data.failure");
            }
        }

        private Map<String, String> resolveUrlPackagesMap(JsonNode node) {
            JsonNode extraData = node.get("extraData");
            if (extraData == null) {
                return Collections.emptyMap();
            }
            JsonNode data = extraData.get("data");
            if (data == null) {
                return Collections.emptyMap();
            }
            JsonNode urlMap = data.get(ExtraData.URL_MODULAR_MAP_KEY);
            if (urlMap == null) {
                return Collections.emptyMap();
            }
            try {
                // 结果类型
                JavaType javaType = this.objectMapper().getTypeFactory()
                        .constructParametricType(Map.class, String.class, String.class);
                return this.objectMapper().readValue(urlMap.toString(), javaType);
            } catch (IOException e) {
                LOGGER.info("urlPackagesMap read failed.", e);
            }
            return Collections.emptyMap();
        }

        private List<ChoerodonRouteData> resolveChoerodonRouteData(JsonNode node) throws IOException {
            JsonNode extraData = node.get("extraData");
            if (extraData == null) {
                return Collections.emptyList();
            }
            JsonNode data = extraData.get("data");
            if (data == null) {
                return Collections.emptyList();
            }
            JsonNode routes = data.get(ExtraData.ZUUL_ROUTE_DATA);
            if (routes == null) {
                return Collections.emptyList();
            }
            List<ChoerodonRouteData> returnVal = new ArrayList<>();
            try {
                returnVal.add(this.objectMapper().readValue(routes.toString(), ChoerodonRouteData.class));
                return returnVal;
            } catch (IOException e) {
                LOGGER.info("resolve ChoerodonRouteData failed because of version upgrade.");
            }
            for (int i = 0; i < routes.size(); i++) {
                JsonNode jsonNode = routes.get(i);
                returnVal.add(this.objectMapper().readValue(jsonNode.toString(), ChoerodonRouteData.class));
            }
            return returnVal;
        }

        /**
         * 解析文档树某个路径的所有方法
         *
         * @param methodIterator 所有方法
         * @param pathNode       路径
         * @param serviceName    服务名
         */
        private String parserMethod(Iterator<Map.Entry<String, JsonNode>> methodIterator,
                                    Map.Entry<String, JsonNode> pathNode, String serviceName, Map<String, String> urlPackagesMap,
                                    List<Permission> permissions) {
            try {
                while (methodIterator.hasNext()) {
                    Map.Entry<String, JsonNode> methodNode = methodIterator.next();
                    JsonNode tags = methodNode.getValue().get("tags");
                    String resourceCode = this.processResourceCode(tags);
                    try {
                        JsonNode extraDataNode = methodNode.getValue().get("description");
                        if (resourceCode == null || extraDataNode == null) {
                            continue;
                        }
                        SwaggerExtraData extraData = this.objectMapper().readValue(extraDataNode.asText(), SwaggerExtraData.class);
                        String path = pathNode.getKey();
                        permissions.add(buildPermission(extraData, path, methodNode, serviceName, urlPackagesMap.get(path),
                                resourceCode));
                    } catch (IOException e) {
                        LOGGER.info("extraData read failed. path={}, ex={}", pathNode.getKey(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("parse method error, ex={}", e.getMessage());
            }
            return "success";
        }

        private void waitSuccess(int count, CompletionService<?> cs) {
            LOGGER.info("waiting for bath task, task={}, count={}", "parserMethod", count);
            for (int i = 0; i < count; i++) {
                try {
                    cs.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info("bath task success, task={}, count={}", "parserMethod", count);
        }

        protected Permission buildPermission(SwaggerExtraData extraData, String path,
                                             Map.Entry<String, JsonNode> methodNode, String serviceName, String rawServiceName,
                                             String resourceCode) {
            String method = methodNode.getKey();
            String description = methodNode.getValue().get("summary").asText();
            PermissionData permission = extraData.getPermission();
            String action = permission.getAction();
            if (rawServiceName == null) {
                // 默认匹配主服务的路由信息，为了兼容旧版ChoerodonRouteData
                rawServiceName = serviceName;
            }
            // 默认取权限code，没有则取方法名
            String code = rawServiceName + "."
                    + StringUtils.defaultIfBlank(permission.getCode(), resourceCode + "." + action);

            return new Permission().setCode(code).setPath(path).setMethod(method)
                    .setLevel(permission.getPermissionLevel()).setDescription(description).setAction(action)
                    .setResource(resourceCode).setPublicAccess(permission.isPermissionPublic())
                    .setLoginAccess(permission.isPermissionLogin()).setWithin(permission.isPermissionWithin())
                    .setSignAccess(permission.isPermissionSign()).setServiceName(serviceName)
                    .setTags(permission.getTags());
        }

        /**
         * 改造：适应空格等方式的Controller
         */
        private String processResourceCode(JsonNode tags) {
            String resourceCode = null;
            for (int i = 0; i < tags.size(); i++) {
                String tag = tags.get(i).asText();
                // 添加choerodon-eureka例外的以-endpoint结尾的tag，
                if (tag.endsWith("-controller")) {
                    resourceCode = tag.substring(0, tag.length() - "-controller".length());
                } else if (tag.endsWith("-endpoint")) {
                    resourceCode = tag.substring(0, tag.length() - "-endpoint".length());
                } else {
                    resourceCode = tag.replace(" ", "-").replace("(", "-").replace(")", "").replaceAll("-+", "-")
                            .toLowerCase();
                }
            }

            return resourceCode;
        }

        /**
         * 处理编码重复的API，拼接上 httpMethod 后缀
         *
         * @param permissions API
         */
        protected void processDuplicatedPermission(List<Permission> permissions) {
            Map<String, List<Permission>> map = permissions.stream().collect(Collectors.groupingBy(Permission::getCode));
            if (map == null) {
                return;
            }
            map.forEach((code, list) -> {
                if (list.size() > 1) {
                    for (Permission permission : list) {
                        permission.setCode(permission.getCode() + "." + permission.getMethod());
                        permission.setAction(permission.getAction() + "." + permission.getMethod());
                    }
                    LOGGER.warn("one code have multiple permission, would add http method as suffix: code=[{}], permissions={}",
                            code, list);
                }
            });
        }
    }
}
