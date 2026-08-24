package org.hzero.mybatis.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.domain.EntityColumn;
import io.choerodon.mybatis.domain.EntityTable;
import io.choerodon.mybatis.helper.EntityHelper;
import io.choerodon.mybatis.language.MultiLanguageInterceptor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 多语言配置
 * </p>
 *
 * @author qingsheng.chen 2018/9/13 星期四 16:51
 */
public class MultiLanguageHelper {
    private static final String LANG = "lang";
    private static final Logger logger = LoggerFactory.getLogger(MultiLanguageHelper.class);

    private MultiLanguageHelper() {
    }

    public static void close() {
        MultiLanguageInterceptor.multiLanguageEnable.set(false);
    }

    public static void open() {
        MultiLanguageInterceptor.multiLanguageEnable.set(true);
    }

    public static void clear() {
        MultiLanguageInterceptor.multiLanguageEnable.remove();
    }

    public static <T extends AuditDomain> List<T> assemblyTls(List<T> domainList, List<Map<String, Object>> tlsList) {
        if (CollectionUtils.isEmpty(domainList) || CollectionUtils.isEmpty(tlsList) || !EntityHelper.contain(domainList.get(0).getClass())) {
            return domainList;
        }
        EntityTable entityTable = EntityHelper.getTableByEntity(domainList.get(0).getClass());
        Set<EntityColumn> pkSet = entityTable.getEntityClassPkColumns();
        Set<EntityColumn> multiSet = entityTable.getMultiLanguageColumns();
        if (CollectionUtils.isEmpty(pkSet) || CollectionUtils.isEmpty(multiSet)) {
            return domainList;
        }
        Map<Map<String, Object>, Map<String, Map<String, String>>> pkFieldLangValueMap = new HashMap<>(domainList.size());
        tlsList.forEach(tls -> {
            String lang = String.valueOf(tls.get(LANG));
            if (!StringUtils.hasText(lang)) {
                return;
            }
            Map<String, Object> pkMap = new HashMap<>(pkSet.size());
            pkSet.forEach(entityColumn -> pkMap.put(entityColumn.getField().getName(), tls.get(entityColumn.getField().getName())));
            Map<String, Map<String, String>> fieldLangValue;
            if (pkFieldLangValueMap.containsKey(pkMap)) {
                fieldLangValue = pkFieldLangValueMap.get(pkMap);
            } else {
                fieldLangValue = new HashMap<>(16);
                pkFieldLangValueMap.put(pkMap, fieldLangValue);
            }
            multiSet.forEach(entityColumn -> {
                Map<String, String> fieldValue;
                if (fieldLangValue.containsKey(entityColumn.getField().getName())) {
                    fieldValue = fieldLangValue.get(entityColumn.getField().getName());
                } else {
                    fieldValue = new HashMap<>(16);
                    fieldLangValue.put(entityColumn.getField().getName(), fieldValue);
                }
                fieldValue.put(lang, String.valueOf(tls.get(entityColumn.getField().getName())));
            });
        });
        return domainList.stream().peek(item -> {
            Map<String, Object> pkMap = new HashMap<>(pkSet.size());
            pkSet.forEach(entityColumn -> {
                try {
                    pkMap.put(entityColumn.getField().getName(), FieldUtils.readField(item, entityColumn.getField().getName(), true));
                } catch (IllegalAccessException e) {
                    logger.error("Unable to read property [{}] in []", entityColumn.getField().getName(), item);
                }
            });
            if (pkFieldLangValueMap.containsKey(pkMap)) {
                item.set_tls(pkFieldLangValueMap.get(pkMap));
            }
        }).collect(Collectors.toList());
    }
}
