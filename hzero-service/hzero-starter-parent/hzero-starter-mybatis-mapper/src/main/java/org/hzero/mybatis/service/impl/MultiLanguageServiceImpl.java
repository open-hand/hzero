package org.hzero.mybatis.service.impl;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.helper.LanguageHelper;

import org.hzero.core.util.FieldNameUtils;
import org.hzero.mybatis.domian.Language;
import org.hzero.mybatis.domian.MultiLanguage;
import org.hzero.mybatis.impl.DefaultDynamicSqlMapper;
import org.hzero.mybatis.service.MultiLanguageService;

/**
 * <p>
 * 多语言 Service
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 14:48
 */
@Service("mybatis.multiLanguageServiceImpl")
public class MultiLanguageServiceImpl implements MultiLanguageService {
    private static final Logger logger = LoggerFactory.getLogger(MultiLanguageServiceImpl.class);
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    public MultiLanguageServiceImpl(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public List<MultiLanguage> listMultiLanguage(String className, String fieldName, Map<String, Object> pkValue) {
        try {
            Class<?> clazz = Class.forName(className);
            EntityTable entityTable = EntityHelper.getTableByEntity(clazz);
            if (!entityTable.isMultiLanguage()) {
                throw new CommonException(entityTable.getName() + " hasn't multi-language!");
            }
            Set<String> multiLanguageColumn = entityTable.getMultiLanguageColumns().stream().map(item -> item.getColumn().toLowerCase()).collect(Collectors.toSet());
            final String columnName = getFieldName(entityTable, fieldName);
            if (!multiLanguageColumn.contains(columnName)) {
                throw new CommonException("Unknown column " + columnName + " in multi language table " + entityTable.getMultiLanguageTableName());
            }
            List<String> pkCondition = new ArrayList<>();
            pkValue.forEach((key, value) -> pkCondition.add(FieldNameUtils.camel2Underline(key, false) + " = " + (value instanceof Number ? value : "'" + value + "'")));
            String executeSql = "SELECT \n" + "LANG,\n" + columnName + " AS VALUE\n" + "FROM " + entityTable.getMultiLanguageTableName() + "\nWHERE " + StringUtils.collectionToDelimitedString(pkCondition, " AND ");
            logger.debug(">>> Multi Language Select : {}", executeSql);
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                return multiLanguage(new DefaultDynamicSqlMapper(sqlSession).selectList(executeSql)
                        .stream()
                        .collect(Collector.of(HashMap::new, (map, entity) -> map.put(String.valueOf(entity.get("LANG")), entity.containsKey("VALUE") ? String.valueOf(entity.get("VALUE")) : null), (k, v) -> v, Collector.Characteristics.IDENTITY_FINISH)));
            }
        } catch (ClassNotFoundException e) {
            throw new CommonException(e);
        }
    }

    private String getFieldName(EntityTable entityTable, String fieldName) {
        return entityTable.getEntityClassColumns()
                .stream()
                .filter(entityColumn -> Objects.equals(entityColumn.getField().getName(), fieldName))
                .findFirst()
                .map(EntityColumn::getColumn)
                .orElseThrow(() -> new CommonException("Unknown field " + fieldName + " in class " + entityTable.getEntityClass()));
    }

    public static List<MultiLanguage> emptyMultiLanguage() {
        List<MultiLanguage> multiLanguageList = new ArrayList<>();
        List<Language> languageList = LanguageHelper.languages();
        if (!CollectionUtils.isEmpty(languageList)) {
            for (Language language : languageList) {
                multiLanguageList.add(new MultiLanguage().setCode(language.getCode()).setName(language.getName()));
            }
        }
        return multiLanguageList;
    }

    public static List<MultiLanguage> multiLanguage(Map<String, String> multiLanguageMap) {
        logger.debug("Selected MultiLanguage : {}", multiLanguageMap);
        List<MultiLanguage> multiLanguageList = new ArrayList<>();
        List<Language> languageList = LanguageHelper.languages();
        if (!CollectionUtils.isEmpty(languageList)) {
            for (Language language : languageList) {
                multiLanguageList.add(new MultiLanguage()
                        .setCode(language.getCode())
                        .setName(language.getName())
                        .setValue(multiLanguageMap.get(language.getCode())));
            }
        }
        logger.debug("Selected MultiLanguage : {}", multiLanguageList);
        return multiLanguageList;
    }
}
