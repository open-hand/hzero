package org.hzero.platform.infra.repository.impl;

import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.entity.Datasource;
import org.hzero.platform.domain.repository.DatasourceRepository;
import org.hzero.platform.infra.mapper.DatasourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import java.util.List;
import java.util.Objects;

/**
 * 数据源配置 资源库实现
 *
 * @author like.zhang@hand-china.com  2018-09-13 14:10:13
 */
@Component
public class DatasourceRepositoryImpl extends BaseRepositoryImpl<Datasource> implements DatasourceRepository {

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Override
    @ProcessLovValue
    public Page<Datasource> pageDatasource(PageRequest pageRequest, Datasource datasource, Boolean orgQueryFlag) {
        return PageHelper.doPageAndSort(pageRequest, () -> datasourceMapper.selectDatasources(datasource, orgQueryFlag));
    }

    @Override
    @ProcessLovValue
    public Datasource selectDatasource(Long datasourceId) {
        return datasourceMapper.selectDatasource(datasourceId);
    }

    @Override
    public Datasource getByUnique(Long tenantId, String datasourceCode) {
        return datasourceMapper.getByUnique(tenantId, datasourceCode);
    }

    @Override
    public List<Datasource> listDatasourceByCondition(Datasource datasource) {
        if (Objects.isNull(datasource)) {
            // 返回所有启用的数据源信息
            return selectByCondition(Condition.builder(Datasource.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(Datasource.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                    )
                    .build());
        } else {
            // 按照条件查询进行返回
            return select(datasource);
        }
    }
}
