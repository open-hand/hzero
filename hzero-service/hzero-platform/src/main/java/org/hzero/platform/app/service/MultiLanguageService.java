package org.hzero.platform.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.hzero.mybatis.domian.MultiLanguage;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 * 多语言接口
 * </p>
 *
 * @author qingsheng.chen 2018/9/16 星期日 16:49
 */
public interface MultiLanguageService {
    /**
     * 查询多语言信息
     *
     * @param token     token
     * @param fieldName 字段名称
     * @return 多语言信息
     * @throws JsonProcessingException      序列化失败
     * @throws UnsupportedEncodingException 不支持的编码格式
     */
    List<MultiLanguage> responseListMultiLanguage(String token, String fieldName) throws JsonProcessingException, UnsupportedEncodingException;
}
