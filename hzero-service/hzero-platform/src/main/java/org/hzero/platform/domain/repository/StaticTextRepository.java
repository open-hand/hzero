package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.StaticText;
import org.hzero.platform.domain.vo.StaticTextVO;

import java.util.List;

/**
 * 平台静态信息资源库
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:19:42
 */
public interface StaticTextRepository extends BaseRepository<StaticText> {

    /**
     * 分页查询静态文本列表
     */
    Page<StaticTextVO> paging(StaticTextVO params, PageRequest pageRequest);

    /**
     * 查询详细，包含富文本
     *
     * @param textId 文本ID
     * @param lang   语言
     */
    StaticTextVO getTextDetails(Long textId, String lang);

    /**
     * 根据编码查询基本信息，包含所有子节点，并缓存到Redis
     *
     * @param organizationId 租户ID
     * @param companyId      公司ID
     * @param textCode       文本编码
     * @return StaticTextVO
     */
    StaticTextVO getTextHead(Long organizationId, Long companyId, String textCode);

    /**
     * 获取所有子节点ID
     */
    List<Long> getAllChildTextId(Long textId);

    /**
     * 清除自身缓存的同时清除父级缓存
     */
    void clearCache(StaticText staticText);
}
