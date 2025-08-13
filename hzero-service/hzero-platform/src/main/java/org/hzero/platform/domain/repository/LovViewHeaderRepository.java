/**
 *
 */
package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.LovViewAggregateDTO;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.vo.LovViewVO;

import java.util.List;

/**
 * 值集视图头表仓库
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午5:26:13
 */
public interface LovViewHeaderRepository extends BaseRepository<LovViewHeader> {

    /**
     * 根据从缓存中装载租户级Lov视图
     * @param viewCode 视图代码
     * @param tenantId 租户ID
     * @param lang 语言
     * @return 缓存中装载的Lov视图, 无权访问是返回accessDeniedFlag为1的空对象
     */
    List<LovViewVO> queryLovViewDTOFromCacheByTenant(String viewCode, Long tenantId, String lang);

    /**
     * 根据视图代码查询Lov视图
     *
     * @param viewCode 视图代码
     * @param tenantId 租户ID
     * @return Lov视图
     */
    LovViewHeader selectByViewCode(String viewCode, Long tenantId);

    /**
     * 根据视图代码查询Lov视图
     *
     * @param viewCode 视图代码
     * @param tenantId 租户ID
     * @return Lov视图
     */
    LovViewHeader selectByViewCode(String viewCode, Long tenantId, String lang);

    /**
     * 根据视图头信息从数据库中加载视图行并组装<br/>
     * <i>此过程中会组织缓存</i>
     *
     * @param lovViewHeader 视图头
     * @return 视图DTO
     */
    LovViewVO queryLovViewDTOByLovViewHeader(LovViewHeader lovViewHeader, String lang);

    /**
     * 条件查询值集视图头
     * @param queryParam 查询条件
     * @return 值集视图头列表
     */
    List<LovViewHeader> selectLovViewHeader(LovViewHeader queryParam, boolean isSite);

    /**
     * 根据LovCode查询视图代码
     * @param lovCode 值集代码
     * @return 值集代码关联的视图代码
     */
    List<String> selectViewCodeByLovCode(String lovCode);

    /**
     * 根据视图头ID删除视图行
     * @param viewHeaderId 视图头ID
     * @return 被删除的数量
     */
    int deleteViewLineByviewHeaderId(Long viewHeaderId);

    /**
     * 根据视图代码清除值集值缓存
     * @param lovViewCode 值集视图代码
     * @return 是否清除成功
     */
    boolean cleanCache(String lovViewCode, Long tenantId, String language);

    /**
     * 根据视图头ID清除值集值缓存<br/>
     * <i>会查询数据库</i>
     * @param viewHeaderId 视图头ID
     * @return 是否清除成功
     */
    boolean cleanCache(Long viewHeaderId);

    /**
     * 查询数据库中相同的code的数量(不计本身)
     * @param queryParam 查询条件
     * @return 相同的code的数量
     */
    int selectRepeatCodeCount(LovViewHeader queryParam);

    /**
     * 按主键更新值集
     * @param record
     * @return
     */
    @Override
    int updateByPrimaryKey(LovViewHeader record);

    /**
     * 根据ID查询视图头
     *
     * @param viewHeaderId
     * @param tenantId
     * @return
     */
    LovViewHeader selectLovViewHeaderByPrimaryKey(Long viewHeaderId, Long tenantId);

    /**
     * 刷新缓存
     *
     * @param viewCode 值集视图编码
     * @param tenantId 租户ID
     * @param lang     语言
     */
    void refreshCacheExpire(String viewCode, Long tenantId, String lang);

    /**
     * 聚合获取值集视图头行信息
     *
     * @param viewCode  视图编码
     * @param tenantId  租户Id
     * @param lang      语言
     * @return 查询结果
     */
    LovViewAggregateDTO selectLovViewAggregate(String viewCode, Long tenantId, String lang);
}
