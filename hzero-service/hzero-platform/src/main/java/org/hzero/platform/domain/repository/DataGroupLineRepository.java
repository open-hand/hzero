package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DataGroupLine;

/**
 * 数据组行定义资源库
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupLineRepository extends BaseRepository<DataGroupLine> {
    /**
     *  分页查询数据分组行
     * @param pageRequest 分页查询条件
     * @param dataGroupLine 数据分组行
     * @return
     */
    Page<DataGroupLine> pageDataGroupLine(PageRequest pageRequest,DataGroupLine dataGroupLine);
}
