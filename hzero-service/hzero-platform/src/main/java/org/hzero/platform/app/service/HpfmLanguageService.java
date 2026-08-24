package org.hzero.platform.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.domain.entity.Language;

/**
 * @author superlee
 */
public interface HpfmLanguageService {

    /**
     * 查询所有语言
     *
     * @return 语言列表
     */
    List<Language> listLanguage();

    /**
     * 分页查询 Language
     *
     * @param code        语言编码
     * @param name        语言名称
     * @param pageRequest 分页封装对象
     * @return 返回语言列表
     */
    Page<Language> pageLanguage(String code, String name, PageRequest pageRequest);

    /**
     * 根据language code单个查询
     *
     * @param code Language
     * @return 返回语言
     */
    Language queryLanguage(String code);

    /**
     * 更新语言
     *
     * @param language 语言
     * @return 语言
     */
    Language updateLanguage(Language language);
}
