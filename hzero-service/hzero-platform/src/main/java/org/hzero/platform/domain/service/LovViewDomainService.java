package org.hzero.platform.domain.service;

import java.util.List;

import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.vo.LovViewVO;

/**
 * 值集视图逻辑统一处理Service
 *
 * @author xiaoyu.zhao@hand-china.com 2019/05/21 15:50
 */
public interface LovViewDomainService {

    /**
     * 查找Lov视图
     * @param viewCode 视图代码
     * @param tenantId 租户ID
     * @return Lov视图
     */
    LovViewVO queryLovViewInfo(String viewCode, Long tenantId, String lang, boolean onlyPublic);

    /**
     * 更新视图头
     * @param header 待更新的视图头
     * @return 更新后的视图头
     */
    LovViewHeader updateByPrimaryKey(LovViewHeader header);

    /**
     * 批量删除视图头
     * @param headers 待删除的视图头
     * @return 后的视图头
     */
    int batchDeleteByPrimaryKey(List<LovViewHeader> headers);

    /**
     * 插入视图头
     * @param header 待插入的视图头
     * @return 插入后的视图头
     */
    LovViewHeader insertSelective(LovViewHeader header);

    /**
     * 复制值集视图
     *
     * @param tenantId 租户Id
     * @param viewCode 视图编码
     * @param viewHeaderId 视图头id
     * @param siteFlag 平台标识
     */
    void copyLovView(Long tenantId, String viewCode, Long viewHeaderId, Integer siteFlag);

    /**
     * 删除值集视图
     *
     * @param lovViewHeader 删除的数据
     */
    void deleteLovViewHeader(LovViewHeader lovViewHeader);
}
