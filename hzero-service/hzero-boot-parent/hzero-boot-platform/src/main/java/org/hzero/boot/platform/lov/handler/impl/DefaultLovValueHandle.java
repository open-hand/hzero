/**
 *
 */
package org.hzero.boot.platform.lov.handler.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.handler.LovValueHandle;
import org.hzero.common.HZeroConstant;
import org.hzero.common.HZeroService;
import org.hzero.core.algorithm.structure.TTLMap;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 值集值处理默认实现类
 *
 * @author gaokuo.dai@hand-china.com 2018年7月3日下午4:42:31
 */
public class DefaultLovValueHandle implements LovValueHandle {

    /**
     * 本地线程缓存超时时间--1分钟
     */
    private static final long LOCAL_CACHE_TTL = 1L;
    private static final String DEFAULT_SOURCE_FIELD_SUFFIX = "Code$";
    private static final String DEFAULT_TARGET_FIELD_SUFFIX = "Meaning";
    private static final char SPLITOR = '.';

    private LovAdapter lovAdapter;
    private RestTemplate restTemplate;
    private String httpProtocol;
    private ObjectMapper objectMapper;

    private final ThreadLocal<Map<String, Map<String, String>>> valueMaps = new ThreadLocal<>();
    private final ThreadLocal<Map<String, LovDTO>> lovMaps = new ThreadLocal<>();
    private Logger logger = LoggerFactory.getLogger(DefaultLovValueHandle.class);
    private static final Map<Class<?>, Field[]> CLASS_FIELD_CACHE = new ConcurrentHashMap<>();
    private static final Map<Field, Object> FIELD_LOV_VALUE_CACHE = new ConcurrentHashMap<>();
    private static final Object LOV_VALUE_NOT_FOUND = new Object();

    /**
     * 构造函数
     *
     * @param lovAdapter LovAdapter
     * @param restTemplate RestTemplate
     * @param httpProtocol 服务间通信使用的协议,如"http"/"https"
     */
    public DefaultLovValueHandle(LovAdapter lovAdapter, RestTemplate restTemplate, String httpProtocol) {
        this.lovAdapter = lovAdapter;
        this.restTemplate = restTemplate;
        this.httpProtocol = httpProtocol;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        this.objectMapper.setVisibility(VisibilityChecker.Std.defaultInstance().withFieldVisibility(Visibility.ANY));
    }

    @Override
    public Object process(String[] targetFields, Object result) {
        try {
            if (result == null) {
                this.logger.debug("result is null, skip translate");
                return null;
            }
            this.logger.debug("lov translate begin");
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("target fields is [{}]", ArrayUtils.isEmpty(targetFields) ? "[]" : Arrays.toString(targetFields));
            }
            // 有其他解析需求的话可以在这里扩展
            if (result instanceof Collection) {
                // 如果传入对象为集合,则直接处理其中的Elements
                this.processCollection(targetFields, (Collection<?>) result);
            } else {
                // 未命中任何解析方式,进行默认解析
                this.processDefault(targetFields, result);
            }
            this.logger.debug("lov translate end");
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 按集合来处理传入对象
     *
     * @param targetFields 待翻译的目标字段名
     * @param collection 传入对象
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    private void processCollection(String[] targetFields, Collection<?> collection) throws IllegalAccessException, NoSuchFieldException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("process collection, target field is [{}]", Arrays.toString(targetFields));
        }
        if (CollectionUtils.isEmpty(collection)) {
            this.logger.debug("target collection is empty, skip...");
            return;
        }
        Optional<?> first = collection.stream().filter(Objects::nonNull).findFirst();
        if (!first.isPresent()) {
            this.logger.debug("target collection is empty, skip...");
            return;
        }
        Object demo = first.get();
        this.prepareValues(demo.getClass(), collection);
        for (Object object : collection) {
            if (object == null) {
                this.logger.debug("there is a null element in the target collection, skip null element...");
                continue;
            }
            this.processDefault(targetFields, object);
        }
    }

    /**
     * 默认处理传入对象
     *
     * @param targetFields 待翻译的目标字段名
     * @param result 传入对象
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    private void processDefault(String[] targetFields, Object result) throws IllegalAccessException, NoSuchFieldException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("process by default method, target field is [{}]", Arrays.toString(targetFields));
        }
        if (targetFields == null || targetFields.length == 0) {
            this.logger.debug("target fields is empty and will process target Object itself");
            // 如果没有传入指定字段,则默认扫描传入对象本身
            this.prepareValues(result.getClass(), result);
            this.processOne(result);
        } else {
            // 否则遍历指定字段
            for (String targetField : targetFields) {
                if (StringUtils.isEmpty(targetField)) {
                    this.logger.debug("target fields is empty and will process target Object itself");
                    // 如果自定字段中包含空字符串(""或null),则扫描传入对象本身
                    this.prepareValues(result.getClass(), result);
                    this.processOne(result);
                } else {
                    // 否则扫描指定字段所对应的对象
                    // 按.分隔,分段解析
                    int indexOfSplitor = targetField.indexOf(SPLITOR);
                    Class<?> clazz = result.getClass();
                    Field field;
                    Object value;
                    if (indexOfSplitor > 0) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("recursive process result.{}", targetField.substring(0, indexOfSplitor));
                        }
                        field = this.getField(clazz, targetField.substring(0, indexOfSplitor));
                        field.setAccessible(true);
                        value = field.get(result);
                        this.process(new String[]{targetField.substring(indexOfSplitor + 1)}, value);
                    } else {
                        this.logger.debug("recursive process result.{}", targetField);
                        field = this.getField(clazz, targetField);
                        field.setAccessible(true);
                        value = field.get(result);
                        this.process(null, value);
                    }

                }
            }
        }
    }

    /**
     * 直接处理当前对象
     * @param obj 当前对象
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void processOne(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return;
        }
        // 声明各种变量
        Class<?> clazz = obj.getClass();
        Field[] fields = getAllFields(clazz);
        Map<String, LovDTO> localLovMap = this.getLocalLovMaps();
        Map<String, Map<String, String>> localLovValueMap = this.getLocalValueMaps();
        Map<String, String> innerLovValueMap;
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Long tenantId = null;
        if (userDetails != null) {
            tenantId = userDetails.getTenantId();
        }
        String meaning;
        Field meaningField;
        Object value;
        String lovCode;
        JsonNode jsonNode;
        // 循环处理对象中的所有字段
        for (Field field : fields) {
            // 找到class中所有被@LovValue注解的字段
            LovValue lovValueAnnotation = getLovValueAnnotation(field);
            if (lovValueAnnotation == null) {
                continue;
            }
            // 准备数据
            lovCode = lovValueAnnotation.lovCode();
            Assert.isTrue(StringUtils.isNotEmpty(lovCode), String.format(LovConstants.ErrorMessage.ERROR_NULL, "lov code"));
            field.setAccessible(true);
            value = field.get(obj);
            if (value == null) {
                // 待翻译对象为null,跳过
                this.logger.debug("field value [{}] is null, skip...", field.getName());
                continue;
            }
            LovDTO lov = localLovMap.get(lovCode);
            if (lov == null || lov.getLovTypeCode() == null) {
                this.logger.debug("invalid lov define [{}]", lovCode);
                // 无效的值集头
                meaning = null;
            } else {
                innerLovValueMap = localLovValueMap.get(lovCode);
                switch (lov.getLovTypeCode()) {
                    // 独立值集
                    case LovConstants.LovTypes.IDP:
                        this.logger.debug("translating IDP lov values [{}]", lovCode);
                        // 获取meaning
                        if (MapUtils.isEmpty(innerLovValueMap)) {
                            this.logger.debug("lov values [{}] local cache is missing while translate, skip...", lovCode);
                            meaning = null;
                        } else {
                            // 如果本地缓存中有该值集的有效缓存,则直接进行映射
                            meaning = getMeaning(innerLovValueMap, value);
                        }
                        break;
                    // 自定义SQL
                    case LovConstants.LovTypes.SQL:
                        this.logger.debug("translating SQL lov values [{}]", lovCode);
                        // 重置meaning
                        if (MapUtils.isEmpty(innerLovValueMap)) {
                            this.logger.debug("lov values [{}] local cache is missing while translate, skip...", lovCode);
                            meaning = null;
                        } else {
                            // 如果本地缓存中有该值集的有效缓存,则直接进行映射
                            meaning = getMeaning(innerLovValueMap, value);
                        }
                        break;

                    // URL
                    case LovConstants.LovTypes.URL:
                        this.logger.debug("translating URL lov values [{}]", lovCode);
                        // 重置meaning
                        meaning = null;
                        // 优先检查ThreadLocal中是否缓存有值
                        if (innerLovValueMap != null && innerLovValueMap.size() > 0) {
                            this.logger.debug("try to use local cache to translate lov values [{}]", lovCode);
                            meaning = getMeaning(innerLovValueMap, value);
                        }
                        if (meaning == null) {
                            this.logger.debug("can not get lov value [{}] from local cache, try to query from remote service", lovCode);
                            // 如果本地缓存未命中,则发起远程调用查询
                            // 处理参数
                            Map<String, String> params = new HashMap<>();
                            params.put(LovConstants.Field.LOV_CODE, lovCode);
                            if (tenantId != null) {
                                params.put(LovConstants.Field.TENANT_ID, String.valueOf(tenantId));
                            }
                            if (StringUtils.isNotEmpty(lov.getValueField())) {
                                params.put(lov.getValueField(), String.valueOf(value));
                            }
                            params.put(BaseConstants.PAGE_FIELD_NAME, BaseConstants.PAGE);
                            params.put(BaseConstants.SIZE_FIELD_NAME, "1000000");
                            this.logger.debug("query UR lov value [{}] with params [{}]", lovCode, params);
                            try {
                                // 利用RestTemplate动态调用
                                Map<?, ?> map;
                                ResponseEntity<String> jsonEntity = this.restTemplate.getForEntity(this.httpProtocol + "://" + HZeroService.getRealName(HZeroService.Gateway.NAME) + this.preProcessUrlParam(this.convertTrueUrl(lov), params), String.class, params);
                                jsonNode = ResponseUtils.getResponse(jsonEntity, JsonNode.class);
                                this.logger.debug("get result from remote service [{}]", jsonEntity.getBody());
                                if (jsonNode.isArray()) {
                                    this.logger.debug("get Array result from remote service");
                                    @SuppressWarnings("rawtypes")
                                    List<Map> tempList = ResponseUtils.getResponse(jsonEntity, new TypeReference<List<Map>>() {
                                    });
                                    if (CollectionUtils.isNotEmpty(tempList)) {
                                        // 刷新本地缓存
                                        if (innerLovValueMap == null) {
                                            innerLovValueMap = new HashMap<>();
                                            localLovValueMap.put(lovCode, innerLovValueMap);
                                        }
                                        for (int i = 0; i < tempList.size(); i++) {
                                            map = tempList.get(i);
                                            innerLovValueMap.put(String.valueOf(map.get(lov.getValueField())), String.valueOf(map.get(lov.getDisplayField())));
                                        }
                                        meaning = getMeaning(innerLovValueMap, value);
                                    }
                                } else {
                                    map = ResponseUtils.getResponse(jsonEntity, new TypeReference<Map<?, ?>>() {
                                    });
                                    // 判断返回值是不是Page对象
                                    if (this.isPageObject(map)) {
                                        this.logger.debug("get Page result from remote service");
                                        if (innerLovValueMap == null) {
                                            innerLovValueMap = new HashMap<>();
                                            localLovValueMap.put(lovCode, innerLovValueMap);
                                        }
                                        @SuppressWarnings({"unchecked", "rawtypes"})
                                        List<Map> tempList = (List<Map>) map.get(LovConstants.Field.CONTENT);
                                        for (int i = 0; i < tempList.size(); i++) {
                                            map = tempList.get(i);
                                            innerLovValueMap.put(String.valueOf(map.get(lov.getValueField())), String.valueOf(map.get(lov.getDisplayField())));
                                        }
                                        meaning = getMeaning(innerLovValueMap, value);

                                    } else {
                                        if (map == null) {
                                            this.logger.warn("can not get any result from remote service with lov code [{}]", lovCode);
                                            continue;
                                        }
                                        this.logger.debug("get Object result from remote service");
                                        meaning = String.valueOf(map.get(lov.getDisplayField()));
                                        if (StringUtils.isNotEmpty(meaning)) {
                                            // 刷新本地缓存
                                            if (innerLovValueMap == null) {
                                                innerLovValueMap = new HashMap<>();
                                                localLovValueMap.put(lovCode, innerLovValueMap);
                                            }
                                            innerLovValueMap.put(String.valueOf(value), meaning);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // 远程调用出现异常,进入fallback
                                if (this.logger.isWarnEnabled()) {
                                    this.logger.warn(e.getMessage(), e);
                                }
                                meaning = null;
                            }
                        }
                        break;

                    // ERROR-非法的值集类型
                    default:
                        throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
            }
            // 将meaning回写到对象中
            String meaningFieldName = lovValueAnnotation.meaningField();
            try {
                if (StringUtils.isEmpty(meaningFieldName)) {
                    this.logger.debug("no meaning field name is assigned in field [{}], use default meaning field name", field.getName());
                    // 如果没有指定meaning字段名,则使用默认映射
                    meaningField = this.getField(clazz, field.getName().replaceAll(DEFAULT_SOURCE_FIELD_SUFFIX, StringUtils.EMPTY) + DEFAULT_TARGET_FIELD_SUFFIX);
                } else {
                    this.logger.debug("using target field name [{}]", meaningFieldName);
                    // 如果指定了有效的meaning字段名,则将meaning写入该字段中
                    meaningField = this.getField(clazz, meaningFieldName);
                }
                meaningField.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException e) {
                // 如果指定了的meaning字段名无效,跳过执行
                if (this.logger.isWarnEnabled()) {
                    // 改成占位符操作
                    this.logger.warn(String.format(LovConstants.ErrorMessage.ERROR_INVALIDE_MEANING_FIELD, meaningFieldName, clazz == null ? "Unknown Class" : clazz.getSimpleName()));
                }
                continue;
            }
            // 没有找到有效的meaning时
            // -- 如果@LovValue指定了默认值,则使用该默认值
            // -- 否则使用value的原值
            if (meaning == null) {
                this.logger.warn("can not get any translate result by lov code [{}] and value [{}], do fallback process", lovCode, value);
                meaning = StringUtils.isEmpty(lovValueAnnotation.defaultMeaning()) ? String.valueOf(value) : lovValueAnnotation.defaultMeaning();
            }
            this.logger.debug("field lov value [{}] translate result is [{}]", lovCode, meaning);
            meaningField.set(obj, meaning);
        }

    }

    private LovValue getLovValueAnnotation(Field field) {
        Object value = FIELD_LOV_VALUE_CACHE.get(field);
        if (value instanceof LovValue) {
            return (LovValue) value;
        } else if (value == LOV_VALUE_NOT_FOUND) {
            return null;
        } else {
            LovValue annotation = AnnotationUtils.getAnnotation(field, LovValue.class);
            FIELD_LOV_VALUE_CACHE.putIfAbsent(field, annotation == null ? LOV_VALUE_NOT_FOUND : annotation);
            return annotation;
        }
    }

    private Field[] getAllFields(Class<?> clazz) {
        Field[] fields = CLASS_FIELD_CACHE.get(clazz);
        if (fields != null) {
            return fields;
        } else {
            fields = FieldUtils.getAllFields(clazz);
            CLASS_FIELD_CACHE.putIfAbsent(clazz, fields);
        }
        return fields;
    }

    /**
     *
     * 解析目标对象中的{@link LovValue}注解,将值集值缓存到ThreadLocal中
     *
     * @param clazz 目标对象的类
     * @param originData 目标对象
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    protected void prepareValues(Class<?> clazz, Object originData) throws NoSuchFieldException, IllegalAccessException {
        this.logger.debug("process lov values...");
        // 各种变量的声明
        Field[] fields = getAllFields(clazz);
        Map<String, LovDTO> localLovMap = this.getLocalLovMaps();
        Map<String, Map<String, String>> localValueMap = this.getLocalValueMaps();
        LovDTO lov;
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Long tenantId = null;
        Object exampleOriginData;
        if (userDetails != null) {
            tenantId = userDetails.getTenantId();
        }
        this.logger.debug("tenant id is: [{}]", tenantId);
        // 循环读取类中所有字段
        for (Field field : fields) {
            // 没有被@lovValue注解修饰的字段略过
            LovValue lovValue = getLovValueAnnotation(field);
            if (lovValue == null) {
                continue;
            }
            String lovCode = lovValue.lovCode();
            Assert.isTrue(StringUtils.isNotEmpty(lovCode), String.format(LovConstants.ErrorMessage.ERROR_NULL, "lov code"));
            this.logger.debug("target field is [{}] and target lov code is [{}]", field.getName(), lovCode);
            // 根据值集Code和租户ID获取值集
            lov = localLovMap.get(lovCode);
            if (lov == null) {
                this.logger.debug("local lov [{}] define cache missing, query lov adapter...", lovCode);
                lov = this.lovAdapter.queryLovInfo(lovCode, tenantId);
                if (lov == null) {
                    // 登记的值集信息无效则跳过
                    this.logger.warn("can not get lov define by code [{}] from local cache and lov adapter", lovCode);
                    continue;
                }
                this.logger.debug("get lov [{}] from lov adapter: [{}] and it will be cached in local thread", lovCode, lov);
                localLovMap.put(lovCode, lov);
            }
            if (tenantId == null) {
                tenantId = lov.getTenantId();
            }
            // 检查值集类型
            if (!(Objects.equals(lov.getLovTypeCode(), LovConstants.LovTypes.IDP) || Objects.equals(lov.getLovTypeCode(), LovConstants.LovTypes.SQL))) {
                // 非独立值集和SQL值集不处理后续,跳过
                this.logger.debug("lov [{}] is not IDP or SQL, skip prepare...", lovCode);
                continue;
            }
            // 校验值集类型
            // 线程缓存中已有该值集,跳过
            if (localValueMap.get(lovCode) != null) {
                this.logger.debug("get values [{}] in local cache: [{}]", lovCode, localValueMap.get(lovCode));
                continue;
            }
            this.logger.debug("can not get values [{}] from local cache,query lov adapter...", lovCode);
            if (Objects.equals(lov.getLovTypeCode(), LovConstants.LovTypes.IDP)) {
                this.logger.debug("lov [{}] is IDP...", lovCode);
                // 独立值集后续处理
                List<LovValueDTO> values = this.lovAdapter.queryLovValue(lovCode, tenantId);
                // 无效的值集,跳过
                if (CollectionUtils.isEmpty(values)) {
                    this.logger.warn("can not get lov values by code [{}] from local cache and lov adapter", lovCode);
                    continue;
                }
                Map<String, String> valueMeaningMap = new HashMap<>(values.size());
                values.forEach(value -> valueMeaningMap.put(value.getValue(), value.getMeaning()));
                this.logger.debug("get lov values [{}] from lov adapter: [{}] and it will be cached in local thread", lovCode, valueMeaningMap);
                localValueMap.put(lovCode, valueMeaningMap);
            } else if (Objects.equals(lov.getLovTypeCode(), LovConstants.LovTypes.SQL)) {
                this.logger.debug("lov [{}] is SQL...", lovCode);
                // SQL值集后续处理
                if (originData == null) {
                    this.logger.debug("can not process SQL values [{}] when input data is null", lovCode);
                    continue;
                }
                // 获取ID串
                List<Object> identities;
                if (originData instanceof Collection) {
                    this.logger.debug("input data is Collection...");
                    Collection<?> originDatas = (Collection<?>) originData;
                    if (originDatas.isEmpty()) {
                        this.logger.debug("can not process SQL values [{}] when input data collection is empty", lovCode);
                        continue;
                    }
                    identities = new ArrayList<>(originDatas.size());
                    exampleOriginData = originDatas.stream().filter(Objects::nonNull).findFirst().orElse(null);
                    if (exampleOriginData == null) {
                        this.logger.debug("can not process SQL values [{}] when all data is null in input collection", lovCode);
                        continue;
                    }
                    field.setAccessible(true);
                    for (Object item : originDatas) {
                        identities.add(field.get(item));
                    }
                } else {
                    this.logger.debug("input data is NOT Collection...");
                    identities = new ArrayList<>(1);
                    field.setAccessible(true);
                    identities.add(field.get(originData));
                }
                this.logger.debug("get identities [{}] for SQL lov [{}] process", identities, lovCode);
                // 如果本地缓存未命中,则发起远程调用查询
                // 处理参数
                Map<String, String> params = new HashMap<>();
                params.put(LovConstants.Field.LOV_CODE, lovCode);
                if (tenantId != null) {
                    params.put(LovConstants.Field.TENANT_ID, String.valueOf(tenantId));
                }
                params.put(LovConstants.Field.IDENTITIES, Optional.of(identities).map(list -> list.stream().map(String::valueOf).collect(Collectors.joining(","))).orElse(null));
                this.logger.debug("query SQL values [{}] info by params: [{}]", lovCode, params);
                try {
                    // 利用RestTemplate动态调用
                    ResponseEntity<String> resultJsonEntity = this.restTemplate.getForEntity(
                            this.httpProtocol + "://" + HZeroService.getRealName(HZeroService.Gateway.NAME) + this.preProcessUrlParam(this.convertTrueUrl(lov).replaceAll("/data", "/meaning"), params),
                            String.class,
                            params
                    );
                    @SuppressWarnings("rawtypes")
                    List<Map> resultList = ResponseUtils.getResponse(resultJsonEntity, new TypeReference<List<Map>>() {
                    });
                    this.logger.debug("query SQL values [{}] info and get result: [{}]", lovCode, resultJsonEntity);
                    if (CollectionUtils.isNotEmpty(resultList)) {
                        // 刷新本地缓存
                        Map<String, Map<String, String>> localLovValueMap = this.getLocalValueMaps();
                        Map<String, String> innerLovValueMap = new HashMap<>(resultList.size());
                        for (Map<?, ?> map : resultList) {
                            innerLovValueMap.put(String.valueOf(map.get(LovConstants.Field.IDENTITY)), String.valueOf(map.get(LovConstants.Field.MEANING)));
                        }
                        this.logger.debug("cache SQL values [{}] to local Thread...", lovCode);
                        localLovValueMap.put(lovCode, innerLovValueMap);
                    }
                } catch (Exception e) {
                    // 远程调用出现异常,进入fallback
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn(e.getMessage(), e);
                    }
                }
            }
        }

    }

    /**
     * 获得给定类及其所有父类中指定的字段,检索公有/私有/保护字段
     *
     * @param clazz 给定类
     * @return 指定的字段
     */
    protected Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field[] fields = getAllFields(clazz);
        for (Field field : fields) {
            if (Objects.equals(fieldName, field.getName())) {
                return field;
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    /**
     * 预处理url,将参数的占位符拼接好
     *
     * @param url 源url
     * @param params 参数
     * @return 处理好的url
     */
    protected String preProcessUrlParam(String url, Map<String, String> params) {
        StringBuilder stringBuilder = new StringBuilder(url);
        Set<String> keySet = params.keySet();
        if (CollectionUtils.isNotEmpty(keySet)) {
            boolean firstKey = true;
            // url上原本就带了参数
            if (url.contains("?")) {
                firstKey = false;
            }
            for (String key : keySet) {
                if (firstKey) {
                    stringBuilder.append('?').append(key).append("={").append(key).append('}');
                    firstKey = false;
                } else {
                    stringBuilder.append('&').append(key).append("={").append(key).append('}');
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 得到真正的数据请求Url地址[带路由信息]
     *
     * @return 真正的数据请求Url地址
     */
    protected String convertTrueUrl(LovDTO lov) {
        Assert.notNull(lov.getLovCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(lov.getLovTypeCode(), BaseConstants.ErrorCode.DATA_INVALID);
        if (StringUtils.isEmpty(lov.getRouteName())) {
            lov.setRouteName(HZeroService.Platform.CODE);
        }
        String queryUrl;
        switch (lov.getLovTypeCode()) {
            case LovConstants.LovTypes.IDP:
                queryUrl = HZeroService.Platform.CODE + HZeroConstant.Lov.ApiAddress.LOV_VALUE_SERVICE_ADDRESS;
                break;
            case LovConstants.LovTypes.URL:
                queryUrl = lov.getRouteName() + lov.getCustomUrl();
                break;
            case LovConstants.LovTypes.SQL:
                if (lov.getTenantId() == null || Objects.equals(lov.getTenantId(), BaseConstants.DEFAULT_TENANT_ID)) {
                    queryUrl = lov.getRouteName() + HZeroConstant.Lov.ApiAddress.SQL_LOV_SERVICE_ADDRESS;
                } else {
                    queryUrl = lov.getRouteName() + HZeroConstant.Lov.ApiAddress.SQL_ORG_LOV_SERVICE_ADDRESS;
                }
                break;
            default:
                queryUrl = null;
                break;
        }
        this.logger.debug("get true url [{}] from lov [/{}]", queryUrl, lov.getLovCode());
        return "/" + queryUrl;
    }

    /**
     * 将一个Json转换为Map对象之后,检查该Map对象是不是一个Page对象
     *
     * @param dataMap
     * @return 该Map对象是不是一个Page对象
     */
    protected boolean isPageObject(Map<?, ?> dataMap) {
        if (dataMap == null) {
            return false;
        }
        return (dataMap.get(LovConstants.Field.TOTAL_PAGES) != null && dataMap.get(BaseConstants.SIZE_FIELD_NAME) != null);
    }

    /**
     * 从ThreadLocal中加载本线程的Map数据,如果没有数据则创建初始化Map数据
     *
     * @return 本线程的数据
     */
    private Map<String, Map<String, String>> getLocalValueMaps() {
        Map<String, Map<String, String>> localValueMaps = this.valueMaps.get();
        if (localValueMaps == null) {
            localValueMaps = new TTLMap.Builder<String, Map<String, String>>().ttl(LOCAL_CACHE_TTL).build();
            this.valueMaps.set(localValueMaps);
        }
        return localValueMaps;
    }

    /**
     * 从ThreadLocal中加载本线程的Map数据,如果没有数据则创建初始化Map数据
     *
     * @return 本线程的数据
     */
    private Map<String, LovDTO> getLocalLovMaps() {
        Map<String, LovDTO> localLovMaps = this.lovMaps.get();
        if (localLovMaps == null) {
            localLovMaps = new TTLMap.Builder<String, LovDTO>().ttl(LOCAL_CACHE_TTL).build();
            this.lovMaps.set(localLovMaps);
        }
        return localLovMaps;
    }

    /**
     * 支持集合类映射
     * @param innerLovValueMap
     * @param value
     * @return
     */
    private String getMeaning(Map<String, String> innerLovValueMap, Object value) {
        if (value instanceof Collection) {
            StringBuilder builder = new StringBuilder("[");
            for (Object part : (Collection) value) {
                String partValue = String.valueOf(part);
                String partMeaning = innerLovValueMap.get(partValue);
                builder.append(",").append(partMeaning);
            }
            return builder.append("]").deleteCharAt(1).toString();
        }
        return innerLovValueMap.get(String.valueOf(value));
    }

}
