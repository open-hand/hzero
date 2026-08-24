package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.Language;
import org.hzero.platform.domain.repository.HpfmLanguageRepository;
import org.hzero.platform.infra.mapper.HpfmLanguageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 语言仓库实现类
 * </p>
 *
 * @author qingsheng.chen 2018/8/29 星期三 10:25
 */
@Component
public class HpfmLanguageRepositoryImpl extends BaseRepositoryImpl<Language> implements HpfmLanguageRepository {
    private HpfmLanguageMapper languageMapper;

    @Autowired
    public HpfmLanguageRepositoryImpl(HpfmLanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }

    @Override
    public Page<Language> pageLanguage(String code, String name, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> languageMapper.selectLanguage(code, name));
    }
}
