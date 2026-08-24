package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.LovAggregateDTO;
import org.hzero.platform.domain.entity.Lov;

import java.util.List;

/**
 * <p><b>name</b> LovMapper</p>
 * <p><b>description</b> 值集头Mapper</p>
 *
 * @author gaokuo.dai@hand-china.com    2018年6月5日下午7:45:46
 * @version 1.0
 */
public interface LovMapper extends BaseMapper<Lov> {

    /**
     * 条件查询Lov头
     *
     * @param queryParam 查询条件
     * @return Lov头
     */
    List<Lov> selectLovHeaders(Lov queryParam);

    /**
     * 查询与给定代码重复的数据库记录数
     *
     * @param queryParam 查询条件
     * @return 重复的数据库记录数
     */
    int selectRepeatCodeCount(Lov queryParam);

    /**
     * 根据值集ID查询值集
     *
     * @param lovId
     * @return
     */
    Lov selectLovHeaderByLovId(@Param("lovId") Long lovId, @Param("tenantId") Long tenantId, @Param("sqlTypeControl") boolean sqlTypeControl);

    /**
     * 数据组维度查询
     *
     * @param queryParam
     * @return
     */
    List<Lov> listLovForDataGroupDimension(Lov queryParam);

    /**
     * 查询0租户下的父级值集
     *
     * @param lovCode  查询code
     * @param tenantId 租户Id
     * @return Lov
     */
    Lov selectLovHeaderByCodeAndTenant(@Param("lovCode") String lovCode, @Param("tenantId") Long tenantId);

    /**
     * 通过视图编码和租户Id获取关联的值集信息
     *
     * @param viewCode 视图编码
     * @param tenantId 租户Id
     * @return 关联值集信息
     */
    Lov selectLovByViewCodeAndTenant(@Param("viewCode") String viewCode, @Param("tenantId") Long tenantId);

    /**
     * 聚合获取值集头行数据
     *
     * @param lovCode  值集编码
     * @param tenantId 租户Id
     * @param lang     语言
     * @param tag      标记，仅独立值集可作为查询条件
     * @return 值集头行聚合结果
     */
    LovAggregateDTO selectLovAggregateLovValues(@Param("lovCode") String lovCode, @Param("tenantId") Long tenantId,
                                                @Param("lang") String lang, @Param("tag") String tag);

    /**
     * 根据ID和语言查询值集
     *
     * @param lovId 值集ID
     * @param lang  语言
     * @return 值集
     */
    Lov selectByIdAndLang(@Param("lovId") Long lovId, @Param("lang") String lang);

    /**
     * 根据ID和语言查询值集
     *
     * @param lovCode 值集编码
     * @param lang    语言
     * @return 值集
     */
    Lov selectByCodeAndLang(@Param("lovCode") String lovCode, @Param("tenantId") Long tenantId, @Param("lang") String lang);
}
