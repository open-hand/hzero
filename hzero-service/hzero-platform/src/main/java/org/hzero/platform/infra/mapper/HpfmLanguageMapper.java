package org.hzero.platform.infra.mapper;

import java.util.List;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.Language;

/**
 * <p>
 * 语言数据库操作映射
 * </p>
 *
 * @author qingsheng.chen 2018/8/29 星期三 10:27
 */
public interface HpfmLanguageMapper extends BaseMapper<Language> {

    /**
     * 查询 Language
     *
     * @param code 语言编码
     * @param name 语言名称
     * @return 返回语言列表
     */
    List<Language> selectLanguage(@Param("code") String code, @Param("name") String name);
}
