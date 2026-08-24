package org.hzero.boot.imported.infra.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.baidu.unbiz.fluentvalidator.Validator;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.IBatchImportService;
import org.hzero.boot.imported.app.service.ValidatorHandler;
import org.springframework.util.CollectionUtils;

/**
 * 校验注册类
 *
 * @author shuangfei.zhu@hand-china.com
 */
public class ImportRegistry {

    private ImportRegistry() {
    }

    private static final String NAME_PREFIX = "sheetName";

    /**
     * 自定义导入逻辑缓存map,启动时初始化(下标匹配)
     */
    private static final Map<List<Object>, Object> SERVICE_MAP = new ConcurrentHashMap<>();
    /**
     * 通用导入逻辑缓存map,启动时初始化
     */
    private static final Map<Object, Object> COMMON_SERVICE_MAP = new ConcurrentHashMap<>();
    /**
     * 通用批量校验链缓存map,启动时初始化
     */
    private static final Map<Integer, ValidatorHandler> COMMON_VALIDATOR_MAP = new ConcurrentHashMap<>();
    /**
     * 自定义校验链缓存map,启动时初始化
     */
    private static final Map<List<Object>, List<ValidatorHandler>> VALIDATOR_MAP = new ConcurrentHashMap<>();
    /**
     * 百度自定义校验链缓存map,启动时初始化
     */
    private static final Map<List<Object>, List<Validator>> BAIDU_VALIDATOR_MAP = new ConcurrentHashMap<>();

    public static Object getDefaultServiceMap(String templateCode, int sheetIndex, String sheetName) {
        // 根据sheet页名称匹配默认
        List<Object> key = Arrays.asList(templateCode, StringUtils.EMPTY, NAME_PREFIX + sheetName);
        if (SERVICE_MAP.containsKey(key)) {
            return SERVICE_MAP.get(key);
        }
        // 根据下标匹配默认
        key = Arrays.asList(templateCode, StringUtils.EMPTY, sheetIndex);
        if (SERVICE_MAP.containsKey(key)) {
            return SERVICE_MAP.get(key);
        }
        return null;
    }

    public static Object getServiceMap(String templateCode, String tenantNum, int sheetIndex, String sheetName) {
        // 先根据sheet页名称匹配
        List<Object> key = Arrays.asList(templateCode, tenantNum, NAME_PREFIX + sheetName);
        if (SERVICE_MAP.containsKey(key)) {
            return SERVICE_MAP.get(key);
        }
        // 根据下标匹配
        key = Arrays.asList(templateCode, tenantNum, sheetIndex);
        if (SERVICE_MAP.containsKey(key)) {
            return SERVICE_MAP.get(key);
        }
        // 获取平台默认service
        Object result = getDefaultServiceMap(templateCode, sheetIndex, sheetName);
        if (result == null) {
            return getCommonService(tenantNum);
        }
        return result;
    }

    public static void setServiceMap(String templateCode, String tenantNum, int sheetIndex, String sheetName, Object importService) {
        List<Object> key;
        if (StringUtils.isNotBlank(sheetName)) {
            // 拼个前缀，避免sheet名称是数字
            key = Arrays.asList(templateCode, tenantNum, NAME_PREFIX + sheetName);
        } else {
            key = Arrays.asList(templateCode, tenantNum, sheetIndex);
        }
        if (!(SERVICE_MAP.containsKey(key) && SERVICE_MAP.get(key) instanceof IBatchImportService)) {
            SERVICE_MAP.put(key, importService);
        }
    }

    public static Object getCommonService(String tenantNum) {
        if (COMMON_SERVICE_MAP.containsKey(tenantNum)) {
            return COMMON_SERVICE_MAP.get(tenantNum);
        }
        // 获取默认service
        if (COMMON_SERVICE_MAP.containsKey(StringUtils.EMPTY)) {
            return COMMON_SERVICE_MAP.get(StringUtils.EMPTY);
        }
        return null;
    }

    public static void setCommonServiceMap(String tenantNum, Object importService) {
        if (!(COMMON_SERVICE_MAP.containsKey(tenantNum) && COMMON_SERVICE_MAP.get(tenantNum) instanceof IBatchImportService)) {
            COMMON_SERVICE_MAP.put(tenantNum, importService);
        }
    }

    public static List<ValidatorHandler> getCommonValidatorList() {
        return COMMON_VALIDATOR_MAP.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public static void addCommonValidator(ValidatorHandler validator, int order) {
        COMMON_VALIDATOR_MAP.put(order, validator);
    }

    public static List<ValidatorHandler> getDefaultValidatorList(String templateCode, int sheetIndex, String sheetName) {
        // 根据sheet页名称匹配默认
        List<Object> key = Arrays.asList(templateCode, StringUtils.EMPTY, NAME_PREFIX + sheetName);
        if (VALIDATOR_MAP.containsKey(key)) {
            return VALIDATOR_MAP.get(key);
        }
        // 根据下标匹配默认
        key = Arrays.asList(templateCode, StringUtils.EMPTY, sheetIndex);
        if (VALIDATOR_MAP.containsKey(key)) {
            return VALIDATOR_MAP.get(key);
        }
        return Collections.emptyList();
    }

    public static List<ValidatorHandler> getValidatorList(String templateCode, String tenantNum, int sheetIndex, String sheetName) {
        // 先根据sheet页名称匹配
        List<Object> key = Arrays.asList(templateCode, tenantNum, NAME_PREFIX + sheetName);
        if (VALIDATOR_MAP.containsKey(key)) {
            return VALIDATOR_MAP.get(key);
        }
        // 根据下标匹配
        key = Arrays.asList(templateCode, tenantNum, sheetIndex);
        if (VALIDATOR_MAP.containsKey(key)) {
            return VALIDATOR_MAP.get(key);
        }
        return getDefaultValidatorList(templateCode, sheetIndex, sheetName);
    }

    public static List<Validator> getBaiDuValidatorList(String templateCode, String tenantNum, int sheetIndex, String sheetName) {
        // 先根据sheet页名称匹配
        List<Object> key = Arrays.asList(templateCode, tenantNum, NAME_PREFIX + sheetName);
        if (BAIDU_VALIDATOR_MAP.containsKey(key)) {
            return BAIDU_VALIDATOR_MAP.get(key);
        }
        // 根据下标匹配
        key = Arrays.asList(templateCode, tenantNum, sheetIndex);
        if (BAIDU_VALIDATOR_MAP.containsKey(key)) {
            return BAIDU_VALIDATOR_MAP.get(key);
        }
        // 根据sheet页名称匹配默认
        key = Arrays.asList(templateCode, StringUtils.EMPTY, NAME_PREFIX + sheetName);
        if (BAIDU_VALIDATOR_MAP.containsKey(key)) {
            return BAIDU_VALIDATOR_MAP.get(key);
        }
        // 根据下标匹配默认
        key = Arrays.asList(templateCode, StringUtils.EMPTY, sheetIndex);
        if (BAIDU_VALIDATOR_MAP.containsKey(key)) {
            return BAIDU_VALIDATOR_MAP.get(key);
        }
        return Collections.emptyList();
    }

    public static void addValidator(String templateCode, String tenantNum, int sheetIndex, String sheetName, ValidatorHandler validator) {
        List<Object> key;
        if (StringUtils.isNotBlank(sheetName)) {
            // 拼个前缀，避免sheet名称是数字
            key = Arrays.asList(templateCode, tenantNum, NAME_PREFIX + sheetName);
        } else {
            key = Arrays.asList(templateCode, tenantNum, sheetIndex);
        }
        List<ValidatorHandler> indexValidatorList = VALIDATOR_MAP.get(key);
        if (CollectionUtils.isEmpty(indexValidatorList)) {
            indexValidatorList = new ArrayList<>();
        }
        indexValidatorList.add(validator);
        VALIDATOR_MAP.put(key, indexValidatorList);
    }

    public static void addBaiDuValidator(String templateCode, String tenantNum, int sheetIndex, String sheetName, Validator validator) {
        List<Object> key;
        if (StringUtils.isNotBlank(sheetName)) {
            // 拼个前缀，避免sheet名称是数字
            key = Arrays.asList(templateCode, tenantNum, NAME_PREFIX + sheetName);
        } else {
            key = Arrays.asList(templateCode, tenantNum, sheetIndex);
        }
        List<Validator> indexValidatorList = BAIDU_VALIDATOR_MAP.get(key);
        if (CollectionUtils.isEmpty(indexValidatorList)) {
            indexValidatorList = new ArrayList<>();
        }
        indexValidatorList.add(validator);
        BAIDU_VALIDATOR_MAP.put(key, indexValidatorList);
    }
}
