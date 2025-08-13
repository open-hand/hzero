package org.hzero.report.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.report.domain.entity.Dataset;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 报表数据集Mapper
 *
 * @author xianzhi.chen@hand-china.com 2018-10-31 15:36:59
 */
public interface DatasetMapper extends BaseMapper<Dataset> {

    /**
     * 获取数据集列表
     *
     * @param dataset 数据集
     * @return 分页
     */
    List<Dataset> selectDatasets(Dataset dataset);

    /**
     * 获取数据集明细
     *
     * @param tenantId  租户Id
     * @param datasetId 数据集Id
     * @return 明细
     */
    Dataset selectDataset(@Param("tenantId") Long tenantId,
                          @Param("datasetId") Long datasetId);

    /**
     * 获取数据集被引用数量
     *
     * @param datasetId 数据集Id
     * @return 被引用数量
     */
    int selectReferenceCount(Long datasetId);

}
