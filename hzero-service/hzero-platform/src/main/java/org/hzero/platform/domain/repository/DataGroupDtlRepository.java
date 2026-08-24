package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DataGroupDtl;

import java.util.List;

/**
 * 数据组明细定义资源库
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupDtlRepository extends BaseRepository<DataGroupDtl> {
    /**
     * 分页查询
     * @param pageRequest 分页条件
     * @param dataGroupDtl 查询条件
     * @return
     */
    Page<DataGroupDtl> pageDataGroupDtl(PageRequest pageRequest,DataGroupDtl dataGroupDtl);

    /**
     * 数据数据组明细
     * @param dataGroupDtlList 数据组
     * @return
     */
    List<DataGroupDtl> createDataGroupDtl(List<DataGroupDtl> dataGroupDtlList);

    /**
     * 删除明细
     * @param dataGroupDtlList
     */
    void deleteDataGroupDtl(List<DataGroupDtl> dataGroupDtlList);
}
