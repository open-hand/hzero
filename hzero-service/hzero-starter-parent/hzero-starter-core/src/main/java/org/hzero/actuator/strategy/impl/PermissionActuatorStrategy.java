package org.hzero.actuator.strategy.impl;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.actuator.strategy.ActuatorStrategy;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.modular.ModularProperties;
import org.hzero.core.util.FieldNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.hzero.core.modular.initialize.MergeExtraDataInitialization.MODULAR_PACKAGES_MAP_KEY;

/**
 * 获取服务权限信息
 *
 * @author modified by bergturing on 2020/05/14
 * @author bojiangzhou 2020/03/27
 */
@Component
public class PermissionActuatorStrategy implements ActuatorStrategy, ApplicationContextAware, InitializingBean {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionActuatorStrategy.class);

    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    /**
     * actuator key
     */
    private static final String ACTUATOR_KEY = "permission";

    private static final String SUFFIX_CONTROLLER = "-controller";
    private static final String SUFFIX_ENDPOINT = "-endpoint";

    /**
     * 配置对象
     */
    private final ModularProperties modularProperties;
    /**
     * 应用名称
     */
    @Value("${spring.application.name}")
    private String applicationName;
    /**
     * 缓存的权限元数据:    key ---> value === className.methodName ---> PermissionData
     */
    private final Map<String, PermissionData> permissions = new HashMap<>(512);
    /**
     * 上下文对象
     */
    private ApplicationContext applicationContext;

    /**
     * 包路径与服务名的映射关系
     */
    private Map<String, String> packageServiceMap;
    /**
     * 是否开启权限码重复检测，默认开启
     * 配置全路径： hzero.actuator.permission.duplicatedCodeCheck
     * true     开启      如果发现权限码重复，会抛出异常，应用不能启动成功
     * false    不开启     如果发现权限码重复，会忽略重复的权限数据，并打印错误日志
     */
    @Value("${hzero.actuator.permission.duplicatedCodeCheck: true}")
    private Boolean duplicatedCodeCheck;

    /**
     * 用于标识是否已经初始化数据
     */
    private boolean isInitialized = false;

    @Autowired
    public PermissionActuatorStrategy(ModularProperties modularProperties) {
        this.modularProperties = modularProperties;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        // 初始化
        this.init();
    }

    @Override
    public String getType() {
        return ACTUATOR_KEY;
    }

    @Override
    public Object queryActuatorData(HttpServletRequest request) {
        return Collections.unmodifiableMap(this.permissions);
    }

    /**
     * 初始化数据的方法
     */
    private void init() {
        if (!this.isInitialized) {
            // 该类只有一个功能点，因此直接对class加锁
            // 如果有新增的功能点，该处需要针对使用资源进行加锁
            synchronized (this) {
                if (!this.isInitialized) {
                    // 初始化参数
                    this.initParameter();
                    // 查询和解析controller
                    this.findAndParseController();

                    this.isInitialized = true;
                }
            }
        }
    }

    /**
     * 初始化参数
     */
    @SuppressWarnings("unchecked")
    private void initParameter() {
        Map<String, String> packageServiceMap = null;
        if (this.modularProperties.isEnable()) {
            // 启用服务合并
            packageServiceMap = (Map<String, String>) ExtraDataManager.extraData.get(MODULAR_PACKAGES_MAP_KEY);
        }

        this.packageServiceMap = packageServiceMap;
    }

    /**
     * 查询和解析controller
     */
    private void findAndParseController() {
        String[] beanNames = this.applicationContext.getBeanNamesForType(Object.class);
        if (ArrayUtils.isNotEmpty(beanNames)) {
            Class<?> beanType = null;
            for (String beanName : beanNames) {
                if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                    try {
                        // 获取bean的类型
                        beanType = this.applicationContext.getType(beanName);
                        if (ClassUtils.isCglibProxyClass(beanType)) {
                            beanType = beanType.getSuperclass();
                        }
                    } catch (Throwable ex) {
                        // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                        }
                    }
                    if (Objects.nonNull(beanType) && this.isHandler(beanType)) {
                        // 解析controller
                        this.parseController(this.getServiceName(beanType), beanName, beanType);
                    }
                }
            }
        }
    }

    /**
     * 根据包路径获取服务名
     *
     * @return 服务名
     */
    private String getServiceName(Class<?> clazz) {
        if (MapUtils.isNotEmpty(this.packageServiceMap)) {
            for (Map.Entry<String, String> entry : this.packageServiceMap.entrySet()) {
                if (clazz.getName().startsWith(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        return this.applicationName;
    }

    /**
     * 判断是否为需要处理的对象
     *
     * @param beanType bean类型
     * @return 判断结果，true 是，false 不是
     */
    private boolean isHandler(Class<?> beanType) {
        return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
    }

    /**
     * 处理controller
     *
     * @param serviceName    服务名
     * @param controllerName controller的名称
     * @param clazz          controller的类
     */
    private void parseController(String serviceName, String controllerName, Class<?> clazz) {
        LOGGER.debug("Start Parse Service [{}] : Controller [{}] Permission", serviceName, controllerName);

        // 处理controller类的Api注解
        Api controllerApi = AnnotatedElementUtils.findMergedAnnotation(clazz, Api.class);
        String resourceCode = this.getResourceCode(controllerApi, clazz.getSimpleName());
        if (StringUtils.isBlank(resourceCode)) {
            // 没有resourceCode，就不进行解析
            LOGGER.warn("Controller's Resource Code Is Empty, Skip Parse Permission");
            return;
        }

        // 处理Controller类的 RequestMapping注解
        RequestMapping controllerMapping = AnnotatedElementUtils.findMergedAnnotation(clazz, RequestMapping.class);
        String[] controllerPaths = null;
        if (Objects.nonNull(controllerMapping)) {
            controllerPaths = controllerMapping.value();
        }
        if (ArrayUtils.isEmpty(controllerPaths)) {
            // 如果没有指定controller的请求路径，则默认为空
            controllerPaths = new String[]{""};
        }

        // 处理Controller内部的方法
        for (Method method : clazz.getMethods()) {
            this.parseMethod(serviceName, method, resourceCode, controllerPaths);
        }

        LOGGER.debug("Finished Parse Service [{}] : Controller [{}] Permission", serviceName, controllerName);
    }

    /**
     * 处理controller类内部的方法
     *
     * @param serviceName     服务名
     * @param method          方法对象
     * @param resourceCode    资源编码
     * @param controllerPaths controller上的路径
     */
    private void parseMethod(String serviceName, Method method, String resourceCode, String[] controllerPaths) {
        RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (Objects.nonNull(methodMapping)) {
            Permission permission = AnnotationUtils.findAnnotation(method, Permission.class);
            if (Objects.isNull(permission)) {
                LOGGER.warn("Method [{}.{}] Without @{} Annotation, Use Default Parameters To Define Permission Data",
                        method.getDeclaringClass().getCanonicalName(), method.getName(), Permission.class.getCanonicalName());
                // 没有permission注解的方法不进行权限的解析
                return;
            }

            String[] methodPaths = methodMapping.value();
            if (ArrayUtils.isEmpty(methodPaths)) {
                // 如果没有指定请求路径，则默认为空
                methodPaths = new String[]{""};
            }

            RequestMethod[] requestMethods = methodMapping.method();
            if (ArrayUtils.isEmpty(requestMethods)) {
                // 如果没有指定请求方法，则默认为全部方法
                requestMethods = RequestMethod.values();
            }

            ApiOperation operation = AnnotationUtils.findAnnotation(method, ApiOperation.class);
            String description = null;
            if (Objects.nonNull(operation)) {
                description = operation.value();
            }

            // 方法名
            String methodName = method.getName();
            // 请求方式
            String requestMethodString;
            // 处理数据的索引
            int index;
            for (RequestMethod requestMethod : requestMethods) {
                requestMethodString = requestMethod.name().toLowerCase();
                // 不同的请求方式下按照数量进行更新索引，主要用于区分相同请求方式的不同路径
                index = 0;
                for (String controllerPath : controllerPaths) {
                    for (String methodPath : methodPaths) {
                        index++;

                        // 构建PermissionData对象
                        PermissionData permissionData = this.builtPermissionData(resourceCode, controllerPaths.length,
                                methodPaths.length, requestMethods.length, description, permission,
                                methodName, requestMethodString, index, controllerPath, methodPath);

                        String globalPermissionCode = permissionData.getGlobalPermissionCode(serviceName);
                        if (this.permissions.containsKey(globalPermissionCode)) {
                            // 已经存在的权限数据对象
                            PermissionData existsPermissionData = this.permissions.get(globalPermissionCode);
                            // 错误信息
                            // 已存在key，代表key重复，需要抛出异常
                            // 比如相同controller内，定义的@Permission注解的code一样的情况下，会存在该问题
                            // 或不同的controller具有相同的tag，存在同名方法或@Permission注解的code一样的情况
                            String message = String.format("Parse Request Path [%s] Failure, Request Path [%s] Have The Same Permission Code: [%s.%s], " +
                                            "Method Is [%s.%s]", permissionData.getPath(), existsPermissionData.getPath(),
                                    permissionData.getResourceCode(), permissionData.getCode(),
                                    method.getDeclaringClass().getCanonicalName(), methodName);
                            if (this.duplicatedCodeCheck) {
                                throw new DuplicatedPermissionCodeException(message);
                            } else {
                                LOGGER.error(message);
                            }
                        } else {
                            // 缓存数据
                            this.permissions.put(globalPermissionCode, permissionData);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取ResourceCode
     *
     * @param apiAnnotation  Api注解对象
     * @param controllerName controller名称
     * @return resourceCode
     */
    private String getResourceCode(Api apiAnnotation, String controllerName) {
        String[] tags = null;
        if (Objects.nonNull(apiAnnotation)) {
            tags = apiAnnotation.tags();
        }
        boolean hasNotTags = ArrayUtils.isEmpty(tags) || (tags.length == 1 && StringUtils.isBlank(tags[0]));
        if (hasNotTags) {
            tags = new String[]{FieldNameUtils.camel2MiddleLine(controllerName, false)};
        }
        Assert.notNull(tags, "tags must not be null");
        String resourceCode = null;
        for (String tag : tags) {
            // 添加choerodon-eureka例外的以-endpoint结尾的tag，
            if (tag.endsWith(SUFFIX_CONTROLLER)) {
                resourceCode = tag.substring(0, tag.length() - SUFFIX_CONTROLLER.length());
            } else if (tag.endsWith(SUFFIX_ENDPOINT)) {
                resourceCode = tag.substring(0, tag.length() - SUFFIX_ENDPOINT.length());
            } else {
                resourceCode = tag.replace(" ", "-")
                        .replaceAll("\\(", "-")
                        .replaceAll("\\)", "")
                        .replaceAll("-+", "-")
                        .toLowerCase();
            }
        }

        return resourceCode;
    }

    /**
     * 构建PermissionData对象
     *
     * @param resourceCode         resourceCode
     * @param controllerPathLength controller路径数量
     * @param methodPathLength     method路径数量
     * @param requestMethodLength  请求方式数量
     * @param description          描述
     * @param permission           @Permission注解对象
     * @param methodName           方法名
     * @param requestMethodString  请求方式字符串
     * @param index                处理的索引
     * @param controllerPath       controller路径
     * @param methodPath           method路径
     * @return PermissionData对象
     */
    private PermissionData builtPermissionData(String resourceCode, int controllerPathLength,
                                               int methodPathLength, int requestMethodLength, String description,
                                               Permission permission, String methodName, String requestMethodString,
                                               int index, String controllerPath, String methodPath) {
        // 权限数据对象
        PermissionData permissionData = new PermissionData();

        permissionData.setResourceCode(resourceCode);
        permissionData.setAction(this.generatePermissionCode(methodName, methodName,
                requestMethodString, index, requestMethodLength, controllerPathLength, methodPathLength));
        permissionData.setDescription(description);
        permissionData.setMethod(requestMethodString);
        permissionData.setPath(this.concatPath(controllerPath, methodPath));

        if (Objects.isNull(permission)) {
            // 没有 Permission 注解，就设置默认值(！！！前面的逻辑是没有permission注解就不解析，这里的逻辑不会执行，但保留)
            permissionData.setCode(this.generatePermissionCode(null, methodName,
                    requestMethodString, index, requestMethodLength, controllerPathLength, methodPathLength));
            permissionData.setPermissionLevel(ResourceLevel.PROJECT.value());
            permissionData.setPermissionLogin(false);
            permissionData.setPermissionPublic(false);
            permissionData.setPermissionWithin(false);
            permissionData.setPermissionSign(false);
            permissionData.setTags(null);
            permissionData.setRoles(null);
        } else {
            // 权限码：如果 Permission 注解的code字段为空，就取当前方法名
            permissionData.setCode(this.generatePermissionCode(permission.code(), methodName,
                    requestMethodString, index, requestMethodLength, controllerPathLength, methodPathLength));
            permissionData.setPermissionLevel(permission.level().value());
            permissionData.setPermissionLogin(permission.permissionLogin());
            permissionData.setPermissionPublic(permission.permissionPublic());
            permissionData.setPermissionWithin(permission.permissionWithin());
            permissionData.setPermissionSign(permission.permissionSign());
            permissionData.setTags(permission.tags());
            permissionData.setRoles(permission.roles());
        }
        return permissionData;
    }

    /**
     * 连接两个路径
     *
     * @param controllerPath 控制器路径
     * @param methodPath     方法路径
     * @return 连接的结果路径
     */
    private String concatPath(String controllerPath, String methodPath) {
        String path = "";
        if (StringUtils.isNotBlank(controllerPath)) {
            path += controllerPath;
        }
        if (!path.startsWith(BaseConstants.Symbol.SLASH)) {
            path = BaseConstants.Symbol.SLASH + path;
        }

        if (StringUtils.isNotBlank(methodPath)) {
            if (path.endsWith(BaseConstants.Symbol.SLASH) && methodPath.startsWith(BaseConstants.Symbol.SLASH)) {
                path += methodPath.substring(1);
            } else if (path.endsWith(BaseConstants.Symbol.SLASH) || methodPath.startsWith(BaseConstants.Symbol.SLASH)) {
                path += methodPath;
            } else {
                path += BaseConstants.Symbol.SLASH + methodPath;
            }
        }

        return path;
    }

    /**
     * 生成权限编码
     * 1. 如果@Permission注解的code(permissionCode)为空，就取方法名(methodName)
     * 2. 如果请求方式是多个，则在权限码后追加请求方式
     * 3. 如果controller路径或者method路径大于1，则在权限码后面追加索引
     *
     * @param permissionCode      @Permission的code字段的值
     * @param methodName          方法名
     * @param requestMethodString 请求方式
     * @param index               索引
     * @param requestMethodLen    请求方法的长度
     * @param controllerPathLen   controller路径长度
     * @param methodPathLen       方法路径长度
     * @return 权限编码
     */
    private String generatePermissionCode(String permissionCode, String methodName, String requestMethodString,
                                          int index, int requestMethodLen, int controllerPathLen, int methodPathLen) {
        // 1. 如果@Permission注解的code(permissionCode)为空，就取方法名(methodName)
        permissionCode = StringUtils.defaultIfBlank(permissionCode, methodName);

        if (requestMethodLen > 1) {
            // 2. 如果请求方式是多个，则在权限码后追加请求方式
            permissionCode += BaseConstants.Symbol.MIDDLE_LINE + requestMethodString;
        }

        if (controllerPathLen > 1 || methodPathLen > 1) {
            // 3. 如果controller路径或者method路径大于1，则在权限码后面追加索引
            permissionCode += BaseConstants.Symbol.MIDDLE_LINE + index;
        }

        return permissionCode;
    }

    /**
     * <p>
     * 权限编码重复异常
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/05/14 11:38
     */
    private static class DuplicatedPermissionCodeException extends RuntimeException {
        public DuplicatedPermissionCodeException(String message) {
            super(message);
        }
    }
}
