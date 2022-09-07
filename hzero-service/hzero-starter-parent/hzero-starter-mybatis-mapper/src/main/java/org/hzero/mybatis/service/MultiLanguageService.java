package org.hzero.mybatis.service;

import java.util.List;
import java.util.Map;

import org.hzero.mybatis.domian.MultiLanguage;

/**
 * <p>
 * 多语言接口
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 14:46
 */
public interface MultiLanguageService {
    /**
     * 查询字段多语言
     *
     * @param className 类名
     * @param fieldName 字段名
     * @param pkValue   记录ID
     * @return 多语言
     */
    List<MultiLanguage> listMultiLanguage(String className, String fieldName, Map<String, Object> pkValue);
}
