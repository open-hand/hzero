package org.hzero.report.infra.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.ds.DatasourceHelper;
import org.hzero.boot.platform.ds.constant.DsConstants;
import org.hzero.boot.platform.ds.vo.DatasourceVO;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.domain.repository.DatasetRepository;
import org.hzero.report.infra.mapper.DatasetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 报表数据集 资源库实现
 *
 * @author xianzhi.chen@hand-china.com 2018-10-31 15:36:59
 */
@Component
public class DatasetRepositoryImpl extends BaseRepositoryImpl<Dataset> implements DatasetRepository {

    @Autowired
    private DatasetMapper datasetMapper;
    @Autowired
    private DatasourceHelper datasourceHelper;

    @Override
    public Page<Dataset> selectDatasets(PageRequest pageRequest, Dataset dataset) {
        Page<Dataset> page = PageHelper.doPageAndSort(pageRequest, () -> datasetMapper.selectDatasets(dataset));
        page.forEach(item -> {
            DatasourceVO datasource = datasourceHelper.getDatasource(DsConstants.DsPurpose.DR, item.getTenantId(), item.getDatasourceCode());
            if (datasource != null) {
                item.setDatasourceName(datasource.getDescription());
            }
        });
        return page;
    }

    @Override
    public Dataset selectDataset(Long tenantId, Long datasetId) {
        Dataset dataset = datasetMapper.selectDataset(tenantId, datasetId);
        if (dataset != null && StringUtils.isNotBlank(dataset.getDatasourceCode())) {
            DatasourceVO datasource = datasourceHelper.getDatasource(DsConstants.DsPurpose.DR, dataset.getTenantId(), dataset.getDatasourceCode());
            if (datasource != null) {
                dataset.setDatasourceName(datasource.getDescription());
            }
        }
        return dataset;
    }

    @Override
    public int selectReferenceCount(Long datasetId) {
        return datasetMapper.selectReferenceCount(datasetId);
    }
}
