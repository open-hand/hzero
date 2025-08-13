package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.platform.api.dto.LovViewAggregateDTO;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.vo.LovViewVO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 值集视图App服务
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午4:12:27
 */
public interface LovViewHeaderService {

    /**
     * 查找Lov视图
     *
     * @param viewCode 视图代码
     * @param tenantId 租户ID
     * @return Lov视图
     */
    default LovViewVO queryLovViewInfo(String viewCode, Long tenantId, String lang){
        return queryLovViewInfo(viewCode, tenantId, lang, false);
    }

    /**
     * 查找Lov视图
     *
     * @param viewCode 视图代码
     * @param tenantId 租户ID
     * @param onlyPublic 是否查询公开值集标识
     * @return Lov视图
     */
    LovViewVO queryLovViewInfo(String viewCode, Long tenantId, String lang, boolean onlyPublic);

    /**
     * 条件查询Lov视图头
     *
     * @param queryParam  查询条件
     * @param isSite      租户级接口默认带出平台数据，平台级接口按照选择的租户筛选数据
     * @param pageRequest 页码.分页大小
     * @return Lov视图头
     */
    Page<LovViewHeader> pageLovViewHeaders(LovViewHeader queryParam, boolean isSite, PageRequest pageRequest);

    /**
     * 更新视图头
     *
     * @param header 待更新的视图头
     * @return 更新后的视图头
     */
    LovViewHeader updateByPrimaryKey(LovViewHeader header);

    /**
     * 批量删除视图头
     *
     * @param headers 待删除的视图头
     * @return 后的视图头
     */
    int batchDeleteByPrimaryKey(List<LovViewHeader> headers);

    /**
     * 插入视图头
     *
     * @param header 待插入的视图头
     * @return 插入后的视图头
     */
    LovViewHeader insertSelective(LovViewHeader header);

    /**
     * 复制值集视图
     *
     * @param tenantId     租户Id
     * @param viewCode     视图编码
     * @param viewHeaderId 视图头id
     * @param siteFlag     平台标识
     */
    void copyLovView(Long tenantId, String viewCode, Long viewHeaderId, Integer siteFlag);

    /**
     * 删除值集视图
     *
     * @param lovViewHeader 删除的数据
     */
    void deleteLovViewHeader(LovViewHeader lovViewHeader);

    /**
     * 聚合获取值集视图头行信息
     *
     * @param viewCode  视图编码
     * @param tenantId  租户Id
     * @param lang      语言
     * @return 查询结果
     */
    LovViewAggregateDTO queryLovViewAggregate(String viewCode, Long tenantId, String lang);
}
