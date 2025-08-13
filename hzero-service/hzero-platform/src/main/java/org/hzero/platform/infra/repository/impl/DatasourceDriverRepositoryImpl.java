package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.domain.entity.DatasourceDriver;
import org.hzero.platform.domain.repository.DatasourceDriverRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.mapper.DatasourceDriverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 数据源驱动配置 资源库实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-08-21 19:55:37
 */
@Component
public class DatasourceDriverRepositoryImpl extends BaseRepositoryImpl<DatasourceDriver> implements DatasourceDriverRepository {

    private final DatasourceDriverMapper mapper;

    @Autowired
    public DatasourceDriverRepositoryImpl(DatasourceDriverMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @ProcessLovValue
    public Page<DatasourceDriver> listDatasourceDrivers(PageRequest pageRequest, DatasourceDriver datasourceDriver,  Boolean orgQueryFlag) {
        datasourceDriver.setOrgQueryFlag(orgQueryFlag);
        return PageHelper.doPageAndSort(pageRequest, () -> mapper.selectDatasourceDrivers(datasourceDriver));
    }

    @Override
    public DatasourceDriver getDriverByDatabaseType(Long tenantId, String databaseType) {
        return mapper.selectDriverByDatabaseType(tenantId, databaseType);
    }

    @Override
    public void validateUnique(DatasourceDriver driver) {
        // 校验同一租户及数据库类型下驱动版本是否唯一
        int count = selectCountByCondition(Condition.builder(DatasourceDriver.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DatasourceDriver.FIELD_TENANT_ID, driver.getTenantId())
                        .andEqualTo(DatasourceDriver.FIELD_DATABASE_TYPE, driver.getDatabaseType())
                        .andEqualTo(DatasourceDriver.FIELD_DRIVER_VERSION, driver.getDriverVersion())
                )
                .build());
        if (count > 0) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_DRIVER_EXISTS, driver.getTenantId(),
                    driver.getDatabaseType(), driver.getDriverVersion());
        }
    }

    @Override
    public DatasourceDriver getDriverDetails(Long driverId) {
        return mapper.selectDriverDetails(driverId);
    }

    @Override
    public List<DatasourceDriver> listDriverByCondition(DatasourceDriver datasourceDriver) {
        if (Objects.isNull(datasourceDriver)) {
            return selectByCondition(Condition.builder(DatasourceDriver.class)
                    .andWhere(Sqls.custom()
                            .andEqualTo(DatasourceDriver.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                    )
                    .build());
        } else {
            return select(datasourceDriver);
        }
    }
}
