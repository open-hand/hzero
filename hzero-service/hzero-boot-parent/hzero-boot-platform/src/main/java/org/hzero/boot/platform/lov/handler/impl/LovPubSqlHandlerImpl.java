package org.hzero.boot.platform.lov.handler.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.handler.LovMeaningSqlTransformer;
import org.hzero.boot.platform.lov.handler.LovPubSqlHandler;
import org.hzero.boot.platform.lov.handler.LovSqlGetter;
import org.hzero.boot.platform.lov.handler.SqlChecker;
import org.hzero.boot.platform.lov.util.SqlUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FieldNameUtils;
import org.hzero.mybatis.DynamicSqlMapper;
import org.hzero.mybatis.impl.DefaultDynamicSqlMapper;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;

/**
 * 默认SQL值集处理器
 *
 * @author gaokuo.dai@hand-china.com 2018年6月26日下午6:50:51
 */
@SuppressWarnings("all")
public class LovPubSqlHandlerImpl implements LovPubSqlHandler {

    private SqlSessionFactory sqlSessionFactory;
    private LovSqlGetter lovSqlGetter;
    private SqlChecker sqlFilter;
    private LovMeaningSqlTransformer lovMeaningSqlTransformer;
    private LovAdapter lovAdapter;
    private BeanFactory beanFactory;
    private ObjectMapper objectMapper;
    private IEncryptionService encryptionService;

    private static final Logger logger = LoggerFactory.getLogger(LovPubSqlHandlerImpl.class);

    private static final Pattern PATTERN = Pattern.compile(LovConstants.CONTENT_REGEX);
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");
    private static final Set<EncryptType> SHOULD = new HashSet<>(Arrays.asList(EncryptType.ENCRYPT, EncryptType.TO_STRING));
    private static final Set<String> AUTO_TO_STRING = new HashSet<>(Arrays.asList("tenantId", "organizationId"));
    private static final String[] EMPTY = new String[0];

    public LovPubSqlHandlerImpl(
            SqlSessionFactory sqlSessionFactory,
            LovSqlGetter lovSqlGetter,
            SqlChecker sqlFilter,
            LovMeaningSqlTransformer lovMeaningSqlTransformer,
            LovAdapter lovAdapter,
            BeanFactory beanFactory,
            ObjectMapper objectMapper,
            IEncryptionService encryptionService) {
        Assert.notNull(sqlSessionFactory, String.format(LovConstants.ErrorMessage.ERROR_NULL, "sqlSessionFactory"));
        Assert.notNull(lovSqlGetter, String.format(LovConstants.ErrorMessage.ERROR_NULL, "lovSqlGetter"));
        this.sqlSessionFactory = sqlSessionFactory;
        this.lovSqlGetter = lovSqlGetter;
        this.sqlFilter = sqlFilter;
        this.lovMeaningSqlTransformer = lovMeaningSqlTransformer;
        this.lovAdapter = lovAdapter;
        this.beanFactory = beanFactory;
        this.objectMapper = objectMapper;
        this.encryptionService = encryptionService;
    }

    @Override
    public List<Map<String, Object>> queryPubData(String lovCode, String lang, Long tenantId, Map<String, Object> queryParam, int page, int size) {
        // 解密参数
        queryParam = decryptParam(queryParam);
        // check lov code
        Assert.notNull(lovCode, String.format(LovConstants.ErrorMessage.ERROR_NULL, "lov code"));
        // check sql
        LovDTO lov = this.lovAdapter.queryLovInfo(lovCode, tenantId, lang, true);
        // 若传入的值集未开启公开标识则不允许访问，其它逻辑不变
        if (BaseConstants.Flag.NO.equals(lov.getPublicFlag())) {
            throw new CommonException(BaseConstants.ErrorCode.FORBIDDEN);
        }
        String customSql = this.lovSqlGetter.getCustomSql(lovCode, tenantId, lang, true);
        if (StringUtils.isBlank(customSql)) {
            throw new CommonException(LovConstants.ErrorMessage.ERROR_NO_SQL_GET, lovCode, tenantId);
        }
        if (this.sqlFilter != null) {
            this.sqlFilter.doFilter(customSql);
        }
        logger.debug("custom sql is :\n {}", customSql);
        //CustomUserDetails request = DetailsHelper.getUserDetails();
        if (queryParam == null) {
            queryParam = new HashMap<>(1);
        } else {
            for (Map.Entry<String, Object> entry : queryParam.entrySet()) {
                if (entry.getValue() instanceof String) {
                    queryParam.put(entry.getKey(), ((String) entry.getValue()).trim());
                }
            }
        }
        //queryParam.put("request", request);
        // do query
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            PageHelper.startPage(page, size);
            if (SqlUtils.isSqlId(customSql)) {
                String sqlId = customSql;
                String[] sqlIdArray = customSql.split("\\.");
                // 如果超过两端则认为是完整的SQL ID，直接执行，否则认为是非全限定名查找Bean（兼容HAP）
                if (sqlIdArray.length == 2) {
                    String beanName = StringUtils.uncapitalize(sqlIdArray[0]);
                    Object mapperObjectDelegate = beanFactory.getBean(beanName);
                    Class<?>[] interfaceClass = mapperObjectDelegate.getClass().getInterfaces();
                    for (Class c : interfaceClass) {
                        if (c.getSimpleName().equalsIgnoreCase(beanName)) {
                            sqlId = c.getPackage().getName() + "." + StringUtils.capitalize(customSql);
                            break;
                        }
                    }
                }
                return encryptResult(lov, sqlSession.selectList(sqlId, convertMapParamToDtoParam(sqlSession, sqlId, queryParam)));
            } else {
                DynamicSqlMapper sqlMapper = new DefaultDynamicSqlMapper(sqlSession);
                List<Map> preResults = sqlMapper.selectList("<script>\n" + customSql + "\n</script>", queryParam, Map.class);
                return encryptResult(lov, this.resultColumnToJavaPage(preResults));
            }
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            throw e;
        }
    }

    private Map<String, Object> decryptParam(Map<String, Object> paramMap) {
        if (EncryptContext.isEncrypt()) {
            Map<String, Object> decryptParam = null;
            if (MapUtils.isNotEmpty(paramMap)) {
                decryptParam = new HashMap<>(paramMap.size());
                for (Map.Entry<String, Object> paramEntry : paramMap.entrySet()) {
                    String k = paramEntry.getKey();
                    Object v = paramEntry.getValue();
                    if (encryptionService.isCipher(k)) {
                        k = encryptionService.decrypt(k, "");
                    }
                    if (v instanceof String && encryptionService.isCipher((String) v)) {
                        v = encryptionService.decrypt((String) v, "");
                    }
                    decryptParam.put(k, v);
                }
            }
            return decryptParam;
        }
        return paramMap;
    }

    private List<Map<String, Object>> encryptResult(LovDTO lov, List<Map<String, Object>> result) {
        // (没有配置加密字段 并且 不包含自动转字符串字段) 或者 结果集为空 或者 当前不需要加密或者转字符串
        if ((StringUtils.isBlank(lov.getEncryptField()) && !AUTO_TO_STRING.contains(lov.getValueField()))
                || CollectionUtils.isEmpty(result)
                || !SHOULD.contains(EncryptContext.encryptType())) {
            return result;
        }
        String[] encryptFields = StringUtils.isBlank(lov.getEncryptField()) ? EMPTY : lov.getEncryptField().split(",");
        result.forEach(item -> {
            if (MapUtils.isNotEmpty(item)) {
                for (String auto2String : AUTO_TO_STRING) {
                    item.computeIfPresent(auto2String, (k, oldValue) -> String.valueOf(oldValue));
                }
                for (String encryptField : encryptFields) {
                    if (encryptField == null) {
                        continue;
                    }
                    encryptField = encryptField.trim();
                    if (item.containsKey(encryptField)) {
                        Object o = item.get(encryptField);
                        if (o != null) {
                            if (EncryptType.ENCRYPT.equals(EncryptContext.encryptType())) {
                                item.put(encryptField, encryptionService.encrypt(String.valueOf(o), ""));
                            } else if (EncryptType.TO_STRING.equals(EncryptContext.encryptType())) {
                                item.put(encryptField, String.valueOf(o));
                            }
                        }
                    }
                }
            }
        });
        return result;
    }

    /**
     * 将 map 类型的参数 转换为 dto 类型.
     *
     * @param sqlSession SqlSession
     * @param sqlId      mapperId
     * @param map        传参Map
     * @return dto对象
     */
    private Object convertMapParamToDtoParam(SqlSession sqlSession, String sqlId, Map<String, Object> map) {
        if (map == null) {
            logger.warn("lov query parameter is null.");
            return map;
        }
        MappedStatement statement = sqlSession.getConfiguration().getMappedStatement(sqlId);
        if (statement == null) {
            logger.warn("no statement found for sqlId:{}", sqlId);
            return map;
        }
        Class dtoClass;
        Class parameterType = statement.getParameterMap().getType();
        if ("class java.lang.Object".equals(parameterType.toString())) {
            List<ResultMap> resultMaps = statement.getResultMaps();
            if (resultMaps == null || resultMaps.isEmpty()) {
                logger.warn("statement has no specified ResultMap, sqlId:{}", sqlId);
                return map;
            }
            ResultMap resultMap = resultMaps.get(0);
            dtoClass = resultMap.getType();
        } else {
            dtoClass = parameterType;
        }
        try {
            if (Map.class.isAssignableFrom(dtoClass)) {
                return map;
            }
            return objectMapper.readValue(objectMapper.writeValueAsBytes((filterMap(map))), dtoClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Map<String, Object> filterMap(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        Map<String, Object> noEmptyMap = new HashMap<>(map.size());
        map.forEach((k, v) -> {
            if (v == null || "".equals(v)) {
                return;
            }
            noEmptyMap.put(k, v);
        });
        return noEmptyMap;
    }

    @Override
    public List<Map<String, Object>> queryPubTranslationData(String lovCode, String lang, Long tenantId, List<String> params) {
        // check lov code
        Assert.notNull(lovCode, String.format(LovConstants.ErrorMessage.ERROR_NULL, "lov code"));
        // check sql
        String translationSql = this.lovSqlGetter.getTranslationSql(lovCode, tenantId, lang, true);
        if (StringUtils.isBlank(translationSql)) {
            throw new CommonException(LovConstants.ErrorMessage.ERROR_NO_SQL_GET, lovCode, tenantId);
        }
        if (this.sqlFilter != null) {
            this.sqlFilter.doFilter(translationSql);
        }
        logger.debug("translation sql is :\n {}", translationSql);
        StringBuilder sb = new StringBuilder();
        if (isNumber(params)) {
            params.forEach(item -> sb.append(item).append(BaseConstants.Symbol.COMMA));
        } else {
            params.forEach(item -> sb.append("'").append(item).append("'").append(BaseConstants.Symbol.COMMA));
        }
        // do query
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            DynamicSqlMapper sqlMapper = new DefaultDynamicSqlMapper(sqlSession);
            Map<String, Object> param = new HashMap<>(16);
            Matcher matcher = PATTERN.matcher(translationSql);
            // 匹配${}声明的变量，且只匹配第一个
            String arg = "${param1}";
            if (matcher.find()) {
                arg = matcher.group();
            }
            param.put(arg.substring(2, arg.length() - 1), sb.substring(0, sb.length() - 1));
            // public API无用户信息，此处判断可去除
            //if (DetailsHelper.getUserDetails() != null) {
            //    param.put("request", DetailsHelper.getUserDetails());
            //}
            List<Map> preResults = sqlMapper.selectList("<script>\n" + translationSql + "\n</script>", param, Map.class);
            return this.resultColumnToJavaList(preResults);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            throw e;
        }
    }

    private boolean isNumber(List<String> list) {
        for (String item : list) {
            boolean flag = NUMBER_PATTERN.matcher(item).matches();
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Map<String, Object>> getPubLovSqlMeaning(String lovCode, String lang, Long tenantId, Map<String, Object> queryParams) {
        queryParams = decryptParam(queryParams);
        // check lov code
        Assert.notNull(lovCode, String.format(LovConstants.ErrorMessage.ERROR_NULL, "lov code"));
        // check sql
        String customSql = this.lovSqlGetter.getCustomSql(lovCode, tenantId, lang, true);
        if (StringUtils.isBlank(customSql)) {
            throw new CommonException(LovConstants.ErrorMessage.ERROR_NO_SQL_GET, lovCode, tenantId);
        }
        // check lov define
        LovDTO lov = this.lovAdapter.queryLovInfo(lovCode, tenantId, lang, true);
        if (lov == null || lov.getLovTypeCode() == null) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        if (this.sqlFilter != null) {
            this.sqlFilter.doFilter(customSql);
        }
        // init query param
        //CustomUserDetails request = DetailsHelper.getUserDetails();
        //queryParams.put("request", request);
        customSql = this.lovMeaningSqlTransformer.doTransform(customSql, lov);
        if (logger.isDebugEnabled()) {
            logger.debug("custom sql is :\n {}", customSql);
        }
        // do query
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            DynamicSqlMapper sqlMapper = new DefaultDynamicSqlMapper(sqlSession);
            List<Map> resultsList = sqlMapper.selectList("<script>\n" + customSql + "\n</script>", queryParams, Map.class);
            return this.resultColumnToJavaList(resultsList);
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            throw e;
        }
    }

    /**
     * 列名转换成Java属性名
     *
     * @param columnName 列名
     * @return Java属性名
     */
    private String columnToJava(String columnName) {
        return FieldNameUtils.underline2Camel(columnName, true);
    }

    /**
     * 查询结果列名转化为小驼峰Page对象
     *
     * @param resultSet 查询结果
     * @return 转化为小驼峰后的查询结果
     */
    private Page resultColumnToJavaPage(List<Map> resultSet) {
        Page results = new Page();
        Page preResults = (Page) resultSet;

        results.setTotalPages(preResults.getTotalPages());
        results.setTotalElements(preResults.getTotalElements());
        results.setNumberOfElements(preResults.getNumberOfElements());
        results.setNumber(preResults.getNumber());
        results.setSize(preResults.getSize());
        results.setContent(this.resultColumnToJavaList(preResults.getContent()));

        return results;
    }

    /**
     * 查询结果列名转化为小驼峰List对象
     *
     * @param resultSet 查询结果
     * @return 转化为小驼峰后的查询结果
     */
    private List<Map<String, Object>> resultColumnToJavaList(List<Map> resultSet) {
        List<Map<String, Object>> results = new ArrayList<>(resultSet.size());

        for (Map m0 : resultSet) {
            if (m0 == null) {
                results.add(MapUtils.EMPTY_SORTED_MAP);
            } else {
                Map map = new HashMap(16);
                m0.forEach((k, v) -> map.put(this.columnToJava((String) k), v));
                results.add(map);
            }
        }
        return results;
    }

}
