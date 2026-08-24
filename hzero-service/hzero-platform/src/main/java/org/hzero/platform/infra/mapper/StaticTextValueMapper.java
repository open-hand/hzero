package org.hzero.platform.infra.mapper;

import java.util.Date;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.StaticTextValue;
import org.hzero.platform.domain.vo.StaticTextValueVO;

/**
 * 平台静态信息Mapper
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:14:29
 */
public interface StaticTextValueMapper extends BaseMapper<StaticTextValue> {

    /**
     * 根据Code和语言查询
     *
     * @param textCode 编码
     * @param lang     语言
     */
    StaticTextValueVO selectTextByCode(@Param("tenantId") Long tenantId,
                                       @Param("companyId") Long companyId,
                                       @Param("textCode") String textCode,
                                       @Param("lang") String lang, @Param("now") Date now);

    /**
     * 查询文本内容
     *
     * @param textId 文本ID
     * @return StaticTextValue
     */
    StaticTextValue selectTextValueById(Long textId);
}
