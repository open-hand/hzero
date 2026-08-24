package org.hzero.report.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.report.domain.entity.Dataset;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表数据集资源库
 *
 * @author xianzhi.chen@hand-china.com 2018-10-31 15:36:59
 */
public interface DatasetRepository extends BaseRepository<Dataset> {

    /**
     * 获取数据集列表
     *
     * @param pageRequest 分页
     * @param dataset     数据集参数
     * @return 分页数据
     */
    Page<Dataset> selectDatasets(PageRequest pageRequest, Dataset dataset);

    /**
     * 获取数据集明细
     *
     * @param tenantId  租户Id
     * @param datasetId 数据集Id
     * @return 明细信息
     */
    Dataset selectDataset(Long tenantId, Long datasetId);

    /**
     * 获取数据集被引用数量
     *
     * @param datasetId 数据集Id
     * @return 被引用数量
     */
    int selectReferenceCount(Long datasetId);

}
