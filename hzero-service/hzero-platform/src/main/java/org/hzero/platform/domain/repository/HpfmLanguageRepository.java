package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Language;

/**
 * <p>
 * 语言仓库
 * </p>
 *
 * @author qingsheng.chen 2018/8/29 星期三 10:24
 */
public interface HpfmLanguageRepository extends BaseRepository<Language> {

    /**
     * 分页查询 Language
     *
     * @param code        语言编码
     * @param name        语言名称
     * @param pageRequest 分页封装对象
     * @return 返回语言列表
     */
    Page<Language> pageLanguage(String code, String name, PageRequest pageRequest);
}
