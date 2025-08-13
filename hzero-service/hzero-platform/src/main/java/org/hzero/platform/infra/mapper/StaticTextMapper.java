package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.StaticText;
import org.hzero.platform.domain.vo.StaticTextVO;

import java.util.List;

/**
 * 平台静态信息Mapper
 *
 * @author fatao.liu@hand-china.com 2018-07-23 14:19:42
 */
public interface StaticTextMapper extends BaseMapper<StaticText> {

    /**
     * 查询静态富文本
     *
     * @param params 参数
     */
    List<StaticTextVO> selectStaticText(StaticTextVO params);

    /**
     * 根据id查询静态富文本
     *
     * @param idList
     * @return
     */
    List<StaticTextVO> selectStaticTextByIds(@Param("idList") List<Long> idList);

    /**
     * 查询所有的静态富文本id和parentIds
     *
     * @param staticTextVO
     * @return
     */
    List<StaticTextVO> selectAllStaticText(StaticTextVO staticTextVO);

    /**
     * 根据编码查询有效的文本，以及子节点
     */
    StaticTextVO selectStaticTextByCode(StaticTextVO params);

    /**
     * 查询文本详情
     *
     * @param textId ID
     * @param lang   语言
     * @return StaticTextDTO
     */
    StaticTextVO selectStaticTextDetails(@Param("textId") Long textId, @Param("lang") String lang);

    /**
     * 查询所有子节点ID
     */
    List<StaticTextVO> selectAllChildTextId(@Param("parentId") Long parentId);

    /**
     * 从当前节点往上查询父级节点
     */
    StaticTextVO selectSelfAndParent(StaticTextVO params);
}
