package org.hzero.platform.app.service.impl;

import org.hzero.core.base.BaseConstants;
import org.hzero.platform.api.dto.LovValueDTO;
import org.hzero.platform.app.service.DatasourceDriverService;
import org.hzero.platform.app.service.LovValueService;
import org.hzero.platform.domain.entity.Datasource;
import org.hzero.platform.domain.entity.DatasourceDriver;
import org.hzero.platform.domain.repository.DatasourceDriverRepository;
import org.hzero.platform.domain.repository.DatasourceRepository;
import org.hzero.platform.domain.service.datasource.DsExtendEntrance;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据源驱动配置应用服务默认实现
 *
 * @author xiaoyu.zhao@hand-china.com 2019-08-21 19:55:37
 */
@Service
public class DatasourceDriverServiceImpl implements DatasourceDriverService {

    private DatasourceDriverRepository driverRepository;
    private DatasourceRepository datasourceRepository;
    private LovValueService lovValueService;
    private DsExtendEntrance dsEventPublish;
    @Autowired
    public DatasourceDriverServiceImpl(DatasourceDriverRepository driverRepository,
            DatasourceRepository datasourceRepository, LovValueService lovValueService, DsExtendEntrance dsEventPublish) {
        this.driverRepository = driverRepository;
        this.datasourceRepository = datasourceRepository;
        this.lovValueService = lovValueService;
        this.dsEventPublish = dsEventPublish;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DatasourceDriver createDatasourceDriver(DatasourceDriver datasourceDriver) {
        // 添加数据库类型和版本的唯一性校验
        driverRepository.validateUnique(datasourceDriver);
        // 处理主类入口参数
        processDriverMainClass(datasourceDriver);
        driverRepository.insertSelective(datasourceDriver);
        return datasourceDriver;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DatasourceDriver updateDatasourceDriver(DatasourceDriver datasourceDriver) {
        Assert.notNull(datasourceDriver.getDriverId(), BaseConstants.ErrorCode.NOT_NULL);
        Assert.notNull(datasourceDriver.getDriverPath(), BaseConstants.ErrorCode.NOT_NULL);
        DatasourceDriver dbDriver = driverRepository.selectByPrimaryKey(datasourceDriver.getDriverId());
        String driverPath = datasourceDriver.getDriverPath();
        // 仅更新描述，驱动路径以及启用禁用状态信息
        driverRepository.updateOptional(datasourceDriver, DatasourceDriver.FIELD_DESCRIPTION,
                DatasourceDriver.FIELD_DRIVER_PATH, DatasourceDriver.FIELD_ENABLED_FLAG);
        // 处理驱动更新事件发布，如果路径没变则无需发布更新事件
        if (!driverPath.equals(dbDriver.getDriverPath())) {
            List<Datasource> eventDatasources =
                    datasourceRepository.select(new Datasource().setDriverId(datasourceDriver.getDriverId()).setDriverType(
                            Constants.Datasource.CUSTOMIZE));
            if (!CollectionUtils.isEmpty(eventDatasources)) {
                // 驱动jar文件更新且驱动被数据源引用，循环发布更新通知事件
                eventDatasources.forEach(datasource -> dsEventPublish.updateEventPublish(datasource, datasource, datasourceDriver));
            }
        }
        return datasourceDriver;
    }

    @Override
    public void deleteDriver(DatasourceDriver datasourceDriver) {
        // 校验数据源驱动是否被数据源所应用
        Assert.notNull(datasourceDriver.getDriverId(), BaseConstants.ErrorCode.NOT_NULL);
        int count = datasourceRepository.selectCount(new Datasource().setDriverId(datasourceDriver.getDriverId()));
        if (count > 0) {
            // 驱动已经被引用，不可删除
            throw new CommonException(HpfmMsgCodeConstants.ERROR_DELETE_USED_DRIVER);
        } else {
            driverRepository.deleteByPrimaryKey(datasourceDriver.getDriverId());
        }
    }

    /**
     * 处理数据源驱动主类入口
     *
     * @param datasourceDriver 新增或更新的数据源驱动
     */
    protected void processDriverMainClass(DatasourceDriver datasourceDriver) {
        List<LovValueDTO> lovValues = lovValueService
                .queryLovValueByParentValue(DatasourceDriver.MAIN_CLASS_LOV_CODE, datasourceDriver.getDatabaseType(),
                        datasourceDriver.getTenantId())
                .stream()
                .filter(database -> database.getParentValue() != null)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(lovValues) || lovValues.size() > 1) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }
        datasourceDriver.setMainClass(lovValues.get(0).getMeaning());
    }
}
