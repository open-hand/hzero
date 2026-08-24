package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.LovViewAggregateDTO;
import org.hzero.platform.domain.entity.LovViewHeader;

import java.util.List;

/**
 * <p>
 * description
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/14 10:10
 */
public interface LovViewHeaderMapper extends BaseMapper<LovViewHeader> {

    /**
     * 条件查询值集视图头
     *
     * @param queryParam 查询条件
     * @return 值集视图头列表
     */
    List<LovViewHeader> selectLovViewHeader(@Param("queryParam") LovViewHeader queryParam, @Param("isSite") boolean isSite);

    /**
     * 根据LovCode查询视图代码
     *
     * @param lovCode 值集代码
     * @return 值集代码关联的视图代码
     */
    List<String> selectViewCodeByLovCode(@Param("lovCode") String lovCode);

    /**
     * 根据视图头ID删除视图行
     *
     * @param viewHeaderId 视图头ID
     * @return 被删除的数量
     */
    int deleteViewLineByviewHeaderId(@Param("viewHeaderId") Long viewHeaderId);

    /**
     * 查询数据库中相同的code的数量(不计本身)
     *
     * @param queryParam 查询条件
     * @return 相同的code的数量
     */
    int selectRepeatCodeCount(LovViewHeader queryParam);

    /**
     * 根据ID查询值集视图头
     *
     * @param viewHeaderId
     * @param tenantId
     * @return
     */
    LovViewHeader selectLovViewHeaderByPrimaryKey(@Param("viewHeaderId") Long viewHeaderId, @Param("tenantId") Long tenantId);

    /**
     * 聚合获取值集视图头行信息
     *
     * @param viewCode 视图编码
     * @param tenantId 租户Id
     * @param lang     语言
     * @return 查询结果
     */
    LovViewAggregateDTO selectLovViewAggregate(@Param("viewCode") String viewCode, @Param("tenantId") Long tenantId, @Param("lang") String lang);

    /**
     * 根据编码查询视图
     *
     * @param viewCode 视图编码
     * @param tenantId 租户ID
     * @param lang     语言
     * @return 视图
     */
    LovViewHeader selectLovViewByCode(@Param("viewCode") String viewCode, @Param("tenantId") Long tenantId, @Param("lang") String lang);
}
