package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.commontemplate.CommonTemplateDTO;
import org.hzero.platform.api.dto.commontemplate.CommonTemplateQueryDTO;
import org.hzero.platform.domain.entity.CommonTemplate;
import org.hzero.platform.domain.vo.CommonTemplateVO;

import java.util.List;
import java.util.Set;

/**
 * 通用模板资源库
 *
 * @author bo.he02@hand-china.com 2020-08-04 09:49:14
 */
public interface CommonTemplateRepository extends BaseRepository<CommonTemplate> {
    /**
     * 按照查询条件查询通用模板数据
     *
     * @param queryDTO 查询参数对象
     * @return 查询结果信息
     */
    List<CommonTemplateDTO> list(CommonTemplateQueryDTO queryDTO);

    /**
     * 批量查询通用模板数据
     *
     * @param organizationId 租户ID
     * @param templateIds    通用模板IDs
     * @return 查询到的通用模板数据
     */
    List<CommonTemplate> list(Long organizationId, Set<Long> templateIds);

    /**
     * 查询通用模板的数据
     *
     * @param organizationId 租户ID
     * @param templateId     通用模板主键
     * @return 通用模板数据
     */
    CommonTemplateDTO detail(Long organizationId, Long templateId);

    /**
     * 查询通用模板数据
     *
     * @param organizationId 租户ID
     * @param templateCode   通用模板编码
     * @param lang           语言
     * @return 通用模板数据
     */
    CommonTemplateVO find(Long organizationId, String templateCode, String lang);

    /**
     * 缓存通用模板数据
     *
     * @param commonTemplate 待缓存的通用模板对象
     * @return 缓存结果对象
     */
    CommonTemplateVO cache(CommonTemplate commonTemplate);

    /**
     * 缓存所有通用模板数据
     */
    void cacheAll();
}
