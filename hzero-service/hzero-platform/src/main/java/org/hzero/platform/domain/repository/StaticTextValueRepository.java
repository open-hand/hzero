package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.StaticTextValue;
import org.hzero.platform.domain.vo.StaticTextValueVO;

/**
 * 平台静态信息资源库
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:14:29
 */
public interface StaticTextValueRepository extends BaseRepository<StaticTextValue> {

    /**
     * 获取富文本值 并缓存到Redis中
     *
     * @param organizationId 租户ID
     * @param companyId      公司
     * @param textCode       文本编码
     * @param lang           语言
     * @return StaticTextValueVO
     */
    StaticTextValueVO getTextValue(Long organizationId, Long companyId, String textCode, String lang);

    /**
     * 根据ID获取文本内容
     *
     * @param textId 文本ID
     * @return StaticTextValue
     */
    StaticTextValue selectTextValueById(Long textId);

    /**
     * 获取富文本值 并缓存到Redis中,无返回空
     *
     * @param organizationId 租户ID
     * @param companyId      公司
     * @param textCode       文本编码
     * @param lang           语言
     * @return StaticTextValueVO
     */
    StaticTextValueVO getTextNullAble(Long organizationId, Long companyId, String textCode, String lang);
}
