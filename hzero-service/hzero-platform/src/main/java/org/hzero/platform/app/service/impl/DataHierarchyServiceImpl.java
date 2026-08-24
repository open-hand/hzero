package org.hzero.platform.app.service.impl;

import io.choerodon.core.exception.CommonException;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.app.service.DataHierarchyService;
import org.hzero.platform.domain.entity.DataHierarchy;
import org.hzero.platform.domain.repository.DataHierarchyRepository;
import org.hzero.platform.infra.constant.DataHierarchyDisplayStyle;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.properties.DataHierarchyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * 数据层级配置服务实现类
 *
 * @author qingsheng.chen@hand-china.com
 */
@Service
public class DataHierarchyServiceImpl implements DataHierarchyService {
    private static final String SPLIT = "|";
    private DataHierarchyRepository dataHierarchyRepository;
    private DataHierarchyProperties dataHierarchyProperties;

    @Autowired
    public DataHierarchyServiceImpl(DataHierarchyRepository dataHierarchyRepository,
                                    DataHierarchyProperties dataHierarchyProperties) {
        this.dataHierarchyRepository = dataHierarchyRepository;
        this.dataHierarchyProperties = dataHierarchyProperties;
    }

    @Override
    public List<DataHierarchyDTO> listDataHierarchy(Long tenantId, String dataHierarchyCode, String dataHierarchyName, Integer enabledFlag) {
        return dataHierarchyRepository.listDataHierarchy(tenantId, dataHierarchyCode, dataHierarchyName, enabledFlag);
    }

    @Override
    public List<DataHierarchyDTO> treeDataHierarchy(Long tenantId, String dataHierarchyCode, String dataHierarchyName, Integer enabledFlag) {
        return TreeBuilder.buildTree(listDataHierarchy(tenantId, dataHierarchyCode, dataHierarchyName, enabledFlag),
                null,
                DataHierarchyDTO::getDataHierarchyId,
                DataHierarchyDTO::getParentId);
    }

    @Override
    public DataHierarchyDTO getDataHierarchy(Long tenantId, Long dataHierarchyId) {
        return dataHierarchyRepository.getDataHierarchy(tenantId, dataHierarchyId);
    }

    @Override
    public DataHierarchyDTO getDataHierarchy(Long tenantId, String dataHierarchyCode) {
        return dataHierarchyRepository.getDataHierarchy(tenantId, dataHierarchyCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataHierarchy createDataHierarchy(DataHierarchy dataHierarchy) {
        if (dataHierarchy.getParentId() != null) {
            DataHierarchyDTO parent = getDataHierarchy(dataHierarchy.getTenantId(), dataHierarchy.getParentId());
            Assert.notNull(parent, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
            Assert.isTrue(Objects.equals(dataHierarchy.getTenantId(), parent.getTenantId()), BaseConstants.ErrorCode.DATA_INVALID);
            dataHierarchy.setEnabledFlag(parent.getEnabledFlag())
                    .setDisplayStyle(parent.getDisplayStyle())
                    .setLevelPath(parent.getLevelPath() + SPLIT + dataHierarchy.getDataHierarchyCode());
        } else {
            dataHierarchy.setLevelPath(dataHierarchy.getDataHierarchyCode());
        }
        dataHierarchyRepository.insertSelective(dataHierarchy);
        selectCountValid(dataHierarchy);
        dataHierarchyRepository.refreshCache(dataHierarchy.getTenantId(),
                treeDataHierarchy(dataHierarchy.getTenantId(), null, null, BaseConstants.Flag.YES));
        return dataHierarchy;
    }

    private void selectCountValid(DataHierarchy dataHierarchy) {
        if (BaseConstants.Flag.YES.equals(dataHierarchy.getEnabledFlag())
                && DataHierarchyDisplayStyle.SELECT.name().equals(dataHierarchy.getDisplayStyle())
                && dataHierarchyRepository.selectCountByCondition(Condition
                .builder(DataHierarchy.class)
                .andWhere(Sqls.custom()
                        .andEqualTo(DataHierarchy.FIELD_TENANT_ID, dataHierarchy.getTenantId())
                        .andEqualTo(DataHierarchy.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES)
                        .andEqualTo(DataHierarchy.FIELD_DISPLAY_STYLE, DataHierarchyDisplayStyle.SELECT.name()))
                .build()) > dataHierarchyProperties.getMaxSelectCount()) {
            throw new CommonException(HpfmMsgCodeConstants.ERROR_DATA_HIERARCHY_SELECT_EXCEEDS_MAXIMUM_NUMBER_LIMIT, dataHierarchyProperties.getMaxSelectCount());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataHierarchy updateDataHierarchy(DataHierarchy dataHierarchy) {
        DataHierarchyDTO exists = getDataHierarchy(dataHierarchy.getTenantId(), dataHierarchy.getDataHierarchyId());
        Assert.notNull(exists, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        dataHierarchyRepository.updateOptional(dataHierarchy, DataHierarchy.getUpdatableField());
        selectCountValid(dataHierarchy);
        // 修改了是否启用标记
        if (!Objects.equals(exists.getEnabledFlag(), dataHierarchy.getEnabledFlag())) {
            DataHierarchyDTO parent = getDataHierarchy(dataHierarchy.getTenantId(), exists.getParentId());
            Assert.isTrue(parent == null
                            || BaseConstants.Flag.YES.equals(parent.getEnabledFlag())
                            || (BaseConstants.Flag.NO.equals(parent.getEnabledFlag()) && BaseConstants.Flag.NO.equals(dataHierarchy.getEnabledFlag())),
                    HpfmMsgCodeConstants.ERROR_PARENT_DISABlE);
            dataHierarchyRepository.listDataHierarchyChildrenByLevelPath(exists.getTenantId(), exists.getLevelPath())
                    .forEach(item -> {
                        dataHierarchyRepository.updateOptional(item.setEnabledFlag(dataHierarchy.getEnabledFlag()), DataHierarchy.FIELD_ENABLED_FLAG);
                        selectCountValid(item);
                    });
        }
        // 是否修改了显示样式
        if (!Objects.equals(exists.getDisplayStyle(), dataHierarchy.getDisplayStyle())) {
            DataHierarchyDTO parent = getDataHierarchy(dataHierarchy.getTenantId(), exists.getParentId());
            Assert.isTrue(parent == null || Objects.equals(parent.getDisplayStyle(), dataHierarchy.getDisplayStyle()), HpfmMsgCodeConstants.ERROR_PARENT_DISPLAY_NOT_MATCH);
            dataHierarchyRepository.listDataHierarchyChildrenByLevelPath(exists.getTenantId(), exists.getLevelPath())
                    .forEach(item -> {
                        dataHierarchyRepository.updateOptional(item.setDisplayStyle(dataHierarchy.getDisplayStyle()), DataHierarchy.FIELD_DISPLAY_STYLE);
                        selectCountValid(item);
                    });
        }
        dataHierarchyRepository.refreshCache(dataHierarchy.getTenantId(),
                treeDataHierarchy(dataHierarchy.getTenantId(), null, null, BaseConstants.Flag.YES));
        return dataHierarchy;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataHierarchy(DataHierarchy dataHierarchy) {
        dataHierarchyRepository.deleteByPrimaryKey(dataHierarchy);
        dataHierarchyRepository.refreshCache(dataHierarchy.getTenantId(),
                treeDataHierarchy(dataHierarchy.getTenantId(), null, null, BaseConstants.Flag.YES));
    }
}
