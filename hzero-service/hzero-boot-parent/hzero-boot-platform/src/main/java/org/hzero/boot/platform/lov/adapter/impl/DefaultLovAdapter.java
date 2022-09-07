package org.hzero.boot.platform.lov.adapter.impl;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.autoconfigure.LovProperties;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;
import org.hzero.boot.platform.lov.feign.LovFeignClient;
import org.hzero.boot.platform.lov.handler.LovSqlHandler;
import org.hzero.common.HZeroCacheKey;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 值集值接口适配器默认实现类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月30日下午6:52:53
 */
public class DefaultLovAdapter implements LovAdapter {

    private final RedisHelper redisHelper;
    private final LovFeignClient lovFeignClient;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final IEncryptionService encryptionService;
    private final LovProperties lovProperties;

    private static final Logger logger = LoggerFactory.getLogger(DefaultLovAdapter.class);

    public DefaultLovAdapter(RedisHelper redisHelper,
                             LovFeignClient lovFeignClient,
                             ObjectMapper objectMapper,
                             RestTemplate restTemplate,
                             IEncryptionService encryptionService,
                             LovProperties lovProperties) {
        this.redisHelper = redisHelper;
        this.lovFeignClient = lovFeignClient;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.encryptionService = encryptionService;
        this.lovProperties = lovProperties;
    }

    /**
     * Hzero平台HTTP协议,默认http
     */
    @Value("${hzero.platform.httpProtocol:http}")
    private String hzeroPlatformHttpProtocol;

    @Override
    public LovDTO queryLovInfo(String lovCode, Long tenantId, String lang, Boolean publicQuery) {
        Assert.notNull(lovCode, BaseConstants.ErrorCode.DATA_INVALID);
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        // 查询租户级数据
        LovDTO result = queryLovInfoWithTenant(lovCode, tenantId, lang, publicQuery);
        // 租户级查询失败，查询平台级
        if (result == null && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            result = queryLovInfoWithTenant(lovCode, BaseConstants.DEFAULT_TENANT_ID, lang, publicQuery);
        }
        return result;
    }

    private LovDTO queryLovInfoWithTenant(String lovCode, Long tenantId, String lang, Boolean publicQuery) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        logger.debug("query lov define using lov code [{}] and tenant id [{}]", lovCode, tenantId);
        // 查询缓存
        logger.debug("try to bind redis db [{}]", HZeroService.Platform.REDIS_DB);
        this.redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String cacheKey = HZeroCacheKey.Lov.HEADER_KEY_PREFIX + lovCode;
        String hashKey = hashKey(tenantId, lang);
        logger.debug("query organization lov define from redis using key [{}]-[{}]", cacheKey, hashKey);
        String lovJson;
        try {
            lovJson = this.redisHelper.hshGet(cacheKey, hashKey);
        } finally {
            this.redisHelper.clearCurrentDatabase();
        }
        // 有fail fast
        if (AccessStatus.FORBIDDEN.name().equals(lovJson)) {
            logger.debug("the redis key [{}]-[{}] is in organization blacklist", cacheKey, hashKey);
            return null;
        }
        if (StringUtils.isBlank(lovJson)) {
            // redis cache missing, query data from remote service
            logger.debug("can not get lov define [{}] from redis cache, try to query from remote service...", lovCode);
            if (!publicQuery) {
                return this.lovFeignClient.queryLovInfo(lovCode, tenantId);
            } else {
                return this.lovFeignClient.queryLovInfo(lovCode, tenantId, lang);
            }
        } else {
            // redis cache found
            logger.debug("get lov define in redis cache: [{}]", lovJson);
            return this.redisHelper.fromJson(lovJson, LovDTO.class);
        }
    }

    @Override
    public LovViewDTO queryLovViewInfo(String lovViewCode, Long tenantId) {
        Assert.notNull(lovViewCode, BaseConstants.ErrorCode.DATA_INVALID);
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        String lang = this.getLang();
        // 查询租户级数据
        LovViewDTO result = queryLovViewInfoWithTenant(lovViewCode, tenantId, lang);
        // 租户级查询失败，查询平台级
        if (result == null && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            result = queryLovViewInfoWithTenant(lovViewCode, BaseConstants.DEFAULT_TENANT_ID, lang);
        }
        return result;
    }

    private LovViewDTO queryLovViewInfoWithTenant(String lovViewCode, Long tenantId, String lang) {
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        logger.debug("query lov view define using lov view code [{}] and tenant id [{}]", lovViewCode, tenantId);
        // 查询缓存
        logger.debug("try to bind redis db [{}]", HZeroService.Platform.REDIS_DB);
        this.redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        String cacheKey = HZeroCacheKey.Lov.VIEW_KEY_PREFIX + lovViewCode;
        String hashKey = hashKey(tenantId, lang);
        logger.debug("query organization lov view define from redis using key [{}]-[{}]", cacheKey, hashKey);
        String lovViewJson;
        try {
            lovViewJson = this.redisHelper.hshGet(cacheKey, hashKey);
        } finally {
            this.redisHelper.clearCurrentDatabase();
        }
        // 有fail fast
        if (AccessStatus.FORBIDDEN.name().equals(lovViewJson)) {
            logger.debug("the redis key [{}]-[{}] is in organization blacklist", cacheKey, hashKey);
            return null;
        }
        if (StringUtils.isBlank(lovViewJson)) {
            // redis cache missing, query data from remote service
            logger.debug("can not get lov define [{}] from redis cache, try to query from remote service...", lovViewCode);
            return this.lovFeignClient.queryLovViewInfo(lovViewCode, tenantId);
        } else {
            // redis cache found
            logger.debug("get lov define in redis cache: [{}]", lovViewJson);
            return this.redisHelper.fromJson(lovViewJson, LovViewDTO.class);
        }
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId) {
        return queryLovValue(lovCode, tenantId, this.getLang());
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String lang) {
        Assert.notNull(lovCode, BaseConstants.ErrorCode.DATA_INVALID);
        if (tenantId == null) {
            tenantId = BaseConstants.DEFAULT_TENANT_ID;
        }
        // 查询租户级数据
        List<LovValueDTO> result = queryLovValueWithTenant(lovCode, tenantId, lang);
        // 租户级查询失败，查询平台级
        if (CollectionUtils.isEmpty(result) && !BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            result = queryLovValueWithTenant(lovCode, BaseConstants.DEFAULT_TENANT_ID, lang);
        }
        return result;
    }

    private List<LovValueDTO> queryLovValueWithTenant(String lovCode, Long tenantId, String lang) {
        List<LovValueDTO> result;
        // data valid
        Assert.notNull(tenantId, BaseConstants.ErrorCode.DATA_INVALID);
        logger.debug("query lov values using lov code [{}] and tenant id [{}]", lovCode, tenantId);
        // select cache db
        logger.debug("try to bind redis db [{}]", HZeroService.Platform.REDIS_DB);
        this.redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        // check if this request can fail fast
        String cacheKey = HZeroCacheKey.Lov.VALUE_KEY_PREFIX + lovCode;
        String hashKey = hashKey(tenantId, lang);
        String lovValueJson;
        try {
            lovValueJson = this.redisHelper.hshGet(cacheKey, hashKey);
        } finally {
            this.redisHelper.clearCurrentDatabase();
        }
        if (AccessStatus.FORBIDDEN.name().equals(lovValueJson)) {
            logger.debug("the redis key [{}]-[{}] is in organization blacklist", cacheKey, hashKey);
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(lovValueJson)) {
            logger.debug("can not get lov values [{}] from global redis cache, try to query from remote service...", lovCode);
            // cache missed, try to get data from platform service
            result = this.lovFeignClient.queryLovValueWithLanguage(lovCode, tenantId, lang);
        } else {
            logger.debug("get lov define in redis cache: [{}]", lovValueJson);
            // find data in cache, convert json to DTO
            result = redisHelper.fromJsonList(lovValueJson, LovValueDTO.class);
        }
        return result;
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, List<String> params) {
        return queryLovValue(lovCode, tenantId, null, params, this.getLang());
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, List<String> params, String lang) {
        return queryLovValue(lovCode, tenantId, null, params, lang);
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, Map<String, Object> queryParams, List<String> params) {
        return queryLovValue(lovCode, tenantId, queryParams, params, this.getLang());
    }

    @Override
    public List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, Map<String, Object> queryParams, List<String> params, String lang) {
        LovDTO lovDTO = queryLovInfo(lovCode, tenantId);
        if (lovDTO == null) {
            return Collections.emptyList();
        }
        // 这里不用考虑租户覆盖，lovDTO已经做了覆盖查询，tenantId是lovDTO的租户Id
        // 默认是IDP
        if (lovDTO.getLovTypeCode() == null) {
            lovDTO.setLovTypeCode(LovConstants.LovTypes.IDP);
        }
        switch (lovDTO.getLovTypeCode()) {
            case LovConstants.LovTypes.IDP:
                return queryLovValue(lovCode, lovDTO.getTenantId(), lang);
            case LovConstants.LovTypes.SQL:
            case LovConstants.LovTypes.URL:
                return sqlList(lovDTO, queryParams, params);
            default:
                return Collections.emptyList();
        }
    }

    /**
     * 执行翻译sql获取结果
     *
     * @param lovDTO      值集代码
     * @param queryParams 普通查询参数
     * @param params      值字段参数
     * @return 查询结果
     */
    private List<LovValueDTO> sqlList(LovDTO lovDTO, Map<String, Object> queryParams, List<String> params) {
        if (queryParams == null) {
            queryParams = new HashMap<>(16);
        }
        // 是否本地执行翻译sql
        boolean local = lovProperties.getTranslateSql().isLocal();
        List<Map<String, Object>> result;
        if (local) {
            // 本地执行翻译sql查询结果
            result = ApplicationContextHelper.getContext().getBean(LovSqlHandler.class).queryTranslationData(lovDTO.getLovCode(), lovDTO.getTenantId(), queryParams, params);
        } else {
            // restTemplate调用查询结果
            result = remoteGetResult(lovDTO, queryParams, params);
        }
        if (CollectionUtils.isNotEmpty(result)) {
            List<LovValueDTO> value = new ArrayList<>();
            String k = lovDTO.getValueField();
            String v = lovDTO.getDisplayField();
            result.forEach(item -> {
                LovValueDTO lovValueDTO = new LovValueDTO();
                lovValueDTO.setMetadata(item);
                if (item.containsKey(k)) {
                    lovValueDTO.setValue(String.valueOf(item.get(k)));
                    if (item.containsKey(v)) {
                        lovValueDTO.setMeaning(String.valueOf(item.get(v)));
                    }
                }
                value.add(lovValueDTO);
            });
            return value;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 远程调用获取翻译sql执行结果
     *
     * @param lovDTO      值集代码
     * @param queryParams 普通查询参数
     * @param params      值字段参数
     * @return 查询结果
     */
    private List<Map<String, Object>> remoteGetResult(LovDTO lovDTO, Map<String, Object> queryParams, List<String> params) {
        StringBuilder url = new StringBuilder(this.hzeroPlatformHttpProtocol + "://" + getServerName(lovDTO.getRouteName()) +
                "/v1/" + lovDTO.getTenantId() + "/lovs/translation-sql/data?lovCode=" + lovDTO.getLovCode());
        if (CollectionUtils.isNotEmpty(params)) {
            // 解密参数
            try {
                params = this.decryptParams(lovDTO, params);
            } catch (Exception e) {
                logger.warn(">>>>>>params decrypt failed! use origin params for query lov value");
            }
            queryParams.put("params", StringUtils.join(params, BaseConstants.Symbol.COMMA));
        }
        // 拼接查询参数
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url.toString(), String.class);
        String body = responseEntity.getBody();
        List<Map<String, Object>> result = null;
        try {
            result = objectMapper.readValue(body, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (IOException e) {
            logger.error("get translation data error.");
        }
        return result;
    }

    /**
     * 解密翻译SQL查询条件参数
     *
     * @param lovDTO 值集
     * @param params 翻译SQL查询条件参数
     * @return 解密后的翻译SQL查询条件参数
     */
    private List<String> decryptParams(LovDTO lovDTO, List<String> params) {
        if (lovDTO.getEncryptField() != null && lovDTO.getValueField() != null && lovDTO.getDisplayField() != null) {
            String[] encryptFields = StringUtils.split(lovDTO.getEncryptField(), BaseConstants.Symbol.COMMA);
            for (String encryptField : encryptFields) {
                if (encryptField.equals(lovDTO.getValueField())) {
                    List<String> resultList = new ArrayList<>(params.size());
                    // 满足字段解密条件，开始处理解密，解密后直接返回即可，该循环仅执行一次
                    for (String param : params) {
                        String decrypt = encryptionService.decrypt(param, "", true);
                        resultList.add(decrypt);
                    }
                    return resultList;
                }
            }
        }
        // for循环执行完并没有返回内容，或者if条件不成立，此时返回原本查询参数即可
        return params;
    }

    /**
     * 获取服务全名
     *
     * @param serverCode 服务简码
     * @return 服务全称
     */
    private String getServerName(String serverCode) {
        this.redisHelper.setCurrentDatabase(HZeroService.Admin.REDIS_DB);
        String serverName = redisHelper.hshGet(HZeroService.Admin.CODE + ":routes", serverCode);
        this.redisHelper.clearCurrentDatabase();
        return serverName;
    }

    /**
     * 获取当前登录用户的语言
     *
     * @return 当前语言
     */
    private String getLang() {
        CustomUserDetails user = DetailsHelper.getUserDetails();
        String lang = BaseConstants.DEFAULT_LOCALE_STR;
        if (user != null && user.getLanguage() != null) {
            lang = user.getLanguage();
        }
        return lang;
    }

    @Override
    public String queryLovMeaning(String lovCode, Long tenantId, String value) {
        return queryLovMeaning(lovCode, tenantId, value, this.getLang());
    }

    @Override
    public String queryLovMeaning(String lovCode, Long tenantId, String value, String lang) {
        List<LovValueDTO> list = queryLovValue(lovCode, tenantId, lang);
        for (LovValueDTO dto : list) {
            if (Objects.equals(dto.getValue(), value)) {
                return dto.getMeaning();
            }
        }
        return value;
    }

    private String hashKey(Long tenantId, String lang) {
        if (StringUtils.isBlank(lang)) {
            return String.valueOf(tenantId);
        }
        return tenantId + "-" + lang;
    }

    /**
     * 值集可访问状态
     *
     * @author gaokuo.dai@hand-china.com 2019年3月1日上午12:09:24
     */
    public enum AccessStatus {
        /**
         * 可访问
         */
        ACCESS,
        /**
         * 禁止访问
         */
        FORBIDDEN,
        /**
         * 未找到
         */
        NOT_FOUND
    }
}
