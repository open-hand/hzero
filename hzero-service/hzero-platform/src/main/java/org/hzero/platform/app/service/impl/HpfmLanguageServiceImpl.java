package org.hzero.platform.app.service.impl;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.platform.app.service.HpfmLanguageService;
import org.hzero.platform.domain.entity.Language;
import org.hzero.platform.domain.repository.HpfmLanguageRepository;


/**
 * @author superlee
 */
@Service
public class HpfmLanguageServiceImpl implements HpfmLanguageService {

    private HpfmLanguageRepository languageRepository;

    public HpfmLanguageServiceImpl(HpfmLanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    @Override
    public List<Language> listLanguage() {
        return languageRepository.selectAll();
    }

    @Override
    public Page<Language> pageLanguage(String code, String name, PageRequest pageRequest) {
        return languageRepository.pageLanguage(code, name, pageRequest);
    }

    @Override
    public Language queryLanguage(String code) {
        return languageRepository.selectOne(new Language().setCode(code));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Language updateLanguage(Language language) {
        languageRepository.updateOptional(language, Language.FIELD_NAME, Language.FIELD_DESCRIPTION);
        return language;
    }
}
