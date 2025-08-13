package org.hzero.oauth.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.hzero.oauth.domain.entity.Language;
import org.hzero.oauth.domain.service.LanguageService;
import org.hzero.oauth.infra.mapper.LanguageMapper;

/**
 * @author qingsheng.chen@hand-china.com
 */
public class LanguageServiceImpl implements LanguageService {
    private static List<Language> languages;

    @Autowired
    private LanguageMapper languageMapper;

    @Override
    public List<Language> listLanguage() {
        if (languages == null) {
            languages = languageMapper.selectAll();
        }
        return languages;
    }
}
