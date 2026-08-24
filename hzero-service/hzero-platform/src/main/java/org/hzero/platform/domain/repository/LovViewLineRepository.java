/**
 *
 */
package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.LovViewLine;

import java.util.List;

/**
 * 值集视图行表仓库
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午5:26:13
 */
public interface LovViewLineRepository extends BaseRepository<LovViewLine> {

    /**
     * 按值集Id分页查询值集视图行
     * @param viewHeaderId 值集视图ID
     * @param tenantId 租户ID
     * @param pageRequest 分页参数
     * @return 分页后的值集视图行
     */
    Page<LovViewLine> pageAndSortLovViewLineByLovId(Long viewHeaderId, Long tenantId, PageRequest pageRequest);

    /**
     * 按主键更新值集
     * @param record
     * @return
     */
    @Override
    int updateByPrimaryKey(LovViewLine record);

    /**
     * 按主键删除
     * @param key
     * @return
     */
    @Override
    int deleteByPrimaryKey(Object key);

    /**
     * 批量删除值集视图行
     *
     * @param lovViewLines 删除数据
     */
    void batchDeleteLovViewLinesByPrimaryKey(List<LovViewLine> lovViewLines);

    /**
     * 根据视图头查行
     *
     * @param lovViewHeaderId 头id
     * @param lang            语言
     * @return 视图行
     */
    List<LovViewLine> selectByHeaderId(Long lovViewHeaderId, String lang);
}
